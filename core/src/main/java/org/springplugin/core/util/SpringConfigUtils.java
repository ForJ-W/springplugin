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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * spring 解析环境中定义的属性配置以及占位符工具
 * <p>
 * {@link PlaceholderConfigurerSupport}
 * {@link EnvironmentAware}
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
public abstract class SpringConfigUtils {


    /**
     * 插入属性源
     * <p>
     * {@link org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration#insertPropertySources(MutablePropertySources, List)}
     *
     * @param propertySources 可变属性源集
     * @param composite       复合属性源列表
     * @author afěi
     */
    public static void insertPropertySources(MutablePropertySources propertySources, List<PropertySource<?>> composite) {
        final Method insertPropertySources = ReflectUtils.findMethod(PropertySourceBootstrapConfiguration.class, "insertPropertySources", MutablePropertySources.class, List.class);
        Objects.requireNonNull(insertPropertySources).setAccessible(true);
        try {
            insertPropertySources.invoke(new PropertySourceBootstrapConfiguration(), propertySources, composite);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建临时包含属性源的环境
     *
     * @param incoming 可变属性源集
     * @return 包含属性源的环境
     * @author afěi
     */
    private static Environment environment(MutablePropertySources incoming) {
        ConfigurableEnvironment environment = new AbstractEnvironment() {
        };
        for (PropertySource<?> source : incoming) {
            environment.getPropertySources().addLast(source);
        }
        return environment;
    }

    /**
     * 根据key获取环境中的配置
     *
     * @param key  配置key
     * @param type 获取的值的类型
     * @param <T>  类型泛型
     * @return 配置值
     * @author afěi
     */
    @Nullable
    public static <T> T getConfig(String key, Class<T> type) {

        return SpringAwareUtils.env().getProperty(key, type);
    }

    /**
     * 根据key获取环境中的配置
     *
     * @param key          配置key
     * @param type         获取的值的类型
     * @param defaultValue 如果没有找到值，则返回默认值
     * @param <T>          类型泛型
     * @return 配置值
     * @author afěi
     */
    @NonNull
    public static <T> T getConfig(String key, Class<T> type, T defaultValue) {

        return SpringAwareUtils.env().getProperty(key, type, defaultValue);
    }

    /**
     * 根据key获取环境中的配置
     *
     * @param key 配置key
     * @return 配置值
     * @author afěi
     */
    @Nullable
    public static String getConfig(String key) {

        return SpringAwareUtils.env().getProperty(key);
    }

    /**
     * 根据key获取环境中的配置
     *
     * @param key          配置key
     * @param defaultValue 如果没有找到值，则返回默认值
     * @return 配置值
     * @author afěi
     */
    @NonNull
    public static String getConfig(String key, String defaultValue) {

        return SpringAwareUtils.env().getProperty(key, defaultValue);
    }

    /**
     * 根据属性配置类前缀获取属性配置类
     *
     * @param propertiesClass 属性配置类class对象
     * @param <T>             类型泛型
     * @return 配置值
     * @author afěi
     */
    @NonNull
    public static <T> T acquireConfigurationPropertiesBean(Class<T> propertiesClass) {

        String prefix = "";
        try {
            final Field prefixField = propertiesClass.getField("PREFIX");
            prefix = String.valueOf(prefixField.get(null));
        } catch (Exception e) {
            log.error("Can not find field 'PREFIX'");
        }
        return acquireConfigurationPropertiesBean(prefix, propertiesClass);
    }


    /**
     * 根据属性配置类前缀获取属性配置类
     *
     * @param prefix          属性配置前缀{@link ConfigurationProperties#prefix()}
     * @param propertiesClass 属性配置类class对象
     * @param <T>             类型泛型
     * @return 配置值
     * @author afěi
     */
    @NonNull
    public static <T> T acquireConfigurationPropertiesBean(String prefix, Class<T> propertiesClass) {

        return acquireConfigurationBindResult(prefix, propertiesClass).get();
    }


    /**
     * 根据属性配置类前缀获取属性绑定对象
     *
     * @param prefix          属性配置前缀{@link ConfigurationProperties#prefix()}
     * @param propertiesClass 属性配置类class对象
     * @param <T>             类型泛型
     * @return 配置值
     * @author afěi
     */
    @NonNull
    public static <T> BindResult<T> acquireConfigurationBindResult(String prefix, Class<T> propertiesClass) {

        final ConfigurableEnvironment env = SpringAwareUtils.env();
        final PropertySourcesPlaceholdersResolver propertySourcesPlaceholdersResolver = new PropertySourcesPlaceholdersResolver(env);
        return new Binder(ConfigurationPropertySources.from(env.getPropertySources()), propertySourcesPlaceholdersResolver)
                .bind(prefix, propertiesClass);
    }
}
