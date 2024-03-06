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

package org.springplugin.core.exception;

import org.springframework.lang.NonNull;

/**
 * spring插件相关异常
 *
 * @author afěi
 * @version 1.0.0
 */
public class SpringPluginException extends PluginException {


    /**
     * 构造方法
     *
     * @param message 异常信息
     * @author afěi
     */
    public SpringPluginException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param code    异常编码
     * @param message 异常信息
     * @author afěi
     */
    public SpringPluginException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 构造方法
     *
     * @param description 异常简述
     * @param throwable   可抛出的错误信息
     * @author afěi
     */
    public SpringPluginException(String description, @NonNull Throwable throwable) {
        super(description, throwable);
    }

    /**
     * 构造方法
     *
     * @param code        异常编码
     * @param description 异常简述
     * @param throwable   可抛出的错误信息
     * @author afěi
     */
    public SpringPluginException(Integer code, String description, @NonNull Throwable throwable) {
        super(code, description, throwable);
    }

    /**
     * 构造方法
     *
     * @param description 异常简述
     * @param message     异常信息
     * @author afěi
     */
    public SpringPluginException(String description, String message) {
        super(description + System.lineSeparator() + message);
    }

    /**
     * 构造方法
     *
     * @param code        异常编码
     * @param description 异常简述
     * @param message     异常信息
     * @author afěi
     */
    public SpringPluginException(Integer code, String description, String message) {
        super(code, description + System.lineSeparator() + message);
    }
}
