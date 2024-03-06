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

package org.springplugin.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.context.PluginContext;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.DefaultPluginInfo;
import org.springplugin.core.util.AssertUtils;

import java.io.File;
import java.util.Objects;
import java.util.Set;

/**
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class SpringPluginServer implements ApplicationRunner {

    final PluginContext pluginContext;

    public static void main(String[] args) {

        SpringApplication.run(SpringPluginServer.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {

        final File plugin = new File(SpringPluginClassLoader.LOAD_PATH);
        if (!plugin.exists()) {
            AssertUtils.isTrue(plugin.mkdirs(), new SpringPluginException("Plugin dir create fail"));
        }
        final File[] plugins = plugin.listFiles();
        Set.of(Objects.requireNonNull(plugins))
                .stream()
                .filter(File::isDirectory)
                .forEach(f -> {
                    final String name = f.getName();
                    try {
                        pluginContext.load(DefaultPluginInfo.of(name));
                    } catch (Exception e) {
                        log.error("Plugin init load fail, {}", name);
                    }
                });
    }
}
