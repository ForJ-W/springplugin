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


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springplugin.core.PluginFuture;
import org.springplugin.core.classloader.PluginClassLoader;
import org.springplugin.core.classloader.PluginClassLoaderFactory;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.env.properties.SpringPluginProperties;
import org.springplugin.core.factory.SpringPluginFactory;
import org.springplugin.core.info.DefaultPluginInfo;
import org.springplugin.core.info.PluginInfo;
import org.springplugin.core.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * spring 插件上下文
 * <p>
 * {@link SpringPluginFactory}
 * <p>
 * {@link SpringPluginProperties}
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
public class SpringPluginContext extends AbstractPluginContext implements PluginContext, PluginFuture {

    /**
     * spring 插件属性配置类
     */
    protected final SpringPluginProperties pluginProperties;
    /**
     * Spring插件工厂
     */
    private final SpringPluginFactory springPluginFactory;


    /**
     * 构造方法
     *
     * @param springPluginFactory Spring插件工厂
     * @param pluginProperties    spring 插件属性配置类
     * @author afěi
     */
    public SpringPluginContext(SpringPluginFactory springPluginFactory, SpringPluginProperties pluginProperties) {
        this.springPluginFactory = springPluginFactory;
        this.pluginProperties = pluginProperties;
    }

    @Override
    public boolean load(PluginInfo info) {

        final String name = future(info.name());
        try {
            // 初始化上下文所需要的类加载器
            final PluginClassLoader classLoader = initContextClassLoader(name);
            final Class<?> mainClass = info.mainClass();
            // 过滤主类上的注解
            processAnnotationOnClass(mainClass, classLoader);
            // 初始化插件应用上下文
            springPluginFactory.initContext(name);
            log.info("load spring plugin success, {}", name);
        } catch (Throwable e) {
            log.error(String.format("load spring plugin fail, %s", name), e);
            return false;
        }
        return true;
    }

    @Override
    public void reset(String name) {

        unload(DefaultPluginInfo.of(name));
    }

    @Override
    public boolean unload(PluginInfo info) {
        // TODO: 如何在卸载时判断该插件是否在使用当中 ?
        final String name = info.name();
        checkContextClassLoader(name);
        final ClassLoader classLoader = ClassUtils.currentClassLoader();
        try {
            springPluginFactory.destroy(name);
        } catch (Throwable e) {
            log.error(String.format("unload spring plugin fail, %s", name), e);
            return false;
        } finally {
            if (classLoader instanceof PluginClassLoader pluginClassLoader) {
                try {
                    IOUtils.close(pluginClassLoader);
                } catch (IOException e) {
                    log.error(String.format("plugin classloader close fail, %s", name), e);
                }
            }
            if (!FileUtils.deleteQuietly(new File(SpringPluginClassLoader.LOAD_PATH + name))) {
                log.error("unload plugin fail, can't delete plugin file: {}", name);
                return false;
            }
        }
        return true;
    }

    @Override
    public DataManager dataManager(String name) {

        checkContextClassLoader(name);
        return Optional.ofNullable(springPluginFactory.getInstance(name, DataManager.class))
                .orElseGet(() -> super.dataManager(name));
    }

    @Override
    public DependencyControl versionControl(String name) {

        checkContextClassLoader(name);
        return Optional.ofNullable(springPluginFactory.getInstance(name, DependencyControl.class))
                .orElseGet(() -> super.versionControl(name));
    }
}
