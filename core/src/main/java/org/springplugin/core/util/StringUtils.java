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

import com.google.common.base.CaseFormat;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 字符串工具类
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class StringUtils extends org.apache.commons.lang3.StringUtils {


    public static final String QUESTION_MARK = "?";
    public static final String LEFT_BRACE = "{";
    public static final String LEFT_SQ_BRACKET = "[";
    public static final String RIGHT_SQ_BRACKET = "]";
    public static final String RIGHT_BRACE = "}";

    /**
     * 模糊匹配
     *
     * @param key     匹配的键
     * @param element 匹配的列表
     * @return 是否匹配成功
     * @author afěi
     */
    public static boolean fuzzyMatch(String key, String... element) {

        for (String ignoreName : element) {
            if (ignoreName.startsWith("*") && ignoreName.endsWith("*")) {
                if (key.contains(ignoreName.substring(1, ignoreName.length() - 1))) {
                    return true;
                }
            } else if (ignoreName.startsWith("*")) {
                if (key.endsWith(ignoreName.substring(1))) {
                    return true;
                }
            } else if (ignoreName.endsWith("*")) {
                if (key.startsWith(ignoreName.substring(0, ignoreName.length() - 1))) {
                    return true;
                }
            } else if (ignoreName.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 带谓词合并字符串
     *
     * @param predicate 谓词
     * @param elements  元素
     * @param <E>       元素泛型
     * @return 合并后的字符串
     * @author afěi
     */
    @SafeVarargs
    public static <E> String merge(Predicate<E> predicate, final E... elements) {

        if (elements.length == 1) {
            return String.valueOf(elements[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (E element : elements) {

            if (predicate.test(element)) {

                sb.append(element);
            }
        }
        return sb.toString();
    }

    /**
     * 合并字符串
     *
     * @param elements 元素
     * @param <E>      元素泛型
     * @return 合并后的字符串
     * @author afěi
     */
    @SafeVarargs
    public static <E> String merge(final E... elements) {

        return merge(e -> true, elements);
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str        指定字符串
     * @param stringList 需要检查的字符串数组
     * @return 是否匹配
     * @author afěi
     */
    public static boolean matches(String str, List<String> stringList) {
        if (StringUtils.isBlank(str) || CollectionUtils.isEmpty(stringList)) {
            return false;
        }
        for (String pattern : stringList) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则配置:
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的url
     * @return 是否匹配
     * @author afěi
     */
    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    /**
     * 为字符串中每个字符前拼接上指定字符
     *
     * @param source 原字符串
     * @param c      指定字符
     * @return 拼接后的字符串
     * @author afěi
     */
    public static String appendChar(String source, final char c) {

        if (StringUtils.isBlank(source)) {
            return source;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {

            sb.append(c).append(source.charAt(i));
        }
        return sb.toString();
    }

    /**
     * 大驼峰转小驼峰
     *
     * @param str 字符串
     * @return 小驼峰字符串
     * @author afěi
     */
    public static String upperToLower(String str) {

        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, str);
    }

    /**
     * 获取url参数map
     *
     * @param url url字符串
     * @return url参数map
     * @author chenjie
     */
    public static Map<String, String> getUrlParams(String url) {
        if (!url.contains(QUESTION_MARK)) {
            return Collections.emptyMap();
        }
        String[] urlParts = url.split("\\?");
        //有参数
        String[] params = urlParts[1].split("&");
        Map<String, String> hashMap = new HashMap<>(params.length);
        for (String param : params) {
            String[] keyValue = param.split("=");
            hashMap.put(keyValue[0], keyValue[1]);
        }
        return hashMap;
    }

    /**
     * 判断字符串是否是"{"、"["开头，以"}"、"]"结束的字符串
     *
     * @param value 字符串
     * @return 是否是"{"、"["开头，以"}"、"]"结束的字符串
     * @author chenjie
     */
    public static boolean isJsonStartEnd(@NonNull String value) {
        return isJsonObjectStartEnd(value) || isJsonArrayStartEnd(value);
    }

    /**
     * 判断字符串是否是"{"开头，以"}"结束的字符串
     *
     * @param value 字符串
     * @return 判断字符串是否是"{"开头，以"}"结束的字符串
     * @author chenjie
     */
    public static boolean isJsonObjectStartEnd(@NonNull String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return StringUtils.startsWith(value, LEFT_BRACE) && StringUtils.endsWith(value, RIGHT_BRACE);
    }

    /**
     * 判断字符串是否是"["开头，以"]"结束的字符串
     *
     * @param value 字符串
     * @return 是否是"["开头，以"]"结束的字符串
     * @author chenjie
     */
    public static boolean isJsonArrayStartEnd(@NonNull String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        return StringUtils.startsWith(value, LEFT_SQ_BRACKET) && StringUtils.endsWith(value, RIGHT_SQ_BRACKET);
    }

    /**
     * 小写首字母<br>
     * 例如：str = Name, return name
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String lowerFirst(String str) {
        if (null == str) {
            return null;
        }
        if (!str.isEmpty()) {
            char firstChar = str.charAt(0);
            if (Character.isUpperCase(firstChar)) {
                return Character.toLowerCase(firstChar) + StringUtils.substring(str, 1);
            }
        }
        return str;
    }
}
