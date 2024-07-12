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

package org.springplugin.core.scan;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springplugin.core.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 插件应用组件扫描注册器
 * <p>
 * 用于注册{@link AppComponentScannerConfigurer} -> {@link #registerBeanDefinitions(AnnotationMetadata, AnnotationAttributes, BeanDefinitionRegistry, String)}
 * <p>
 * {@link ImportBeanDefinitionRegistrar}的机制步骤:
 * <p>
 * 1. {@link org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions(BeanDefinitionRegistry)}
 * <p>
 * 2. {@link org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsFromRegistrars(Map)}
 * <p>
 * 3. {@link ImportBeanDefinitionRegistrar#registerBeanDefinitions(AnnotationMetadata, BeanDefinitionRegistry)}
 *
 * @author afěi
 * @version 1.0.0
 * @see AppComponentScan
 * @see AppComponentScannerConfigurer
 * @see AppComponentClassScanner
 */
public class AppComponentScannerRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * 生成基础bean名称
     *
     * @param importingClassMetadata 导入类注解元数据
     * @return bean名称
     * @author afěi
     */
    private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata) {
        return importingClassMetadata.getClassName() + "#" + AppComponentScannerRegistrar.class.getSimpleName() + "#" + 0;
    }

    /**
     * 获取默认扫描路径
     *
     * @param importingClassMetadata 导入类注解元数据
     * @return 默认扫描路径
     * @author afěi
     */
    private static String getDefaultBasePackage(AnnotationMetadata importingClassMetadata) {
        return ClassUtils.getPackageName(importingClassMetadata.getClassName());
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        final String beanName = generateBaseBeanName(importingClassMetadata);
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        AnnotationAttributes pluginScanAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(AppComponentScan.class.getName()));
        if (pluginScanAttrs != null) {
            registerBeanDefinitions(importingClassMetadata, pluginScanAttrs, registry, beanName);
        }
    }

    /**
     * 注册bean描述
     *
     * @param annoMeta  注解元数据
     * @param annoAttrs 注解属性
     * @param registry  bean描述注册器
     * @param beanName  bean名称
     * @author afěi
     */
    void registerBeanDefinitions(AnnotationMetadata annoMeta, AnnotationAttributes annoAttrs, BeanDefinitionRegistry registry, String beanName) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AppComponentScannerConfigurer.class);

        builder.addPropertyValue("mainClassName", annoMeta.getClassName());

        Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
        generatorClass = generatorClass.equals(BeanNameGenerator.class) ? AnnotationBeanNameGenerator.class : generatorClass;
        builder.addPropertyValue("nameGenerator", BeanUtils.instantiateClass(generatorClass));

        List<String> basePackages = new ArrayList<>();
        basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));

        basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText).collect(Collectors.toList()));

        basePackages.addAll(Arrays.stream(annoAttrs.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName).collect(Collectors.toList()));

        if (basePackages.isEmpty()) {
            basePackages.add(getDefaultBasePackage(annoMeta));
        }

        Boolean lazyInitialization = annoAttrs.getBoolean("lazyInitialization");
        builder.addPropertyValue("lazyInitialization", lazyInitialization);

        String defaultScope = annoAttrs.getString("defaultScope");
        if (!AbstractBeanDefinition.SCOPE_DEFAULT.equals(defaultScope)) {
            builder.addPropertyValue("defaultScope", defaultScope);
        }

        builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));

        // for spring-native
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }
}
