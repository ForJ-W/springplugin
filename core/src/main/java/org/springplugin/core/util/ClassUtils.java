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

package org.springplugin.core.util;

import org.springframework.lang.NonNull;
import org.springplugin.core.exception.PluginException;

import java.util.Optional;

/**
 * 类对象工具
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class ClassUtils extends org.springframework.util.ClassUtils {

    /**
     * 获取当前类加载器
     *
     * @return 当前类加载器
     * @author afěi
     */
    @NonNull
    public static ClassLoader currentClassLoader() {

        return Optional.ofNullable(ClassUtils.getDefaultClassLoader()).orElseThrow(() -> new PluginException("Get current class loader fail"));
    }
}
