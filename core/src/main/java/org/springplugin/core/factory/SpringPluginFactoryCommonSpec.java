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


import org.springframework.lang.Nullable;
import org.springplugin.core.exception.SpringPluginException;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Spring插件工厂公共规范
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringPluginFactoryCommonSpec extends SpringPluginFactorySpec {

    @Nullable
    private final Supplier<Class<?>[]> configurationSupplier;

    /**
     * 构造方法
     *
     * @param configurationSupplier 插件配置类供应函数
     * @author afěi
     */
    public SpringPluginFactoryCommonSpec(@Nullable Supplier<Class<?>[]> configurationSupplier) {
        super(null);
        this.configurationSupplier = configurationSupplier;
    }

    /**
     * 构造方法
     *
     * @param configuration 插件配置类
     * @author afěi
     */
    public SpringPluginFactoryCommonSpec(Class<?>... configuration) {
        super(null, configuration);
        this.configurationSupplier = null;
    }

    @Override
    public String getName() {
        throw new SpringPluginException("Common spec not allow get name");
    }

    @Override
    public Class<?>[] getConfiguration() {
        return Objects.isNull(configurationSupplier) ? super.getConfiguration() : configurationSupplier.get();
    }
}
