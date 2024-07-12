/*
 * Copyright 2024 ForJ-W
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springplugin.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springplugin.core.classloader.SpringAppClassLoader;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.AppInfo;
import org.springplugin.core.info.FileAppInfo;
import org.springplugin.core.server.context.AppServerContext;
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
public class SpringPluginServerApp implements InitializingBean {

    final AppServerContext serverContext;

    public static void main(String[] args) {
        SpringApplication.run(SpringPluginServerApp.class, args);
    }

    @Override
    public void afterPropertiesSet() {
//        init();
    }

    public void init() {
        final File plugin = new File(SpringAppClassLoader.LOAD_PATH);
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
                        final AppInfo pi = FileAppInfo.create(name, null);
                        serverContext.load(pi);
                    } catch (Exception e) {
                        log.error("Plugin init load fail, {}", name);
                    }
                });
    }
}
