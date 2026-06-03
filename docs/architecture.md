# 架构设计文档

## 系统架构

```
┌──────────────┐        ┌──────────────┐        ┌──────────┐
│   Frontend   │  HTTP  │   Backend    │  JDBC  │  MySQL   │
│  Vue 3 + EP  │ ──────▶│ Spring Boot  │ ──────▶│  8.0     │
│  Vite + TS   │ ◀──────│  3.5.9       │ ◀──────│          │
│  :5173       │        │  :8080       │        └──────────┘
└──────────────┘        └──────────────┘        ┌──────────┐
                                                 │  Redis   │
        ┌──────────────┐                         │  7       │
        │  Production  │  reverse  ┌──────────┐  └──────────┘
        │   Nginx      │ ────────▶ │ Backend  │
        │  :80/:443    │  proxy    │ (Docker) │
        └──────────────┘           └──────────┘
```

## 包结构约定

```
com.yiyixing/
├── entity/         # JPA 实体，对应数据库表
├── repository/     # Spring Data JPA 接口
├── service/        # 业务接口定义
│   └── impl/       # 业务实现
├── controller/     # REST 控制器
├── dto/            # 数据传输对象
│   ├── request/    # 请求 DTO
│   └── response/   # 响应 DTO
├── security/       # Spring Security 相关
├── config/         # Spring 配置类
├── exception/      # 异常处理
└── util/           # 工具类
```

**规则：**
- 每个 entity 对应一个 repository
- service 层必须有 interface + impl 分离
- controller 只做参数校验和转发，不含业务逻辑
- DTO 与 entity 分离，不直接暴露 entity 到 API

## 安全架构

### JWT 认证流程

```
客户端                        后端
  │                           │
  ├── POST /auth/register ───▶│ 创建用户 + BCrypt 加密密码
  │◀── 200 OK ────────────────┤
  │                           │
  ├── POST /auth/login ──────▶│ 验证密码
  │◀── { accessToken,         │ 生成 JWT（15分钟过期）
  │     refreshToken }        │ 生成 Refresh JWT（7天过期）
  │                           │
  ├── GET /users/me ─────────▶│ 解析 Authorization header
  │   Authorization: Bearer   │ JWT 过滤器验证签名+过期时间
  │   <token>                 │ 设置 SecurityContext
  │◀── UserResponse ──────────┤
  │                           │
  ├── Token expired (401) ───▶│ 前端拦截器捕获 401
  │◀── 跳转 /login ───────────┤
```

### 安全配置

- 密码: BCrypt 加密存储
- JWT: HS256 签名，密钥 256+ bits
- CORS: 仅允许开发/生产域名
- Session: STATELESS（无状态）
- 未端点: `/auth/**`, `/public/**`, `/error`

## 数据库设计

### 命名约定

- 表名: 复数，小写下划线（`users`, `question_tags`）
- 列名: 小写下划线（`user_id`, `created_at`）
- 主键: `id BIGINT AUTO_INCREMENT`
- 外键: `{关联表名}_id`
- 时间戳: `created_at`, `updated_at`

### 当前已创建

```
users (V1 migration)
├── id (PK)
├── username (UNIQUE)
├── email (UNIQUE)
├── password_hash
├── avatar_url
├── bio
├── reputation
├── role (ENUM)
├── status (ENUM)
├── created_at
└── updated_at
```

### 待创建

| 表名 | Phase | 关联 |
|------|-------|------|
| questions | Phase 2 | users(id) |
| answers | Phase 2 | questions(id), users(id) |
| tags | Phase 2 | — |
| question_tags | Phase 2 | questions(id), tags(id) |
| comments | Phase 2 | users(id), poly target |
| articles | Phase 3 | users(id) |
| article_tags | Phase 3 | articles(id), tags(id) |
| user_interactions | Phase 3 | users(id), poly target |
| follows | Phase 3 | users(id) × 2 |
| notifications | Phase 3 | users(id) |

## 前端架构

### 状态管理

```
Pinia stores:
├── auth.ts     # token, user, login/logout/register
├── user.ts     # 用户资料（Phase 3）
└── ui.ts       # UI 状态（Phase 4）
```

### API 层

```
api/
├── request.ts   # Axios 实例（唯一入口，JWT 拦截器）
├── auth.ts      # 认证相关 API
├── question.ts  # 问答 API（Phase 2）
├── article.ts   # 文章 API（Phase 3）
└── ...
```

### 路由守卫

- `guest: true` — 仅未登录可访问（登录/注册页）
- `requiresAuth: true` — 仅登录可访问（首页及业务页面）
- 默认 — 所有用户可访问

## 关键设计决策

| 决策 | 选择 | 理由 |
|------|------|------|
| Auth 方式 | JWT（无状态） | REST API 友好，前后端分离 |
| Token 存储 | localStorage + Pinia | 简单，XSS 通过 DOMPurify 控制 |
| 刷新令牌 | JWT（7天） | 无需 Redis 存储，签名验证即可 |
| 密码加密 | BCrypt | Spring Security 默认，行业标准 |
| DB 迁移 | Flyway | 版本化、可审计、自动执行 |
| Markdown | flexmark-java（服务端） | 渲染一致性，减轻前端负担 |
| 文件上传 | 腾讯云 COS（预签名 URL） | 不经过应用服务器 |
