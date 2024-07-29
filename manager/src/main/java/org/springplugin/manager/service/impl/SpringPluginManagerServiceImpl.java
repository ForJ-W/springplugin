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

package org.springplugin.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springplugin.core.classloader.SpringAppClassLoader;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.AppInfoFactory;
import org.springplugin.core.info.FileAppInfo;
import org.springplugin.core.server.SpringPluginProperties;
import org.springplugin.core.server.context.AppServerContext;
import org.springplugin.core.util.AssertUtils;
import org.springplugin.manager.service.SpringPluginManagerService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * 插件管理service实现
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpringPluginManagerServiceImpl implements SpringPluginManagerService {

    private final AppServerContext serverContext;
    private final SpringPluginProperties springPluginProps;


    @Override
    public String load(MultipartFile file, String mainClass) throws IOException {

        AssertUtils.nonNull(file, new SpringPluginException("file must not be null"));
        final String originalFilename = file.getOriginalFilename();
        AssertUtils.isTrue(Optional.ofNullable(originalFilename).orElse("").endsWith(".jar"), new SpringPluginException("file must be jar"));
        String plugin = originalFilename.split("\\.")[0];
        final File pluginPath = new File(SpringAppClassLoader.LOAD_PATH, plugin);
        final String successMessage = "load plugin success: " + plugin;
        final String failMessage = "load plugin fail: " + plugin;
        if (springPluginProps.getDebug().isResourceCache() && pluginPath.exists()) {
            final FileAppInfo fpi = FileAppInfo.create(plugin, mainClass);
            if (!serverContext.load(fpi)) {
                return failMessage;
            }
            log.info(successMessage);
            return successMessage;
        }
        final File jarTempPath = new File("temp/" + UUID.randomUUID());
        final File jarFilePath = new File(jarTempPath, originalFilename);
        final boolean deleteQuietly;
        try {
            final FileAppInfo fpi = FileAppInfo.create(plugin, mainClass);
            FileUtils.writeByteArrayToFile(jarFilePath, file.getBytes());
            try (ZipFile zipFile = new ZipFile(jarFilePath)) {
                zipFile.extractAll(SpringAppClassLoader.LOAD_PATH + plugin);
            }
            if (!serverContext.load(fpi)) {
                return failMessage;
            }
        } finally {
            deleteQuietly = FileUtils.deleteQuietly(jarTempPath);
        }
        if (!deleteQuietly) {
            return "load plugin fail, can't delete temp jar: " + plugin;
        }
        log.info(successMessage);
        return successMessage;
    }

    @Override
    public String unload(String name) {
        try {
            serverContext.unload(AppInfoFactory.get(name));
        } catch (Exception e) {
            final String errorMessage = "unload plugin fail: " + name;
            log.error(errorMessage, e);
            return errorMessage;
        }
        final String successMessage = "unload plugin success: " + name;
        log.info(successMessage);
        return successMessage;
    }
}
