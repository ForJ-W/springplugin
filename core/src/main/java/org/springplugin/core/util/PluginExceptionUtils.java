package org.springplugin.core.util;

import org.springplugin.core.exception.PluginException;

/**
 * 插件异常工具
 *
 * @author wujing
 * @version 1.0.0
 */
public abstract class PluginExceptionUtils {

    /**
     * 没找到插件
     *
     * @param name 插件名
     * @return 插件异常
     */
    public static PluginException canNotFindPlugin(String name) {
        return new PluginException(String.format("Can not find plugin '%s'", name));
    }

    /**
     * 插件名不能空
     *
     * @return 插件异常
     */
    public static PluginException pluginNameNotBlank() {
        return new PluginException("The plugin name can't be blank");
    }

    /**
     * 没找到插件类
     *
     * @return 插件异常
     */
    public static PluginException canNotFindPluginClass(String name) {
        return new PluginException("Can not find plugin class '%s'", name);
    }
}
