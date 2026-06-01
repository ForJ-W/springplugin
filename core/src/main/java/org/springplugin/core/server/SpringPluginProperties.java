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

package org.springplugin.core.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springplugin.core.contant.PluginConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件属性配置类
 *
 * @author afěi
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(SpringPluginProperties.PREFIX)
public class SpringPluginProperties {

    /**
     * 配置前缀
     */
    public static final String PREFIX = "springplugin";

    /**
     * 插件拦截配置
     */
    private Intercept intercept = new Intercept();

    /**
     * 插件调试配置
     */
    private Debug debug = new Debug();

    /**
     * 插件调试配置
     *
     * @author afěi
     * @version 1.0.0
     */
    @Data
    public static class Debug {

        /**
         * 插件资源缓存, 开启后插件资源不会刷新/删除
         */
        private boolean resourceCache;
    }

    /**
     * 插件拦截配置
     *
     * @author afěi
     * @version 1.0.0
     */
    @Data
    public static class Intercept {

        /**
         * 插件拦截身份key
         */
        private String identityKey = PluginConstant.META_HEADER;

        /**
         * 插件拦截身份验证模式
         */
        private IdentityMode identityMode = IdentityMode.HEADER;

        /**
         * 插件拦截白名单
         */
        private List<String> whiteList = new ArrayList<>();
    }

    /**
     * 插件拦截身份验证模式
     *
     * @author afěi
     * @version 1.0.0
     */
    public enum IdentityMode {

        /**
         * 请求头
         */
        HEADER,

        /**
         * 请求参数
         */
        PARAMETER,

        /**
         * 请求url
         */
        URL
    }
}
