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

package org.springplugin.core.factory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.Validator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * Spring web插件配置
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringWebPluginConfiguration extends SpringPluginConfiguration {


    /**
     * 请求映射处理器
     * <p>
     * 用来初始化插件上下文中的处理器方法
     * <p>
     * {@link WebMvcAutoConfiguration.EnableWebMvcConfiguration#requestMappingHandlerMapping(ContentNegotiationManager, FormattingConversionService, ResourceUrlProvider)}
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public RequestMappingHandlerMapping requestMappingHandlerMapping(WebMvcAutoConfiguration.EnableWebMvcConfiguration enableWebMvcConfiguration,
                                                                     @Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
                                                                     @Qualifier("mvcConversionService") FormattingConversionService conversionService,
                                                                     @Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {

        return enableWebMvcConfiguration.requestMappingHandlerMapping(contentNegotiationManager, conversionService, resourceUrlProvider);
    }

    /**
     * 请求处理器适配器
     * <p>
     * 用来初始化插件上下文中的处理器适配器
     * <p>
     * {@link WebMvcAutoConfiguration.EnableWebMvcConfiguration#requestMappingHandlerAdapter(ContentNegotiationManager, FormattingConversionService, Validator)}
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter(WebMvcAutoConfiguration.EnableWebMvcConfiguration enableWebMvcConfiguration,
                                                                     @Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
                                                                     @Qualifier("mvcConversionService") FormattingConversionService conversionService,
                                                                     @Qualifier("mvcValidator") Validator validator) {

        return enableWebMvcConfiguration.requestMappingHandlerAdapter(contentNegotiationManager, conversionService, validator);
    }
}
