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

import org.springframework.beans.factory.InitializingBean;

/**
 * 依赖控制扩展接口
 *
 * @author afěi
 * @version 1.0.0
 */
public interface DependencyControl extends InitializingBean {

    /**
     * 空依赖控制
     */
    DependencyControl EMPTY = new DependencyControl() {

        @Override
        public void control() {
        }

        @Override
        public void upgrade() {
        }
    };

    /**
     * 初始化时检查依赖控制
     *
     * @author afěi
     */
    @Override
    default void afterPropertiesSet() {
        control();
    }

    /**
     * 依赖控制
     *
     * @author afěi
     */
    void control();

    /**
     * 版本升级
     *
     * @author afěi
     */
    void upgrade();
}
