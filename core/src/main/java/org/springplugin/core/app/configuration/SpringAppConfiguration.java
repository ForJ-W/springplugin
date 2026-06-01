/*
 * Copyright 2023-2025 the original author or authors.
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

package org.springplugin.core.app.configuration;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springplugin.core.app.context.SpringAppContextFactorySpecification;

/**
 * Spring 应用配置
 * <p>
 * 每个插件都内置的配置, 一般用来限定在插件中不去依赖父容器的bean
 * <p>
 * 此处不进行配置声明, 由子类扩展并通过{@link SpringAppContextFactorySpecification}去规范
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class SpringAppConfiguration implements BeanClassLoaderAware, Ordered {

    protected ClassLoader beanClassLoader;

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
