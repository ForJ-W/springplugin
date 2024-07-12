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

package org.springplugin.autoconfigure;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springplugin.core.app.configuration.SpringWebAppConfiguration;
import org.springplugin.core.app.context.SpringAppContextFactory;
import org.springplugin.core.app.context.SpringAppContextFactorySpecification;
import org.springplugin.core.app.context.initializer.SpringAppBeanRegisterInitializer;
import org.springplugin.core.app.context.initializer.SpringAppContextInitializers;
import org.springplugin.core.app.context.initializer.SpringAppMetaReaderInitializer;
import org.springplugin.core.app.context.initializer.SpringAppWebMvcConfigureInitializer;
import org.springplugin.core.server.SpringPluginProperties;

import static org.springplugin.core.app.context.AppContextFactory.Specification.DEFAULT_SPECIFICATION;

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
     * Spring插件应用工厂
     *
     * @author afěi
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringAppContextFactory appContextFactory() {
        return new SpringAppContextFactory()
                .specifications(new SpringAppContextFactorySpecification(DEFAULT_SPECIFICATION,
                        SpringWebAppConfiguration.class));
    }

    /**
     * spring插件应用上下文初始化集
     *
     * @param factory 上下文工厂
     */
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("unchecked")
    public SpringAppContextInitializers springPluginContextInitializers(SpringAppContextFactory factory) {
        return new SpringAppContextInitializers(factory,
                SpringAppMetaReaderInitializer.class,
                SpringAppBeanRegisterInitializer.class,
                SpringAppWebMvcConfigureInitializer.class);
    }
}
