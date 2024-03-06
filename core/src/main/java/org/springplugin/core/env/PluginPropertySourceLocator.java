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

package org.springplugin.core.env;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.StringUtils;
import org.springplugin.core.env.properties.SpringPluginProperties;
import org.springplugin.core.util.ClassUtils;
import org.springplugin.core.util.CollectionUtils;
import org.springplugin.core.util.SpringConfigUtils;
import org.springplugin.core.util.SpringResourceUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 插件属性源定位器
 *
 * @author afěi
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class PluginPropertySourceLocator implements PropertySourceLocator {

    /**
     * 插件复合属性源名称
     */
    private static final String COMPOSITE_NAME = "PLUGIN";

    /**
     * 属性源加载器列表
     */
    private static final List<PropertySourceLoader> PROPERTY_SOURCE_LOADER_LIST = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class, ClassUtils.currentClassLoader());

    /**
     * spring 插件属性配置
     */
    private final SpringPluginProperties pluginProps;

    /**
     * 确定插件配置属性源
     *
     * @param name        插件名称
     * @param classLoader 类加载器
     * @author afěi
     */
    public static void locateConfigPropertySource(GenericApplicationContext context, String name, ClassLoader classLoader) {

        final SpringPluginProperties pluginProperties = SpringConfigUtils.acquireConfigurationPropertiesBean(SpringPluginProperties.PREFIX, SpringPluginProperties.class);
        final SpringPluginProperties.Config config = pluginProperties.getConfig();
        if (config.getEnable()) {
            final PluginPropertySourceLocator pluginPropertySourceLocator = new PluginPropertySourceLocator(pluginProperties);
            final String fileExtension = config.getFileExtension();
            final String configFileName = config.getPrefix() + name + "." + fileExtension;
            final Resource resource = new DefaultResourceLoader(classLoader).getResource(configFileName);
            final PropertySource<?> propertySource = pluginPropertySourceLocator.locate(name, resource, fileExtension);
            if (propertySource instanceof CompositePropertySource) {
                Collection<PropertySource<?>> sources = ((CompositePropertySource) propertySource).getPropertySources();
                List<PropertySource<?>> filteredSources = new ArrayList<>();
                for (PropertySource<?> p : sources) {
                    if (p != null) {
                        filteredSources.add(p);
                    }
                }
                if (CollectionUtils.isNotEmpty(filteredSources)) {
                    SpringConfigUtils.insertPropertySources(context.getEnvironment().getPropertySources(), filteredSources);
                }
            }
        }
    }

    @Override
    public PropertySource<?> locate(Environment environment) {

        final String pluginName = pluginProps.getName();
        final SpringPluginProperties.Config config = pluginProps.getConfig();
        final String prefix = config.getPrefix();
        final String pluginConfigFileExtension = config.getFileExtension();
        final CompositePropertySource composite = new CompositePropertySource(COMPOSITE_NAME);
        final String location = SpringResourceUtils.CLASSPATH_URL_PREFIX + prefix + SpringResourceUtils.MATCH_ALL_PATTERN + pluginConfigFileExtension;
        for (Resource resource : SpringResourceUtils.getResources(location)) {

            addComposite(pluginName, resource, pluginConfigFileExtension, composite);
        }

        return composite;
    }

    /**
     * 确定属性源
     *
     * @param pluginName 插件名称
     * @param resource   资源信息
     * @param extension  文件扩展名
     * @return 复合属性源
     * @author afěi
     */
    public PropertySource<?> locate(String pluginName, Resource resource, String extension) {

        final CompositePropertySource composite = new CompositePropertySource(COMPOSITE_NAME);
        addComposite(pluginName, resource, extension, composite);
        return composite;
    }

    /**
     * 添加到复合属性源
     *
     * @param pluginName 插件名称
     * @param resource   资源信息
     * @param extension  文件扩展名
     * @param composite  复合属性源
     * @author afěi
     */
    @SuppressWarnings("unchecked")
    private void addComposite(String pluginName, Resource resource, String extension, CompositePropertySource composite) {
        try {
            final List<PropertySource<?>> propertySourceList = loadPluginConfig(resource, extension);
            for (PropertySource<?> propertySource : propertySourceList) {
                if (propertySource != null && propertySource.getSource() instanceof Map) {

                    final Map<String, Object> source = (Map<String, Object>) propertySource.getSource();
                    addFirstPropertySource(composite, new PluginPropertySource(pluginName, source));
                }
            }
        } catch (Exception e) {
            log.info("Can not load plugin config: {}:{}, {}", pluginName, resource.getFilename(), e.getMessage());
        }
    }

    /**
     * 加载插件配置
     *
     * @param resource  资源信息
     * @param extension 配置文件后缀名
     * @return 属性配置列表
     * @throws IOException 属性配置加载器加载时可能出现的IO异常{@link  PropertySourceLoader#load(String, Resource)}
     * @author afěi
     */
    private List<PropertySource<?>> loadPluginConfig(Resource resource, String extension) throws IOException {

        for (PropertySourceLoader propertySourceLoader : PROPERTY_SOURCE_LOADER_LIST) {
            if (!canLoadFileExtension(propertySourceLoader, extension)) {
                continue;
            }

            List<PropertySource<?>> propertySourceList = propertySourceLoader.load(resource.getFilename(), resource);
            if (CollectionUtils.isEmpty(propertySourceList)) {
                return Collections.emptyList();
            }
            return propertySourceList.stream().filter(Objects::nonNull)
                    .map(propertySource -> {
                        if (propertySource instanceof EnumerablePropertySource) {
                            String[] propertyNames = ((EnumerablePropertySource<?>) propertySource)
                                    .getPropertyNames();
                            if (propertyNames.length > 0) {
                                Map<String, Object> map = new LinkedHashMap<>();
                                Arrays.stream(propertyNames).forEach(name -> map.put(name, propertySource.getProperty(name)));
                                return new OriginTrackedMapPropertySource(
                                        propertySource.getName(), map, true);
                            }
                        }
                        return propertySource;
                    }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 添加到复合属性源首位
     *
     * @param composite            复合属性源
     * @param pluginPropertySource 插件属性源
     * @author afěi
     */
    private void addFirstPropertySource(final CompositePropertySource composite,
                                        PluginPropertySource pluginPropertySource) {
        if (null == pluginPropertySource || null == composite) {
            return;
        }
        if (pluginPropertySource.getSource().isEmpty()) {
            return;
        }
        composite.addFirstPropertySource(pluginPropertySource);
    }

    /**
     * 判定当前加载器是否与配置文件匹配(扩展名判定)
     *
     * @param loader    属性配源加载器
     * @param extension 配置文件扩展名
     * @return 当前加载器是否与配置文件匹配
     * @author afěi
     */
    private boolean canLoadFileExtension(PropertySourceLoader loader, String extension) {
        return Arrays.stream(loader.getFileExtensions())
                .anyMatch((fileExtension) -> StringUtils.endsWithIgnoreCase(extension,
                        fileExtension));
    }
}
