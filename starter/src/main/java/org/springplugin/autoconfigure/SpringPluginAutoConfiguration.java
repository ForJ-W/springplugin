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

package org.springplugin.autoconfigure;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springplugin.core.factory.SpringPluginFactory;
import org.springplugin.core.env.PluginPropertySourceLocator;
import org.springplugin.core.env.properties.SpringPluginProperties;

/**
 * 插件配置类
 *
 * @author afěi
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(SpringPluginProperties.class)
public class SpringPluginAutoConfiguration {


    /**
     * 插件属性源定位器
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnMissingBean
    public PluginPropertySourceLocator pluginPropertySourceLocator(SpringPluginProperties pluginProps) {

        return new PluginPropertySourceLocator(pluginProps);
    }

    /**
     * Spring插件工厂
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringPluginFactory pluginContextFactory() {

        return new SpringPluginFactory();
    }
}
