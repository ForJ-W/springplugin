///*
// * Copyright 2023 original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// */
//
//package org.springplugin.core.server;
//
//import org.springframework.lang.NonNull;
//
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 未来命名
// *
// * @author afěi
// * @version 1.0.0
// */
//public abstract class NamedFuture {
//    protected static final String F = "~F_";
//    protected static final Map<String, NamedNode> FUTURE_NODES = new ConcurrentHashMap<>();
//
//    public static String get(String plugin) {
//        NamedNode cur = FUTURE_NODES.get(plugin);
//        while (cur.next!=null) {
//            cur = cur.next;
//        }
//        return cur.name;
//    }
//
//    /**
//     * 生成未来的命名
//     *
//     * @param name 名称
//     * @return 未来的名称
//     */
//    public static  String future(String name) {
//        if (!FUTURE_NODES.containsKey(name)) {
//            final NamedNode root = new NamedNode(name);
//            FUTURE_NODES.put(name, root.root = root);
//            return name;
//        }
//        final NamedNode root = FUTURE_NODES.get(name);
//        return futureNode(root).name;
//    }
//
//    /**
//     * 重置节点
//     *
//     * @param name 名称
//     */
//    public static  void reset(String name) {
//        FUTURE_NODES.remove(name);
//    }
//
//    /**
//     * 获取未来命名的大小
//     *
//     * @return 默认2
//     */
//    public static  int getFutureSize() {
//        return 2;
//    }
//
//    /**
//     * 生成一个命名节点
//     *
//     * @param nn 当前节点
//     * @return 新节点
//     */
//    public static  NamedNode futureNode(@NonNull NamedNode nn) {
//        if (nn.next == null) {
//            final int cnt = nn.index + 1;
//            final NamedNode root;
//            final NamedNode next = new NamedNode(cnt, F + cnt + nn.root.name, root = nn.root);
//            if (cnt == getFutureSize()) {
//                final NamedNode nrt;
//                NamedNode cur;
//                FUTURE_NODES.put(root.name, cur = nrt = root.next);
//                final int ni = --next.index;
//                for (int i = 0; i < ni; i++) {
//                    if (cur != null) {
//                        --cur.index;
//                        cur.root = nrt;
////                        if (cur.next == null) {
////                            cur.next = next;
////                        }
//                        cur = cur.next;
//                    }
//                }
//            }
//            return nn.next = next;
//        } else {
//            return futureNode(nn.next);
//        }
//    }
//
//    /**
//     * 命名节点
//     *
//     * @author afěi
//     * @version 1.0.0
//     */
//    public static class NamedNode {
//
//        int index;
//
//        /**
//         * 节点名
//         */
//        String name;
//
//        NamedNode root;
//
//        /**
//         * 下一个节点
//         */
//        NamedNode next;
//
//        NamedNode(String name) {
//            this.name = name;
//        }
//
//        NamedNode(int index, String name, NamedNode root) {
//            this.index = index;
//            this.name = name;
//            this.root = root;
//        }
//
//        @Override
//        public boolean equals(Object object) {
//            if (this == object) {
//                return true;
//            }
//            if (object == null || getClass() != object.getClass()) {
//                return false;
//            }
//            NamedNode that = (NamedNode) object;
//            return Objects.equals(name, that.name);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(name);
//        }
//
//        @Override
//        public String toString() {
//            return "NamedNode{" +
//                    "name='" + name + '\'' +
//                    ", next=" + next +
//                    '}';
//        }
//    }
//}
