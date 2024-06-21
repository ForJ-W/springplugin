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

package org.springplugin.core.context;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.PluginInfo;
import org.springplugin.core.info.PluginInfoFactory;
import org.springplugin.core.util.SpringIocUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    protected final Map<String, ApplicationContextInitializer<AnnotationConfigApplicationContext>> applicationContextInitializers = new ConcurrentHashMap<>();

    /**
     * {@link NamedContextFactory#contexts}
     */
    protected final Map<String, AnnotationConfigApplicationContext> contexts;

    /**
     * {@link NamedContextFactory#configurations}
     */
    private final Map<String, SpringPluginFactorySpec> configurations;


    /**
     * 构造方法
     *
     * @author afěi
     */
    @SuppressWarnings("unchecked")
    public SpringPluginFactory() {
        super(DEFAULT_CONFIG_TYPE, PROPERTY_SOURCE_NAME, PROPERTY_NAME);
        final String contextsErrorMessage = "Can not initialize applicationContextInitializers";
        this.contexts = Optional.ofNullable(ReflectionUtils.findField(NamedContextFactory.class, "contexts", Map.class))
                .map(f -> {
                    f.setAccessible(true);
                    try {
                        return (Map<String, AnnotationConfigApplicationContext>) f.get(this);
                    } catch (IllegalAccessException e) {
                        throw new SpringPluginException(contextsErrorMessage, e);
                    }
                }).orElseThrow(() -> new SpringPluginException(contextsErrorMessage));

        this.configurations = Optional.ofNullable(ReflectionUtils.findField(NamedContextFactory.class, "configurations", Map.class))
                .map(f -> {
                    f.setAccessible(true);
                    try {
                        return (Map<String, SpringPluginFactorySpec>) f.get(this);
                    } catch (IllegalAccessException e) {
                        throw new SpringPluginException(contextsErrorMessage, e);
                    }
                }).orElseThrow(() -> new SpringPluginException(contextsErrorMessage));
    }

    /**
     * 根据上下文获取插件信息
     *
     * @param context 插件上下文
     * @return 插件名称
     * @author afěi
     */
    public static PluginInfo getPluginInfo(@NonNull GenericApplicationContext context) {

        final ConfigurableEnvironment environment = context.getEnvironment();
        return PluginInfoFactory.get(environment.getProperty(PROPERTY_NAME));
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
        super.getContext(NamedFuture.get(name));
    }

    /**
     * 初始化上下文
     *
     * @param info 插件信息
     * @author afěi
     */
    public void initContext(PluginInfo info) {
        super.getContext(info.name());
    }

    @Override
    public AnnotationConfigApplicationContext createContext(String name) {
        name = NamedFuture.get(name);
        final ConfigurableApplicationContext parent = (ConfigurableApplicationContext) getParent();
        this.applicationContextInitializers.put(name, parent.getBean(SpringPluginChildContextInitializer.class));
        AnnotationConfigApplicationContext context = buildContext(name);
        if (applicationContextInitializers.get(name) != null) {
            applicationContextInitializers.get(name).initialize(context);
            context.refresh();
            return context;
        }
        registerBeans(name, context);
        context.refresh();
        return context;
    }

    public void registerBeans(String name, GenericApplicationContext context) {
        Assert.isInstanceOf(AnnotationConfigRegistry.class, context);
        AnnotationConfigRegistry registry = (AnnotationConfigRegistry) context;
        if (this.configurations.containsKey(name)) {
            for (Class<?> configuration : this.configurations.get(name).getConfiguration()) {
                registry.register(configuration);
            }
        }
        for (Map.Entry<String, SpringPluginFactorySpec> entry : this.configurations.entrySet()) {
            if (entry.getKey().startsWith("default.")) {
                for (Class<?> configuration : entry.getValue().getConfiguration()) {
                    registry.register(configuration);
                }
            }
        }
        registry.register(PropertyPlaceholderAutoConfiguration.class, DEFAULT_CONFIG_TYPE);
    }

    public AnnotationConfigApplicationContext buildContext(String name) {
        ClassLoader classLoader = getClass().getClassLoader();
        AnnotationConfigApplicationContext context;
        final ApplicationContext parent = getParent();
        if (parent != null) {
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            if (parent instanceof ConfigurableApplicationContext) {
                beanFactory.setBeanClassLoader(
                        ((ConfigurableApplicationContext) parent).getBeanFactory().getBeanClassLoader());
            } else {
                beanFactory.setBeanClassLoader(classLoader);
            }
            context = new AnnotationConfigApplicationContext(beanFactory);
        } else {
            context = new AnnotationConfigApplicationContext();
        }
        context.setClassLoader(classLoader);
        registerBeans(name, context);
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME,
                Collections.singletonMap(PROPERTY_NAME, name)));
        if (parent != null) {
            // Uses Environment from parent as well as beans
            context.setParent(parent);
        }
        context.setDisplayName(generateDisplayName(name));
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
            if (runner instanceof ApplicationRunner) {
                ApplicationRunner applicationRunner = (ApplicationRunner) runner;
                callRunner(applicationRunner, args);
            }
            if (runner instanceof CommandLineRunner) {
                CommandLineRunner commandLineRunner = (CommandLineRunner) runner;
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
    public AnnotationConfigApplicationContext getContext(String name) {
        name = NamedFuture.get(name);
        if (!this.contexts.containsKey(name)) {
            throw new SpringPluginException(String.format("The current plugin does not exist, %s", name));
        }
        return this.contexts.get(name);
    }


    public boolean hasContext(String name) {
        return this.contexts.containsKey(name);
    }
}
