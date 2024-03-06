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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合帮助
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class CollectionUtils extends org.springframework.util.CollectionUtils {


    /**
     * 构建一个1.25倍的 {@link LinkedHashSet}
     *
     * @param capacity 容量
     * @param <K>      key类型
     * @return {@link LinkedHashSet}
     * @author afěi
     */
    public static <K> Set<K> linkedHashSet(int capacity) {

        return new LinkedHashSet<>((int) (capacity * 1.25));
    }

    /**
     * 构建一个1.25倍的 {@link HashSet}
     *
     * @param capacity 容量
     * @param <K>      key类型
     * @return {@link HashSet}
     * @author afěi
     */
    public static <K> Set<K> hashSet(int capacity) {

        return new HashSet<>((int) (capacity * 1.25));
    }

    /**
     * 构建一个1.25倍的 {@link LinkedHashMap}
     *
     * @param capacity 容量
     * @param <K>      key类型
     * @param <V>      value类型
     * @return {@link LinkedHashMap}
     * @author afěi
     */
    public static <K, V> Map<K, V> linkedHashMap(int capacity) {

        return new LinkedHashMap<>((int) (capacity * 1.25));
    }

    /**
     * 构建一个1.25倍的{@link HashMap}
     *
     * @param capacity 容量
     * @param <K>      key类型
     * @param <V>      value类型
     * @return {@link HashMap}
     * @author afěi
     */
    public static <K, V> Map<K, V> hashMap(int capacity) {

        return new HashMap<>((int) (capacity * 1.25));
    }

    /**
     * 字符串切割转换成set
     *
     * @param value 字符串值
     * @param regex 匹配的规则字符串
     * @return 切割后的set
     * @author afěi
     */
    @NonNull
    public static Set<String> convertToSet(String value, String regex) {
        Set<String> cell = new LinkedHashSet<>();
        if (StringUtils.isBlank(value)) {
            return cell;
        }

        if (!value.contains(regex)) {

            cell.add(value);
            return cell;
        }

        String[] split = value.split(regex);
        Collections.addAll(cell, split);
        return cell;
    }

    /**
     * 判断并设置map中的集合
     *
     * @param targetMap   目标map
     * @param key         map的key
     * @param targetValue 目标值
     * @param <T>         目标值的类型
     * @author afěi
     */
    @SafeVarargs
    public static <K, T> void judgeSetMap(Map<K, Collection<T>> targetMap, K key, T... targetValue) {

        boolean flag = targetMap.containsKey(key);
        Collection<T> targetCollection;
        if (flag) {
            targetCollection = targetMap.get(key);
        } else {
            targetCollection = new LinkedHashSet<>(4);
        }
        if (targetValue != null) {
            for (T t : targetValue) {

                if (null != t) {
                    targetCollection.add(t);
                }
            }
        }

        targetMap.put(key, targetCollection);
    }

    /**
     * 判断集合元素数量在不为空的情况下是否相等
     *
     * @param oldC 旧集合
     * @param newC 新集合
     * @return 是否数量一致
     * @author afěi
     */
    public static boolean judgeCount(Collection<?> oldC, Collection<?> newC) {

        return !CollectionUtils.isEmpty(oldC) && !CollectionUtils.isEmpty(newC) && oldC.size() == newC.size();
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection {@link Collection}
     * @return 是否数量一致
     * @author afěi
     */
    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !CollectionUtils.isEmpty(collection);
    }

    /**
     * 判断集合是否不为空
     *
     * @param map {@link Map}
     * @return 是否数量一致
     * @author afěi
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !CollectionUtils.isEmpty(map);
    }

    /**
     * 切分集合为list
     *
     * @param originCollection  源集合
     * @param subCollectionSize 子集合切分的大小
     * @param <T>               list中的元素泛型
     * @return 切分后的list
     * @author afěi
     */
    public static <T> List<List<T>> splitCollectionToList(Collection<T> originCollection, int subCollectionSize) {

        int limit = (originCollection.size() + subCollectionSize - 1) / subCollectionSize;
        return Stream.iterate(0, n -> n + 1).limit(limit)
                .map(a -> originCollection.stream()
                        .skip((long) a * subCollectionSize)
                        .limit(subCollectionSize)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}
