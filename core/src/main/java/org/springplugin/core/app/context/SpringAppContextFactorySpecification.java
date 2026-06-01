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

package org.springplugin.core.app.context;

/**
 * Spring插件应用工厂规范
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringAppContextFactorySpecification implements AppContextFactory.Specification {

    private final String name;

    /**
     * 插件配置类数组
     */
    private final Class<?>[] configuration;

    public SpringAppContextFactorySpecification(String name, Class<?>... configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<?>[] getConfigurations() {
        return this.configuration;
    }
}
