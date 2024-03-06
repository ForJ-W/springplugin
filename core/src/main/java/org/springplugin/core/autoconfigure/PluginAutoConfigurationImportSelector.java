/*
 * Copyright 2023 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springplugin.core.autoconfigure;

import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springplugin.core.classloader.SpringPluginClassLoader;

import java.net.URL;
import java.util.*;

/**
 * 插件自动配置导入选择器
 * <p>
 * {@link AutoConfigurationImportSelector}
 *
 * @author afěi
 * @version 1.0.0
 */
public class PluginAutoConfigurationImportSelector implements DeferredImportSelector,
        EnvironmentAware, BeanFactoryAware, ResourceLoaderAware {


    private static final String LOCATION = "META-INF/spring/%s.imports";

    private static final String PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE = "spring.autoconfigure.exclude";

    private Environment environment;

    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    private ConfigurationClassFilter configurationClassFilter;

    private ClassLoader beanClassLoader;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    protected final Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @NonNull
    @Override
    public String[] selectImports(@NonNull AnnotationMetadata annotationMetadata) {
        final String plugin = annotationMetadata.getClassName().split("\\.")[0];
        this.beanClassLoader = SpringPluginClassLoader.getInstance(plugin);
        String location = String.format(LOCATION, AutoConfiguration.class.getName());
        Enumeration<URL> urls = ImportCandidates.findUrlsInClasspath(this.beanClassLoader, location);
        List<String> importCandidates = new ArrayList<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url.getPath().contains(plugin)) {

                final List<String> candidates = ImportCandidates.readCandidateConfigurations(url);
                importCandidates.addAll(candidates);
            }
        }
        importCandidates = removeDuplicates(importCandidates);
        importCandidates = getConfigurationClassFilter().filter(importCandidates);
        getExcludeAutoConfigurationsProperty().forEach(importCandidates::remove);
        return StringUtils.toStringArray(importCandidates);
    }

    /**
     * {@link AutoConfigurationImportSelector#removeDuplicates(List)}
     */
    protected final <T> List<T> removeDuplicates(List<T> list) {
        return new ArrayList<>(new LinkedHashSet<>(list));
    }

    /**
     * {@link AutoConfigurationImportSelector#getConfigurationClassFilter()}
     */
    private ConfigurationClassFilter getConfigurationClassFilter() {
        if (this.configurationClassFilter == null) {
            List<AutoConfigurationImportFilter> filters = getAutoConfigurationImportFilters();
            for (AutoConfigurationImportFilter filter : filters) {
                invokeAwareMethods(filter);
            }
            this.configurationClassFilter = new ConfigurationClassFilter(this.beanClassLoader, filters);
        }
        return this.configurationClassFilter;
    }

    /**
     * {@link AutoConfigurationImportSelector#invokeAwareMethods(Object)}
     */
    private void invokeAwareMethods(Object instance) {
        if (instance instanceof Aware) {
            if (instance instanceof BeanClassLoaderAware beanClassLoaderAwareInstance) {
                beanClassLoaderAwareInstance.setBeanClassLoader(this.beanClassLoader);
            }
            if (instance instanceof BeanFactoryAware beanFactoryAwareInstance) {
                beanFactoryAwareInstance.setBeanFactory(this.beanFactory);
            }
            if (instance instanceof EnvironmentAware environmentAwareInstance) {
                environmentAwareInstance.setEnvironment(this.environment);
            }
            if (instance instanceof ResourceLoaderAware resourceLoaderAwareInstance) {
                resourceLoaderAwareInstance.setResourceLoader(this.resourceLoader);
            }
        }
    }

    /**
     * {@link AutoConfigurationImportSelector#getAutoConfigurationImportFilters()}
     */
    protected List<AutoConfigurationImportFilter> getAutoConfigurationImportFilters() {
        return SpringFactoriesLoader.loadFactories(AutoConfigurationImportFilter.class, this.beanClassLoader);
    }

    /**
     * Returns the auto-configurations excluded by the
     * {@code spring.autoconfigure.exclude} property.
     * <p>
     * {@link AutoConfigurationImportSelector#getExcludeAutoConfigurationsProperty()}
     *
     * @return excluded auto-configurations
     * @since 2.3.2
     */
    protected List<String> getExcludeAutoConfigurationsProperty() {
        Environment environment = getEnvironment();
        if (environment == null) {
            return Collections.emptyList();
        }
        if (environment instanceof ConfigurableEnvironment) {
            final PropertySourcesPlaceholdersResolver propertySourcesPlaceholdersResolver = new PropertySourcesPlaceholdersResolver(environment);
            return new Binder(ConfigurationPropertySources.from(((ConfigurableEnvironment) environment).getPropertySources()), propertySourcesPlaceholdersResolver)
                    .bind(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());
        }
        String[] excludes = environment.getProperty(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class);
        return (excludes != null) ? Arrays.asList(excludes) : Collections.emptyList();
    }
}
