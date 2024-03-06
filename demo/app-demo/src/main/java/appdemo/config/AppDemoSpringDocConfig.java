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

package appdemo.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;


/**
 * openApi 自动配置
 *
 * @author afěi
 * @version 1.0.09
 */
@Configuration
public class AppDemoSpringDocConfig {

    @Value("${spring.application.name:app-demo}")
    private String appName;

    /**
     * openApi
     *
     * @param swaggerUiConfigParameters swagger ui配置参数
     * @author afěi
     */
    @Bean
    public OpenAPI openApi(SwaggerUiConfigParameters swaggerUiConfigParameters) {

        swaggerUiConfigParameters.addGroup(appName);
        final Info info = new Info();
        info.setTitle("App Demo API");
        return new OpenAPI(){

            @Override
            public Paths getPaths() {
                return null;
            }
        }.info(info);
    }

    /**
     * open api定制器
     * <p>
     * 默认添加授权认证头
     *
     * @author afěi
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {

        return openApi -> openApi.getPaths()
                .values()
                .stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> operation.addParametersItem(new HeaderParameter()
                        .name(HttpHeaders.AUTHORIZATION)
                        .required(true)
                        .in(ParameterIn.HEADER.toString())
                        .description("Token Authorization")));
    }
}
