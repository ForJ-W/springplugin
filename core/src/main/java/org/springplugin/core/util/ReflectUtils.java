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

package org.springplugin.core.util;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.ReflectionUtils;
import org.springplugin.core.exception.PluginException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;


/**
 * 反射帮助
 * <p>
 * <p>
 * +{@link #isFinalType(Class)}
 * +{@link #isWrapType(Class)}
 * +{@link #isCommonType(Class)}
 * +{@link #isString(Class)}
 *
 * @author afěi
 * @version 1.0.0
 */
public abstract class ReflectUtils extends ReflectionUtils {


    /**
     * 获取完整泛型类型的类名
     *
     * @param cls class对象
     * @return 完整泛型类型的类名
     * @author afěi
     */
    public static String acquireIntegratedGenericTypeClassName(Class<?> cls) {

        final String typeName = cls.getTypeName();
        final Type[] genericTypeArr = acquireGenericTypeArr(cls);
        if (ArrayUtils.isEmpty(genericTypeArr)) {
            return typeName;
        }

        final StringBuilder builder = new StringBuilder(typeName);
        builder.append("<");
        for (int i = 0, genericTypeArrLength = genericTypeArr.length; i < genericTypeArrLength; i++) {

            Type type = genericTypeArr[i];
            builder.append(type.getTypeName());
            if (i < genericTypeArrLength - 1) {

                builder.append(",");
            }
        }
        builder.append(">");
        return builder.toString();
    }

    /**
     * 判断是否是基础数据类型的包装类型
     *
     * @param clz class对象
     * @return 是否是包装类型
     * @author afěi
     */
    public static boolean isWrapType(Class<?> clz) {
        try {
            return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否是基础数据类型，即 int,double,long等类似格式
     *
     * @author afěi
     */
    public static boolean isCommonType(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    /**
     * 获取调用者的方法栈信息
     * <p>
     * {@link StackTraceElement}
     *
     * @return 栈链路
     * @author afěi
     */
    public static StackTraceElement acquireCallerMethodStackTrace() {

        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        int callerMethodStackTraceIndex = 1;
        if (stackTrace.length > callerMethodStackTraceIndex) {

            return stackTrace[callerMethodStackTraceIndex];
        }


        throw new PluginException("Can't acquire caller");
    }


    /**
     * 递归接口获取存在指定注解的类对象
     *
     * @param annotation     注解class对象
     * @param interfaceClass 接口class对象
     * @return 存在注解的class对象
     * @author afěi
     */
    private static Class<?> judgeInterface(Class<? extends Annotation> annotation, Class<?> interfaceClass) {

        for (Class<?> clsI : interfaceClass.getInterfaces()) {
            if (clsI.isAnnotationPresent(annotation)) {
                return clsI;
            }
            return judgeInterface(annotation, clsI);
        }
        return null;
    }

    /**
     * 获取方法参数名
     *
     * @param method 方法对象
     * @return 参数信息
     * @author afěi
     */
    public static String acquireMethodParameterName(Method method) {

        Parameter[] parameters = method.getParameters();
        String parameterTypeNameListStr = Arrays.stream(parameters)
                .map(Parameter::getType)
                .map(Class::getName)
                .toList()
                .toString();
        return parameterTypeNameListStr.substring(1, parameterTypeNameListStr.length() - 1);
    }

    /**
     * 获取main方法所在的类对象
     *
     * @return class对象
     * @author afěi
     */
    public static Class<?> acquireMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ignored) {
        }
        throw new PluginException("Can't acquire main");
    }

    /**
     * 获取类的所有泛型类型
     *
     * @param cls class对象
     * @return 泛型类型
     * @author afěi
     */
    public static Type[] acquireGenericTypeArr(Class<?> cls) {

        Type genericReturnType = cls.getGenericSuperclass();
        if (Objects.isNull(genericReturnType)) {

            final Type[] genericInterfaces = cls.getGenericInterfaces();
            // 只取第一个接口的泛型
            if (ArrayUtils.isNotEmpty(genericInterfaces)) {
                genericReturnType = genericInterfaces[0];
            }
        }
        Type[] actualTypeArguments = null;
        if (genericReturnType instanceof ParameterizedType) {
            actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
        }
        return actualTypeArguments;
    }

    /**
     * 获取类的某个泛型类型
     *
     * @param cls          class对象
     * @param genericIndex 泛型索引
     * @return 泛型类型
     * @author afěi
     */
    public static Type acquireGenericType(Class<?> cls, int genericIndex) {


        Type[] acquireGenericTypeArr = acquireGenericTypeArr(cls);
        Type actualTypeArgument = null;
        if (acquireGenericTypeArr.length > 0) {
            actualTypeArgument = acquireGenericTypeArr[genericIndex];
        }
        return actualTypeArgument;
    }

    /**
     * 获取方法返回值的某个泛型类型
     *
     * @param currentMethod 当前方法
     * @param genericIndex  泛型索引
     * @return 泛型类型
     * @author afěi
     */
    public static Type acquireGenericType(Method currentMethod, int genericIndex) {

        Type[] acquireGenericTypeArr = acquireGenericTypeArr(currentMethod);
        Type actualTypeArgument = null;
        if (acquireGenericTypeArr.length > 0) {
            actualTypeArgument = acquireGenericTypeArr[genericIndex];
        }
        return actualTypeArgument;
    }

    /**
     * 获取方法返回值的所有泛型类型
     *
     * @param currentMethod 当前方法
     * @return 泛型类型
     * @author afěi
     */
    public static Type[] acquireGenericTypeArr(Method currentMethod) {

        Type genericReturnType = currentMethod.getGenericReturnType();
        Type[] actualTypeArguments = null;
        if (genericReturnType instanceof ParameterizedType) {
            actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
        }
        return actualTypeArguments;
    }

    /**
     * 是否是最终类型
     *
     * @param beanClass class对象
     * @return 是否是最终类型
     * @author afěi
     */
    private boolean isFinalType(Class<?> beanClass) {

        return isCommonType(beanClass) || isWrapType(beanClass) || isString(beanClass);
    }

    /**
     * 是否是{@link String}
     *
     * @param cls class对象
     * @return 是否是{@link String}
     * @author afěi
     */
    public boolean isString(Class<?> cls) {

        return String.class.equals(cls);
    }
}
