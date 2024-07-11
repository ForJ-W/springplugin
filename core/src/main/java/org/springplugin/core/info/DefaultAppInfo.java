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

import org.springplugin.core.classloader.SpringAppClassLoader;
import org.springplugin.core.util.AssertUtils;
import org.springplugin.core.util.StringUtils;

/**
 * 默认插件应用信息
 *
 * @author afěi
 * @version 1.0.0
 */
public class DefaultAppInfo implements AppInfo {
    private final String name;
    private final String mainClassName;


    /**
     * 构造方法
     *
     * @param name          插件名称
     * @param mainClassName 主类名称
     * @author afěi
     */
    public DefaultAppInfo(String name, String mainClassName) {
        AssertUtils.isTrue(StringUtils.isNotBlank(name), "The plugin name cannot be empty");
        this.name = name;
        this.mainClassName = mainClassName;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String mainClassName() {
        return this.mainClassName;
    }

    /**
     * 静态工厂构建对象
     *
     * @param name      插件名称
     * @param mainClass 主类名称
     * @return 插件应用信息
     * @author afěi
     */
    public static DefaultAppInfo of(String name, String mainClass) {
        return new DefaultAppInfo(name, mainClass);
    }

    /**
     * 静态工厂构建对象
     *
     * @param name 插件名称
     * @return 插件应用信息
     * @author afěi
     */
    public static DefaultAppInfo of(String name) {
        return new DefaultAppInfo(name, null);
    }

    @Override
    public Class<?> mainClass() throws ClassNotFoundException {
        return StringUtils.isNotBlank(this.mainClassName)
                ? SpringAppClassLoader.getInstance(name()).forName(this.mainClassName)
                : AppInfo.super.mainClass();
    }
}
