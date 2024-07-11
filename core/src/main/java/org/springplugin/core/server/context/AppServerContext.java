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

package org.springplugin.core.server.context;

import org.springplugin.core.app.context.AppContextFactory;
import org.springplugin.core.bytecode.ByteCode;
import org.springplugin.core.info.AppInfo;

/**
 * 插件应用服务上下文
 *
 * @author afěi
 * @version 1.0.0
 */
public interface AppServerContext {

    /**
     * 加载插件
     *
     * @param info 插件信息
     * @return 是否加载成功
     * @author afěi
     */
    boolean load(AppInfo info);

    /**
     * 卸载插件
     *
     * @param info 插件信息
     * @author afěi
     */
    void unload(AppInfo info);

    /**
     * 获取字节码
     *
     * @param classLoader 类加载器
     * @return 字节码
     * @author afěi
     */
    ByteCode bytecode(ClassLoader classLoader);

    /**
     * 获取插件应用上下文工厂
     *
     * @return 插件应用上下文工厂
     */
    AppContextFactory<?> appContextFactory();
}
