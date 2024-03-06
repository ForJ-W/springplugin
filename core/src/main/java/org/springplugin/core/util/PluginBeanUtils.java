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
import org.springframework.lang.NonNull;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.factory.SpringPluginFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 插件bean工具
 * <p>
 * 一般用于在插件中引用另一个插件的bean
 *
 * @author afěi
 * @version 1.0.0
 */
public class PluginBeanUtils {

    /**
     * 获取插件bean
     *
     * @param name           插件名称
     * @param interfaceClass 接口类
     * @param <T>            bean实例泛型
     * @return 插件bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(@NonNull String name, @NonNull Class<T> interfaceClass) {

        final Object bean;
        try {
            // 检查当前插件中是否存在该bean
            if (SpringIocUtils.hasBean(interfaceClass)) {
                return SpringIocUtils.mustGetBean(interfaceClass);
            }
            // 寻找指定插件中的bean
            AssertUtils.isTrue(interfaceClass.isInterface(), "Current class type must be interface");
            bean = SpringPluginFactory.getInstance()
                    .getContext(name)
                    .getBean(SpringPluginClassLoader.getInstance(name).loadClass(interfaceClass.getName()));
        } catch (ClassNotFoundException e) {
            throw new SpringPluginException(String.format("This class is not found in the plugin, %s -> %s", name, interfaceClass), e);
        }
        final Class<?>[] interfaces = ArrayUtils.addFirst(interfaceClass.getInterfaces(), interfaceClass);
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), interfaces, new PluginBeanInvocationHandler(bean));
    }


    /**
     * 插件bean调用处理器
     *
     * @author afěi
     * @version 1.0.0
     */
    static class PluginBeanInvocationHandler implements InvocationHandler {

        /**
         * 插件bean
         */
        private final Object pluginBean;

        /**
         * 构造方法
         *
         * @param pluginBean 插件bean
         */
        PluginBeanInvocationHandler(@NonNull Object pluginBean) {

            AssertUtils.isNotNull(pluginBean, new SpringPluginException("Plugin bean must not be null"));
            this.pluginBean = pluginBean;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 拿到代理方法的信息调用插件bean的方法
            return pluginBean.getClass().getMethod(method.getName(), method.getParameterTypes()).invoke(pluginBean, args);
        }
    }
}
