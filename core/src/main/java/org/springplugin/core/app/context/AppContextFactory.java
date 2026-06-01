package org.springplugin.core.app.context;

import org.springplugin.core.info.AppInfo;

/**
 * 插件应用上下文工厂
 *
 * @author afěi
 * @date 2024/7/10 13:55
 */
public interface AppContextFactory<C> {

    /**
     * 初始化插件应用上下文
     *
     * @param name 插件名
     * @author afěi
     */
    void initContext(String name);

    /**
     * 获取插件应用上下文
     *
     * @param name 插件名
     * @return 插件应用上下文
     * @author afěi
     */
    C getContext(String name);

    /**
     * 销毁插件应用上下文
     *
     * @param name 插件名
     * @author afěi
     */
    void destroyContext(String name);

    /**
     * 获取插件应用信息
     *
     * @param context 插件应用上下文
     * @return 插件应用信息
     * @author afěi
     */
    AppInfo getAppInfo(C context);

    /**
     * 判断插件应用上下文是否存在
     *
     * @param name 插件名
     * @return 插件应用上下文是否存在
     * @author afěi
     */
    boolean hasContext(String name);

    /**
     * 新增插件应用上下文规范
     *
     * @param specifications 插件应用上下文规范
     * @return 插件应用上下文
     */
    <F extends AppContextFactory<C>> F specifications(Specification... specifications);

    /**
     * 获取插件应用上下文中的插件对象
     *
     * @param name 插件名
     * @param type 插件对象类型
     * @return 插件应用上下文中的插件对象
     * @param <T> 插件对象泛型
     * @author afěi
     */
    <T> T getObject(String name, Class<T> type);

    /**
     * 插件应用上下文规范
     *
     * @author afěi
     * @date 2024/7/10 13:55
     */
    interface Specification {

        String DEFAULT_SPECIFICATION = "default.";


        /**
         * 获取插件规范名
         * @return 插件规范名
         * @author afěi
         */
        String getName();

        /**
         * 获取插件配置类
         *
         * @return 插件配置类
         * @author afěi
         */
        Class<?>[] getConfigurations();
    }
}
