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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springplugin.core.PluginFuture;
import org.springplugin.core.autoconfigure.ImportCandidates;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.util.ClassUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.jar.JarFile;

/**
 * spring插件类加载器
 *
 * @author afěi
 * @version 1.0.0
 * @see PluginClassLoaderFactory
 */
public class SpringPluginClassLoader extends PluginClassLoader implements SmartClassLoader {

    /**
     * 默认的class文件路径
     */
    public static final String LOAD_PATH = System.getProperty("user.dir") + File.separator + "plugin" + File.separator;
    /**
     * 默认的ClassLoader名称
     */
    public static final String DEFAULT_NAME = "#default";
    /**
     * 插件候选
     */
    final static String[] PLUGIN_CANDIDATES;
    /**
     * 插件资源地址
     */
    final static String PLUGIN_RESOURCE_LOCATION = "META-INF/spring.plugin";

    static {

        final Enumeration<URL> urls = ImportCandidates.findUrlsInClasspath(ClassUtils.currentClassLoader(), PLUGIN_RESOURCE_LOCATION);
        Set<String> importCandidates = new HashSet<>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            final List<String> candidates = ImportCandidates.readCandidateConfigurations(url);
            importCandidates.addAll(candidates);
        }
        PLUGIN_CANDIDATES = importCandidates.toArray(String[]::new);
    }

    /**
     * 私有构造
     * <p>
     * 只允许通过工厂获取{@link #getInstance()}
     *
     * @param name 插件名称
     * @param urls 插件class url
     * @author afěi
     */
    private SpringPluginClassLoader(String name, URL[] urls) {
        super(name, urls);

    }

    /**
     * 静态工厂方法获取对象
     *
     * @return {@link PluginClassLoader}
     * @author afěi
     */
    @NonNull
    public static PluginClassLoader getInstance() {

        return getInstance(DEFAULT_NAME);
    }

    /**
     * 静态工厂方法获取对象
     *
     * @return {@link PluginClassLoader}
     * @author afěi
     */
    @NonNull
    public static PluginClassLoader getInstance(String name) {
        final String fn = PluginFuture.get(name);
        if (PluginClassLoaderFactory.has(fn)) {
            return PluginClassLoaderFactory.get(fn);
        }
        URL[] urls = null;
        try {
            final URL parent = new URL("file:" + LOAD_PATH + fn + File.separator);
            final File[] files = findFiles(parent);
            if (files != null) {
                urls = new URL[1 + files.length];
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    if (f.isFile() && f.getName().endsWith(".jar")) {
                        urls[i] = new URL("jar:file:" + f.getPath() + "!/");
                    } else {
                        urls[i] = new URL("file:" + f.getPath() + "/");
                    }
                }
            }
            if (urls != null) {
                urls[urls.length - 1] = parent;
            } else {
                throw new SpringPluginException(String.format("Plugin not exist, %s", fn));
            }
        } catch (IOException e) {
            throw new SpringPluginException(String.format("Instantiation url failure, %s", fn), e);
        }

        return getInstance(fn, urls);

    }

    /**
     * 寻找class/jar文件列表
     *
     * @param parent 插件根url
     * @return class/jar文件列表
     * @author afěi
     */
    private static File[] findFiles(URL parent) {

        final File parentFile = new File(parent.getFile());
        final File[] files = parentFile.listFiles();
        final File bootInf = new File(parentFile, "BOOT-INF");
        if (Objects.nonNull(files) && bootInf.exists()) {

            final File classes = new File(bootInf, "classes");
            final File lib = new File(bootInf, "lib");
            return ArrayUtils.addAll(files, ArrayUtils.addAll(lib.listFiles(), classes, lib));
        }

        return files;
    }

    /**
     * 静态工厂方法获取对象
     *
     * @param name 类加载器名称
     * @param urls 类文件url
     * @return {@link PluginClassLoader}
     * @author afěi
     */
    @NonNull
    public static PluginClassLoader getInstance(String name, URL... urls) {

        return PluginClassLoaderFactory.get(SpringPluginClassLoader.class, name, urls);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        // 检查当前加载器是否已加载该类
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        for (String pluginCandidate : PLUGIN_CANDIDATES) {
            if (name.startsWith(pluginCandidate)) {
                // 尝试用子类加载器加载插件类
                Class<?> clazz = loadPluginClass(name);
                if (clazz != null) {
                    return clazz;
                }
            }
        }

        // 使用默认的双亲委派方式加载
        return super.loadClass(name);
    }

    /**
     * 加载插件类
     *
     * @param name 类名
     * @return 类对象
     * @author afěi
     */
    protected Class<?> loadPluginClass(String name) {
        // 从ucs列表加载类
        for (URLConnection uc : ucs) {
            try {
                if (uc instanceof JarURLConnection jar) {
                    JarFile jarFile = jar.getJarFile();
                    String entryName = jar.getEntryName();
                    if (entryName == null) {
                        // 如果entryName为空，则使用默认的类名作为entryName
                        entryName = name.replace('.', '/').concat(".class");
                    }
                    // 检查entryName是否为null
                    if (jarFile.getEntry(entryName) == null) {
                        continue; // 如果entryName对应的资源不存在，则尝试下一个URL
                    }
                    byte[] classBytes;
                    try (InputStream is = jarFile.getInputStream(jarFile.getEntry(entryName))) {
                        classBytes = is.readAllBytes();
                    }
                    return defineClass(name, classBytes, 0, classBytes.length);
                } else {
                    byte[] classBytes;
                    try (InputStream is = new FileInputStream(uc.getURL().getFile() + name.replace('.', '/').concat(".class"))) {
                        classBytes = is.readAllBytes();
                        IOUtils.close(is);
                    }
                    return defineClass(name, classBytes, 0, classBytes.length);
                }
            } catch (Throwable ignored) {
                // 忽略异常，尝试下一个URL
            }
        }
        // 如果在ucs列表中未找到类，则返回null，由父类加载器继续加载
        return null;
    }

    @Override
    public boolean isClassReloadable(@NonNull Class<?> clazz) {
        return true;
    }

    @NonNull
    @Override
    public Class<?> publicDefineClass(@NonNull String name, @NonNull byte[] b, @Nullable ProtectionDomain protectionDomain) {
        return defineClass(name, b, 0, b.length, protectionDomain);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }
}
