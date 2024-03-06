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

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.commons.lang3.ArrayUtils;
import org.springplugin.core.exception.PluginException;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * javassist字节码
 *
 * @param pool 类对象池
 * @author afěi
 * @version 1.0.0
 */
public record JavassistBytecode(ClassPool pool) implements ByteCode {

    /**
     * 构造方法
     *
     * @param pool 类对象池
     * @author afěi
     */
    public JavassistBytecode {
    }

    @Override
    public ByteCode removeAnnotationToClass(Class<?> cls,
                                            Class<? extends java.lang.annotation.Annotation> annotationClass) {

        if (!cls.isAnnotationPresent(annotationClass)) {
            return this;
        }
        try {
            final CtClass ctClass = pool.getCtClass(cls.getName());
            final ClassFile classFile = ctClass.getClassFile();
            AnnotationsAttribute attribute = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
            attribute.removeAnnotation(annotationClass.getTypeName());
        } catch (Throwable e) {
            throw new PluginException(String.format("Failed to remove an annotation on class, %s -> %s", cls, annotationClass), e);
        }
        return this;
    }

    @Override
    public ByteCode addAnnotationToClass(Class<?> cls, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        if (cls.isAnnotationPresent(annotationClass)) {
            return this;
        }
        try {
            final CtClass ctClass = pool.getCtClass(cls.getName());
            final ClassFile classFile = ctClass.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            AnnotationsAttribute attribute = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
            attribute.addAnnotation(new Annotation(annotationClass.getTypeName(), constPool));
        } catch (Throwable e) {
            throw new PluginException(String.format("Failed to add an annotation on class, %s -> %s", cls, annotationClass), e);
        }
        return this;
    }

    @Override
    public ByteCode addAnnotationToField(Class<?> cls,
                                         String fieldName, Class<? extends java.lang.annotation.Annotation> annotationClass,
                                         BiConsumer<Annotation, ConstPool> initAnnotation) {

        CtClass ctClass;
        try {
            ctClass = pool.getCtClass(cls.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            CtField ctField = ctClass.getDeclaredField(fieldName);
            ConstPool constPool = ctClass.getClassFile().getConstPool();

            Annotation annotation = new Annotation(annotationClass.getName(), constPool);
            if (initAnnotation != null) {
                initAnnotation.accept(annotation, constPool);
            }

            AnnotationsAttribute attr = getAnnotationsAttributeFromField(ctField);
            if (attr == null) {
                attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                ctField.getFieldInfo().addAttribute(attr);
            }
            attr.addAnnotation(annotation);

        } catch (NotFoundException e) {
            throw new PluginException(String.format("Failed to add an annotation on field, %s -> %s", cls, annotationClass), e);
        }
        return this;
    }

    @Override
    public ByteCode removeAnnotationFromField(Class<?> cls,
                                              String fieldName, Class<? extends java.lang.annotation.Annotation> annotationClass) {

        if (!cls.isAnnotationPresent(annotationClass)) {
            return this;
        }
        CtClass ctClass;
        try {
            ctClass = pool.getCtClass(cls.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            CtField ctField = ctClass.getDeclaredField(fieldName);

            AnnotationsAttribute attr = getAnnotationsAttributeFromField(ctField);
            if (attr != null) {
                attr.removeAnnotation(annotationClass.getName());
            }

        } catch (NotFoundException e) {
            throw new PluginException(String.format("Failed to remove an annotation on field, %s -> %s", cls, annotationClass), e);
        }
        return this;
    }

    @Override
    public void transformClass(Class<?> clazz, byte[] byteCode) {
        ClassFileTransformer cft = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                return byteCode;
            }
        };

        Instrumentation instrumentation = ByteBuddyAgent.install();
        try {
            instrumentation.addTransformer(cft, true);
            instrumentation.retransformClasses(clazz);
        } catch (Throwable e) {
            throw new PluginException(String.format("Class byte transform fail, %s -> %s", clazz, ArrayUtils.toString(byteCode)), e);
        } finally {
            instrumentation.removeTransformer(cft);
        }
    }

    @Override
    public void transformClass(Class<?> clazz) {
        try {
            transformClass(clazz, pool.getCtClass(clazz.getName()).toBytecode());
        } catch (IOException | CannotCompileException | NotFoundException e) {
            throw new PluginException(String.format("Class byte transform fail, %s", clazz), e);
        }
    }

    /**
     * 获取字段上的注解属性
     *
     * @param ctField 字段
     * @return 注解属性
     * @author afěi
     */
    private AnnotationsAttribute getAnnotationsAttributeFromField(CtField ctField) {
        List<AttributeInfo> attrs = ctField.getFieldInfo().getAttributes();
        AnnotationsAttribute attr = null;
        if (attrs != null) {
            Optional<AttributeInfo> optional = attrs.stream()
                    .filter(AnnotationsAttribute.class::isInstance)
                    .findFirst();
            if (optional.isPresent()) {
                attr = (AnnotationsAttribute) optional.get();
            }
        }
        return attr;
    }
}
