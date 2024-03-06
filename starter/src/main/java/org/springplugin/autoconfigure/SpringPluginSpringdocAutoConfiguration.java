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

package org.springplugin.autoconfigure;


import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.providers.SpringWebProvider;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.core.providers.SpringWebMvcProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springplugin.core.contant.PluginConstant;
import org.springplugin.core.env.properties.SpringPluginProperties;
import org.springplugin.core.factory.SpringPluginFactory;
import org.springplugin.core.springdoc.SpringDocBeanPostProcessor;
import org.springplugin.core.springdoc.SpringPluginWebMvcProvider;

import java.util.Set;

/**
 * 插件配置类
 *
 * @author afěi
 * @version 1.0.0
 */
@Configuration
@AutoConfigureAfter(SpringPluginAutoConfiguration.class)
@ConditionalOnClass(SpringWebMvcProvider.class)
@ConditionalOnBean(SpringPluginFactory.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RequiredArgsConstructor
@AutoConfigureBefore(SpringDocWebMvcConfiguration.class)
public class SpringPluginSpringdocAutoConfiguration {

    private final SpringPluginFactory factory;


    /**
     * spring插件 webmvc springdoc提供者
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringWebProvider springWebProvider() {
        return new SpringPluginWebMvcProvider();
    }


    /**
     * spring doc bean后置处理器
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringDocBeanPostProcessor springDocBeanPostProcessor() {
        return new SpringDocBeanPostProcessor();
    }

    /**
     * 定制open api构建
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenApiBuilderCustomizer openApiBuilderCustomizer() {
        return builder -> {
            final Set<String> contextNames = factory.getContextNames();
            // 获取所有插件上下文中的相关注解的bean
            for (String contextName : contextNames) {
                final GenericApplicationContext context = factory.getContext(contextName);
                builder.addMappings(context.getBeansWithAnnotation(RestController.class));
                builder.addMappings(context.getBeansWithAnnotation(Controller.class));
                builder.addMappings(context.getBeansWithAnnotation(RequestMapping.class));
            }
        };
    }

    /**
     * open api定制器
     * <p>
     * 添加插件元信息头
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SpringPluginProperties.PREFIX, name = "intercept.identity-mode", havingValue = "HEADER")
    public OpenApiCustomizer openApiCustomizer() {

        return openApi -> openApi.getPaths()
                .values()
                .stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> operation
                        .addParametersItem(new HeaderParameter()
                                .name(PluginConstant.META_HEADER)
                                .required(true)
                                .in(ParameterIn.HEADER.toString())
                                .description("Plugin Meta Info")
                        ));
    }
}
