# 会员列表展开行显示持卡信息 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在会员列表中通过表格展开行显示会员的持卡信息，支持激活、作废、查看使用记录等操作，并移除独立的会员持卡模块。

**Architecture:** 在现有会员列表页面添加 Element Plus 展开行功能，展开时加载会员卡片数据并显示嵌套表格，复用现有 API。

**Tech Stack:** Vue 3, TypeScript, Element Plus

---

## 文件结构

| 文件 | 操作 | 说明 |
|------|------|------|
| `boxing-gym-frontend/src/views/member/index.vue` | 修改 | 添加展开行功能 |
| `boxing-gym-frontend/src/router/index.ts` | 修改 | 移除 member-card 路由 |
| `boxing-gym-frontend/src/views/member-card/` | 删除 | 整个目录 |
| `boxing-gym-frontend/src/api/memberCard.ts` | 修改 | 移除未使用的 listMemberCards |

---

### Task 1: 修改会员列表页面 - 添加展开行功能

**Files:**
- Modify: `boxing-gym-frontend/src/views/member/index.vue`

- [ ] **Step 1: 添加 memberCard API 和类型导入**

在 `<script setup>` 顶部添加导入：

```typescript
import { getMemberCards, activateCard, voidCard, getCardUsageRecords } from '@/api/memberCard'
import {
  MemberCardStatus,
  MEMBER_CARD_STATUS_MAP,
  MEMBER_CARD_STATUS_TAG_TYPE,
  CardCategory,
  CARD_CATEGORY_MAP,
  CARD_USAGE_TYPE_MAP,
  type MemberCard,
  type CardUsageRecord,
} from '@/types/memberCard'
```

- [ ] **Step 2: 添加展开行相关的状态变量**

在现有状态变量后添加：

```typescript
// 展开行相关
const memberCardsMap = ref<Map<number, MemberCard[]>>(new Map())
const cardsLoadingMap = ref<Map<number, boolean>>(new Map())
const expandedRowKeys = ref<number[]>([])

// 使用记录抽屉
const drawerVisible = ref(false)
const usageRecords = ref<CardUsageRecord[]>([])
const usageLoading = ref(false)
const selectedCard = ref<MemberCard | null>(null)
```

- [ ] **Step 3: 添加展开行处理函数**

在 `handlePageChange` 函数后添加：

```typescript
/** 处理行展开 */
const handleExpandChange = async (row: Member, expandedRows: Member[]) => {
  expandedRowKeys.value = expandedRows.map(r => r.id!)

  if (expandedRows.some(r => r.id === row.id) && !memberCardsMap.value.has(row.id!)) {
    await loadMemberCards(row.id!)
  }
}

/** 加载会员卡片 */
const loadMemberCards = async (memberId: number) => {
  cardsLoadingMap.value.set(memberId, true)
  try {
    const res = await getMemberCards(memberId)
    memberCardsMap.value.set(memberId, res.data || [])
  } catch (error) {
    console.error('Failed to load member cards:', error)
    memberCardsMap.value.set(memberId, [])
  } finally {
    cardsLoadingMap.value.set(memberId, false)
  }
}

/** 格式化日期 */
const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return date.split('T')[0] || date.split(' ')[0]
}

/** 激活卡片 */
const handleActivateCard = async (card: MemberCard, memberId: number) => {
  try {
    await ElMessageBox.confirm(`确定要激活卡片 "${card.cardNo}" 吗？`, '激活确认', {
      confirmButtonText: '确定激活',
      cancelButtonText: '取消',
      type: 'info',
    })
    await activateCard({ memberCardId: card.id })
    ElMessage.success('激活成功')
    memberCardsMap.value.delete(memberId)
    await loadMemberCards(memberId)
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('Failed to activate card:', error)
      ElMessage.error('激活失败')
    }
  }
}

/** 作废卡片 */
const handleVoidCard = async (card: MemberCard, memberId: number) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入作废原因', '作废确认', {
      confirmButtonText: '确定作废',
      cancelButtonText: '取消',
      inputPattern: /\S+/,
      inputErrorMessage: '请输入作废原因',
      type: 'warning',
    })
    await voidCard({ memberCardId: card.id, reason: value })
    ElMessage.success('作废成功')
    memberCardsMap.value.delete(memberId)
    await loadMemberCards(memberId)
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('Failed to void card:', error)
      ElMessage.error('作废失败')
    }
  }
}

/** 查看使用记录 */
const handleViewRecords = async (card: MemberCard) => {
  selectedCard.value = card
  drawerVisible.value = true
  usageLoading.value = true
  try {
    const res = await getCardUsageRecords(card.id)
    usageRecords.value = res.data || []
  } catch (error) {
    console.error('Failed to load usage records:', error)
    usageRecords.value = []
  } finally {
    usageLoading.value = false
  }
}
```

