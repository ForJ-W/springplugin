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

import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.util.AssertUtils;
import org.springplugin.core.util.StringUtils;

import java.util.Objects;

/**
 * 默认插件信息
 *
 * @author afěi
 * @version 1.0.0
 */
public class DefaultPluginInfo implements PluginInfo {

    /**
     * 插件名称
     */
    private final String name;

    /**
     * 主类名称
     */
    private final String mainClassName;

    /**
     * 构造方法
     *
     * @param name          插件名称
     * @param mainClassName 主类名称
     * @author afěi
     */
    public DefaultPluginInfo(String name, String mainClassName) {
        AssertUtils.isTrue(StringUtils.isNotBlank(name), "The plugin name cannot be empty");
        this.name = name;
        this.mainClassName = mainClassName;
    }

    /**
     * 静态工厂构建对象
     *
     * @param name      插件名称
     * @param mainClass 主类名称
     * @return 插件信息
     * @author afěi
     */
    public static DefaultPluginInfo of(String name, String mainClass) {

        return new DefaultPluginInfo(name, mainClass);
    }

    /**
     * 静态工厂构建对象
     *
     * @param name 插件名称
     * @return 插件信息
     * @author afěi
     */
    public static DefaultPluginInfo of(String name) {

        return new DefaultPluginInfo(name, null);
    }

    @Override
    public String name() {

        return this.name;
    }

    @Override
    public Class<?> mainClass() throws ClassNotFoundException {

        return StringUtils.isNotBlank(this.mainClassName)
                ? SpringPluginClassLoader.getInstance(name()).forName(this.mainClassName)
                : PluginInfo.super.mainClass();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        DefaultPluginInfo that = (DefaultPluginInfo) object;
        return Objects.equals(name, that.name) && Objects.equals(mainClassName, that.mainClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mainClassName);
    }
}
