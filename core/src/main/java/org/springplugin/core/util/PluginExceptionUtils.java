/*
 * Copyright 2024 ForJ-W
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springplugin.core.util;

import org.springplugin.core.exception.PluginException;

/**
 * 插件异常工具
 *
 * @author wujing
 * @version 1.0.0
 */
public abstract class PluginExceptionUtils {

    /**
     * 没找到插件
     *
     * @param name 插件名
     * @return 插件异常
     */
    public static PluginException canNotFindPlugin(String name) {
        return new PluginException(String.format("Can not find plugin '%s'", name));
    }

    /**
     * 插件名不能空
     *
     * @return 插件异常
     */
    public static PluginException pluginNameNotBlank() {
        return new PluginException("The plugin name can't be blank");
    }

    /**
     * 没找到插件类
     *
     * @return 插件异常
     */
    public static PluginException canNotFindPluginClass(String name) {
        return new PluginException("Can not find plugin class '%s'", name);
    }
}
