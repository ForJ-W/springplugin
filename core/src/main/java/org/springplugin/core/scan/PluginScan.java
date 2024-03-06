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

package org.springplugin.core.scan;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springplugin.core.autoconfigure.PluginAutoConfigurationImportSelector;

import java.lang.annotation.*;

/**
 * 插件扫描注解
 * <p>
 * 手动声明要扫描的插件包, 一般来说可以不使用
 * <p>
 * 由{@link org.springplugin.core.bytecode.ByteCode}动态添加
 * <p>
 * {@link org.springplugin.core.context.AbstractPluginContext#processAnnotationOnClass(Class, ClassLoader)}
 *
 * @author afěi
 * @version 1.0.0
 * @see PluginScannerRegistrar
 * @see PluginScannerConfigurer
 * @see PluginClassScanner
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableAspectJAutoProxy(exposeProxy = true)
@Import({PluginScannerRegistrar.class, PluginAutoConfigurationImportSelector.class})
public @interface PluginScan {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation declarations
     * <p>
     * e.g.:
     * <pre>{@code
     * @PluginScan("my.pkg")
     * public class Main {
     *     public static void main(String[] args) {
     *
     *     }
     * }
     * }</pre>
     *
     * @return base package names
     */
    String[] value() default {};

    /**
     * Base packages to scan for plugin components. Note that only interfaces with at least one method will be
     * registered; concrete classes will be ignored.
     *
     * @return base package names for scanning plugin interface
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned.
     * <p>
     * Consider creating a special no-op marker class or interface in each package that serves no purpose other than being
     * referenced by this attribute.
     *
     * @return classes that indicate base package for scanning plugin component
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * The {@link BeanNameGenerator} class to be used for naming detected components within the Spring container.
     *
     * @return the class of {@link BeanNameGenerator}
     */
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    /**
     * Whether enable lazy initialization of plugin bean.
     *
     * <p>
     * Default is {@code false}.
     * </p>
     *
     * @return set {@code true} to enable lazy initialization
     */
    boolean lazyInitialization() default false;

    /**
     * Specifies the default scope of scanned plugins.
     *
     * <p>
     * Default is {@code ""} (equiv to singleton).
     * </p>
     *
     * @return the default scope
     */
    String defaultScope() default AbstractBeanDefinition.SCOPE_DEFAULT;
}
