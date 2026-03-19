# 会员列表展开行显示持卡信息

## 背景
当前系统有独立的"会员持卡"模块，但实际使用中，管理员更希望在会员列表中直接查看和管理会员的持卡信息，无需切换到单独的页面。

## 目标
- 移除独立的"会员持卡"模块
- 在会员列表中通过表格展开行显示会员的持卡信息
- 支持在展开行中进行激活、作废、查看使用记录等操作

## 设计

### 1. 会员列表页面修改 (`src/views/member/index.vue`)

#### 1.1 表格启用展开功能
使用 Element Plus 的 `type="expand"` 列类型实现展开行。

#### 1.2 展开内容
展开后显示嵌套表格，包含以下信息：
| 字段 | 说明 |
|------|------|
| 卡号 | memberCard.cardNo |
| 卡分类 | 团课期限卡/团课次卡/私教次卡 |
| 卡类型 | 具体卡名称 |
| 剩余次数/有效期 | 次卡显示剩余次数，期限卡显示有效期 |
| 状态 | 未激活/生效中/已过期/已作废 |
| 购买时间 | purchaseTime |
| 激活时间 | activationTime |
| 操作 | 激活/作废/查看使用记录 |

#### 1.3 数据加载
- 展开时调用 `getMemberCards(memberId)` API 获取该会员的卡片列表
- 使用展开状态管理避免重复请求

#### 1.4 操作功能
- **激活**: 仅未激活的卡片显示，调用 `activateCard` API
- **作废**: 仅生效中的卡片显示，调用 `voidCard` API（需输入作废原因）
- **查看使用记录**: 打开抽屉显示该卡片的使用记录，调用 `getCardUsageRecords` API

### 2. 路由清理

#### 2.1 移除路由
删除 `src/router/index.ts` 中的 `/member-card` 路由配置。

#### 2.2 删除文件
删除 `src/views/member-card/` 目录及其所有文件。

### 3. API 复用
复用现有 API，无需新增：
- `getMemberCards(memberId)` - 获取会员卡片列表
- `activateCard(data)` - 激活卡片
- `voidCard(data)` - 作废卡片
- `getCardUsageRecords(memberCardId)` - 获取使用记录

## 文件变更清单

| 文件 | 操作 |
|------|------|
| `src/views/member/index.vue` | 修改 - 添加展开行功能 |
| `src/router/index.ts` | 修改 - 移除 member-card 路由 |
| `src/views/member-card/` | 删除 - 整个目录 |
| `src/api/memberCard.ts` | 保留 - 可移除未使用的 `listMemberCards` |
| `src/types/memberCard.ts` | 保留 |

## 风险
- 无，这是一个纯前端重构，不影响后端 API
