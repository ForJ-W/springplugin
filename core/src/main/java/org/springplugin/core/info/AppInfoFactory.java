package org.springplugin.core.info;

import org.springplugin.core.exception.PluginException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件应用信息工厂
 *
 * @author afěi
 * @version 1.0.0
 */
public class AppInfoFactory {

    private static final AppInfoFactory INSTANCE = new AppInfoFactory();

    private final Map<String, AppInfo> infoMap = new ConcurrentHashMap<>();

    /**
     * 获取插件应用信息
     *
     * @param name 插件名
     * @return 插件应用信息
     * @author afěi
     */
    public static AppInfo get(String name) {
        final Map<String, AppInfo> infoMap = INSTANCE.infoMap;
        return Optional.ofNullable(infoMap.get(name)).orElseThrow(() -> new PluginException(String.format("Can not find plugin '%s'", name)));
    }

    /**
     * 设置插件应用信息
     *
     * @param name 插件名
     * @param info 插件应用信息
     * @author afěi
     */
    public static void set(String name, AppInfo info) {
        final Map<String, AppInfo> infoMap = INSTANCE.infoMap;
        infoMap.put(name, info);
    }

    /**
     * 设置插件应用信息
     *
     * @param info 插件应用信息
     * @author afěi
     */
    public static void set(AppInfo info) {
        set(info.name(), info);
    }
}
