# Boxing Gym Management System

拳馆管理系统单仓库，包含后端（Spring Boot）和前端（Vue3）。

## 项目结构

```text
boxing-gym/
├── src/                        # 后端源码（Spring Boot）
│   └── main/
│       ├── java/com/boxinggym/
│       └── resources/
├── pom.xml                     # 后端 Maven 配置
├── boxing-gym-fronted/         # 前端源码（Vue3 + Vite + Element Plus）
│   ├── src/
│   │   ├── App.vue
│   │   ├── api/
│   │   ├── router/
│   │   ├── store/
│   │   ├── views/
│   │   └── utils/
│   └── package.json
└── target/                     # 后端编译产物
```

说明：`boxing-gym-fronted` 是当前前端工程目录，前端入口组件为
`boxing-gym-fronted/src/App.vue`。

## 后端启动

环境要求：
- JDK 17+
- MySQL 8+
- Redis
- Maven 3.9+

命令：

```bash
mvn spring-boot:run
```

默认地址：
- API: `http://localhost:8080`
- Knife4j: `http://localhost:8080/doc.html`

## 前端启动

环境要求：
- Node.js 20 LTS（推荐）
- npm 9+

命令：

```bash
cd boxing-gym-fronted
npm install
npm run dev
```

默认地址：
- Web: `http://localhost:5173`（Vite 默认端口，若被占用会自动调整）

备注：当前前端依赖版本在 Node.js 24 下可能触发 `vue-tsc` 兼容问题，建议使用 Node.js 20 LTS。

## 构建命令

后端：

```bash
mvn -DskipTests compile
```

前端：

```bash
cd boxing-gym-fronted
npm run build
```
