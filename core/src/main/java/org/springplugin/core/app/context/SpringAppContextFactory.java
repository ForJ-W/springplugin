package org.springplugin.core.app.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springplugin.core.app.context.initializer.SpringAppContextInitializers;
import org.springplugin.core.classloader.AppClassLoader;
import org.springplugin.core.classloader.AppClassLoaderFactory;
import org.springplugin.core.exception.SpringPluginException;
import org.springplugin.core.info.AppInfo;
import org.springplugin.core.info.AppInfoFactory;
import org.springplugin.core.util.SpringIocUtils;

import java.util.*;

import static org.springplugin.core.app.context.AppContextFactory.Specification.DEFAULT_SPECIFICATION;

/**
 * spring插件应用上下文工厂
 *
 * @author afěi
 * @date 2024/7/10 14:05
 */
public class SpringAppContextFactory extends AbstractAppContextFactory<AnnotationConfigApplicationContext>
        implements AppContextFactory<AnnotationConfigApplicationContext>, DisposableBean, ApplicationContextAware {

    public final static String PROPERTY_SOURCE_NAME = "plugin";
    public final static String PROPERTY_NAME = "plugin.name";

    /**
     * 单例实例
     */
    private static volatile SpringAppContextFactory instance;

    private ApplicationContext parent;


    public static SpringAppContextFactory getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (SpringAppContextFactory.class) {
                if (Objects.isNull(instance)) {
                    return instance = SpringIocUtils.mustGetBean(SpringAppContextFactory.class);
                }
            }
        }
        return instance;
    }

    public ApplicationContext getParent() {
        return parent;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.parent = applicationContext;
    }

    @Override
    public void destroy() {
        Collection<AnnotationConfigApplicationContext> values = this.contexts.values();
        for (AnnotationConfigApplicationContext context : values) {
            context.close();
        }
        this.contexts.clear();
    }

    @Override
    protected AnnotationConfigApplicationContext doBuildContext(String name) {
        ClassLoader classLoader = getClass().getClassLoader();
        AnnotationConfigApplicationContext context;
        final ApplicationContext parent = this.parent;
        if (parent != null) {
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            beanFactory.setBeanClassLoader(classLoader);
            context = new AnnotationConfigApplicationContext(beanFactory);
        } else {
            context = new AnnotationConfigApplicationContext();
        }
        context.setClassLoader(classLoader);
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME,
                Collections.singletonMap(PROPERTY_NAME, name)));
        if (parent != null) {
            context.setParent(parent);
        }
        context.setDisplayName(this.getClass().getSimpleName() + "-" + name);
        registerBeans(name, context);
        return context;
    }

    @Override
    protected void createContext(String name, AnnotationConfigApplicationContext context) {
        final ConfigurableApplicationContext parent = (ConfigurableApplicationContext) this.parent;
        parent.getBean(SpringAppContextInitializers.class).getInitializers().forEach(initializer -> initializer.initialize(context));
        final AppClassLoader pcl = AppClassLoaderFactory.get(name);
        final SpringApplication app = new SpringApplication();
        app.setResourceLoader(new DefaultResourceLoader(pcl));
        app.setBannerMode(Banner.Mode.OFF);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.setApplicationContextFactory(new ApplicationContextFactory() {
            @Override
            public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
                return context;
            }

            @Override
            public ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
                return context.getEnvironment();
            }
        });
        final Class<?> mainClass;
        try {
            mainClass = AppInfoFactory.get(name).mainClass();
        } catch (ClassNotFoundException e) {
            throw new SpringPluginException(String.format("Plugin main class not found: %s", name), e);
        }
        app.addPrimarySources(Collections.singleton(mainClass));
        app.setMainApplicationClass(mainClass);
        app.run();
    }

    @Override
    public void destroyContext(String name) {
        Optional.ofNullable(this.contexts.remove(name)).ifPresent(AbstractApplicationContext::close);
    }

    @Override
    public boolean hasContext(String name) {
        return this.contexts.containsKey(name);
    }

    @Override
    public AppInfo getAppInfo(AnnotationConfigApplicationContext context) {
        final ConfigurableEnvironment environment = context.getEnvironment();
        return AppInfoFactory.get(environment.getProperty(PROPERTY_NAME));
    }

    @Override
    public <T> T getObject(String name, Class<T> type) {
        AnnotationConfigApplicationContext context = getContext(name);
        try {
            return context.getBean(type);
        } catch (NoSuchBeanDefinitionException e) {
            // ignore
        }
        return null;
    }

    private void registerBeans(String name, GenericApplicationContext context) {
        Assert.isInstanceOf(AnnotationConfigRegistry.class, context);
        AnnotationConfigRegistry registry = (AnnotationConfigRegistry) context;
        if (this.specifications.containsKey(name)) {
            for (Class<?> configuration : this.specifications.get(name).getConfigurations()) {
                registry.register(configuration);
            }
        }
        for (Map.Entry<String, Specification> entry : this.specifications.entrySet()) {
            if (entry.getKey().startsWith(DEFAULT_SPECIFICATION)) {
                for (Class<?> configuration : entry.getValue().getConfigurations()) {
                    registry.register(configuration);
                }
            }
        }
        registry.register(PropertyPlaceholderAutoConfiguration.class);
    }
}
