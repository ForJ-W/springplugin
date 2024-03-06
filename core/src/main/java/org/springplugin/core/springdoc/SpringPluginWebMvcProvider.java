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

package org.springplugin.core.springdoc;

import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.webmvc.core.providers.SpringWebMvcProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springplugin.core.factory.SpringPluginFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * spring插件 webmvc springdoc提供者
 * <p>
 * 重写{@link #getHandlerMethods()}保证加载到所有插件
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringPluginWebMvcProvider extends SpringWebMvcProvider {

    @Override
    public Map<?, ?> getHandlerMethods() {
        // 获取所有应用上下文中的处理器方法
        final SpringPluginFactory factory = applicationContext.getBean(SpringPluginFactory.class);
        final Set<String> contextNames = factory.getContextNames();
        final LinkedHashMap<RequestMappingInfo, HandlerMethod> map = new LinkedHashMap<>(getHandlerMethods(applicationContext));
        for (String contextName : contextNames) {

            map.putAll(getHandlerMethods(factory.getContext(contextName)));
        }

        return map;
    }

    /**
     * 获取指定上下文中的请求处理器方法
     *
     * @param context 应用上下文
     * @return 上下文中的请求处理器方法
     * @author afěi
     */
    public Map<RequestMappingInfo, HandlerMethod> getHandlerMethods(ApplicationContext context) {

        return context.getBeansOfType(RequestMappingHandlerMapping.class)
                .values().stream()
                .map(AbstractHandlerMethodMapping::getHandlerMethods)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a1, a2) -> a1, LinkedHashMap::new));
    }
}
