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

package appdemo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springplugin.core.context.DependencyControl;
import org.springplugin.core.context.PluginContext;
import org.springplugin.core.factory.SpringPluginFactory;
import org.springplugin.core.info.DefaultPluginInfo;

/**
 * @author afěi
 * @version 1.0.0
 */
@Component
@Lazy
@RequiredArgsConstructor
public class AppDemoDependencyControl implements DependencyControl {

    final SpringPluginFactory pcf;
    final PluginContext pc;

    @Override
    public void control() {
        if (!pcf.hasContext("mybatisplusdemo")) {
            pc.load(DefaultPluginInfo.of("mybatisplusdemo"));
        }
    }

    @Override
    public void upgrade() {

    }
}
