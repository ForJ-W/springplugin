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

package org.springplugin.core.classloader;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springplugin.core.exception.PluginException;
import org.springplugin.core.util.StringUtils;

import java.lang.reflect.Constructor;
import java.net.URL;

/**
 * 插件应用类加载器工厂
 *
 * @author afěi
 * @version 1.0.0
 * @see AppClassLoader
 */
public class AppClassLoaderFactory extends AbstractClassLoaderFactory<AppClassLoader> implements ClassLoaderFactory<AppClassLoader> {

    /**
     * 单例
     */
    private static final ClassLoaderFactory<AppClassLoader> INSTANCE = new AppClassLoaderFactory();

    /**
     * 静态工厂获取容器中的AppClassLoader
     *
     * @param classLoaderClass 插件类加载器class对象
     * @param name             类加载器名称
     * @param urls             类加载器url
     * @return 插件类加载器
     * @author afěi
     */
    @NonNull
    public static AppClassLoader get(Class<? extends AppClassLoader> classLoaderClass, String name, URL[] urls) {

        return INSTANCE.getClassLoader(classLoaderClass, name, (Object[]) urls);
    }

    /**
     * 静态工厂获取容器中的AppClassLoader
     *
     * @param name 类加载器名称
     * @return 插件类加载器
     * @author afěi
     */
    @Nullable
    public static AppClassLoader get(String name) {

        return INSTANCE.getClassLoader(name);
    }

    /**
     * 静态工厂移除容器中的AppClassLoader
     *
     * @param name 类加载器名称
     * @return 类加载器
     * @author afěi
     */
    public static AppClassLoader remove(String name) {

        return INSTANCE.removeClassLoader(name);
    }

    /**
     * 判定是否存在指定名称的ClassLoader
     *
     * @param name 插件名
     * @return 是否存在指定名称的ClassLoader
     * @author afěi
     */
    public static boolean has(String name) {

        return INSTANCE.hasClassLoader(name);
    }

    /**
     * 检查线程上下文的类加载器
     *
     * @param name 插件名称
     * @author afěi
     */
    public static void checkContextClassLoader(String name) {
        if (AppClassLoaderFactory.has(name)) {
            Thread.currentThread().setContextClassLoader(SpringAppClassLoader.getInstance(name));
        }
    }

    @NonNull
    @Override
    public AppClassLoader getClassLoader(Class<? extends AppClassLoader> classLoaderClass, String name, Object... args) {
        if (!hasClassLoader(name)) {
            synchronized (this.classLoaders) {
                if (!hasClassLoader(name)) {
                    try {
                        final Constructor<? extends AppClassLoader> constructor = classLoaderClass.getDeclaredConstructor(String.class, URL[].class);
                        constructor.setAccessible(true);
                        final URL[] urls = (URL[]) args;
                        final AppClassLoader newInstance = constructor.newInstance(name, urls);
                        classLoaders.put(name, newInstance);
                        return newInstance;
                    } catch (Exception e) {
                        throw new PluginException(String.format("AppClassLoader instantiate fail, %s", ArrayUtils.toString(args)), e);
                    }
                }
            }
        }
        return this.classLoaders.get(name);
    }

    @Nullable
    @Override
    public AppClassLoader getClassLoader(String name) {
        return this.classLoaders.get(name);
    }

    @Override
    public boolean hasClassLoader(String name) {
        return StringUtils.isNotBlank(name) && classLoaders.containsKey(name);
    }

    @Override
    public AppClassLoader removeClassLoader(String name) {
        return classLoaders.remove(name);
    }
}
