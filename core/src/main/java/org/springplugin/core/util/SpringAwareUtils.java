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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.bootstrap.config.BootstrapPropertySource;
import org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.bootstrap.config.SimpleBootstrapPropertySource;
import org.springframework.context.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springplugin.core.exception.SpringPluginException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring aware 工具
 * <p>
 * {@link Aware}参考spring bean的生命周期: {@link BeanFactory}上声明的注释信息
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
@Component
public class SpringAwareUtils implements Ordered, ApplicationContextInitializer<ConfigurableApplicationContext>, BeanFactoryPostProcessor, BeanFactoryAware, ApplicationContextAware, EnvironmentAware, ApplicationEventPublisherAware {

    /**
     * {@link org.springframework.cloud.context.refresh.ContextRefresher#REFRESH_ARGS_PROPERTY_SOURCE}
     */
    protected static final String REFRESH_ARGS_PROPERTY_SOURCE = "refreshArgs";
    /**
     * {@link org.springframework.boot.web.servlet.context.ApplicationServletEnvironment}
     */
    private static final String APPLICATION_SERVLET_ENVIRONMENT = "org.springframework.boot.web.servlet.context.ApplicationServletEnvironment";

    /**
     * 子Aware扩展map
     */
    private static final Map<String, ChildAware> CHILD_AWARE_MAP = new ConcurrentHashMap<>();

    private static ApplicationContext applicationContext;
    private static ConfigurableListableBeanFactory beanFactory;
    private static SpringAwareUtils self;
    private static ConfigurableEnvironment environment;
    private static ApplicationEventPublisher eventPublisher;

    /**
     * {@link Ordered}
     * <p>
     * 在{@link org.springframework.cloud.bootstrap.BootstrapApplicationListener.AncestorInitializer}后
     * <p>
     * 在{@link PropertySourceBootstrapConfiguration}前
     */
    private int order = Ordered.HIGHEST_PRECEDENCE + 9;

    /**
     * {@link BeanFactory}
     *
     * @return {@link BeanFactory}
     * @author afěi
     */
    @NonNull
    public static ConfigurableListableBeanFactory beanFactory() {

        selfInspection();
        final String name = ClassUtils.currentClassLoader().getName();
        return hasChildAware(name)
                ? Optional.ofNullable(CHILD_AWARE_MAP.get(name)).map(ChildAware::getBeanFactory).orElse(beanFactory)
                : beanFactory;
    }

    /**
     * {@link ApplicationContext}
     *
     * @return {@link ApplicationContext}
     * @author afěi
     */
    @NonNull
    public static ApplicationContext applicationContext() {

        selfInspection();
        final String name = ClassUtils.currentClassLoader().getName();
        return hasChildAware(name)
                ? Optional.ofNullable(CHILD_AWARE_MAP.get(name)).map(ChildAware::getApplicationContext).orElse(applicationContext)
                : applicationContext;
    }

    /**
     * {@link ApplicationContext}
     *
     * @return {@link ApplicationContext}
     * @author afěi
     */
    @NonNull
    public static ConfigurableEnvironment env() {

        selfInspection();
        final String name = ClassUtils.currentClassLoader().getName();
        return hasChildAware(name)
                ? Optional.ofNullable(CHILD_AWARE_MAP.get(name)).map(ChildAware::getEnvironment).orElse(environment)
                : environment;
    }


    /**
     * {@link ApplicationEventPublisher}
     *
     * @return {@link ApplicationEventPublisher}
     * @author afěi
     */
    @NonNull
    public static ApplicationEventPublisher eventPublisher() {

        selfInspection();
        final String name = ClassUtils.currentClassLoader().getName();
        return hasChildAware(name)
                ? Optional.ofNullable(CHILD_AWARE_MAP.get(name)).map(ChildAware::getEventPublisher).orElse(eventPublisher)
                : eventPublisher;
    }

    /**
     * 根据名称移除子Aware
     *
     * @param name 子名称
     * @author afěi
     */
    @Nullable
    public static void removeChildAware(@Nullable String name) {
        synchronized (CHILD_AWARE_MAP) {
            if (!hasChildAware(name)) {
                return;
            }
            CHILD_AWARE_MAP.remove(name);
        }
    }

    /**
     * 判断指定名称的子Aware是否存在
     *
     * @param name 子名称
     * @return 指定名称的子Aware是否存在
     * @author afěi
     */
    public static boolean hasChildAware(@Nullable String name) {
        synchronized (CHILD_AWARE_MAP) {
            return StringUtils.isNotBlank(name) && CHILD_AWARE_MAP.containsKey(name);
        }
    }

    /**
     * 自检
     *
     * @author afěi
     */
    private static void selfInspection() {
        if (Objects.isNull(self)) {

            throw new SpringPluginException("The current spring environment is unavailable");
        }
    }

