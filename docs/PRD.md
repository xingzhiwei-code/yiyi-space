# 产品需求文档 (PRD)

> 一亩三分学田 — 技术交流社区
> 创建日期: 2026-06-03
> 版本: v1.0

## 1. 项目背景

### 1.1 为什么做

想要一个属于自己的技术学习交流社区，类似 Stack Overflow + 技术博客的结合体。参考了 [acres-of-code](https://gitee.com/li_shuokang/acres-of-code) 和 [编程导航](https://www.codefather.cn/) 的模式，但定位更聚焦于**开发者问答和技术分享**。

### 1.2 目标用户

- 开发者（初学者到进阶）
- 技术分享者
- 寻找技术问题答案的程序员

### 1.3 项目定位

**技术交流论坛** — 以问答和技术文章为核心，辅以标签分类、用户互动和通知系统。

---

## 2. 功能需求

### 2.1 用户系统

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 注册 | 用户名 + 邮箱 + 密码 | P0 |
| 登录 | 用户名 + 密码，返回 JWT | P0 |
| 退出登录 | 清除 Token | P0 |
| 获取用户信息 | 当前用户资料查询 | P0 |
| 修改资料 | 头像、简介 | P1 |
| 个人主页 | 展示用户问题和文章 | P1 |
| 用户关注 | Follow/Unfollow | P2 |

### 2.2 问答系统

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 提问 | 标题 + Markdown 内容 + 标签 | P0 |
| 问题列表 | 分页、筛选、排序 | P0 |
| 问题详情 | 内容 + 回答列表 + Markdown 渲染 | P0 |
| 编辑问题 | 作者可编辑自己的问题 | P1 |
| 回答问题 | Markdown 内容 | P0 |
| 采纳回答 | 问题作者选择最佳答案 | P0 |
| 编辑回答 | 作者可编辑自己的回答 | P1 |
| 浏览计数 | 问题浏览量统计 | P1 |

### 2.3 标签系统

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 创建标签 | 名称 + 描述 | P1 |
| 标签列表 | 按使用量排序 | P1 |
| 按标签筛选 | 查看某标签下的所有问题/文章 | P0 |
| 自动补全 | 提问时标签自动补全 | P2 |

### 2.4 文章系统

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 写文章 | 标题 + Markdown 内容 + 标签 | P1 |
| 文章列表 | 分页、排序 | P1 |
| 文章详情 | 通过 slug URL 访问 | P1 |
| 草稿 | 保存为草稿，后续发布 | P1 |
| 编辑文章 | 作者可编辑 | P1 |
| 删除文章 | 作者可删除 | P2 |

### 2.5 评论系统

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 短评论 | 关联问题/回答/文章，1000字符以内 | P1 |
| 评论列表 | 按时间排序 | P1 |
| 编辑/删除 | 作者可操作 | P2 |

### 2.6 互动功能

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 点赞 | 问题/回答/文章/评论 | P1 |
| 收藏 | 问题/文章 | P1 |
| 我的收藏列表 | 查看已收藏内容 | P1 |

### 2.7 通知系统

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 回答通知 | 有人回答你的问题 | P1 |
| 评论通知 | 有人评论你的内容 | P1 |
| 采纳通知 | 你的回答被采纳 | P1 |
| 点赞通知 | 有人点赞你的内容 | P2 |
| 通知列表 | 按时间排序 | P1 |
| 标记已读 | 单个/全部 | P1 |
| 未读计数 | 导航栏显示未读数量 | P1 |

## 2.8 搜索

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 全文搜索 | 搜索问题/文章标题和内容 | P1 |
| 按标签搜索 | 查看某标签下的内容 | P0 |
| 高级筛选 | 按时间、类型、状态筛选 | P2 |

### 2.9 用户关注

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 关注/取关 | Follow/Unfollow 用户 | P1 |
| 关注者/粉丝列表 | 查看关注关系 | P2 |
| 关注通知 | 被关注时产生通知 | P1 |
| 关注者发帖 | 关注的用户发布问题时通知 | P1 |
| 关注者发文章 | 关注的用户发布文章时通知 | P2 |

---

## 3. API 设计

### 3.1 基本规范

- Base URL: `/api/v1`
- 统一响应格式:
```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": "2026-06-03T12:00:00Z"
}
```
- 认证方式: `Authorization: Bearer <jwt>`
- 分页参数: `?page=0&size=20&sort=createdAt,desc`

### 3.2 端点列表

#### 认证 `/api/v1/auth`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| POST | `/register` | 否 | 注册 |
| POST | `/login` | 否 | 登录 |
| POST | `/refresh` | 否 | 刷新 Token |
| POST | `/logout` | 是 | 退出登录 |

#### 用户 `/api/v1/users`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| GET | `/me` | 是 | 当前用户信息 |
| PUT | `/me` | 是 | 更新资料 |
| GET | `/{id}` | 否 | 公开用户信息 |
| POST | `/me/follow/{userId}` | 是 | 关注用户 |
| DELETE | `/me/follow/{userId}` | 是 | 取消关注 |

#### 问答 `/api/v1/questions`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| GET | `/` | 否 | 问题列表 |
| GET | `/{id}` | 否 | 问题详情 |
| POST | `/` | 是 | 创建问题 |
| PUT | `/{id}` | 是 | 更新问题 |
| DELETE | `/{id}` | 是 | 删除问题 |
| POST | `/{id}/answers` | 是 | 发布回答 |
| PUT | `/{id}/answers/{aid}` | 是 | 编辑回答 |
| PUT | `/{id}/accept/{aid}` | 是 | 采纳回答（仅问题作者） |

#### 文章 `/api/v1/articles`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| GET | `/` | 否 | 文章列表 |
| GET | `/slug/{slug}` | 否 | 按 slug 获取文章（slug 格式：`art-{nanoid8}`，如 `art-xK7mQ2pL`） |
| POST | `/` | 是 | 创建文章（自动生成 slug） |
| PUT | `/{id}` | 是 | 更新文章 |
| DELETE | `/{id}` | 是 | 删除文章 |
| PUT | `/{id}/publish` | 是 | 发布草稿 |

#### 用户 `/api/v1/users`（关注扩展）
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| POST | `/me/follow/{userId}` | 是 | 关注用户 |
| DELETE | `/me/follow/{userId}` | 是 | 取消关注 |
| GET | `/{id}/followers` | 否 | 粉丝列表 |
| GET | `/{id}/following` | 否 | 关注列表 |

#### 互动 `/api/v1/interactions`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| POST | `/like` | 是 | 点赞/取消点赞（toggle），返回 `{liked, likeCount}` |
| POST | `/favorite` | 是 | 收藏/取消收藏（toggle） |
| GET | `/me/favorites` | 是 | 我的收藏列表 |

#### 标签 `/api/v1/tags`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| GET | `/` | 否 | 标签列表 |
| GET | `/{id}/questions` | 否 | 标签下的问题 |

#### 评论 `/api/v1/comments`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| GET | `?targetType=&targetId=` | 否 | 获取评论 |
| POST | `/` | 是 | 创建评论 |
| PUT | `/{id}` | 是 | 编辑评论 |
| DELETE | `/{id}` | 是 | 删除评论 |

#### 互动 `/api/v1/interactions`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| POST | `/like` | 是 | 点赞/取消点赞 |
| POST | `/favorite` | 是 | 收藏/取消收藏 |
| GET | `/me/favorites` | 是 | 我的收藏 |

#### 通知 `/api/v1/notifications`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| GET | `/` | 是 | 通知列表 |
| PUT | `/read` | 是 | 全部标已读 |
| PUT | `/{id}/read` | 是 | 单个标已读 |

#### 搜索 `/api/v1/search`
| 方法 | 端点 | 认证 | 描述 |
|------|------|------|------|
| GET | `/` | 否 | 全文搜索 |

---

## 4. 数据库设计

详见 [架构设计](architecture.md#数据库设计)。

### 核心表

- `users` — 用户（✅ 已创建）
- `questions` — 问题（Phase 2）
- `answers` — 回答（Phase 2）
- `tags` — 标签（Phase 2）
- `question_tags` — 问题-标签关联（Phase 2）
- `comments` — 评论（Phase 2）
- `articles` — 文章（Phase 3）
- `article_tags` — 文章-标签关联（Phase 3）
- `user_interactions` — 互动记录（Phase 3）
- `follows` — 用户关注（Phase 3）
- `notifications` — 通知（Phase 3）

### Phase 3 数据库详细设计

#### articles — 文章表

```sql
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content MEDIUMTEXT NOT NULL,
    content_html MEDIUMTEXT,
    slug VARCHAR(64) NOT NULL UNIQUE,
    status ENUM('DRAFT', 'PUBLISHED') NOT NULL DEFAULT 'DRAFT',
    view_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    favorite_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    FOREIGN KEY (author_id) REFERENCES users(id)
);
```

- **slug 格式**：`art-{nanoid8}`，如 `art-xK7mQ2pL`，发布时自动生成且不可修改
- **状态**：DRAFT（草稿）/ PUBLISHED（已发布），草稿可发布但已发布不能退回草稿

#### article_tags — 文章-标签关联

```sql
CREATE TABLE article_tags (
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);
```

#### user_interactions — 用户互动（统一表）

```sql
CREATE TABLE user_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_type ENUM('QUESTION', 'ANSWER', 'ARTICLE', 'COMMENT') NOT NULL,
    target_id BIGINT NOT NULL,
    type ENUM('LIKE', 'FAVORITE') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_target_type (user_id, target_type, target_id, type),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

- 一条表搞定所有点赞和收藏
- 复合唯一索引天然防重复
- **点赞 toggle**：存在 → 删除（取消），不存在 → 插入（点赞）
- **收藏 toggle**：同上逻辑
- 点赞/收藏数通过 `COUNT(*)` 查询，不冗余存储（Phase 3 数据量小）

#### follows — 用户关注

```sql
CREATE TABLE follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);
```

- `follower_id`：关注者，`following_id`：被关注者
- 禁止自关注（follower_id != following_id）

#### notifications — 通知

```sql
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('ANSWER', 'COMMENT', 'ACCEPT', 'LIKE', 'FOLLOW') NOT NULL,
    from_user_id BIGINT,
    target_type ENUM('QUESTION', 'ANSWER', 'ARTICLE', 'COMMENT') NULL,
    target_id BIGINT NULL,
    content VARCHAR(500),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (from_user_id) REFERENCES users(id),
    INDEX idx_user_read_created (user_id, is_read, created_at DESC)
);
```

- **通知类型**：
  - `ANSWER`：有人回答你的问题
  - `COMMENT`：有人评论你的内容
  - `ACCEPT`：你的回答被采纳
  - `LIKE`：有人点赞你的内容（P2）
  - `FOLLOW`：有人关注了你
- **from_user_id**：触发通知的用户
- **target_type/target_id**：关联的目标内容
- **content**：通知摘要文本

### Phase 3 通知触发机制（Spring Event）

采用 **Spring Event 同步解耦** 方式触发通知：

```
AnswerService ──publishEvent──▶ AnswerCreatedEvent ──@EventListener──▶ NotificationEventListener ──▶ 创建通知
QuestionService ──▶ QuestionCreatedEvent ──▶ 通知关注者
CommentService ──▶ CommentCreatedEvent ──▶ 通知被评论者
LikeService ──▶ LikedEvent ──▶ 通知被点赞者
FollowService ──▶ FollowedEvent ──▶ 通知被关注者
```

**理由**：
- 同步执行（同一事务），数据一致性有保证
- 解耦：主业务 Service 不依赖通知逻辑
- 可测试：通知逻辑可独立测试
- 未来可扩展为异步（只需加 `@Async`）

**关注通知规则**：
- A 关注 B → B 收到 `FOLLOW` 通知
- A 发布问题 → A 的所有关注者收到通知（单次最多 100 人）
- A 发布文章 → A 的所有关注者收到通知
- 关注/取关不产生通知给关注者本人（避免自通知）

---

## 5. 开发阶段

### Phase 1: 基础框架 + 认证系统 ✅ 已完成

**交付物：**
- Spring Boot 3.5.9 + Vue 3 项目骨架
- 用户注册、登录、JWT 认证
- Spring Security 过滤器链
- 全局异常处理
- 前端登录/注册/首页
- 路由守卫

**状态：** 代码已编写，编译通过。需要数据库运行后做 E2E 验证。

### Phase 2: 核心问答功能

**交付物：**
- 提问、回答、标签、评论
- Markdown 渲染
- 问题列表（分页、筛选）

### Phase 3: 文章 + 互动 + 通知

**交付物：**
- 文章系统（草稿 + 发布，slug 格式 `art-{nanoid8}`）
- 点赞、收藏（统一 `user_interactions` 表，toggle 模式，返回 `{liked, likeCount}`）
- 用户关注（`follows` 表，关注通知，发帖通知）
- 通知系统（Spring Event 解耦，5 种通知类型）

**设计决策（2026-06-04）：**
1. 文章 slug 自动生成：`art-{nanoid8}`，不可读但保证唯一且 URL 短
2. 互动统一表：`user_interactions` + 复合唯一索引防重复
3. 点赞/收藏 toggle：存在 → 删除（取消），不存在 → 插入（操作）
4. 通知触发：Spring Event 同步解耦，同一事务保证一致性
5. 关注通知：被关注通知 + 关注者发帖通知（单次最多 100 人）

### Phase 4: 搜索 + 优化

**交付物：**
- 全文搜索
- 文件上传（腾讯云 COS）
- UI 美化、响应式
- 限流

### Phase 5: 部署上线

**交付物：**
- Docker Compose 生产配置
- Nginx 反向代理
- 腾讯云部署
- 域名备案

---

## 6. 非功能需求

| 需求 | 要求 |
|------|------|
| 性能 | API 响应 < 200ms（缓存命中） |
| 安全 | XSS 防护、SQL 注入防护、CSRF 防护 |
| 可用 | 支持水平扩展（无状态后端） |
| 文件存储 | 腾讯云 COS 对象存储 |
| 域名 | 中国大陆备案域名 |
| 服务器 | 腾讯云 CVM |

---

## 7. 变更记录

| 日期 | 变更 | 作者 |
|------|------|------|
| 2026-06-03 | 初始版本，完成 Phase 1 代码 | AI Assistant |
| 2026-06-04 | 完善 Phase 3 设计：文章 slug、统一互动表、Spring Event 通知、关注规则 | AI Assistant |
