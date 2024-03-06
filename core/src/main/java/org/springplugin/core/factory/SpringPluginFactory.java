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

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;
import org.springplugin.core.PluginFuture;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.util.SpringIocUtils;

import java.util.*;

/**
 * Spring插件工厂
 * {@link NamedContextFactory}
 * <p>
 * 管理维护着:
 * <p>
 * 插件上下文{@link GenericApplicationContext}
 * <p>
 * 初始化器{@link ApplicationContextInitializer}
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringPluginFactory extends NamedContextFactory<SpringPluginFactorySpec> {

    /**
     * 默认指定的插件上下文配置类
     */
    public final static Class<?> DEFAULT_CONFIG_TYPE = SpringPluginConfiguration.class;
    /**
     * 属性源名称
     */
    public final static String PROPERTY_SOURCE_NAME = "plugin";

    /**
     * 属性名称
     */
    public final static String PROPERTY_NAME = "plugin.name";

    /**
     * 单例实例
     */
    private static volatile SpringPluginFactory instance;

    /**
     * {@link  NamedContextFactory#applicationContextInitializers}
     */
    protected final Map<String, ApplicationContextInitializer<GenericApplicationContext>> applicationContextInitializers;

    /**
     * {@link NamedContextFactory#contexts}
     */
    protected final Map<String, GenericApplicationContext> contexts;

    /**
     * 构造方法
     *
     * @author afěi
     */
    @SuppressWarnings("unchecked")
    public SpringPluginFactory() {
        super(DEFAULT_CONFIG_TYPE, PROPERTY_SOURCE_NAME, PROPERTY_NAME);
        // 反射获取私有核心属性 上下文初始化器和上下文容器
        final String applicationContextInitializersErrorMessage = "Can not initialize applicationContextInitializers";
        this.applicationContextInitializers = Optional.ofNullable(ReflectionUtils.findField(NamedContextFactory.class, "applicationContextInitializers", Map.class))
                .map(f -> {
                    f.setAccessible(true);
                    try {
                        return (Map<String, ApplicationContextInitializer<GenericApplicationContext>>) f.get(this);
                    } catch (IllegalAccessException e) {
                        throw new SpringPluginException(applicationContextInitializersErrorMessage, e);
                    }
                }).orElseThrow(() -> new SpringPluginException(applicationContextInitializersErrorMessage));
        final String contextsErrorMessage = "Can not initialize applicationContextInitializers";
        this.contexts = Optional.ofNullable(ReflectionUtils.findField(NamedContextFactory.class, "contexts", Map.class))
                .map(f -> {
                    f.setAccessible(true);
                    try {
                        return (Map<String, GenericApplicationContext>) f.get(this);
                    } catch (IllegalAccessException e) {
                        throw new SpringPluginException(contextsErrorMessage, e);
                    }
                }).orElseThrow(() -> new SpringPluginException(contextsErrorMessage));
    }

    /**
     * 根据上下文获取插件名称
     *
     * @param context 插件上下文
     * @return 插件名称
     * @author afěi
     */
    public static String getPluginName(@NonNull GenericApplicationContext context) {

        return context.getEnvironment().getProperty(PROPERTY_NAME);
    }

    /**
     * 获取单例Spring插件工厂
     *
     * @return Spring插件工厂
     * @author afěi
     */
    public static SpringPluginFactory getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (SpringPluginFactory.class) {
                if (Objects.isNull(instance)) {

                    return instance = SpringIocUtils.mustGetBean(SpringPluginFactory.class);
                }
            }
        }
        return instance;
    }

    /**
     * 根据名称销毁指定的上下文
     *
     * @param name 插件名称
     * @author afěi
     */
    public void destroy(String name) {

        this.applicationContextInitializers.remove(name);
        Optional.ofNullable(this.contexts.remove(name)).ifPresent(AbstractApplicationContext::close);
    }

    /**
     * 初始化上下文
     *
     * @param name 插件名称
     * @author afěi
     */
    public void initContext(String name) {
        super.getContext(PluginFuture.get(name));
    }

    @Override
    public synchronized GenericApplicationContext createContext(String name) {
        final GenericApplicationContext context = super.createContext(name);
        callRunners(context, new DefaultApplicationArguments());
        return context;
    }

    /**
     * 调用Runner相关接口
     *
     * @param context 应用上下文
     * @param args    应用参数
     * @author afěi
     */
    protected void callRunners(ApplicationContext context, ApplicationArguments args) {
        List<Object> runners = new ArrayList<>();
        runners.addAll(context.getBeansOfType(ApplicationRunner.class).values());
        runners.addAll(context.getBeansOfType(CommandLineRunner.class).values());
        AnnotationAwareOrderComparator.sort(runners);
        for (Object runner : new LinkedHashSet<>(runners)) {
            if (runner instanceof ApplicationRunner applicationRunner) {
                callRunner(applicationRunner, args);
            }
            if (runner instanceof CommandLineRunner commandLineRunner) {
                callRunner(commandLineRunner, args);
            }
        }
    }

    /**
     * 调用应用Runner相关接口
     *
     * @param args 应用参数
     * @author afěi
     */
    protected void callRunner(ApplicationRunner runner, ApplicationArguments args) {
        try {
            (runner).run(args);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to execute ApplicationRunner", ex);
        }
    }

    /**
     * 调用命令行Runner相关接口
     *
     * @param args 应用参数
     * @author afěi
     */
    protected void callRunner(CommandLineRunner runner, ApplicationArguments args) {
        try {
            (runner).run(args.getSourceArgs());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to execute CommandLineRunner", ex);
        }
    }

    @Override
    public GenericApplicationContext buildContext(String name) {
        name = PluginFuture.get(name);
        final ConfigurableApplicationContext parent = (ConfigurableApplicationContext) getParent();
        this.applicationContextInitializers.put(name, parent.getBean(SpringPluginChildContextInitializer.class));
        return super.buildContext(name);
    }

    @Override
    public GenericApplicationContext getContext(String name) {
        name = PluginFuture.get(name);
        if (!this.contexts.containsKey(name)) {
            throw new SpringPluginException(String.format("The current plugin does not exist, %s", name));
        }
        return this.contexts.get(name);
    }


    public boolean hasContext(String name) {
        return this.contexts.containsKey(name);
    }
}
