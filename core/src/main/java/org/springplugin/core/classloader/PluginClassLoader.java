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


import org.springframework.lang.Nullable;
import org.springplugin.core.util.ClassUtils;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 插件类加载器
 *
 * @author afěi
 * @version 1.0.0
 */
public class PluginClassLoader extends URLClassLoader {

    /**
     * url连接列表
     */
    protected final List<URLConnection> ucs = new ArrayList<>();

    /**
     * 构造方法
     *
     * @param name ClassLoader名称
     * @param urls url数组
     * @author afěi
     */
    public PluginClassLoader(String name, URL[] urls) {

        super(name, new URL[0], ClassUtils.currentClassLoader() instanceof PluginClassLoader ? ClassUtils.currentClassLoader().getParent() : ClassUtils.currentClassLoader());
        for (URL url : urls) {
            try {
                addUrl(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 判定是否为插件类加载器
     *
     * @param classLoader 类加载器
     * @return 是否为插件类加载器
     * @author afěi
     */
    public static boolean isPluginClassLoader(@Nullable ClassLoader classLoader) {
        return classLoader instanceof SpringPluginClassLoader;
    }

    /**
     * 判定是否为插件类
     *
     * @param cls 类对象
     * @return 是否为插件类
     * @author afěi
     */
    public static boolean isPluginClass(@Nullable Class<?> cls) {
        return null != cls && isPluginClassLoader(cls.getClassLoader());
    }

    /**
     * 判定是否为当前插件类
     *
     * @param cls  类对象
     * @param name 插件名称
     * @return 是否为插件类
     * @author afěi
     */
    public static boolean isCurrentPluginClass(@Nullable Class<?> cls, String name) {
        return null != cls && isPluginClassLoader(cls.getClassLoader()) && cls.getClassLoader().getName().equals(name);
    }

    /**
     * 判定是否为插件类但又非当前插件类
     *
     * @param cls  类对象
     * @param name 插件名称
     * @return 是否为插件类但又非当前插件类
     * @author afěi
     */
    public static boolean isPluginAndNotCurrentPluginClass(@Nullable Class<?> cls, String name) {
        return isPluginClass(cls) && !isCurrentPluginClass(cls, name);
    }

    /**
     * 添加url
     * <p>
     * 扩大 {@link #addURL(URL)}权限
     *
     * @param url url
     * @author afěi
     */
    public void addUrl(URL url) throws IOException {
        final URLConnection uc = url.openConnection();
        uc.setUseCaches(true);
        ucs.add(uc);
        super.addURL(url);
    }

    /**
     * 使用当前类加载器获取类对象
     * <p>
     * {@link Class#forName(String, boolean, ClassLoader)}
     *
     * @param name 类名
     * @return class对象
     * @throws ClassNotFoundException 类未找到
     * @author afěi
     */
    public Class<?> forName(String name) throws ClassNotFoundException {

        return Class.forName(name, true, this);
    }

    @Override
    public void close() throws IOException {
        final PluginClassLoader remove = PluginClassLoaderFactory.remove(getName());
        if (Objects.nonNull(remove)) {
            remove.close();
        }
        for (URLConnection uc : this.ucs) {
            if (uc instanceof JarURLConnection jar) {
                jar.getJarFile().close();

            } else {
                uc.getInputStream().close();
            }
        }
        super.close();
    }
}
