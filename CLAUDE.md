# 一亩三分学田 — 技术交流社区

> Java + Spring Boot + Vue 3 全栈项目，AI 辅助开发。

## 快速导航

- [PRD 需求文档](docs/PRD.md) — 完整功能规划、API 设计、阶段拆分
- [架构设计](docs/architecture.md) — 技术栈、目录结构、数据库 schema
- [开发指南](docs/development.md) — 开发环境搭建、常用命令、AI 编程宪法
- [部署规划](docs/deployment.md) — 腾讯云部署方案、COS、域名备案

## 技术栈

| 层 | 技术 |
|----|------|
| **后端** | Java 21 + Spring Boot 3.5.9 + Spring Security + JPA + Flyway |
| **前端** | Vue 3 + Vite + TypeScript + Element Plus + Pinia |
| **数据库** | MySQL 8.0 + Redis 7 |
| **部署** | Docker Compose + Nginx → 腾讯云 CVM + COS + 备案域名 |

## 项目结构

```
yiyixing-space/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/yiyixing/
│   │   ├── YiyixingApplication.java  # 启动类
│   │   ├── entity/                   # JPA 实体
│   │   ├── repository/               # 数据访问
│   │   ├── service/ + impl/          # 业务逻辑
│   │   ├── controller/               # REST API
│   │   ├── dto/{request,response}/   # DTO
│   │   ├── security/                 # JWT 过滤器
│   │   ├── config/                   # Spring 配置
│   │   ├── exception/                # 全局异常
│   │   └── util/                     # 工具类（JWT）
│   └── src/main/resources/
│       ├── application.yml           # 主配置
│       └── db/migration/             # Flyway 迁移
├── frontend/                         # Vue 3 前端
│   ├── src/
│   │   ├── api/                      # Axios API 层（request.ts + auth.ts）
│   │   ├── stores/                   # Pinia（auth.ts）
│   │   ├── router/                   # Vue Router
│   │   ├── types/                    # TypeScript 类型
│   │   ├── views/                    # 页面组件
│   │   └── components/               # 可复用组件
│   └── vite.config.ts                # 含 API 代理
├── database/migrations/              # SQL 迁移备份
├── docker-compose.yml                # MySQL + Redis
└── docs/                             # 项目文档
```

## 开发进度

### ✅ Phase 1: 基础框架 + 认证系统（已完成）

- [x] 项目骨架（Spring Boot + Vue 3）
- [x] 用户注册/登录/获取用户信息
- [x] JWT 认证 + Spring Security 过滤器
- [x] 全局异常处理 + 统一响应格式
- [x] 前端登录/注册/首页 + 路由守卫
- [x] 后端编译通过 + 前端 TypeScript + Vite 构建通过
- [ ] 本地数据库运行 + 全链路 E2E 验证

### 📋 Phase 2: 核心问答功能（未开始）

提问、回答、标签、评论、Markdown 渲染

### 📋 Phase 3: 文章 + 互动 + 通知（未开始）

技术文章、点赞收藏、用户关注、通知系统

### 📋 Phase 4: 搜索 + 优化（未开始）

全文搜索、文件上传、UI 美化、响应式

### 📋 Phase 5: 部署上线（未开始）

Docker 部署 + Nginx + 腾讯云 COS + 域名备案

## 常用命令

```bash
# 后端
cd backend && mvn spring-boot:run          # 启动
cd backend && mvn compile                   # 编译验证
cd backend && mvn test                      # 运行测试

# 前端
cd frontend && npm run dev                  # 开发服务器
cd frontend && npm run build                # 构建

# 数据库（有 Docker 时）
docker compose up -d                        # 启动 MySQL + Redis
docker compose down                         # 停止

# API 测试
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"123456"}'

curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

## API 规范

- Base URL: `/api/v1`
- 统一响应: `{ "code": 200, "message": "success", "data": {...}, "timestamp": "..." }`
- 认证: `Authorization: Bearer <jwt>`
- 公开端点: `/api/v1/auth/**`, `/api/v1/public/**`, `/error`

## AI 编程宪法

所有开发必须遵守：
1. **先思考再编码** — 明确假设，不静默选择
2. **最简方案优先** — 不做超出需求的功能
3. **手术式改动** — 只动必须改的
4. **目标驱动** — 每个任务定义可验证的成功标准
5. **用测试验证** — 改 bug 先写失败测试

工具使用规则：
- `/context7-mcp` — 查文档时必用
- `/frontend-design` — 构建前端页面时必用
- `/ui-ux-pro-max` — 前端美化时使用
- `/verify` — 改动完成后用于验证
- `/code-review` — 每阶段完成后用于审查

## 关键文件速查

| 文件 | 作用 |
|------|------|
| `backend/pom.xml` | 后端依赖清单 |
| `backend/.../SecurityConfig.java` | 安全过滤器链，JWT 集成 |
| `backend/.../util/JwtUtil.java` | JWT 生成/验证工具 |
| `backend/.../impl/UserServiceImpl.java` | 认证核心逻辑 |
| `backend/.../resources/db/migration/` | Flyway 数据库迁移 |
| `frontend/src/api/request.ts` | Axios 唯一入口 + JWT 拦截器 |
| `frontend/src/stores/auth.ts` | 认证状态管理 |
| `frontend/vite.config.ts` | Vite 配置 + API 代理 |
| `docker-compose.yml` | 基础设施定义 |
| `docs/PRD.md` | 完整产品需求文档 |

## 部署环境

- **云服务商**: 腾讯云 CVM
- **对象存储**: 腾讯云 COS（头像、附件上传）
- **域名**: 需备案（中国大陆服务器强制要求）
- **当前状态**: 规划中，未开始部署配置
