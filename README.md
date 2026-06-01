# Spring Plugin

English | [简体中文](README.zh-CN.md)

Spring Plugin is a dynamic plugin application framework for Spring Boot 3. It
lets a host application load, unload, route to, and isolate plugin applications
at runtime by using independent `ApplicationContext` instances, plugin-level
`ClassLoader` isolation, and Spring Web MVC request dispatching.

The repository includes the core runtime, a Spring Boot starter, a plugin
manager module, a runnable host server, and demo plugins for MVC, Flyway,
MyBatis Plus, app-level integration, and runner scenarios.

## Features

- Dynamically load plugin jars and create an isolated Spring application context
  for each plugin.
- Route HTTP requests to target plugins through the `app-meta` request header.
- Support plugin class loading isolation, bean registration, Spring MVC mapping,
  and OpenAPI integration.
- Provide `/pm/load` and `/pm/unload` plugin management endpoints.
- Include runnable demos that cover MVC, database migration, MyBatis Plus, and
  application plugin scenarios.

## Modules

| Module | Description |
| --- | --- |
| `core` | Core plugin context, class loading, scanning, auto-configuration filtering, and MVC dispatching. |
| `starter` | Spring Boot auto-configuration entry point for host applications. |
| `server` | Runnable plugin host service. |
| `manager` | Plugin management application that exposes load and unload endpoints. |
| `demo` | Demo plugin collection for local validation and implementation reference. |

## Requirements

- JDK 25+
- Maven 3.9+
- Spring Boot 3.5.x

## Quick Start

Build the project:

```bash
mvn -B clean package
```

Start the host server:

```bash
java -Dfile.encoding=UTF-8 -jar server/target/server.jar
```

Load the MyBatis Plus demo plugin:

```bash
curl -X POST \
  -F "file=@./demo/mybatisplus-demo/springplugin-mybatisplus-demo-server/target/mybatisplusdemo.jar" \
  http://localhost:8000/pm/load
```

Call plugin endpoints through the host server:

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

Unload the plugin:

```bash
curl -X POST "http://localhost:8000/pm/unload?name=mybatisplusdemo"
```

## Development

1. Start the `server` module.
2. Package a plugin jar from the `demo` modules.
3. Use Swagger/OpenAPI or curl to call `/pm/load` on the host server.
4. Call plugin endpoints with the `app-meta` request header.

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the project architecture.

## Open Source Maintenance

- Contribution guide: [CONTRIBUTING.md](CONTRIBUTING.md)
- Roadmap: [ROADMAP.md](ROADMAP.md)
- Security policy: [SECURITY.md](SECURITY.md)
- Codex for OSS application notes: [docs/CODEX_FOR_OSS_APPLICATION.md](docs/CODEX_FOR_OSS_APPLICATION.md)

## License

Spring Plugin is licensed under the Apache License 2.0. See [LICENSE](LICENSE).
