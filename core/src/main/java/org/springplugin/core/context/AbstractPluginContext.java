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


import javassist.ClassPool;
import javassist.LoaderClassPath;
import lombok.extern.slf4j.Slf4j;
import org.springplugin.core.bytecode.ByteCode;
import org.springplugin.core.bytecode.JavassistBytecode;
import org.springplugin.core.classloader.PluginClassLoader;
import org.springplugin.core.classloader.PluginClassLoaderFactory;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.scan.PluginScan;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象的插件上下文
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
public abstract class AbstractPluginContext implements PluginContext {

    /**
     * 被过滤的注解
     */
    protected final Set<Class<? extends Annotation>> filterAnnotation = ConcurrentHashMap.newKeySet();


    /**
     * 添加需要过滤的注解
     *
     * @param annotationClass 注解类对象
     * @return 抽象的插件上下文
     * @author afěi
     */
    public AbstractPluginContext addFilterAnnotation(Class<? extends Annotation> annotationClass) {

        filterAnnotation.add(annotationClass);
        return this;
    }

    @Override
    public ByteCode bytecode(ClassLoader classLoader) {
        final ClassPool pool = new ClassPool();
        pool.appendClassPath(new LoaderClassPath(classLoader));
        return new JavassistBytecode(pool);
    }

    @Override
    public DataManager dataManager(String name) {
        log.warn("use empty DataManager");
        return DataManager.EMPTY;
    }

    @Override
    public DependencyControl versionControl(String name) {
        log.warn("use empty VersionControl");
        return DependencyControl.EMPTY;
    }

    /**
     * 初始化上下文相关的类加载器
     *
     * @param name 插件名称
     * @author afěi
     */
    protected PluginClassLoader initContextClassLoader(String name) {

        final PluginClassLoader classLoader = SpringPluginClassLoader.getInstance(name);
        Thread.currentThread().setContextClassLoader(classLoader);
        return classLoader;
    }

    /**
     * 初始化上下文相关的类加载器
     *
     * @param name 插件名称
     * @author afěi
     */
    protected void checkContextClassLoader(String name) {

        if (PluginClassLoaderFactory.has(name)) {
            Thread.currentThread().setContextClassLoader(SpringPluginClassLoader.getInstance(name));
        }
    }

    /**
     * 处理主类上的注解
     *
     * @param mainClass   主类对象
     * @param classLoader 类加载器
     * @author afěi
     */
    protected void processAnnotationOnClass(Class<?> mainClass, ClassLoader classLoader) {
        final ByteCode bytecode = bytecode(classLoader);
        synchronized (this.filterAnnotation) {
            filterAnnotation.forEach(ann -> bytecode.removeAnnotationToClass(mainClass, ann));
            bytecode.addAnnotationToClass(mainClass, PluginScan.class)
                    .transformClass(mainClass);
        }
    }
}
