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

package org.springplugin.core.server.context;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springplugin.core.app.context.SpringAppContextFactory;
import org.springplugin.core.bytecode.ByteCode;
import org.springplugin.core.classloader.AppClassLoader;
import org.springplugin.core.classloader.SpringAppClassLoader;
import org.springplugin.core.info.AppInfo;
import org.springplugin.core.info.AppInfoFactory;
import org.springplugin.core.scan.AppAutoConfiguration;
import org.springplugin.core.server.SpringPluginProperties;
import org.springplugin.core.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring 插件应用上下文
 * <p>
 * {@link SpringAppContextFactory}
 * <p>
 * {@link SpringPluginProperties}
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
public class SpringAppServerContext extends AbstractAppServerContext implements AppServerContext {

    /**
     * 被过滤的注解
     */
    private final Set<Class<? extends Annotation>> filterAnnotation = ConcurrentHashMap.newKeySet();
    /**
     * spring插件服务属性配置类
     */
    private final SpringPluginProperties properties;

    {
        filterAnnotation.add(SpringBootApplication.class);
        filterAnnotation.add(EnableAutoConfiguration.class);
    }

    /**
     * 构造方法
     *
     * @param factory Spring插件应用工厂
     * @param properties spring插件服务属性配置类
     * @author afěi
     */
    public SpringAppServerContext(SpringAppContextFactory factory, SpringPluginProperties properties) {
        super(factory);
        this.properties = properties;
    }

    /**
     * 添加需要过滤的注解
     *
     * @param annotationClass 注解类对象
     * @return 抽象的插件应用服务上下文
     * @author afěi
     */
    public AbstractAppServerContext addFilterAnnotation(Class<? extends Annotation> annotationClass) {
        filterAnnotation.add(annotationClass);
        return this;
    }

    /**
     * 处理主类上的注解
     *
     * @param mainClass   主类对象
     * @param classLoader 类加载器
     * @author afěi
     */
    protected void processAnnotationOnClass(Class<?> mainClass, ClassLoader classLoader) {
        if (properties.getDebug().isResourceCache()) {
            return;
        }
        final ByteCode bytecode = bytecode(classLoader);
        synchronized (this.filterAnnotation) {
            filterAnnotation.forEach(ann -> bytecode.removeAnnotationToClass(mainClass, ann));
            bytecode.addAnnotationToClass(mainClass, AppAutoConfiguration.class)
                    .transformClass(mainClass);
        }
    }

    @Override
    public boolean load(AppInfo info) {
        final String name = info.name();
        try {
            AppInfoFactory.set(name, info);
            processAnnotationOnClass(info.mainClass(), initContextClassLoader(name));
            factory.initContext(name);
            log.info("load spring plugin success, {}", name);
        } catch (Throwable e) {
            log.error(String.format("load spring plugin fail, %s", name), e);
            unload(info);
            return false;
        }
        return true;
    }

    @Override
    public void unload(AppInfo info) {
        final String name = info.name();
        checkContextClassLoader(name);
        final ClassLoader classLoader = ClassUtils.currentClassLoader();
        try {
            factory.destroyContext(name);
        } catch (Throwable e) {
            log.error(String.format("unload spring plugin fail, %s", name), e);
        } finally {
            if (classLoader instanceof AppClassLoader appClassLoader) {
                try {
                    IOUtils.close(appClassLoader);
                } catch (IOException e) {
                    log.error(String.format("plugin classloader close fail, %s", name), e);
                }
            }
            final SpringPluginProperties.Debug debug = properties.getDebug();
            if (!debug.isResourceCache()) {
                if (!FileUtils.deleteQuietly(new File(SpringAppClassLoader.LOAD_PATH + name))) {
                    log.error("unload plugin fail, can't delete plugin file: {}", name);
                }
            }
        }
    }
}
