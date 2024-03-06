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

package org.springplugin.core.context;

import java.io.File;

/**
 * 数据管理扩展接口
 *
 * @author afěi
 * @version 1.0.0
 */
public interface DataManager {

    /**
     * 空数据管理
     */
    DataManager EMPTY = new DataManager() {
        @Override
        public void init(Object param) {
        }

        @Override
        public void doExport() {
        }

        @Override
        public void doImport(File file) {
        }

        @Override
        public void destroy() {
        }
    };

    /**
     * 初始化数据
     *
     * @param param 初始化所需的参数
     * @author afěi
     */
    void init(Object param);

    /**
     * 导出数据
     *
     * @author afěi
     */
    void doExport();

    /**
     * 导入数据
     *
     * @param file 导入的文件
     * @author afěi
     */
    void doImport(File file);

    /**
     * 销毁数据
     *
     * @author afěi
     */
    void destroy();
}
