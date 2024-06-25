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

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未来命名
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class NamedFuture {

    protected static final Map<String, NamedNode> FUTURE_NODES = new ConcurrentHashMap<>();

    protected static final String FLAG = "~F_";

    /**
     * 获取最新名称
     *
     * @param name 名称
     * @return 最新名称
     */
    public static String get(String name) {
        name = name.startsWith(FLAG) ? name.replace(FLAG, "") : name;
        final NamedNode namedNode = FUTURE_NODES.get(name);
        if (Objects.isNull(namedNode)) {
            return name;
        }
        if (Objects.isNull(namedNode.next)) {
            return namedNode.name;
        } else {
            return namedNode.next.name;
        }
    }

    /**
     * 获取根名称
     *
     * @param name 名称
     * @return 根名称
     */
    public static String getRootName(String name) {

        name = name.startsWith(FLAG) ? name.replace(FLAG, "") : name;
        return FUTURE_NODES.get(name).name;
    }

    /**
     * 生成未来的命名
     *
     * @param name 名称
     * @return 未来的名称
     */
    protected String future(String name) {
        name = name.startsWith(FLAG) ? name.replace(FLAG, "") : name;
        if (!FUTURE_NODES.containsKey(name)) {
            FUTURE_NODES.put(name, new NamedNode(name));
            return name;
        }
        final NamedNode root = FUTURE_NODES.get(name);
        if (Objects.isNull(root.next)) {
            root.next = new NamedNode(FLAG + name);
        } else {
            reset(root.next.name);
            root.next.name = name;
            FUTURE_NODES.put(name, root.next);
            return future(name);
        }
        return root.next.name;
    }

    /**
     * 重置节点
     *
     * @param name 名称
     */
    protected void reset(String name) {
        FUTURE_NODES.remove(name);
    }

    /**
     * 命名节点
     *
     * @author afěi
     * @version 1.0.0
     */
    public static class NamedNode {

        /**
         * 节点名
         */
        String name;

        /**
         * 下一个节点
         */
        NamedNode next;

        NamedNode(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            NamedNode that = (NamedNode) object;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "NamedNode{" +
                    "name='" + name + '\'' +
                    ", next=" + next +
                    '}';
        }
    }
}