- [ ] **Step 4: 修改表格添加展开列**

将 `<el-table>` 修改为支持展开：

```vue
<!-- 数据表格 -->
<el-table
  v-loading="loading"
  :data="memberList"
  stripe
  border
  :row-key="(row: Member) => row.id"
  :expand-row-keys="expandedRowKeys"
  @expand-change="handleExpandChange"
>
  <el-table-column type="expand">
    <template #default="{ row }">
      <div class="expand-content" style="padding: 12px 20px;">
        <div style="margin-bottom: 8px; font-weight: 500; color: #606266;">
          持卡信息
        </div>
        <el-table
          v-loading="cardsLoadingMap.get(row.id)"
          :data="memberCardsMap.get(row.id) || []"
          size="small"
          border
        >
          <el-table-column prop="cardNo" label="卡号" width="140" />
          <el-table-column label="卡分类" width="100">
            <template #default="{ row: card }">
              <el-tag size="small" :type="card.cardCategory === CardCategory.GROUP_TIME ? 'primary' : card.cardCategory === CardCategory.GROUP_SESSION ? 'success' : 'warning'">
                {{ CARD_CATEGORY_MAP[card.cardCategory] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="cardTypeDesc" label="卡类型" min-width="120" />
          <el-table-column label="剩余次数/有效期" min-width="180">
            <template #default="{ row: card }">
              <template v-if="card.totalSessions">
                <span>{{ card.remainingSessions }} / {{ card.totalSessions }} 次</span>
              </template>
              <template v-else-if="card.expireDate">
                <span>{{ formatDate(card.startDate) }} ~ {{ formatDate(card.expireDate) }}</span>
              </template>
              <template v-else>
                <span>-</span>
              </template>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row: card }">
              <el-tag size="small" :type="MEMBER_CARD_STATUS_TAG_TYPE[card.status]">
                {{ MEMBER_CARD_STATUS_MAP[card.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="购买时间" width="110">
            <template #default="{ row: card }">
              {{ formatDate(card.purchaseTime) }}
            </template>
          </el-table-column>
          <el-table-column label="激活时间" width="110">
            <template #default="{ row: card }">
              {{ formatDate(card.activationTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row: card }">
              <el-button
                v-if="card.canBeActivated"
                type="warning"
                link
                size="small"
                @click="handleActivateCard(card, row.id)"
              >
                激活
              </el-button>
              <el-button type="primary" link size="small" @click="handleViewRecords(card)">
                使用记录
              </el-button>
              <el-button
                v-if="card.status === MemberCardStatus.ACTIVE"
                type="danger"
                link
                size="small"
                @click="handleVoidCard(card, row.id)"
              >
                作废
              </el-button>
            </template>
          </el-table-column>
          <template #empty>
            <span style="color: #909399;">暂无持卡信息</span>
          </template>
        </el-table>
      </div>
    </template>
  </el-table-column>
  <el-table-column type="index" label="序号" width="60" align="center" />
  <!-- 其余列保持不变 -->
  <el-table-column prop="memberNo" label="会员号" width="120" />
  <el-table-column prop="name" label="姓名" min-width="100" />
  <el-table-column prop="gender" label="性别" width="80">
    <template #default="{ row }">
      {{ getDictLabel(GENDER, row.gender) }}
    </template>
  </el-table-column>
  <el-table-column prop="phone" label="手机号" width="120" />
  <el-table-column prop="membershipLevel" label="会员等级" width="100">
    <template #default="{ row }">
      {{ getDictLabel(MEMBERSHIP_LEVEL, row.membershipLevel) }}
    </template>
  </el-table-column>
  <el-table-column prop="expiryDate" label="到期日期" width="120" />
  <el-table-column prop="remainingBalance" label="余额" width="120">
    <template #default="{ row }">
      {{ formatCurrency(row.remainingBalance || 0) }}
    </template>
  </el-table-column>
  <el-table-column prop="status" label="状态" width="100">
    <template #default="{ row }">
      <el-tag :type="row.status === '0' ? 'success' : row.status === '1' ? 'warning' : 'info'">
        {{ getDictLabel(MEMBER_STATUS, row.status) }}
      </el-tag>
    </template>
  </el-table-column>
  <el-table-column prop="createTime" label="创建时间" width="180" />
  <el-table-column label="操作" width="280" fixed="right">
    <template #default="{ row }">
      <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
      <el-button type="success" link :icon="Coin" @click="handleRecharge(row)">充值</el-button>
      <el-button type="warning" link :icon="Wallet" @click="handleDeduct(row)">扣费</el-button>
      <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
    </template>
  </el-table-column>
</el-table>
```

