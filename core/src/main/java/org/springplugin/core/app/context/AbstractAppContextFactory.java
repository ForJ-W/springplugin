package org.springplugin.core.app.context;

import org.springplugin.core.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.springplugin.core.app.context.AppContextFactory.Specification.DEFAULT_SPECIFICATION;

/**
 * 抽象的插件应用上下文工厂
 *
 * @author afěi
 * @date 2024/7/10 14:07
 */
public abstract class AbstractAppContextFactory<C> implements AppContextFactory<C> {

    protected final Map<String, C> contexts = new ConcurrentHashMap<>();
    protected final Map<String, Specification> specifications = new ConcurrentHashMap<>();

    public Set<String> getContextNames() {
        return this.contexts.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <F extends AppContextFactory<C>> F specifications(Specification... specifications) {
        for (Specification specification : specifications) {
            final String name = specification.getName();
            this.specifications.put(StringUtils.isBlank(name) ? DEFAULT_SPECIFICATION : name, specification);
        }
        return (F) this;
    }

    @Override
    public void initContext(String name) {
        if (hasContext(name)) {
            return;
        }
        createContext(name, buildContext(name));
    }

    @Override
    public C getContext(String name) {
        return this.contexts.get(name);
    }


    /**
     * 构建插件应用上下文
     *
     * @param name 插件名
     * @return 插件应用上下文
     */
    protected C buildContext(String name) {
        if (!this.contexts.containsKey(name)) {
            synchronized (this.contexts) {
                if (!this.contexts.containsKey(name)) {
                    this.contexts.put(name, doBuildContext(name));
                }
            }
        }
        return this.contexts.get(name);
    }

    /**
     * 构建插件应用上下文
     *
     * @param name 插件名
     * @return 插件应用上下文
     */
    protected abstract C doBuildContext(String name);

    /**
     * 创建插件应用上下文
     *
     * @param name 插件名
     * @param c 插件应用上下文
     * @author afěi
     */
    protected abstract void createContext(String name, C c);
}
