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

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springplugin.core.PluginFuture;
import org.springplugin.core.classloader.PluginClassLoader;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.context.PluginContextCleaner;
import org.springplugin.core.env.PluginPropertySourceLocator;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.DefaultPluginInfo;
import org.springplugin.core.util.SpringAwareUtils;

import java.util.Collections;

/**
 * spring插件子上下文初始化器
 *
 * @author afěi
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class SpringPluginChildContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    /**
     * Spring插件工厂
     */
    private final SpringPluginFactory contextFactory;

    /**
     * Spring插件工厂公共规范
     */
    private final SpringPluginFactoryCommonSpec commonSpec;

    @Override
    public void initialize(@NonNull GenericApplicationContext context) {

        final String name = SpringPluginFactory.getPluginName(context);
        contextFactory.setConfigurations(Collections.singletonList(new SpringPluginFactorySpec(name, commonSpec.getConfiguration())));
        PluginPropertySourceLocator.locateConfigPropertySource(context, PluginFuture.getRootName(name), SpringPluginClassLoader.getInstance(name));
        registerBean(context, name);
        PluginContextCleaner.register(context, SpringAwareUtils::removeChildAware);
    }

    /**
     * 注册插件子上下文所需要的bean
     *
     * @param context 通用的应用上下文
     * @param name    插件名称
     * @author afěi
     */
    private void registerBean(GenericApplicationContext context, String name) {
        final DefaultPluginInfo info = DefaultPluginInfo.of(name);
        final Class<?> mainClass;
        try {
            mainClass = info.mainClass();
        } catch (ClassNotFoundException e) {
            throw new SpringPluginException(String.format("Can not find main class, %s", name), e);
        }
        final PluginClassLoader classLoader = SpringPluginClassLoader.getInstance(name);
        context.setClassLoader(classLoader);
        context.getBeanFactory().setBeanClassLoader(classLoader);
        context.registerBean(mainClass.getName(), mainClass);
        this.contextFactory.registerBeans(name, context);
    }
}
