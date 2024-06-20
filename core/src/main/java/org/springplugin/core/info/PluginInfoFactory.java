package org.springplugin.core.info;

import org.springplugin.core.exception.PluginException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件信息工厂
 *
 * @author afěi
 * @version 1.0.0
 */
public class PluginInfoFactory {

    private static final PluginInfoFactory INSTANCE = new PluginInfoFactory();

    private final Map<String, PluginInfo> infoMap = new ConcurrentHashMap<>();

    /**
     * 获取插件信息
     *
     * @param name 插件名
     * @return 插件信息
     */
    public static PluginInfo get(String name) {
        final Map<String, PluginInfo> infoMap = INSTANCE.infoMap;
        return Optional.ofNullable(infoMap.get(name)).orElseThrow(() -> new PluginException(String.format("Can not find plugin '%s'", name)));
    }

    /**
     * 设置插件信息
     *
     * @param name 插件名
     * @param info 插件信息
     */
    public static void set(String name, PluginInfo info) {
        final Map<String, PluginInfo> infoMap = INSTANCE.infoMap;
        infoMap.put(name, info);
    }
}