- [ ] **Step 5: 添加使用记录抽屉**

在 `</el-dialog>` 后面添加：

```vue
<!-- 使用记录抽屉 -->
<el-drawer
  v-model="drawerVisible"
  :title="`使用记录 - ${selectedCard?.cardNo}`"
  direction="rtl"
  size="500px"
>
  <el-table v-loading="usageLoading" :data="usageRecords" stripe border size="small">
    <el-table-column type="index" label="序号" width="60" align="center" />
    <el-table-column label="类型" width="100">
      <template #default="{ row }">
        <el-tag size="small">{{ CARD_USAGE_TYPE_MAP[row.usageType] }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="次数变化" width="120">
      <template #default="{ row }">
        <template v-if="row.sessionsBefore !== undefined">
          {{ row.sessionsBefore }} → {{ row.sessionsAfter }}
        </template>
        <template v-else>-</template>
      </template>
    </el-table-column>
    <el-table-column prop="remark" label="备注" min-width="150" />
    <el-table-column prop="createTime" label="时间" width="180" />
  </el-table>
</el-drawer>
```

- [ ] **Step 6: 验证功能正常**

Run: `cd boxing-gym-frontend && npm run dev`
Expected: 会员列表可以展开，显示持卡信息，激活/作废/使用记录功能正常

- [ ] **Step 7: 提交代码**

```bash
git add boxing-gym-frontend/src/views/member/index.vue
git commit -m "feat: 会员列表添加展开行显示持卡信息

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

### Task 2: 移除会员持卡路由

**Files:**
- Modify: `boxing-gym-frontend/src/router/index.ts`

- [ ] **Step 1: 删除 member-card 路由配置**

删除以下代码块（约第 130-142 行）：

```typescript
  {
    path: '/member-card',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'MemberCard',
        component: () => import('@/views/member-card/index.vue'),
        meta: { title: '会员持卡', icon: 'Wallet' }
      }
    ]
  },
```

- [ ] **Step 2: 提交代码**

```bash
git add boxing-gym-frontend/src/router/index.ts
git commit -m "refactor: 移除独立的会员持卡路由

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

### Task 3: 删除会员持卡模块文件

**Files:**
- Delete: `boxing-gym-frontend/src/views/member-card/`

- [ ] **Step 1: 删除 member-card 目录**

```bash
rm -rf boxing-gym-frontend/src/views/member-card
```

- [ ] **Step 2: 提交代码**

```bash
git add boxing-gym-frontend/src/views/member-card
git commit -m "refactor: 删除独立的会员持卡模块

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

### Task 4: 清理未使用的 API 函数

**Files:**
- Modify: `boxing-gym-frontend/src/api/memberCard.ts`
- Modify: `boxing-gym-frontend/src/types/memberCard.ts`

- [ ] **Step 1: 删除 listMemberCards 函数**

从 `memberCard.ts` 删除：

```typescript
/** 分页查询会员持卡列表 */
export function listMemberCards(params: MemberCardQuery) {
  return request.get<ApiResponse<PageResult<MemberCard>>>(`${BASE_URL}/page`, { params })
}
```

- [ ] **Step 2: 删除 MemberCardQuery 类型**

从 `types/memberCard.ts` 删除：

```typescript
/** 会员持卡查询参数 */
export interface MemberCardQuery {
  pageNum: number
  pageSize: number
  cardNo?: string
  memberName?: string
  status?: MemberCardStatus | ''
  cardCategory?: CardCategory | ''
}
```

同时从 `memberCard.ts` 的导入中移除 `MemberCardQuery`：

```typescript
import type {
  MemberCard,
  PurchaseCardDTO,
  ActivateCardDTO,
  VoidCardDTO,
  CardUsageRecord,
  CardUsageRecordQuery,
} from '@/types/memberCard'
```

- [ ] **Step 3: 提交代码**

```bash
git add boxing-gym-frontend/src/api/memberCard.ts boxing-gym-frontend/src/types/memberCard.ts
git commit -m "refactor: 移除未使用的 listMemberCards API 和 MemberCardQuery 类型

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

### Task 5: 最终验证

- [ ] **Step 1: 运行前端开发服务器**

```bash
cd boxing-gym-frontend && npm run dev
```

- [ ] **Step 2: 验证功能**
1. 访问会员管理页面
2. 点击会员行展开图标，确认显示持卡信息
3. 测试激活、作废、查看使用记录功能
4. 确认独立的会员持卡菜单已移除

- [ ] **Step 3: 推送代码**

```bash
git push origin main
```
