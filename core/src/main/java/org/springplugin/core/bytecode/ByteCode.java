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

package org.springplugin.core.bytecode;

import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

import java.util.function.BiConsumer;

/**
 * 字节码接口
 *
 * @author afěi
 * @version 1.0.0
 */
public interface ByteCode {

    /**
     * 移除类上的注解
     *
     * @param cls             类对象
     * @param annotationClass 注解类对象
     * @return {@link ByteCode}
     * @author afěi
     */
    ByteCode removeAnnotationToClass(Class<?> cls,
                                     Class<? extends java.lang.annotation.Annotation> annotationClass);

    /**
     * 添加类上的注解
     *
     * @param cls             类对象
     * @param annotationClass 注解类对象
     * @return {@link ByteCode}
     * @author afěi
     */
    ByteCode addAnnotationToClass(Class<?> cls,
                                  Class<? extends java.lang.annotation.Annotation> annotationClass);

    /**
     * 新增字段上的注解
     *
     * @param cls             类对象
     * @param fieldName       字段名称
     * @param annotationClass 注解类对象
     * @param initAnnotation  双参消费注解初始化函数
     * @return {@link ByteCode}
     * @author afěi
     */
    ByteCode addAnnotationToField(Class<?> cls, String fieldName, Class<? extends java.lang.annotation.Annotation> annotationClass,
                                  BiConsumer<Annotation, ConstPool> initAnnotation);

    /**
     * 移除字段上的注解
     *
     * @param cls             类对象
     * @param fieldName       字段名称
     * @param annotationClass 注解类对象
     * @return {@link ByteCode}
     * @author afěi
     */
    ByteCode removeAnnotationFromField(Class<?> cls, String fieldName, Class<? extends java.lang.annotation.Annotation> annotationClass);

    /**
     * 转换class
     *
     * @param clazz    类对象
     * @param byteCode 类字节数组
     * @author afěi
     */
    void transformClass(Class<?> clazz, byte[] byteCode);

    /**
     * 转换class
     *
     * @param clazz 类对象
     * @author afěi
     */
    void transformClass(Class<?> clazz);
}


