# 开发指南

## 开发环境搭建

### 必需工具

| 工具 | 版本 | 用途 |
|------|------|------|
| JDK | 21 | 后端编译运行 |
| Maven | 3.9+ | 后端依赖管理 |
| Node.js | 20+ | 前端构建 |
| MySQL | 8.0 | 主数据库 |
| Redis | 7.x | 缓存/会话 |

### 安装 MySQL + Redis（无 Docker 方案）

```bash
# macOS
brew install mysql redis

# 启动 MySQL
brew services start mysql

# 启动 Redis
brew services start redis

# 创建数据库
mysql -u root -e "CREATE DATABASE yiyixing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 修改后端配置（application-dev.yml）
# 如果你的 MySQL 没有密码:
# spring.datasource.password=
```

### 有 Docker 方案

```bash
docker compose up -d
# 自动创建 yiyixing 数据库，密码为 root
```

## 启动项目

```bash
# 1. 确保数据库和 Redis 已运行
mysql -u root -e "SELECT 1"  # 验证 MySQL
redis-cli ping                # 验证 Redis → PONG

# 2. 启动后端（端口 8080）
cd backend
mvn spring-boot:run

# 3. 启动前端（端口 5173，自动代理 /api 到 8080）
cd frontend
npm run dev

# 4. 打开浏览器
open http://localhost:5173
```

## API 测试

```bash
# 注册
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"123456"}'

# 登录
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
# 返回: { "code": 200, "data": { "accessToken": "...", "refreshToken": "...", "tokenType": "Bearer", "expiresIn": 900000 } }

# 获取用户信息（替换 YOUR_TOKEN）
curl http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 代码规范

### 后端

- 使用 Lombok（@Getter, @Setter, @Builder）减少样板代码
- Service 层必须有 interface + impl
- Controller 不包含业务逻辑
- 异常通过 `@RestControllerAdvice` 统一处理
- 使用 `ApiResponse<T>` 统一响应格式
- 数据库变更必须通过 Flyway 迁移

### 前端

- 使用 Composition API + `<script setup>`
- 组件文件名: PascalCase（`LoginPage.vue`）
- API 调用必须通过 `api/` 层，不能直接在组件中使用 axios
- 状态管理使用 Pinia stores
- TypeScript 类型必须完整定义

## AI 编程宪法

本项目全程 AI 辅助开发，遵守以下规范：

### 编码原则

1. **先思考再编码** — 明确假设，不静默选择，不假设外部 API 行为
2. **最简方案优先** — 不做超出需求的功能，不做单次使用的抽象
3. **手术式改动** — 只动必须改的，不顺手"优化"相邻代码
4. **目标驱动** — 每个任务定义可验证的成功标准
5. **用测试验证** — 改 bug 先写失败测试，新功能先写契约测试

### 工具使用

| 工具 | 使用时机 |
|------|----------|
| `/context7-mcp` | 查框架/库/API 文档时 **必须** 使用 |
| `/frontend-design` | 构建前端页面/组件时使用 |
| `/ui-ux-pro-max` | 前端美化/样式调整时使用 |
| `/verify` | 改动完成后用于验证 |
| `/code-review` | 每阶段完成后用于审查 |
| `/simplify` | 发现过度设计时使用 |

### 开发流程

```
收到任务 → 查文档(/context7-mcp) → 设计方案 → 编码
    → 验证(/verify) → 审查(/code-review) → 精简(/simplify) → 完成
```

## 常用 Maven 命令

```bash
cd backend
mvn compile                      # 编译
mvn spring-boot:run              # 运行
mvn test                         # 测试
mvn clean package                # 打包
mvn dependency:tree              # 查看依赖树
```

## 常用 npm 命令

```bash
cd frontend
npm run dev                      # 开发服务器
npm run build                    # 生产构建
npm run lint                     # 代码检查
npx vue-tsc --noEmit             # TypeScript 类型检查
```
