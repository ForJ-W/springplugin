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

package org.springplugin.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springplugin.core.PluginFuture;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.context.PluginContext;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.DefaultPluginInfo;
import org.springplugin.core.util.AssertUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author afěi
 * @version 1.0.0
 */
@Tag(name = "pm")
@Slf4j
@RestController
@RequestMapping("pm")
@RequiredArgsConstructor
public class SpringPluginManagerController {

    private final PluginContext pc;

    /**
     * 加载插件
     *
     * @param file 插件文件
     * @return 加载结果信息
     * @throws IOException 插件文件io异常
     * @author afěi
     */
    @PostMapping(value = "load", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String load(@RequestPart("file") MultipartFile file) throws IOException {

        AssertUtils.isNotNull(file, new SpringPluginException("file must not be null"));
        final String originalFilename = file.getOriginalFilename();
        AssertUtils.isTrue(Optional.ofNullable(originalFilename).orElse("").endsWith(".jar"), new SpringPluginException("file must be jar"));
        final File jarTempPath = new File("temp/" + UUID.randomUUID());
        final File jarFilePath = new File(jarTempPath, originalFilename);
        final boolean deleteQuietly;
        String plugin;
        try {
            FileUtils.writeByteArrayToFile(jarFilePath, file.getBytes());
            plugin = originalFilename.split("\\.")[0];
            if (PluginFuture.FUTURE_NODES.containsKey(plugin)) {
                plugin = PluginFuture.FLAG + plugin;
            }
            try (ZipFile zipFile = new ZipFile(jarFilePath)) {
                zipFile.extractAll(SpringPluginClassLoader.LOAD_PATH + plugin);
            }
            final DefaultPluginInfo dpi = DefaultPluginInfo.of(plugin);
            if (!pc.load(dpi)) {
                pc.unload(dpi);
                return "load plugin fail";
            }
        } finally {
            deleteQuietly = FileUtils.deleteQuietly(jarTempPath);
        }
        if (!deleteQuietly) {
            return "load plugin fail, can't delete temp jar: " + plugin;
        }
        final String successMessage = "load plugin success: " + plugin;
        log.info(successMessage);
        return successMessage;
    }

    /**
     * 卸载插件
     *
     * @param name 插件名称
     * @return 卸载结果信息
     */
    @PostMapping("unload")
    public String unload(@RequestParam("name") String name) {

        if (!pc.unload(DefaultPluginInfo.of(name))) {
            return "unload plugin fail: " + name;
        }
        final String successMessage = "unload plugin success: " + name;
        log.info(successMessage);
        return successMessage;
    }
}
