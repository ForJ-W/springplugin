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

package org.springplugin.core.app.context.initializer;

import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springplugin.core.app.context.AppContextCleaner;
import org.springplugin.core.app.context.SpringAppContextFactory;
import org.springplugin.core.classloader.AppClassLoader;
import org.springplugin.core.classloader.SpringAppClassLoader;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.AppInfo;
import org.springplugin.core.util.SpringAwareUtils;

/**
 * spring插件bean注册初始化器
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringAppBeanRegisterInitializer extends AbstractSpringAppContextInitializer implements ApplicationContextInitializer<AnnotationConfigApplicationContext>, Ordered {

    public static final int ORDER = SpringAppMetaReaderInitializer.ORDER + 1;

    public SpringAppBeanRegisterInitializer(SpringAppContextFactory factory) {
        super(factory);
    }


    @Override
    protected void initialize(AnnotationConfigApplicationContext context, AppInfo appInfo) {
        registerBean(context, appInfo);
        AppContextCleaner.register(context, SpringAwareUtils::removeChildAware);
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
    private void registerBean(GenericApplicationContext context, AppInfo info) {
        final String name = info.name();
        final Class<?> mainClass;
        try {
            mainClass = info.mainClass();
        } catch (ClassNotFoundException e) {
            throw new SpringPluginException(String.format("Can not find main class, %s", name), e);
        }
        final AppClassLoader classLoader = SpringAppClassLoader.getInstance(name);
        context.setClassLoader(classLoader);
        context.getBeanFactory().setBeanClassLoader(classLoader);
        context.registerBean(mainClass.getName(), mainClass);
        Assert.isInstanceOf(AnnotationConfigRegistry.class, context);
        final AnnotationConfigRegistry registry = (AnnotationConfigRegistry) context;
        registry.register(PropertyPlaceholderAutoConfiguration.class);
    }
}
