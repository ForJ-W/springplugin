# Architecture

Spring Plugin 由宿主服务、插件管理器、核心运行时和示例插件组成。

## 核心流程

1. 宿主应用启动 `server`。
2. `starter` 自动装配 `AppServerContext`、`SpringAppContextFactory` 和插件初始化器。
3. `manager` 提供 `/pm/load` 接口接收插件 jar。
4. 插件 jar 解压到运行目录后，`core` 使用 `SpringAppClassLoader` 加载插件类。
5. `SpringAppContextFactory` 为插件创建独立的 Spring `ApplicationContext`。
6. `SpringPluginDispatcherServlet` 根据 `app-meta` 请求头把请求分发到对应插件上下文。
7. `/pm/unload` 触发插件上下文清理和卸载。

## 运行时边界

- 宿主服务负责进程生命周期、端口监听和基础自动装配。
- 插件拥有独立应用上下文，避免直接污染宿主 bean。
- 插件类加载由 `SpringAppClassLoader` 处理，用于隔离插件自身依赖。
- Web 请求通过统一入口转发，插件通过 `app-meta` 标识被路由。

## 主要模块

### `core`

核心运行时，包含：

- `app.context`：插件应用上下文创建和清理。
- `classloader`：插件类加载器。
- `scan`：插件组件扫描。
- `mvc`：插件 Web MVC 分发和 provider 集成。
- `server.context`：宿主侧插件加载、卸载和上下文管理。

### `starter`

Spring Boot 自动装配模块。宿主应用引入后会注册插件工厂、服务上下文和 MVC/OpenAPI 相关配置。

### `server`

可运行宿主服务。启动后初始化插件目录，并尝试加载内置 manager 插件和已有插件目录。

### `manager`

插件管理模块。当前提供：

- `POST /pm/load`：上传并加载插件 jar。
- `POST /pm/unload?name=<plugin>`：卸载插件。

### `demo`

示例插件集合。当前包含基础 MVC、Flyway、MyBatis Plus 和 runner 示例，用于验证插件加载和请求路由。

## 需要重点维护的风险点

- 动态 jar 加载需要持续增加校验和访问控制。
- 插件卸载后的资源释放需要集成测试覆盖。
- 插件依赖冲突需要更清晰的诊断信息。
- Web 请求路由头需要避免被未授权调用方滥用。
