# Contributing

感谢你对 Spring Plugin 的兴趣。这个项目的目标是让 Spring Boot 应用可以用更低成本加载、隔离和管理插件应用。

## 开发环境

- JDK 17+
- Maven 3.9+
- Git

## 本地检查

提交前请尽量运行：

```bash
mvn -B clean verify
```

如果只改文档，也请确认 Markdown 链接和命令示例仍然准确。

## 分支和提交

- `main` 保持可构建。
- 功能分支建议命名为 `feat/<short-name>`。
- 修复分支建议命名为 `fix/<short-name>`。
- 提交信息建议使用 Conventional Commits，例如：
  - `feat(core): support plugin context lifecycle`
  - `fix(manager): reject non-jar upload`
  - `docs(readme): add quick start`

## Pull Request 要求

PR 描述请包含：

- 变更目的。
- 影响的模块。
- 本地验证命令和结果。
- 是否影响插件加载、卸载、请求路由或类加载隔离。

## 代码风格

- 保持现有包结构和命名风格。
- 新增核心行为时优先补充测试或 demo 验证路径。
- 不在无关 PR 中做大规模格式化。
- 公共 API 变更需要同步更新 README 或 `docs/`。

## 问题反馈

提交 issue 时请尽量提供：

- Spring Plugin 版本或 commit。
- JDK、Maven、Spring Boot 版本。
- 最小复现步骤。
- 相关日志和异常堆栈。
