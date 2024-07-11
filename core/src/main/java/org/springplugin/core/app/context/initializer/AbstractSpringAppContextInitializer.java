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

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.NonNull;
import org.springplugin.core.app.context.SpringAppContextFactory;
import org.springplugin.core.info.AppInfo;

/**
 * 抽象的spring插件应用上下文初始化器
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class AbstractSpringAppContextInitializer implements ApplicationContextInitializer<AnnotationConfigApplicationContext> {

    /**
     * Spring插件应用工厂
     */
    protected final SpringAppContextFactory factory;


    protected AbstractSpringAppContextInitializer(SpringAppContextFactory factory) {
        this.factory = factory;

    }

    @Override
    public void initialize(@NonNull AnnotationConfigApplicationContext context) {
        initialize(context, factory.getAppInfo(context));
    }

    /**
     * 初始化给定的上下文
     *
     * @param context    应用上下文
     * @param appInfo 插件信息
     * @author afěi
     */
    protected abstract void initialize(AnnotationConfigApplicationContext context, AppInfo appInfo);
}
