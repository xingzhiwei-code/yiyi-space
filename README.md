# 一亩三分学田 - 技术交流社区

> Java + Spring Boot 技术交流论坛（问答 + 文章 + 社区）

## 技术栈

- **后端**: Spring Boot 3.x + Spring Security + JPA + Flyway
- **前端**: Vue 3 + Vite + TypeScript + Element Plus + Pinia
- **数据库**: MySQL 8.0 + Redis 7
- **部署**: Docker Compose + Nginx

## 快速开始

### 1. 启动基础设施

```bash
docker compose up -d
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

## 项目结构

```
├── backend/          # Spring Boot 后端
├── frontend/         # Vue 3 前端
├── database/         # Flyway 数据库迁移
└── docker-compose.yml
```

## API 文档

Base URL: `http://localhost:8080/api/v1`

| 模块 | 路径 |
|------|------|
| 认证 | `/auth/register`, `/auth/login` |
| 用户 | `/users/me` |
| 问答 | `/questions` |
| 文章 | `/articles` |
| 标签 | `/tags` |
| 评论 | `/comments` |
| 互动 | `/interactions` |
| 通知 | `/notifications` |
| 搜索 | `/search` |

## AI 编程宪法

本项目全程 AI 辅助开发，遵守以下规范：
- 先思考再编码
- 最简方案优先
- 手术式改动
- 目标驱动执行
- 用测试验证

详细规范见计划文件。
