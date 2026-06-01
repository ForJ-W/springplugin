# Spring Plugin

Spring Plugin 是一个面向 Spring Boot 3 的动态插件应用框架。它通过独立的
`ApplicationContext`、插件级 `ClassLoader` 和 Web MVC 转发能力，让主应用在
运行时加载、卸载和隔离插件应用。

项目当前包含核心运行时、自动装配 starter、插件管理模块、可运行 server，以及
MVC、Flyway、MyBatis Plus 等示例插件。

## 特性

- 动态加载插件 jar，并为每个插件创建独立的 Spring 应用上下文。
- 使用 `app-meta` 请求头把 HTTP 请求路由到目标插件。
- 支持插件类加载隔离、bean 注册、Spring MVC 映射和 OpenAPI 集成。
- 提供 `/pm/load` 和 `/pm/unload` 插件管理接口。
- 提供多个 demo，覆盖基础 MVC、数据库迁移和 MyBatis Plus 场景。

## 模块

| 模块 | 说明 |
| --- | --- |
| `core` | 插件上下文、类加载、扫描、自动装配过滤、MVC 分发等核心能力。 |
| `starter` | 面向宿主应用的 Spring Boot 自动装配入口。 |
| `server` | 可直接运行的插件宿主服务。 |
| `manager` | 插件管理应用，提供加载和卸载接口。 |
| `demo` | 示例插件集合，用于本地验证和使用参考。 |

## 环境要求

- JDK 25+
- Maven 3.9+
- Spring Boot 3.5.x

## 快速开始

构建项目：

```bash
mvn -B clean package
```

启动宿主服务：

```bash
java -Dfile.encoding=UTF-8 -jar server/target/server.jar
```

加载 MyBatis Plus 示例插件：

```bash
curl -X POST \
  -F "file=@./demo/mybatisplus-demo/springplugin-mybatisplus-demo-server/target/mybatisplusdemo.jar" \
  http://localhost:8000/pm/load
```

调用插件接口：

```bash
curl -X POST \
  -H "app-meta: mybatisplusdemo" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"xin-yizi\",\"code\":\"xyz\"}" \
  http://localhost:8000/mybatisplusdemo/save

curl -X GET \
  -H "app-meta: mybatisplusdemo" \
  http://localhost:8000/mybatisplusdemo/list
```

卸载插件：

```bash
curl -X POST "http://localhost:8000/pm/unload?name=mybatisplusdemo"
```

## 开发调试

1. 启动 `server` 模块。
2. 打包 `demo` 中的插件 jar。
3. 访问宿主服务的 Swagger/OpenAPI 页面或使用 curl 调用 `/pm/load`。
4. 通过 `app-meta` 请求头调用插件接口。

更多结构说明见 [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)。

## 开源维护

- 贡献指南：[CONTRIBUTING.md](CONTRIBUTING.md)
- 路线图：[ROADMAP.md](ROADMAP.md)
- 安全问题：[SECURITY.md](SECURITY.md)
- Codex for OSS 申请材料草稿：[docs/CODEX_FOR_OSS_APPLICATION.md](docs/CODEX_FOR_OSS_APPLICATION.md)

## 许可证

Apache License 2.0。详见 [LICENSE](LICENSE)。
