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

package org.springplugin.core.scan;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.util.ClassUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * 插件类扫描器
 *
 * @author afěi
 * @version 1.0.0
 * @see PluginScan
 * @see PluginScannerConfigurer
 */
@Setter
public class PluginClassScanner extends ClassPathBeanDefinitionScanner {

    /**
     * log
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginClassScanner.class);

    /**
     * 主类名
     */
    private String mainClassName;

    /**
     * 是否懒加载
     */
    private boolean lazyInitialization;

    /**
     * bean的默认生命周期
     */
    private String defaultScope;

    /**
     * 构造方法
     *
     * @param registry bean描述注册器
     * @author afěi
     */
    public PluginClassScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    /**
     * 转化bean class
     *
     * @param definition 抽象的bean描述
     * @author afěi
     */
    private void resolveBeanClass(AbstractBeanDefinition definition) {
        try {
            if (!definition.hasBeanClass()) {
                definition.resolveBeanClass(ClassUtils.currentClassLoader());
            }
        } catch (ClassNotFoundException e) {
            throw new SpringPluginException(String.format("Resolve bean class fail, %s", definition), e);
        }
    }

    /**
     * Configures parent scanner to search for the right interfaces. It can search for all interfaces or just for those
     * that extends a markerInterface or/and those annotated with the annotationClass
     *
     * @author afěi
     */
    public void registerFilters() {

        // default include filter that accepts all classes
        addIncludeFilter((metadataReader, metadataReaderFactory) -> true);

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    @Override
    @NonNull
    public Set<BeanDefinitionHolder> doScan(@NonNull String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            LOGGER.warn("No plugin was found in '" + Arrays.toString(basePackages)
                    + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    /**
     * bean描述处理
     *
     * @param beanDefinitions bean描述集合
     * @author afěi
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        final BeanDefinitionRegistry registry = getRegistry();
        Assert.notNull(registry, "this argument is required; it must not be null");
        for (BeanDefinitionHolder holder : beanDefinitions) {

            final AbstractBeanDefinition definition = (AbstractBeanDefinition) holder.getBeanDefinition();
            resolveBeanClass(definition);
            if (definition instanceof RootBeanDefinition rbd) {
                final BeanDefinitionHolder decoratedDefinitionHolder = rbd.getDecoratedDefinition();
                if (Objects.nonNull(decoratedDefinitionHolder)) {
                    resolveBeanClass((AbstractBeanDefinition) decoratedDefinitionHolder.getBeanDefinition());
                }
            }
            definition.setLazyInit(lazyInitialization);

            if (ConfigurableBeanFactory.SCOPE_SINGLETON.equals(definition.getScope()) && defaultScope != null) {
                definition.setScope(defaultScope);
            }

            if (!definition.isSingleton()) {
                BeanDefinitionHolder proxyHolder = ScopedProxyUtils.createScopedProxy(holder, registry, true);
                if (registry.containsBeanDefinition(proxyHolder.getBeanName())) {
                    registry.removeBeanDefinition(proxyHolder.getBeanName());
                }
                registry.registerBeanDefinition(proxyHolder.getBeanName(), proxyHolder.getBeanDefinition());
            }
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

        final AnnotationMetadata metadata = beanDefinition.getMetadata();
        return !metadata.getClassName().equals(mainClassName) && metadata.getAnnotations().isPresent(Component.class);
    }

    @Override
    protected boolean isCandidateComponent(MetadataReader metadataReader) {
        final AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        return !metadata.getClassName().equals(mainClassName) && metadata.getAnnotations().isPresent(Component.class);
    }

    @Override
    protected boolean checkCandidate(@NonNull String beanName, @NonNull BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            LOGGER.warn("Skipping plugin bean with name '" + beanName + "' and '"
                    + beanDefinition.getBeanClassName() + "' class" + ". Bean already defined with the same name!");
            return false;
        }
    }

    @NonNull
    @Override
    public Set<BeanDefinition> findCandidateComponents(@NonNull String basePackage) {

        return super.findCandidateComponents(basePackage);
    }
}
