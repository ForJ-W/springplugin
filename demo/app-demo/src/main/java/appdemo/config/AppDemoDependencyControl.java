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

package appdemo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springplugin.core.app.DependencyControl;
import org.springplugin.core.app.context.SpringAppContextFactory;
import org.springplugin.core.info.DefaultAppInfo;
import org.springplugin.core.server.context.AppServerContext;

/**
 * @author afěi
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class AppDemoDependencyControl implements DependencyControl {

    final SpringAppContextFactory factory;
    final AppServerContext serverContext;

    @Override
    public void control() {
        if (!factory.hasContext("mybatisplusdemo")) {
            serverContext.load(DefaultAppInfo.of("mybatisplusdemo"));
        }
    }

    @Override
    public void upgrade() {

    }
}
