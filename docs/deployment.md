# 部署规划

> 目标：将项目部署到腾讯云 CVM，使用 COS 存储，通过备案域名访问。

## 1. 基础设施

### 1.1 腾讯云资源清单

| 资源 | 规格建议 | 用途 |
|------|----------|------|
| CVM | 2C4G / 50GB 系统盘（起步） | 应用服务器 |
| MySQL | CVM 自建 / 云数据库 TDSQL-C | 主数据库 |
| Redis | CVM 自建 / 云数据库 Redis | 缓存 |
| COS | 标准存储桶 | 头像、附件存储 |
| 域名 | .com / .cn | 访问入口 |
| CDN | COS 加速域名 | 静态资源加速 |
| SSL | 腾讯云免费证书 | HTTPS |

### 1.2 架构

```
用户请求
    │
    ▼
┌────────────┐
│  域名 DNS   │  腾讯云 DNSPod
└─────┬──────┘
      ▼
┌────────────┐
│   CDN      │  静态资源缓存（前端 + COS）
└─────┬──────┘
      ▼
┌────────────┐
│   Nginx    │  反向代理 + HTTPS 终止
│  :80/:443  │
└─────┬──────┘
      │
      ├── /api/* ───▶ ┌────────────┐
      │               │ Spring Boot│  :8080
      │               └─────┬──────┘
      │                     │
      │               ┌─────┴──────┐
      │               │  MySQL     │
      │               │  Redis     │
      │               └────────────┘
      │
      ├── /* ───────▶ ┌────────────┐
      │               │  前端静态   │  Vue 3 build 产物
      │               └────────────┘
      │
      └── /upload/* ─▶ 腾讯云 COS（可选，直接前端上传）
```

---

## 2. 域名备案

### 2.1 备案流程

```
1. 购买腾讯云 CVM（大陆地域）
2. 购买域名（腾讯云 DNSPod 推荐）
3. 在腾讯云备案系统提交备案
4. 填写主体信息（个人/企业）
5. 填写网站信息
6. 上传证件照片
7. 等待审核（通常 1-20 个工作日）
8. 备案通过后，域名解析到 CVM IP
```

### 2.2 备案要求

| 要求 | 说明 |
|------|------|
| 服务器 | 必须是大陆地域 CVM，且剩余时长 ≥ 3 个月 |
| 个人备案 | 身份证正反面 + 人脸核验 |
| 网站名称 | 不能含 "中国"、"国家" 等敏感词 |
| 网站内容 | 备案通过前不得开放访问 |

### 2.3 建议

- 先买 CVM → 立即提交备案（备案周期长，越早越好）
- 域名推荐: `.com`（国际通用）或 `.cn`（国内信任度高）
- 个人备案网站名称建议: "一亩三分学田" 或 "学田笔记"

---

## 3. 腾讯云 COS 配置

### 3.1 创建存储桶

```
1. 进入腾讯云 COS 控制台
2. 创建存储桶
   - 名称: yiyixing-space-{appid}
   - 地域: 选择离 CVM 最近的（如 ap-guangzhou）
   - 权限: 私有读写（头像可设为公有读）
3. 开启 CDN 加速（可选）
4. 配置 CORS 规则（允许前端直接上传）
```

### 3.2 CORS 配置

```xml
<CORSRule>
    <AllowedOrigin>https://yourdomain.com</AllowedOrigin>
    <AllowedMethod>PUT</AllowedMethod>
    <AllowedMethod>POST</AllowedMethod>
    <AllowedHeader>*</AllowedHeader>
</CORSRule>
```

### 3.3 后端集成

```xml
<!-- Maven 依赖 -->
<dependency>
    <groupId>com.qcloud</groupId>
    <artifactId>cos_api</artifactId>
    <version>5.6.227</version>
</dependency>
```

```java
// 生成预签名上传 URL
COSClient cosClient = new COSClient(cred, clientConfig);
GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(
    bucketName, key, HttpMethodName.PUT);
req.setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
URL url = cosClient.generatePresignedUrl(req);
```

---

## 4. 生产部署

### 4.1 Docker 部署

```yaml
# docker-compose.prod.yml
services:
  app:
    build:
      context: ./backend
      dockerfile: ../Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/yiyixing
      - SPRING_DATA_REDIS_HOST=redis
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: yiyixing
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      retries: 5

  redis:
    image: redis:7-alpine
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      retries: 5

volumes:
  mysql_data:
  redis_data:
```

### 4.2 Nginx 配置

```nginx
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    # 前端静态资源
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理
    location /api/ {
        proxy_pass http://app:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 4.3 后端生产配置

```yaml
# application-prod.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  flyway:
    enabled: true

jwt:
  secret: ${JWT_SECRET}  # 环境变量注入，不写在配置文件里

logging:
  level:
    com.yiyixing: INFO
    org.springframework.security: WARN
```

---

## 5. 部署 Checklist

- [ ] 购买腾讯云 CVM（大陆地域）
- [ ] 购买域名
- [ ] 提交域名备案申请
- [ ] 创建 COS 存储桶
- [ ] 配置 COS CORS 规则
- [ ] 申请 SSL 证书
- [ ] 编写 Dockerfile
- [ ] 编写 docker-compose.prod.yml
- [ ] 编写 Nginx 配置
- [ ] 配置 application-prod.yml
- [ ] 设置环境变量（JWT_SECRET, DB_PASSWORD）
- [ ] 部署并验证
- [ ] 域名解析到 CVM IP
- [ ] HTTPS 生效

---

## 6. CI/CD（可选）

### 方案一：GitHub Actions

```yaml
# .github/workflows/deploy.yml
name: Deploy
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build backend
        run: cd backend && mvn clean package -DskipTests
      - name: Build frontend
        run: cd frontend && npm ci && npm run build
      - name: Deploy to server
        run: |
          scp docker-compose.prod.yml user@server:
          scp -r backend/target/*.jar user@server:
          scp -r frontend/dist/* user@server:/usr/share/nginx/html/
          ssh user@server "docker compose -f docker-compose.prod.yml up -d"
```

### 方案二：手动部署

```bash
# 在服务器上执行
git pull origin main
docker compose -f docker-compose.prod.yml build
docker compose -f docker-compose.prod.yml up -d
```

---

## 7. 监控与日志

| 项目 | 方案 |
|------|------|
| 应用日志 | Spring Boot 日志 → 文件 → 腾讯云 CLS |
| 系统监控 | 腾讯云 CVM 自带监控 |
| 健康检查 | `GET /actuator/health` |
| 错误告警 | Sentry / 腾讯云监控告警 |

---

## 8. 费用估算（月）

| 项目 | 费用（元/月） |
|------|---------------|
| CVM 2C4G | ~80-150 |
| 域名 | ~5-10（首年后续费） |
| COS 存储 | ~1-5（小规模） |
| SSL 证书 | 免费（腾讯云 TrustAsia） |
| CDN 流量 | ~1-10（视访问量） |
| **合计** | **~100-180** |
