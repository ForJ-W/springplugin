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

import org.apache.commons.lang3.StringUtils;
import org.springplugin.core.exception.PluginException;
import org.springplugin.core.exception.SpringPluginException;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言工具类
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class AssertUtils {


    /**
     * 如果不满足断言标记抛出异常
     *
     * @param flag    断言标记
     * @param message 异常信息
     * @author afěi
     */
    public static void isTrue(boolean flag, String message) {

        isTrue(flag, new SpringPluginException(message));
    }

    /**
     * 如果不满足断言标记抛出异常
     *
     * @param flag            断言标记
     * @param pluginException 插件异常{@link PluginException}
     * @author afěi
     */
    public static void isTrue(boolean flag, PluginException pluginException) {
        if (!flag) {

            throw pluginException;
        }
    }

    /**
     * 不允许对象为null
     * <p>
     * 如果{@link Objects#isNull(Object)}抛出异常
     *
     * @param obj             对象
     * @param pluginException 插件异常{@link PluginException}
     * @author afěi
     */
    public static void isNotNull(Object obj, PluginException pluginException) {

        if (Objects.isNull(obj)) {

            throw pluginException;
        }
    }

    /**
     * 不允许对象不为null
     * <p>
     * 如果{@link Objects#nonNull(Object)}抛出异常
     *
     * @param obj             对象
     * @param pluginException 插件异常{@link PluginException}
     * @author afěi
     */
    public static void isNull(Object obj, PluginException pluginException) {

        if (Objects.nonNull(obj)) {

            throw pluginException;
        }
    }

    /**
     * 不允许集合为空
     * <p>
     * 如果{@link CollectionUtils#isEmpty(Collection)}抛出异常
     *
     * @param collection      集合{@link Collection}
     * @param pluginException 插件异常{@link PluginException}
     * @author afěi
     */
    public static void isNotEmpty(Collection<?> collection, PluginException pluginException) {

        if (CollectionUtils.isEmpty(collection)) {

            throw pluginException;
        }
    }

    /**
     * 不允许集合不为空
     * <p>
     * 如果{@link CollectionUtils#isNotEmpty(Collection)}抛出异常
     *
     * @param collection      集合{@link Collection}
     * @param pluginException 插件异常{@link PluginException}
     * @author afěi
     */
    public static void isEmpty(Collection<?> collection, PluginException pluginException) {

        if (CollectionUtils.isNotEmpty(collection)) {

            throw pluginException;
        }
    }

    /**
     * 不允许集合为空
     * <p>
     * 如果{@link CollectionUtils#isEmpty(Map)}抛出异常
     *
     * @param map             集合{@link Map}
     * @param pluginException 插件异常{@link PluginException}
     * @author afěi
     */
    public static void isNotEmpty(Map<?, ?> map, PluginException pluginException) {

        if (CollectionUtils.isEmpty(map)) {

            throw pluginException;
        }
    }

    /**
     * 不允许集合不为空
     * <p>
     * 如果{@link CollectionUtils#isNotEmpty(Map)}抛出异常
     *
     * @param map             集合{@link Map}
     * @param pluginException 插件异常{@link PluginException}
     * @author afěi
     */
    public static void isEmpty(Map<?, ?> map, PluginException pluginException) {

        if (CollectionUtils.isNotEmpty(map)) {

            throw pluginException;
        }
    }

    /**
     * 不允许字符序列为空
     * <p>
     * 如果{@link StringUtils#isBlank(CharSequence)}抛出异常
     *
     * @param charSequence    字符序列{@link CharSequence}
     * @param pluginException 插件异常{@link PluginException}
     * @author afěi
     */
    public static void isNotBlank(CharSequence charSequence, PluginException pluginException) {

        if (StringUtils.isBlank(charSequence)) {

            throw pluginException;
        }
    }

    /**
     * 不允许字符序列不为空
     * <p>
     * 如果{@link StringUtils#isNotBlank(CharSequence)}抛出异常
     *
     * @param charSequence    字符序列{@link CharSequence}
     * @param pluginException 插件异常{@link PluginException}
     * @author afěi
     */
    public static void isBlank(CharSequence charSequence, PluginException pluginException) {

        if (StringUtils.isNotBlank(charSequence)) {

            throw pluginException;
        }
    }
}
