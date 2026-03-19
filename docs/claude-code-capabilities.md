# Claude Code 可用能力清单

> 最后更新：2026-03-18

---

## 目录

- [Skills（斜杠命令）](#skills斜杠命令)
- [Agents（专家代理）](#agents专家代理)

---

## Skills（斜杠命令）

### 文档处理

| Skill | 命令 | 用途 |
|-------|------|------|
| `pdf` | `/pdf` | PDF 读取、合并、拆分、加密、OCR、提取文本/图片/表格 |
| `docx` | `/docx` | Word 文档创建、编辑、格式化、目录、页眉页脚 |
| `pptx` | `/pptx` | PowerPoint 演示文稿创建、编辑、模板、幻灯片管理 |
| `xlsx` | `/xlsx` | Excel 电子表格创建、编辑、公式、图表、数据清洗 |

### 设计/前端

| Skill | 命令 | 用途 |
|-------|------|------|
| `frontend-design` | `/frontend-design` | 高质量前端界面设计、React/Vue 组件、Landing Page |
| `canvas-design` | `/canvas-design` | 海报、艺术作品、静态视觉设计（PNG/PDF） |
| `algorithmic-art` | `/algorithmic-art` | p5.js 算法艺术、生成艺术、粒子系统、流场 |
| `slack-gif-creator` | `/slack-gif-creator` | Slack 优化的动态 GIF 制作 |
| `theme-factory` | `/theme-factory` | 主题样式工具箱，10+ 预设主题应用于文档/幻灯片/页面 |
| `web-artifacts-builder` | `/web-artifacts-builder` | 复杂 HTML artifacts（React + Tailwind + shadcn/ui） |

### 开发工具

| Skill | 命令 | 用途 |
|-------|------|------|
| `claude-api` | `/claude-api` | Claude API / Anthropic SDK 应用开发 |
| `mcp-builder` | `/mcp-builder` | MCP (Model Context Protocol) 服务器构建 |
| `skill-creator` | `/skill-creator` | 创建、修改、优化 skills，运行评估测试 |
| `webapp-testing` | `/webapp-testing` | Playwright 本地 Web 应用测试、截图、调试 |

### 写作/沟通

| Skill | 命令 | 用途 |
|-------|------|------|
| `doc-coauthoring` | `/doc-coauthoring` | 文档协作、技术规范、提案撰写 |
| `internal-comms` | `/internal-comms` | 内部沟通（状态报告、公告、FAQ、事故报告） |

### 其他

| Skill | 命令 | 用途 |
|-------|------|------|
| `brand-guidelines` | `/brand-guidelines` | Anthropic 品牌颜色和排版样式应用 |

---

## Agents（专家代理）

### 语言/框架专家

| Agent | 用途 |
|-------|------|
| `typescript-pro` | TypeScript 高级类型系统、泛型、类型安全 |
| `javascript-pro` | ES2023+ 现代 JavaScript、异步模式 |
| `python-pro` | 类型安全的 Python 代码、Web API |
| `java-architect` | 企业级 Java 架构、Spring Boot 微服务 |
| `spring-boot-engineer` | Spring Boot 3+ 企业应用开发 |
| `golang-pro` | Go 并发编程、高性能系统 |
| `rust-engineer` | Rust 内存安全、零成本抽象 |
| `cpp-pro` | C++20/23 现代特性、高性能系统 |
| `csharp-developer` | ASP.NET Core、Entity Framework |
| `dotnet-core-expert` | .NET Core 云原生应用、微服务 |
| `dotnet-framework-4.8-expert` | .NET Framework 4.8 企业应用维护 |
| `kotlin-specialist` | Kotlin 协程、多平台开发 |
| `swift-expert` | iOS/macOS 原生开发、SwiftUI |
| `flutter-expert` | Flutter 跨平台移动应用 |
| `react-specialist` | React 18+ 性能优化、状态管理 |
| `vue-expert` | Vue 3 Composition API、Nuxt 3 |
| `angular-architect` | Angular 15+ 企业应用、RxJS 优化 |
| `nextjs-developer` | Next.js 14+ App Router、服务端组件 |
| `django-developer` | Django 4+ Web 应用、REST API |
| `rails-expert` | Rails 全栈开发、Hotwire |
| `laravel-specialist` | Laravel 10+ Eloquent、队列系统 |
| `elixir-expert` | Elixir/OTP、Phoenix 实时应用 |
| `php-pro` | PHP 8.3+ 严格类型、企业框架 |
| `sql-pro` | SQL 查询优化、数据库设计（PostgreSQL/MySQL/SQL Server/Oracle） |

### 基础设施/DevOps

| Agent | 用途 |
|-------|------|
| `cloud-architect` | 多云架构设计、成本优化、灾难恢复 |
| `azure-infra-engineer` | Azure 基础设施、Entra ID、Bicep IaC |
| `kubernetes-specialist` | K8s 集群设计、部署配置、故障排查 |
| `docker-expert` | Docker 镜像构建、编排优化、安全加固 |
| `terraform-engineer` | Terraform IaC、多云部署、模块架构 |
| `terragrunt-expert` | Terragrunt 编排、DRY 配置、多环境部署 |
| `devops-engineer` | CI/CD 流水线、容器化、自动化部署 |
| `deployment-engineer` | 部署策略、发布自动化 |
| `sre-engineer` | SLO 定义、错误预算、可观测性、故障容错 |
| `platform-engineer` | 内部开发者平台、黄金路径设计 |
| `network-engineer` | 网络架构、安全、性能优化 |
| `security-engineer` | 安全方案、合规、漏洞管理 |
| `database-administrator` | 数据库性能优化、高可用架构、灾难恢复 |
| `postgres-pro` | PostgreSQL 查询优化、复制配置、企业部署 |
| `database-optimizer` | 慢查询分析、索引策略、性能优化 |

### 安全/测试

| Agent | 用途 |
|-------|------|
| `penetration-tester` | 授权渗透测试、漏洞验证 |
| `security-auditor` | 安全审计、合规评估 |
| `incident-responder` | 安全事件响应、取证恢复 |
| `devops-incident-responder` | 生产事故响应、事后复盘 |
| `ad-security-reviewer` | AD 安全审计、权限评估 |
| `accessibility-tester` | WCAG 合规性测试 |
| `qa-expert` | 质量保证策略、测试规划 |
| `test-automator` | 自动化测试框架、CI 集成 |
| `chaos-engineer` | 故障注入实验、韧性验证 |

### 数据/AI

| Agent | 用途 |
|-------|------|
| `data-engineer` | 数据管道、ETL/ELT 设计 |
| `data-scientist` | 数据分析、预测模型 |
| `data-analyst` | 业务数据分析、可视化报告 |
| `data-researcher` | 数据收集、质量验证 |
| `ml-engineer` | ML 模型训练、部署优化 |
| `mlops-engineer` | ML 基础设施、模型版本管理 |
| `ai-engineer` | AI 系统架构、RAG 实现 |
| `llm-architect` | LLM 生产部署、推理优化、多模型管理 |
| `nlp-engineer` | NLP 系统构建、文本处理 |
| `prompt-engineer` | Prompt 设计、优化、测试 |
| `quant-analyst` | 量化交易策略、风险分析 |

### 开发工具/流程

| Agent | 用途 |
|-------|------|
| `debugger` | Bug 诊断、根因分析 |
| `code-reviewer` | 代码审查、最佳实践 |
| `refactoring-specialist` | 代码重构、技术债务清理 |
| `architect-reviewer` | 架构评估、技术选型 |
| `microservices-architect` | 微服务架构设计 |
| `api-designer` | API 设计、OpenAPI 规范 |
| `api-documenter` | API 文档、OpenAPI 规范、交互式文档 |
| `graphql-architect` | GraphQL Schema 设计、联邦架构 |
| `git-workflow-manager` | Git 工作流、分支策略 |
| `dependency-manager` | 依赖审计、版本冲突解决 |
| `build-engineer` | 构建性能优化 |
| `dx-optimizer` | 开发者体验优化 |
| `tooling-engineer` | 开发工具构建（CLI、代码生成器、IDE 扩展） |
| `documentation-engineer` | 文档系统架构、API 文档 |
| `legacy-modernizer` | 遗留系统现代化、增量迁移 |

### 业务/产品

| Agent | 用途 |
|-------|------|
| `product-manager` | 产品策略、路线图规划 |
| `business-analyst` | 业务流程分析、需求收集 |
| `project-manager` | 项目计划、风险管理 |
| `scrum-master` | 敏捷流程、迭代管理 |
| `ux-researcher` | 用户研究、可用性测试 |
| `ui-designer` | UI 设计、设计系统 |
| `technical-writer` | 技术文档、API 文档 |
| `market-researcher` | 市场分析、竞品研究 |
| `competitive-analyst` | 竞争分析、对标研究 |
| `customer-success-manager` | 客户健康、留存策略 |
| `legal-advisor` | 合同起草、合规审查 |
| `risk-manager` | 风险识别、控制框架 |
| `content-marketer` | 内容策略、SEO 优化 |
| `trend-analyst` | 趋势分析、行业预测 |
| `research-analyst` | 综合研究、报告撰写 |

### 开发角色

| Agent | 用途 |
|-------|------|
| `frontend-developer` | 跨框架前端开发（React/Vue/Angular） |
| `backend-developer` | 服务端 API、微服务 |
| `fullstack-developer` | 全栈功能开发 |
| `mobile-developer` | React Native/Flutter 跨平台移动应用 |
| `mobile-app-developer` | iOS/Android 原生开发 |
| `game-developer` | 游戏系统、渲染优化 |
| `embedded-systems` | 嵌入式开发、RTOS |
| `iot-engineer` | IoT 解决方案、边缘计算 |
| `blockchain-developer` | 智能合约、DApp 开发 |
| `payment-integration` | 支付系统集成、PCI 合规 |
| `fintech-engineer` | 金融应用、交易处理 |
| `websocket-engineer` | WebSocket 实时通信 |
| `cli-developer` | 命令行工具开发 |
| `electron-pro` | Electron 桌面应用 |

### PowerShell/Windows

| Agent | 用途 |
|-------|------|
| `powershell-5.1-expert` | Windows 自动化、AD/DNS/GPO（PowerShell 5.1） |
| `powershell-7-expert` | 跨平台云自动化、Azure（PowerShell 7+） |
| `powershell-ui-architect` | PowerShell GUI/TUI 开发 |
| `powershell-module-architect` | PowerShell 模块设计 |
| `powershell-security-hardening` | PowerShell 安全加固 |
| `windows-infra-admin` | Windows Server 管理 |
| `m365-admin` | Microsoft 365 管理（Exchange/Teams/SharePoint） |

### 系统/协调

| Agent | 用途 |
|-------|------|
| `Explore` | 代码库探索（快速/彻底搜索） |
| `Plan` | 实现方案规划 |
| `context-manager` | 共享状态、信息同步 |
| `multi-agent-coordinator` | 多 Agent 协调 |
| `task-distributor` | 任务分发、负载均衡 |
| `error-coordinator` | 分布式错误处理 |
| `error-detective` | 错误诊断、关联分析 |
| `knowledge-synthesizer` | 经验总结、知识提取 |
| `agent-organizer` | Agent 团队组建 |
| `search-specialist` | 信息检索、搜索优化 |
| `workflow-orchestrator` | 工作流设计、状态管理 |
| `performance-monitor` | 可观测性、性能监控 |

### 其他

| Agent | 用途 |
|-------|------|
| `sales-engineer` | 技术前售、POC 开发 |
| `performance-engineer` | 性能瓶颈分析、优化 |
| `compliance-auditor` | 合规审计（GDPR/HIPAA/PCI DSS/SOC 2/ISO） |
| `it-ops-orchestrator` | IT 运维编排（PowerShell/Azure/M365） |

---

## 使用方式

### Skills

通过斜杠命令调用：

```
/pdf          # 处理 PDF 文件
/docx         # 处理 Word 文档
/frontend-design    # 前端设计
```

或直接描述需求，系统会自动匹配：

- "帮我创建一个 Word 文档" → `docx` skill
- "读取这个 PDF" → `pdf` skill

### Agents

在 Task 工具中指定 `subagent_type`：

```
使用 typescript-pro agent 来优化这段代码
使用 Explore agent 来搜索数据库配置
使用 security-auditor agent 来审计安全漏洞
```

```
区分 2 者
是个好问题。让我解释两者的区别：                                                                                                                                                        
                                         
  自动 vs 手动                                                                                                                                                                              
  ┌────────┬───────────────┬──────────────────────────┐
  │  类型  │   自动调用    │         手动调用         │                                                                                                                                     
  ├────────┼───────────────┼──────────────────────────┤     
  │ Skills │ ✅ 会自动匹配 │ 也可用 /skill-name       │
  ├────────┼───────────────┼──────────────────────────┤
  │ Agents │ ❌ 不会自动   │ 需要我主动使用 Task 工具 │
  └────────┴───────────────┴──────────────────────────┘
  什么时候用什么

  Skills（文件格式/特定工具）

  自动触发 - 当你的请求涉及特定文件格式或工具时：

  "帮我读取这个 PDF"           → 自动用 pdf skill
  "创建一个 Word 文档"          → 自动用 docx skill
  "设计一个登录页面"            → 自动用 frontend-design skill
  "这个 Excel 表格数据清洗一下" → 自动用 xlsx skill

  Agents（专业领域任务）

  需要我判断调用 - 当任务需要专业领域知识或复杂探索时：

  "优化这段 TypeScript 的类型"    → 我会调用 typescript-pro
  "帮我探索代码库的认证逻辑"      → 我会调用 Explore agent
  "审查这段代码的安全漏洞"        → 我会调用 security-auditor
  "设计微服务架构"               → 我会调用 microservices-architect

  简单判断原则
  ┌─────────────────────────┬─────────────────────────┐
  │          场景           │         用什么          │
  ├─────────────────────────┼─────────────────────────┤
  │ 处理 PDF/Word/Excel/PPT │ Skills（自动）          │
  ├─────────────────────────┼─────────────────────────┤
  │ 生成前端 UI/设计        │ Skills（自动）          │
  ├─────────────────────────┼─────────────────────────┤
  │ 代码库探索、搜索        │ Agents - Explore        │
  ├─────────────────────────┼─────────────────────────┤
  │ 代码审查、重构          │ Agents - 专业 agent     │
  ├─────────────────────────┼─────────────────────────┤
  │ 架构设计、技术选型      │ Agents - 架构类 agent   │
  ├─────────────────────────┼─────────────────────────┤
  │ 安全审计、渗透测试      │ Agents - 安全类 agent   │
  ├─────────────────────────┼─────────────────────────┤
  │ 写文档、API 规范        │ Agents 或 Skills 都可能 │
  └─────────────────────────┴─────────────────────────┘
  实际上

  你不需要手动指定。只需要告诉我你想做什么：

  ❌ "用 typescript-pro agent 帮我优化类型"
  ✅ "帮我优化这段 TypeScript 代码的类型定义"

  ❌ "用 pdf skill 读取这个文件"
  ✅ "帮我读取 report.pdf"

  我会根据任务复杂度和领域自动选择合适的工具。
  ```