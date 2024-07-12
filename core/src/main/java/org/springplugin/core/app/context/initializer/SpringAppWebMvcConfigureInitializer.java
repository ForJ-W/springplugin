/*
 * Copyright 2023-2025 the original author or authors.
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

package org.springplugin.core.app.context.initializer;

import org.springframework.boot.web.servlet.context.WebApplicationContextServletContextAwareProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springplugin.core.app.context.SpringAppContextFactory;
import org.springplugin.core.info.AppInfo;

/**
 * spring插件webmvc配置初始化器
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringAppWebMvcConfigureInitializer extends AbstractSpringAppContextInitializer implements ApplicationContextInitializer<AnnotationConfigApplicationContext>, Ordered {

    public static final int ORDER = SpringAppBeanRegisterInitializer.ORDER + 1;


    public SpringAppWebMvcConfigureInitializer(SpringAppContextFactory factory) {
        super(factory);
    }

    @Override
    protected void initialize(AnnotationConfigApplicationContext context, AppInfo appInfo) {
        context.getBeanFactory().addBeanPostProcessor(new WebApplicationContextServletContextAwareProcessor((ConfigurableWebApplicationContext) factory.getParent()));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
