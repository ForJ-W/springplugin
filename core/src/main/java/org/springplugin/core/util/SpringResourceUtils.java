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

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * spring 资源工具
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class SpringResourceUtils extends ResourceUtils {

    /**
     * 匹配当前路径下的所有文件
     */
    public static final String MATCH_ALL_PATTERN = "*.";

    /**
     * 资源解析器
     */
    private static final ResourcePatternResolver RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();

    /**
     * 转换合并多个路径资源
     *
     * @param locations 资源路径
     * @return 资源数组
     * @author afěi
     */
    public static Resource[] resolveLocations(String[] locations) {
        return Stream.of(Optional.ofNullable(locations).orElse(new String[0]))
                .flatMap(location -> Stream.of(getResources(location))).toArray(Resource[]::new);
    }

    /**
     * 转换路径资源
     *
     * @param location 资源路径
     * @return 资源数组
     * @author afěi
     */
    public static Resource[] getResources(String location) {
        try {
            return RESOURCE_RESOLVER.getResources(location);
        } catch (IOException e) {
            return new Resource[0];
        }
    }
}
