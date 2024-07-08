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

package org.springplugin.core.context.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springplugin.core.classloader.PluginClassLoader;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.context.PluginContextCleaner;
import org.springplugin.core.context.SpringPluginFactory;
import org.springplugin.core.context.SpringPluginFactoryCommonSpec;
import org.springplugin.core.context.SpringPluginFactorySpec;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.PluginInfo;
import org.springplugin.core.util.SpringAwareUtils;

import java.util.Collections;

/**
 * spring插件bean注册初始化器
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringPluginBeanRegisterInitializer extends AbstractSpringPluginContextInitializer implements ApplicationContextInitializer<AnnotationConfigApplicationContext>, Ordered {

    public static final int ORDER = SpringPluginPropertySourceInitializer.ORDER + 1;


    public SpringPluginBeanRegisterInitializer(SpringPluginFactory contextFactory, SpringPluginFactoryCommonSpec commonSpec) {
        super(contextFactory, commonSpec);
    }

    @Override
    protected void initialize(AnnotationConfigApplicationContext context, PluginInfo pluginInfo) {
        contextFactory.setConfigurations(Collections.singletonList(new SpringPluginFactorySpec(pluginInfo.name(), commonSpec.getConfiguration())));
        registerBean(context, pluginInfo);
        PluginContextCleaner.register(context, SpringAwareUtils::removeChildAware);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * 注册插件子上下文所需要的bean
     *
     * @param context 通用的应用上下文
     * @param info    插件信息
     * @author afěi
     */
    private void registerBean(GenericApplicationContext context, PluginInfo info) {
        final String name = info.name();
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
        contextFactory.registerBeans(name, context);
    }
}
