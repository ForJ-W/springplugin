# Codex for OSS Application Draft

本文档用于准备 Codex for OSS 申请。提交前请用 GitHub 仓库真实数据替换统计项，并补充维护者联系方式。

## Project URL

https://github.com/ForJ-W/springplugin

## Project summary

Spring Plugin is a Spring Boot 3 plugin runtime that dynamically loads plugin jar files into isolated Spring `ApplicationContext` instances. It provides plugin class loading, Spring MVC request routing, Spring Boot auto-configuration, management endpoints, and runnable demo plugins.

## Why this project matters

Spring applications often need modular extension points, but many teams either rebuild a custom plugin loader or tightly couple optional business modules into one application. Spring Plugin explores a Spring-native runtime model where plugins can be packaged, loaded, routed, and unloaded while preserving familiar Spring Boot development practices.

The project is especially useful for developers building extensible Java platforms, admin systems, internal PaaS tools, or modular business applications that need Spring Boot plugin isolation.

## Repository readiness

- License: Apache License 2.0.
- Primary branch: `main`.
- Runtime: Java 25, Maven, Spring Boot 3.5.x.
- Main modules: `core`, `starter`, `server`, `manager`, `demo`.
- Public maintenance files: README, CONTRIBUTING, SECURITY, ROADMAP, issue templates, PR template, CI workflow.

## How Codex would help maintain the project

Codex credits would be used for maintenance work that is valuable but time-intensive:

- Add tests for class loading, plugin metadata, plugin lifecycle, and MVC routing.
- Improve integration coverage by automatically building demo plugins and loading them into the server.
- Review dynamic jar loading security boundaries and propose safer defaults.
- Refactor complex runtime code while preserving Spring Boot 3 compatibility.
- Keep documentation, examples, and migration notes in sync with code changes.
- Triage issues and convert reproductions into regression tests.

## Current gaps and planned work

The next maintenance milestones are:

1. Add baseline unit tests for `core`.
2. Add an integration test that packages a demo plugin and verifies `/pm/load`.
3. Harden manager upload validation and error responses.
4. Document a minimal custom plugin example.
5. Publish a stable release artifact.

## Suggested form answers

### What does the project do?

Spring Plugin provides a dynamic plugin runtime for Spring Boot 3. It lets a host application load plugin jar files, create isolated Spring application contexts, route HTTP requests to plugins using an `app-meta` header, and unload plugins through management endpoints.

### Who uses or benefits from it?

Java and Spring Boot developers building extensible platforms, modular business systems, internal tools, and applications that need plugin-style deployment without giving up Spring Boot programming models.

### What would Codex credits be used for?

Credits would support test coverage, security review, integration testing, documentation, example plugins, and issue triage. The highest-impact work is turning the current runtime and demos into a better-tested OSS project with safer plugin loading defaults.

### Why is the project a good fit for OSS support?

The project is Apache-2.0 licensed, public, and focused on a reusable infrastructure problem in the Spring ecosystem. Codex can help accelerate the maintenance tasks that make the project safer and easier for external contributors to adopt.

## Data to fill before submission

- GitHub stars:
- Forks:
- Open issues:
- Recent release or commit date:
- Maintainer GitHub handle:
- Maintainer email or preferred contact:
- Notable users or downstream projects:
