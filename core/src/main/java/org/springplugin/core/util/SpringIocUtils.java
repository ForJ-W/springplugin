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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * SpringIoc帮助
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class SpringIocUtils {

    /**
     * 是否有该类型的bean
     *
     * @param beanType bean的类型
     * @return 是否有该类型的bean
     * @author afěi
     */
    public static boolean hasBean(Class<?> beanType) {

        return Objects.nonNull(getBean(beanType));
    }

    /**
     * 是否有该名称的bean
     *
     * @param beanName bean名称
     * @return 是否有该类型的bean
     * @author afěi
     */
    public static boolean hasBean(String beanName) {

        return Objects.nonNull(getBean(beanName));
    }

    /**
     * 获取该类型的所有的bean名称
     *
     * @param type bean的类型
     * @return 所有的bean名称
     * @author afěi
     */
    @Nullable
    public static String[] getBeanNames(Class<?> type) {
        String[] names = null;
        try {
            names = defaultBeanFactory().getBeanNamesForType(type);
        } catch (BeansException ignored) {
        }
        return names;
    }

    /**
     * 获取该类型的首个bean名称
     *
     * @param type bean的类型
     * @return 首个bean名称
     * @author afěi
     */
    @Nullable
    public static String getBeanName(Class<?> type) {

        String[] beanNames = getBeanNames(type);
        return Objects.nonNull(beanNames) && beanNames.length > 1 ? beanNames[0] : null;
    }

    /**
     * 根据类型获取bean
     *
     * @param type bean的类型
     * @param <T>  bean的类型泛型
     * @return bean对象
     * @author afěi
     */
    @Nullable
    public static <T> T getBean(Class<T> type) {
        T b = null;
        try {
            b = defaultBeanFactory().getBean(type);
        } catch (Exception ignored) {
        }
        return b;
    }

    /**
     * 根据名称获取bean
     *
     * @param beanName bean的名称
     * @return bean对象
     * @author afěi
     */
    @Nullable
    public static Object getBean(String beanName) {
        Object b = null;
        try {

            b = defaultBeanFactory().getBean(beanName);
        } catch (Exception ignored) {
        }
        return b;
    }

    /**
     * 根据名称以及类型获取bean
     *
     * @param beanName bean的名称
     * @param type     bean的类型
     * @param <T>      bean的类型泛型
     * @return bean对象
     * @author afěi
     */
    @Nullable
    public static <T> T getBean(String beanName, Class<T> type) {
        T b = null;
        try {

            b = defaultBeanFactory().getBean(beanName, type);
        } catch (Exception ignored) {
        }
        return b;
    }

    /**
     * 根据bean的类型进行替换bean
     *
     * @param beanType bean的类型
     * @return 是否替换成功
     * @author afěi
     */
    public static boolean replaceBean(Class<?> beanType, Object instance) {

        return replaceBean(StringUtils.upperToLower(beanType.getSimpleName()), beanType, instance);
    }

    /**
     * 根据bean的名称,类型和指定实例进行替换bean
     *
     * @param beanName bean的名称
     * @param beanType bean的类型
     * @param instance 新bean的实例
     * @return 是否替换成功
     * @author afěi
     */
    public static boolean replaceBean(String beanName, Class<?> beanType, Object instance) {

        try {
            DefaultListableBeanFactory factory = defaultBeanFactory();
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanType).getBeanDefinition();
            if (hasBean(beanName)) {

                factory.removeBeanDefinition(beanName);
            }
            beanDefinition.setInstanceSupplier(() -> instance);
            factory.registerBeanDefinition(beanName, beanDefinition);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取默认bean工厂
     *
     * @return 默认bean工厂
     * @author afěi
     */
    @NonNull
    private static DefaultListableBeanFactory defaultBeanFactory() {

        return (DefaultListableBeanFactory) SpringAwareUtils.beanFactory();
    }

    /**
     * 根据类型获取bean包装{@link Optional}
     *
     * @param type bean的类型
     * @param <T>  bean的类型泛型
     * @return bean对象
     * @author afěi
     */
    @NonNull
    public static <T> Optional<T> getBeanOp(Class<T> type) {

        return Optional.ofNullable(getBean(type));
    }

    /**
     * 根据名称获取bean包装{@link Optional}
     *
     * @param beanName bean的名称
     * @return bean对象
     * @author afěi
     */
    @NonNull
    public static Optional<Object> getBeanOp(String beanName) {

        return Optional.ofNullable(getBean(beanName));
    }

    /**
     * 根据名称以及类型获取bean包装{@link Optional}
     *
     * @param beanName bean的名称
     * @param type     bean的类型
     * @param <T>      bean的类型泛型
     * @return bean对象
     * @author afěi
     */
    @NonNull
    public static <T> Optional<T> getBeanOp(String beanName, Class<T> type) {

        return Optional.ofNullable(getBean(beanName, type));
    }


    /**
     * 根据类型获取bean
     * <p>
     * 未获取到就抛出异常
     *
     * @param type bean的类型
     * @param <T>  bean的类型泛型
     * @return bean对象
     * @author afěi
     */
    @NonNull
    public static <T> T mustGetBean(Class<T> type) {

        return defaultBeanFactory().getBean(type);
    }

    /**
     * 根据名称获取bean
     * <p>
     * 未获取到就抛出异常
     *
     * @param beanName bean的名称
     * @return bean对象
     * @author afěi
     */
    @NonNull
    public static Object mustGetBean(String beanName) {

        return defaultBeanFactory().getBean(beanName);
    }

    /**
     * 根据名称以及类型获取bean
     * <p>
     * 未获取到就抛出异常
     *
     * @param beanName bean的名称
     * @param type     bean的类型
     * @param <T>      bean的类型泛型
     * @return bean对象
     * @author afěi
     */
    @NonNull
    public static <T> T mustGetBean(String beanName, Class<T> type) {

        return defaultBeanFactory().getBean(beanName, type);
    }
}
