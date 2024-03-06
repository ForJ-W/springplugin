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

package flywaydemo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * 数据库初始化后置处理器
 *
 * @author afěi
 * @version 1.0.0
 */
@Component
public class FlywayDemoDataSourceInitializationPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware {

    ConfigurableEnvironment environment;

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

        // 获取sql初始化属性配置类
        String databaseKey = "server.mysql.database";
        final PropertySourcesPlaceholdersResolver propertySourcesPlaceholdersResolver = new PropertySourcesPlaceholdersResolver(environment);
        final SqlInitializationProperties initializationProperties = new Binder(ConfigurationPropertySources.from(environment.getPropertySources()), propertySourcesPlaceholdersResolver)
                .bind("spring.sql.init", SqlInitializationProperties.class).get();
        final DynamicDataSourceProperties dynamicDataSourceProperties = new Binder(ConfigurationPropertySources.from(environment.getPropertySources()), propertySourcesPlaceholdersResolver)
                .bind(DynamicDataSourceProperties.PREFIX, DynamicDataSourceProperties.class).get();
        final String database = environment.getProperty(databaseKey, String.class);
        Assert.isTrue(StringUtils.isNotBlank(database), databaseKey + " 获取失败");
        final DataSourceProperty dataSourceProperty = dynamicDataSourceProperties.getDatasource().get(database);
        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setName("spring-sql-init");
        dataSource.setUsername(initializationProperties.getUsername());
        dataSource.setPassword(initializationProperties.getPassword());
        dataSource.setUrl(dataSourceProperty.getUrl().replace(database, "sys"));
        dataSource.setDriverClassName(dataSourceProperty.getDriverClassName());
        new DataSourceScriptDatabaseInitializer(dataSource, SqlDataSourceScriptDatabaseInitializer.getSettings(initializationProperties))
                .initializeDatabase();
        dataSource.close();
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}
