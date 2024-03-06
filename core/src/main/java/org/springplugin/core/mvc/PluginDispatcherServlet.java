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

package org.springplugin.core.mvc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springplugin.core.classloader.PluginClassLoaderFactory;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.env.properties.SpringPluginProperties;
import org.springplugin.core.factory.SpringPluginFactory;
import org.springplugin.core.context.PluginContextCleaner;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.util.ClassUtils;
import org.springplugin.core.util.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件分发Servlet
 * <p>
 * {@link DispatcherServlet}
 * <p>
 * 用于初始化和切换插件中的{@link HandlerMapping}, {@link HandlerAdapter}, {@link HandlerInterceptor}
 *
 * @author afěi
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class PluginDispatcherServlet extends DispatcherServlet {

    /**
     * Spring插件工厂
     */
    private final SpringPluginFactory contextFactory;

    /**
     * 插件属性配置类
     */
    private final SpringPluginProperties pluginProps;

    /**
     * 处理器方法缓存
     */
    private final Map<String, List<HandlerMapping>> handlerMappings = new ConcurrentHashMap<>();

    /**
     * 处理器适配器缓存
     */
    private final Map<String, List<HandlerAdapter>> handlerAdapters = new ConcurrentHashMap<>();

    @Override
    protected void initStrategies(@NonNull ApplicationContext context) {
        super.initStrategies(context);
    }

    @Override
    protected HandlerExecutionChain getHandler(@NonNull HttpServletRequest request) throws Exception {

        final SpringPluginProperties.Intercept intercept = pluginProps.getIntercept();
        final String identityKey = intercept.getIdentityKey();
        final String uri = request.getRequestURI();
        final String plugin = switch (intercept.getIdentityMode()) {
            case HEADER -> request.getHeader(identityKey);
            case PARAMETER -> request.getParameter(identityKey);
            default -> "/".equals(uri) ? null : uri.split("/")[1];
        };
        if (PluginClassLoaderFactory.has(plugin)) {
            Thread.currentThread().setContextClassLoader(SpringPluginClassLoader.getInstance(plugin));
            // 获取指定插件的应用上下文中的处理器方法
            if (!handlerMappings.containsKey(plugin)) {
                final GenericApplicationContext context = contextFactory.getContext(plugin);
                this.handlerMappings.put(plugin, initPluginHandlerMapping(context));
                PluginContextCleaner.register(context, this.handlerMappings::remove);
            }

            return getHandler(handlerMappings.get(plugin), request);
        }
        return super.getHandler(request);
    }

    @NonNull
    @Override
    protected HandlerAdapter getHandlerAdapter(@NonNull Object handler) throws ServletException {
        final String plugin = ClassUtils.currentClassLoader().getName();
        if (PluginClassLoaderFactory.has(plugin)) {
            // 获取指定插件的应用上下文中的处理器方法
            if (!handlerAdapters.containsKey(plugin)) {
                final GenericApplicationContext context = contextFactory.getContext(plugin);
                this.handlerAdapters.put(plugin, new ArrayList<>(context.getBeanFactory().getBeansOfType(HandlerAdapter.class).values()));
                PluginContextCleaner.register(context, this.handlerAdapters::remove);
            }

            final HandlerAdapter handlerAdapter = getHandlerAdapter(handlerAdapters.get(plugin), handler);
            if (Objects.nonNull(handlerAdapter)) {
                return handlerAdapter;
            }
        }
        return super.getHandlerAdapter(handler);
    }

    /**
     * 初始化插件请求处理器
     * <p>
     * 插件自己的拦截器
     *
     * @param context 应用上下文
     * @return 处理器方法列表
     * @author afěi
     */
    @SuppressWarnings("unchecked")
    protected List<HandlerMapping> initPluginHandlerMapping(GenericApplicationContext context) {
        // 获取所有的请求处理器
        final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        final List<HandlerMapping> handlerMappings = new ArrayList<>(beanFactory.getBeansOfType(HandlerMapping.class).values());
        for (HandlerMapping handlerMapping : handlerMappings) {
            if (handlerMapping instanceof AbstractHandlerMapping abstractHandlerMapping) {
                for (WebMvcConfigurer webMvcConfigurer : beanFactory.getBeansOfType(WebMvcConfigurer.class).values()) {
                    // 获取并添加所有插件自己的拦截器
                    final InterceptorRegistry registry = new InterceptorRegistry();
                    webMvcConfigurer.addInterceptors(registry);
                    Optional.ofNullable(ReflectUtils.findMethod(InterceptorRegistry.class, "getInterceptors"))
                            .ifPresent(gm -> {
                                try {
                                    gm.setAccessible(true);
                                    final List<Object> interceptors = (List<Object>) gm.invoke(registry);
                                    abstractHandlerMapping.setInterceptors(interceptors.toArray());
                                    final Field adaptedInterceptors = ReflectUtils.findField(AbstractHandlerMapping.class, "adaptedInterceptors", List.class);
                                    Objects.requireNonNull(adaptedInterceptors).setAccessible(true);
                                    // 重新初始化插件请求处理器的拦截器
                                    ((List<HandlerInterceptor>) adaptedInterceptors.get(abstractHandlerMapping)).clear();
                                    Optional.ofNullable(ReflectUtils.findMethod(AbstractHandlerMapping.class, "initInterceptors"))
                                            .ifPresent(im -> {
                                                try {
                                                    im.setAccessible(true);
                                                    im.invoke(abstractHandlerMapping);
                                                } catch (IllegalAccessException | InvocationTargetException e) {
                                                    throw new SpringPluginException("Invoke AbstractHandlerMapping#initInterceptors fail", e);
                                                }
                                            });
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new SpringPluginException("Invoke InterceptorRegistry#getInterceptors fail", e);
                                }
                            });
                }
            }
        }
        return handlerMappings;
    }

    /**
     * 获取处理器方法
     * <p>
     * 用于使用插件自己的处理器方法
     * <p>
     * {@link #getHandlerAdapter(Object)}
     *
     * @param handlerMappings 处理器方法列表
     * @param request         http请求
     * @return 处理器方法
     * @throws Exception 处理器方法获取处理器异常
     * @author afěi
     */
    @Nullable
    protected HandlerExecutionChain getHandler(List<HandlerMapping> handlerMappings, HttpServletRequest request) throws Exception {
        if (handlerMappings != null) {
            for (HandlerMapping mapping : handlerMappings) {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    /**
     * 获取处理器适配器
     * <p>
     * 用于使用插件自己的处理器适配器
     * <p>
     * {@link #getHandlerAdapter(Object)}
     *
     * @param handlerAdapters 处理器适配器列表
     * @param handler         处理器
     * @return 处理器适配器
     * @author afěi
     */
    @Nullable
    protected HandlerAdapter getHandlerAdapter(List<HandlerAdapter> handlerAdapters, Object handler) {
        if (handlerAdapters != null) {
            for (HandlerAdapter adapter : handlerAdapters) {
                if (adapter.supports(handler)) {
                    return adapter;
                }
            }
        }
        return null;
    }
}
