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

package org.springplugin.server.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springplugin.core.contant.PluginConstant;
import org.springplugin.core.context.PluginContext;
import org.springplugin.core.context.SpringPluginContext;
import org.springplugin.core.env.properties.SpringPluginProperties;
import org.springplugin.core.factory.SpringPluginChildContextInitializer;
import org.springplugin.core.factory.SpringPluginFactory;
import org.springplugin.core.factory.SpringPluginFactoryCommonSpec;
import org.springplugin.core.factory.SpringWebPluginConfiguration;

/**
 * 插件配置
 *
 * @author afěi
 * @version 1.1.3
 */
@Configuration
@RequiredArgsConstructor
public class SpringPluginConfig {

    /**
     * 插件上下文
     *
     * @author afěi
     */
    @Bean
    public PluginContext pluginContext(SpringPluginFactory springPluginFactory, SpringPluginProperties configProps) {

        return new SpringPluginContext(springPluginFactory, configProps)
                .addFilterAnnotation(SpringBootApplication.class)
                .addFilterAnnotation(EnableAutoConfiguration.class);
    }

    /**
     * Spring插件工厂公共规范
     *
     * @author afěi
     */
    @Bean
    public SpringPluginFactoryCommonSpec springPluginFactoryCommonSpec() {

        return new SpringPluginFactoryCommonSpec(SpringWebPluginConfiguration.class);
    }

    /**
     * 插件子上下文初始化器
     *
     * @param springPluginFactory           Spring插件工厂
     * @param springPluginFactoryCommonSpec Spring插件工厂公共规范
     * @author afěi
     */
    @Bean
    public SpringPluginChildContextInitializer springPluginChildContextInitializer(SpringPluginFactory springPluginFactory,
                                                                                   SpringPluginFactoryCommonSpec springPluginFactoryCommonSpec) {
        return new SpringPluginChildContextInitializer(springPluginFactory, springPluginFactoryCommonSpec);
    }

    /**
     * open api定制器
     * <p>
     * 添加插件元信息头
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnProperty(prefix = SpringPluginProperties.PREFIX, name = "intercept.identity-mode", havingValue = "HEADER")
    public OpenApiCustomizer openApiCustomizer() {

        return openApi -> openApi.getPaths()
                .values()
                .stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .filter(operation -> !operation.getTags().contains("pm"))
                .forEach(operation -> operation
                        .addParametersItem(new HeaderParameter()
                                .name(PluginConstant.META_HEADER)
                                .required(true)
                                .in(ParameterIn.HEADER.toString())
                                .description("Plugin Meta Info")
                        ));
    }
}
