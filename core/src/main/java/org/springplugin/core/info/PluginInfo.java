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

package org.springplugin.core.info;


import org.springplugin.core.PluginFuture;
import org.springplugin.core.classloader.SpringPluginClassLoader;

/**
 * 插件信息
 *
 * @author afěi
 * @version 1.0.0
 */
public interface PluginInfo {

    /**
     * 默认主类名
     */
    String DEFAULT_MAIN_CLASS_NAME = "Main";

    /**
     * 获取 插件名称
     *
     * @return 插件名称
     * @author afěi
     */
    String name();

    /**
     * 获取主类
     *
     * @return 主类
     * @throws ClassNotFoundException 类未找到异常
     * @author afěi
     */
    default Class<?> mainClass() throws ClassNotFoundException {

        return SpringPluginClassLoader.getInstance(name()).forName(PluginFuture.getRootName(name()) + "." + DEFAULT_MAIN_CLASS_NAME);
    }
}
