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

package org.springplugin.manager.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 插件管理service
 *
 * @author afěi
 * @version 1.0.0
 */
public interface SpringPluginManagerService {

    /**
     * 加载插件
     *
     * @param file 插件文件
     * @return 加载结果信息
     * @throws IOException 插件文件io异常
     * @author afěi
     */
    String load(@RequestPart("file") MultipartFile file, @RequestParam(name = "mainClass", required = false) String mainClass) throws IOException;

    /**
     * 卸载插件
     *
     * @param name 插件名称
     * @return 卸载结果信息
     * @author afěi
     */
    String unload(@RequestParam("name") String name);
}
