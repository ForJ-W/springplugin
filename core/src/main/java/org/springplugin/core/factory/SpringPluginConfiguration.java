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

import org.springframework.context.annotation.Bean;

/**
 * Spring 插件配置
 * <p>
 * 每个插件都内置的配置, 一般用来限定在插件中不去依赖父容器的bean
 * <p>
 * 与{@link SpringPluginFactorySpec}功能基本一致, 粒度不一样
 * <p>
 * 此处不进行配置声明, 由子类扩展并通过{@link SpringPluginFactorySpec}去规范
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringPluginConfiguration {

}