    @Override
    public int getOrder() {

        return order;
    }

    public void setOrder(int order) {

        this.order = order;
    }

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {

        SpringAwareUtils.self = this;
        final ConfigurableEnvironment environment = applicationContext.getEnvironment();
        SpringAwareUtils.environment = environment;
        if (environment.getPropertySources().contains(REFRESH_ARGS_PROPERTY_SOURCE)) {
            return;
        }
        SpringAwareUtils.beanFactory = applicationContext.getBeanFactory();
        SpringAwareUtils.applicationContext = applicationContext;
        final ApplicationContext parentContext = applicationContext.getParent();
        if (Objects.isNull(parentContext)) {
            return;
        }
        try {
            if (Class.forName(APPLICATION_SERVLET_ENVIRONMENT).isInstance(environment)) {
                final List<PropertySourceLocator> propertySourceLocators = new ArrayList<>(parentContext.getBeansOfType(PropertySourceLocator.class).values());
                AnnotationAwareOrderComparator.sort(propertySourceLocators);
                List<PropertySource<?>> composite = new ArrayList<>();
                boolean empty = true;
                for (PropertySourceLocator locator : propertySourceLocators) {
                    Collection<PropertySource<?>> source = locator.locateCollection(environment);
                    if (source == null || source.isEmpty()) {
                        continue;
                    }
                    List<PropertySource<?>> sourceList = new ArrayList<>();
                    for (PropertySource<?> p : source) {
                        if (p instanceof EnumerablePropertySource<?> enumerable) {
                            sourceList.add(new BootstrapPropertySource<>(enumerable));
                        } else {
                            sourceList.add(new SimpleBootstrapPropertySource<>(p));
                        }
                    }
                    log.info("Located property source: " + sourceList);
                    composite.addAll(sourceList);
                    empty = false;
                }
                if (!empty) {
                    MutablePropertySources propertySources = environment.getPropertySources();
                    for (PropertySource<?> p : environment.getPropertySources()) {
                        if (p.getName().startsWith(PropertySourceBootstrapConfiguration.BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
                            propertySources.remove(p.getName());
                        }
                    }
                    SpringConfigUtils.insertPropertySources(propertySources, composite);
                }
            }
        } catch (NoSuchBeanDefinitionException e) {

            log.info("Current environment is not SpringCloud, the lack of bootstrap {}", e.getBeanType());
        } catch (Exception e) {

            log.warn("Can not init cloud env property");
        }
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

        setBeanFactory(beanFactory);
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {

        if (Objects.equals(SpringAwareUtils.beanFactory, ((ConfigurableListableBeanFactory) beanFactory).getParentBeanFactory())) {
            final String name = ClassUtils.currentClassLoader().getName();
            if (StringUtils.isNotBlank(name)) {
                if (!CHILD_AWARE_MAP.containsKey(name)) {
                    synchronized (CHILD_AWARE_MAP) {
                        if (!CHILD_AWARE_MAP.containsKey(name)) {
                            CHILD_AWARE_MAP.put(name, new ChildAware());
                        }
                    }
                }
                CHILD_AWARE_MAP.get(name).setBeanFactory(beanFactory);
                return;
            }
        }
        SpringAwareUtils.self = this;
        SpringAwareUtils.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {

        final String name = ClassUtils.currentClassLoader().getName();
        if (hasChildAware(name)) {

            CHILD_AWARE_MAP.get(name).setApplicationContext(applicationContext);
            return;
        }
        SpringAwareUtils.self = this;
        SpringAwareUtils.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {

        final String name = ClassUtils.currentClassLoader().getName();
        if (hasChildAware(name)) {

            CHILD_AWARE_MAP.get(name).setEnvironment(environment);
            return;
        }
        SpringAwareUtils.self = this;
        SpringAwareUtils.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {

        final String name = ClassUtils.currentClassLoader().getName();
        if (hasChildAware(name)) {

            CHILD_AWARE_MAP.get(name).setApplicationEventPublisher(applicationEventPublisher);
            return;
        }
        SpringAwareUtils.self = this;
        SpringAwareUtils.eventPublisher = applicationEventPublisher;
    }

    /**
     * 子Aware
     *
     * @author afěi
     * @version 1.0.0
     */
    @Getter
    public static class ChildAware implements BeanFactoryAware, ApplicationContextAware, EnvironmentAware, ApplicationEventPublisherAware {

        ApplicationContext applicationContext;
        ConfigurableListableBeanFactory beanFactory;
        ApplicationEventPublisher eventPublisher;
        ConfigurableEnvironment environment;

        @Override
        public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
            this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        }

        @Override
        public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
            this.eventPublisher = applicationEventPublisher;
        }

        @Override
        public void setEnvironment(@NonNull Environment environment) {
            this.environment = (ConfigurableEnvironment) environment;
        }
    }
}
