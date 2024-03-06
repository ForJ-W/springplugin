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


import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * 通用异常抽象类
 *
 * @author afěi
 * @version 1.0.0
 */
@Getter
public class PluginException extends RuntimeException {

    /**
     * 异常编码
     */
    private final Integer code;

    /**
     * 异常信息
     */
    private final String errorMessage;


    /**
     * 构造方法
     *
     * @param message 异常信息
     * @author afěi
     */
    public PluginException(String message) {
        super(Objects.requireNonNull(message, "message requireNonNull"));
        this.code = message.hashCode();
        this.errorMessage = message;
    }

    /**
     * 构造方法
     *
     * @param code    异常编码
     * @param message 异常信息
     * @author afěi
     */
    public PluginException(Integer code, String message) {
        super(Objects.requireNonNull(message, "message requireNonNull"));
        this.code = code;
        this.errorMessage = message;
    }

    /**
     * 构造方法
     *
     * @param description 异常简述
     * @param throwable   可抛出的错误信息
     * @author afěi
     */
    public PluginException(String description, @NonNull Throwable throwable) {

        this(description, Optional.of(throwable)
                .map(t -> t.getMessage() + "\n" + Optional.ofNullable(t.getCause())
                        .map(tt -> tt.getMessage() + "\n" + Optional.ofNullable(tt.getCause())
                                .map(Throwable::getMessage)
                                .orElse(""))
                        .orElse(""))
                .get());
    }

    /**
     * 构造方法
     *
     * @param code        异常编码
     * @param description 异常简述
     * @param throwable   可抛出的错误信息
     * @author afěi
     */
    public PluginException(Integer code, String description, @NonNull Throwable throwable) {

        this(code, description, Optional.of(throwable)
                .map(t -> t.getMessage() + "\n" + Optional.ofNullable(t.getCause())
                        .map(tt -> tt.getMessage() + "\n" + Optional.ofNullable(tt.getCause())
                                .map(Throwable::getMessage)
                                .orElse(""))
                        .orElse(""))
                .get());
    }

    /**
     * 构造方法
     *
     * @param description 异常简述
     * @param message     异常信息
     * @author afěi
     */
    public PluginException(String description, String message) {
        this(description + System.lineSeparator() + message);
    }

    /**
     * 构造方法
     *
     * @param code        异常编码
     * @param description 异常简述
     * @param message     异常信息
     * @author afěi
     */
    public PluginException(Integer code, String description, String message) {
        this(code, description + System.lineSeparator() + message);
    }
}
