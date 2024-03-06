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

import org.springplugin.core.bytecode.ByteCode;
import org.springplugin.core.info.PluginInfo;

/**
 * 插件上下文
 *
 * @author afěi
 * @version 1.0.0
 */
public interface PluginContext {


    /**
     * 加载插件
     *
     * @param info 插件信息
     * @return 是否加载成功
     * @author afěi
     */
    boolean load(PluginInfo info);

    /**
     * 卸载插件
     *
     * @param info 插件信息
     * @return 是否卸载成功
     * @author afěi
     */
    boolean unload(PluginInfo info);

    /**
     * 获取字节码
     *
     * @param classLoader 类加载器
     * @return 字节码
     * @author afěi
     */
    ByteCode bytecode(ClassLoader classLoader);

    /**
     * 获取数据管理扩展接口
     *
     * @param name 插件名称
     * @return 数据管理扩展接口
     * @author afěi
     */
    DataManager dataManager(String name);

    /**
     * 获取依赖控制扩展接口
     *
     * @param name 插件名称
     * @return 依赖控制扩展接口
     * @author afěi
     */
    DependencyControl versionControl(String name);
}
