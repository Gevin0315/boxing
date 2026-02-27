# boxing-gym-fronted

拳馆管理系统前端工程（Vue 3 + Vite + TypeScript + Element Plus）。

## 目录结构

```text
boxing-gym-fronted/
├── src/
│   ├── App.vue                 # 应用根组件
│   ├── main.ts                 # 应用入口
│   ├── api/                    # 接口层
│   ├── router/                 # 路由
│   ├── store/                  # Pinia 状态管理
│   ├── views/                  # 页面模块
│   ├── components/             # 复用组件
│   ├── constants/              # 常量/枚举
│   ├── types/                  # 类型定义
│   ├── utils/                  # 工具方法
│   └── styles/                 # 全局样式
├── index.html
├── vite.config.ts
├── tsconfig.json
└── package.json
```

## 开发命令

```bash
npm install
npm run dev
```

建议环境：
- Node.js 20 LTS
- npm 9+

## 构建命令

```bash
npm run build
```

提示：Node.js 24 下 `vue-tsc` 可能报兼容错误，优先使用 Node.js 20 LTS。

## 代码规范

- 使用 Composition API + `<script setup lang="ts">`
- 业务请求统一放在 `src/api`
- 路由守卫在 `src/router/index.ts`
- 用户登录态在 `src/store/modules/user.ts`
