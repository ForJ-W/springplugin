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

package org.springplugin.server.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.springframework.stereotype.Component;
import org.springplugin.core.classloader.SpringAppClassLoader;
import org.springplugin.core.contant.PluginConstant;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.AppInfo;
import org.springplugin.core.info.FileAppInfo;
import org.springplugin.core.server.context.AppServerContext;
import org.springplugin.core.util.AssertUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 插件服务初始化器
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ServerInitializer {

    static final File MANAGER_JAR = new File(System.getProperty("user.dir") + File.separator + PluginConstant.MANAGER_NAME + File.separator + "target" + File.separator + "manager.jar");
    final AppServerContext serverContext;

    /**
     * 初始化
     *
     * @author afěi
     */
    public void init() {
        final File plugin = new File(SpringAppClassLoader.LOAD_PATH);
        if (!plugin.exists()) {
            AssertUtils.isTrue(plugin.mkdirs(), new SpringPluginException("Plugin dir create fail"));
        }
        manager(plugin);
        final File[] plugins = plugin.listFiles();
        Set.of(Objects.requireNonNull(plugins))
                .stream()
                .filter(File::isDirectory)
                .forEach(f -> {
                    final String name = f.getName();
                    try {
                        final AppInfo pi = FileAppInfo.create(name, null);
                        serverContext.load(pi);
                    } catch (Exception e) {
                        log.error("Plugin init load fail, {}", name);
                    }
                });
    }

    /**
     * 初始化管理者
     *
     * @param pluginDir 插件目录
     * @author afěi
     */
    private void manager(File pluginDir) {
        final File[] plugins = Optional.ofNullable(pluginDir.listFiles()).orElse(new File[0]);
        if (Arrays.stream(plugins).map(File::getName).noneMatch(name -> name.endsWith(PluginConstant.MANAGER_NAME))) {
            if (MANAGER_JAR.exists()) {
                try (ZipFile zipFile = new ZipFile(MANAGER_JAR)) {
                    final String managerPlugin = SpringAppClassLoader.LOAD_PATH + PluginConstant.MANAGER_NAME;
                    zipFile.extractAll(managerPlugin);
                    FileAppInfo.create(PluginConstant.MANAGER_NAME, PluginConstant.MANAGER_MAIN_CLASS);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
