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

package org.springplugin.core.classloader;

import org.springframework.lang.NonNull;

/**
 * 类加载器工厂
 *
 * @author afěi
 * @version 1.0.0
 */
public interface ClassLoaderFactory<T extends ClassLoader> {

    /**
     * 获取类加载器名称id
     *
     * @param ld 类加载器
     * @return 类加载器名称id
     * @author afěi
     */
    static String nameAndId(ClassLoader ld) {

        String nid = ld.getName() != null ? "'" + ld.getName() + "'"
                : ld.getClass().getName();
        String id = Integer.toHexString(System.identityHashCode(ld));
        nid = nid + " @" + id;
        return nid;
    }

    /**
     * 获取容器中的ClassLoader
     *
     * @param classLoaderClass 插件类加载器class对象
     * @param name             类加载器名称
     * @param args             类加载器构建参数
     * @return 类加载器
     * @author afěi
     */
    @NonNull
    T getClassLoader(Class<? extends T> classLoaderClass, String name, Object... args);

    /**
     * 获取容器中的ClassLoader
     *
     * @param name 类加载器名称
     * @return 类加载器
     * @author afěi
     */
    @NonNull
    T getClassLoader(String name);

    /**
     * 移除容器中的ClassLoader
     *
     * @param name 类加载器名称
     * @return 类加载器
     * @author afěi
     */
    T removeClassLoader(String name);

    /**
     * 判定是否存在指定名称的ClassLoader
     *
     * @param name 插件名
     * @return 是否存在指定名称的ClassLoader
     * @author afěi
     */
    boolean hasClassLoader(String name);
}
