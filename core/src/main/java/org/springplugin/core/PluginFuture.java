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

package org.springplugin.core;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未来的插件
 *
 * @author afěi
 * @version 1.0.0
 */
public interface PluginFuture {

    Map<String, FutureNode> FUTURE_NODES = new ConcurrentHashMap<>();

    String FLAG = "F_";

    default String future(String name) {
        name = name.startsWith(FLAG) ? name.replace(FLAG, "") : name;
        if (!FUTURE_NODES.containsKey(name)) {

            FUTURE_NODES.put(name, new FutureNode(name));
            return name;
        }
        final FutureNode root = FUTURE_NODES.get(name);
        if (Objects.isNull(root.next)) {

            root.next = new FutureNode(FLAG + name);
        } else {
            reset(root.next.name);
            root.next.name = name;
            FUTURE_NODES.put(name, root.next);
            return future(name);
        }

        return root.next.name;
    }

    static String get(String name) {

        name = name.startsWith(FLAG) ? name.replace(FLAG, "") : name;
        final FutureNode futureNode = FUTURE_NODES.get(name);
        if (Objects.isNull(futureNode.next)) {
            return futureNode.name;
        } else {
            return futureNode.next.name;
        }
    }

    static String getRootName(String name) {

        name = name.startsWith(FLAG) ? name.replace(FLAG, "") : name;
       return FUTURE_NODES.get(name).name;
    }

    default void reset(String name) {

    }

    class FutureNode {

        String name;

        FutureNode next;

        public FutureNode(String name) {
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
            FutureNode that = (FutureNode) object;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
