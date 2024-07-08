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

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.NonNull;
import org.springplugin.core.context.SpringPluginFactory;
import org.springplugin.core.context.SpringPluginFactoryCommonSpec;
import org.springplugin.core.info.PluginInfo;

/**
 * 抽象的spring插件上下文初始化器
 *
 * @author afěi
 * @version 1.0.0
 */
@RequiredArgsConstructor
public abstract class AbstractSpringPluginContextInitializer implements ApplicationContextInitializer<AnnotationConfigApplicationContext> {

    /**
     * Spring插件工厂
     */
    protected final SpringPluginFactory contextFactory;

    /**
     * Spring插件工厂公共规范
     */
    protected final SpringPluginFactoryCommonSpec commonSpec;

    @Override
    public void initialize(@NonNull AnnotationConfigApplicationContext context) {
        final PluginInfo pi = SpringPluginFactory.getPluginInfo(context);
        initialize(context, pi);
    }

    /**
     * 初始化给定的上下文
     *
     * @param context    应用上下文
     * @param pluginInfo 插件信息
     */
    protected abstract void initialize(AnnotationConfigApplicationContext context, PluginInfo pluginInfo);
}
