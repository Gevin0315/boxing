# Boxing Gym Management System

拳馆管理系统 — 基于 Spring Boot 3 + Vue 3 的 monorepo 项目。

## 技术栈请求

| 后端 | 前端 |
|------|------|
| Spring Boot 3.2 / Java 17 | Vue 3 + Vite 5 + TypeScript |
| MyBatis-Plus / MySQL 8 / Druid | Element Plus / Pinia / Vue Router |
| Spring Security + JWT | Axios / dayjs |
| Redis | - |
| Knife4j (API 文档) | - |

## 功能模块

- **系统用户**：管理员、前台、教练，RBAC 权限
- **教练档案**：擅长流派、简介、课时费
- **会员管理**：会员信息、储值、课时、卡有效期
- **课程**：团课 / 私教，课程定义
- **排课**：课程安排、教练、时间、人数
- **财务订单**：充值 / 退款 / 课程消费
- **训练签到**：预约、签到、旷课记录

## 项目结构

```text
boxing-gym/
├── src/main/java/com/boxinggym/
│   ├── controller/       # REST 接口
│   ├── service/          # 业务逻辑
│   ├── mapper/           # MyBatis 映射
│   ├── entity/           # 实体
│   ├── dto/              # 请求/响应对象
│   ├── security/         # JWT、认证配置
│   ├── config/           # 全局配置
│   └── common/           # 统一响应、异常
├── src/main/resources/
│   ├── application.yml   # 主配置
│   └── sql/init.sql      # 数据库初始化
├── boxing-gym-fronted/   # 前端（目录名 typo 沿用）
│   └── src/
│       ├── views/        # 页面
│       ├── api/          # 接口封装
│       ├── store/        # Pinia
│       └── router/
├── pom.xml
└── target/
```

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8+
- Redis
- Maven 3.9+
- Node.js 20 LTS（前端，Node 24 可能有 vue-tsc 兼容问题）

### 1. 数据库初始化

```bash
mysql -u root -p < src/main/resources/sql/init.sql
```

或在 MySQL 中执行 `src/main/resources/sql/init.sql`。会创建数据库 `boxing_gym` 并初始化表及默认管理员。

### 2. 后端配置

修改 `src/main/resources/application.yml` 中数据源和 Redis 配置（默认 MySQL root/root、3306，Redis localhost:6379 无密码）。

### 3. 启动后端

```bash
mvn spring-boot:run
```

- API：`http://localhost:8080`
- Knife4j 文档：`http://localhost:8080/doc.html`
- Druid 监控：`http://localhost:8080/druid/`（admin/admin）

### 4. 启动前端

```bash
cd boxing-gym-fronted
npm install
npm run dev
```

- 前端：`http://localhost:3000`（Vite 配置端口）

### 5. 默认登录

- 用户名：`admin`
- 密码：`admin123`

## 构建

```bash
# 后端
mvn -DskipTests compile
# 或打可执行 jar
mvn -DskipTests package

# 前端
cd boxing-gym-fronted
npm run build
```

## API 代理

开发环境下，前端 Vite 将 `/api` 代理到 `http://localhost:8080`，需确保后端已启动。
