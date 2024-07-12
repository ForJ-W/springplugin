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

package org.springplugin.core.scan;

import org.springframework.context.annotation.Import;
import org.springplugin.core.autoconfigure.AppAutoConfigurationImportSelector;

import java.lang.annotation.*;

/**
 * 插件应用组件扫描注解
 * <p>
 * 手动声明要扫描的插件包, 一般来说可以不使用
 * <p>
 * 由{@link org.springplugin.core.bytecode.ByteCode}动态添加
 * <p>
 * {@link org.springplugin.core.server.context.SpringAppServerContext#processAnnotationOnClass(Class, ClassLoader)}
 *
 * @author afěi
 * @version 1.0.0
 * @see AppAutoConfigurationImportSelector
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AppComponentScan
@Import(AppAutoConfigurationImportSelector.class)
public @interface AppAutoConfiguration {

    /**
     * Exclude specific auto-configuration classes such that they will never be applied.
     * @return the classes to exclude
     */
    Class<?>[] exclude() default {};

    /**
     * Exclude specific auto-configuration class names such that they will never be
     * applied.
     * @return the class names to exclude
     * @since 1.3.0
     */
    String[] excludeName() default {};

}
