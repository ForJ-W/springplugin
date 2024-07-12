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


import javassist.ClassPool;
import javassist.LoaderClassPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springplugin.core.app.context.AppContextFactory;
import org.springplugin.core.bytecode.ByteCode;
import org.springplugin.core.bytecode.JavassistBytecode;
import org.springplugin.core.classloader.AppClassLoader;
import org.springplugin.core.classloader.AppClassLoaderFactory;
import org.springplugin.core.classloader.SpringAppClassLoader;

/**
 * 抽象的插件应用服务上下文
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAppServerContext implements AppServerContext {

    protected final AppContextFactory<?> factory;

    @Override
    public ByteCode bytecode(ClassLoader classLoader) {
        final ClassPool pool = new ClassPool();
        pool.appendClassPath(new LoaderClassPath(classLoader));
        return new JavassistBytecode(pool);
    }

    @Override
    public AppContextFactory<?> appContextFactory() {
        return this.factory;
    }

    /**
     * 初始化上下文相关的类加载器
     *
     * @param name 插件名称
     * @author afěi
     */
    protected AppClassLoader initContextClassLoader(String name) {
        final AppClassLoader classLoader = SpringAppClassLoader.getInstance(name);
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
        if (AppClassLoaderFactory.has(name)) {
            Thread.currentThread().setContextClassLoader(SpringAppClassLoader.getInstance(name));
        }
    }
}
