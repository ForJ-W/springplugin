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

package org.springplugin.core.context.initializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.OrderComparator;
import org.springplugin.core.context.SpringPluginFactory;
import org.springplugin.core.context.SpringPluginFactoryCommonSpec;
import org.springplugin.core.util.AssertUtils;
import org.springplugin.core.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * spring插件上下文初始化集
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
public class SpringPluginContextInitializers {

    private final List<AbstractSpringPluginContextInitializer> initializers = new ArrayList<>();

    public SpringPluginContextInitializers(AbstractSpringPluginContextInitializer... initializers) {
        if (initializers != null) {
            Collections.addAll(this.initializers, initializers);
        }
    }

    @SuppressWarnings("unchecked")
    public SpringPluginContextInitializers(SpringPluginFactory contextFactory,
                                           SpringPluginFactoryCommonSpec commonSpec,
                                           Class<? extends AbstractSpringPluginContextInitializer>... initializerClasses) {
        AssertUtils.isTrue(ArrayUtils.isNotEmpty(initializerClasses), "'initializerClasses' should not be empty");
        for (Class<? extends AbstractSpringPluginContextInitializer> initializerClass : initializerClasses) {
            try {
                this.initializers.add(Objects.requireNonNull(ClassUtils.getConstructorIfAvailable(initializerClass, contextFactory.getClass(), commonSpec.getClass()))
                        .newInstance(contextFactory, commonSpec));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error("Can't instantiate {}", initializerClass.getName(), e);
            }
        }
        this.initializers.sort(OrderComparator.INSTANCE);
    }

    public List<AbstractSpringPluginContextInitializer> getInitializers() {
        return initializers;
    }
}
