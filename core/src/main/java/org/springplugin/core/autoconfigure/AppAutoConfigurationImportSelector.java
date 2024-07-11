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

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springplugin.core.scan.AppAutoConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 插件应用自动配置导入选择器
 * <p>
 * {@link AutoConfigurationImportSelector}
 *
 * @author afěi
 * @version 1.0.0
 */
public class AppAutoConfigurationImportSelector extends AutoConfigurationImportSelector {

    @Override
    protected boolean isEnabled(AnnotationMetadata metadata) {
        return true;
    }

    @Override
    protected Class<?> getAnnotationClass() {
        return AppAutoConfiguration.class;
    }

    @Override
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
