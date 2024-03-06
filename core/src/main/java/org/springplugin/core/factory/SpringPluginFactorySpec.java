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

package org.springplugin.core.factory;

import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * Spring插件工厂规范
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringPluginFactorySpec implements NamedContextFactory.Specification {

    /**
     * 插件名称
     */
    protected final String name;

    /**
     * 插件配置类数组
     */
    protected final Class<?>[] configuration;

    /**
     * 构造方法
     *
     * @param name          插件名称
     * @param configuration 插件配置类
     * @author afěi
     */
    public SpringPluginFactorySpec(String name, Class<?>... configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return configuration;
    }
}
