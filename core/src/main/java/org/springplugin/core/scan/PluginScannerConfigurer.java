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

import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import static org.springframework.util.Assert.notNull;

/**
 * 插件扫描器配置
 *
 * @author afěi
 * @version 1.0.0
 * @see PluginScan
 * @see PluginScannerRegistrar
 * @see PluginClassScanner
 */
@Setter
public class PluginScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, PriorityOrdered {

    /**
     * 基础包路径
     */
    private String basePackage;

    /**
     * 主类名
     */
    private String mainClassName;

    /**
     * 是否懒加载
     */
    private Boolean lazyInitialization;

    /**
     * 应用上下文
     */
    private ApplicationContext applicationContext;

    /**
     * bean名称生成器
     */
    private BeanNameGenerator nameGenerator;

    /**
     * bean的默认生命周期
     */
    private String defaultScope;

    @Override
    public int getOrder() {

        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void afterPropertiesSet() {
        notNull(this.basePackage, "Property 'basePackage' is required");
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) {

        PluginClassScanner scanner = new PluginClassScanner(registry);
        scanner.setMainClassName(this.mainClassName);
        scanner.setResourceLoader(this.applicationContext);
        scanner.setBeanNameGenerator(this.nameGenerator);
        scanner.setLazyInitialization(lazyInitialization);
        if (StringUtils.hasText(defaultScope)) {
            scanner.setDefaultScope(defaultScope);
        }
        scanner.registerFilters();
        scanner.scan(
                StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }
}
