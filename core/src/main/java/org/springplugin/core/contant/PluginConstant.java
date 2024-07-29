/*
 * Copyright 2023-2025 the original author or authors.
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

package org.springplugin.core.contant;

/**
 * 插件常量
 *
 * @author afěi
 * @version 1.0.0
 */
public interface PluginConstant {

    /**
     * 插件元信息头
     */
    String META_HEADER = "app-meta";

    /**
     * 插件信息描述
     */
    String INFO = ".info";

    /**
     * 插件管理者名称
     */
    String MANAGER_NAME = "manager";

    /**
     * 插件管理者路径
     */
    String MANAGER_PATH = "/pm";

    /**
     * 插件管理者标签
     */
    String MANAGER_TAG = "plugin-manager";

    /**
     * 插件管理者主类
     */
    String MANAGER_MAIN_CLASS = "org.springplugin.manager.ManagerApplication";

    /**
     * classes目录名
     */
    String CLASSES = "classes";

    /**
     * classes目录路径
     */
    String CLASSES_PATH = "/classes/";

    /**
     * 主类文件名
     */
    String MAIN_CLASS_NAME = "Main.class";
}
