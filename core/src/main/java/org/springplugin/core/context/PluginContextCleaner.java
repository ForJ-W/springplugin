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

package org.springplugin.core.context;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.support.GenericApplicationContext;
import org.springplugin.core.util.ClassUtils;

/**
 * 插件上下文清理器
 *
 * @author afěi
 * @version 1.0.0
 */
public interface PluginContextCleaner extends DisposableBean {

    /**
     * 注册插件上下文清理器
     *
     * @param context 通用应用上下文
     * @param cleaner 插件上下文清理器
     * @author afěi
     */
    static void register(GenericApplicationContext context, PluginContextCleaner cleaner) {

        context.registerBean(cleaner.getClass().getName(), PluginContextCleaner.class, () -> new Adapter(cleaner));
        context.getBeanFactory().getBeansOfType(PluginContextCleaner.class);
    }

    /**
     * 利用{@link #destroy()} 清理相关资源
     *
     * @param name 插件名称
     * @author afěi
     */
    void clean(String name);

    /**
     * bean销毁回调
     *
     * @author afěi
     */
    @Override
    default void destroy() {

        clean(ClassUtils.currentClassLoader().getName());
    }

    /**
     * 适配器
     * <p>
     * 用于手动/动态注册内部类形式的清理器bean
     * <p>
     * {@link #register(GenericApplicationContext, PluginContextCleaner)}
     *
     * @author afěi
     * @version 1.0.0
     */
    @RequiredArgsConstructor
    class Adapter implements PluginContextCleaner {

        private final PluginContextCleaner pcc;

        @Override
        public void clean(String name) {
            pcc.clean(name);
        }
    }
}
