# Member Card List Dialog Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert the nested expandable member card list to a dialog-based display with quick actions.

**Architecture:** Replace `el-table-column type="expand"` with a clickable icon column that opens a dialog. The dialog contains the card table with all existing functionality plus a Purchase Card button.

**Tech Stack:** Vue 3 Composition API, Element Plus, TypeScript

---

## Files

| File | Action | Purpose |
|------|--------|---------|
| `boxing-gym-frontend/src/views/member/index.vue` | Modify | Main member list page - add dialog, remove expand |

---

## Task 1: Add `computed` to Imports

**Files:**
- Modify: `boxing-gym-frontend/src/views/member/index.vue` (line 2, imports)

- [ ] **Step 1: Add `computed` to Vue imports**

Find the import statement on line 2:
```typescript
import { ref, reactive, onMounted } from 'vue'
```

Change it to:
```typescript
import { ref, reactive, onMounted, computed } from 'vue'
```

- [ ] **Step 2: Verify no TypeScript errors**

Run: `cd boxing-gym-frontend && npm run build 2>&1 | head -50`
Expected: Build succeeds (we haven't used computed yet)

- [ ] **Step 3: Commit**

```bash
git add boxing-gym-frontend/src/views/member/index.vue
git commit -m "feat(member): add computed to Vue imports

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

## Task 2: Add New State Variables and Computed

**Files:**
- Modify: `boxing-gym-frontend/src/views/member/index.vue` (script section, after expand-related refs)

- [ ] **Step 1: Add new state variables**

Find the expand-related refs block (contains `memberCardsMap`, `cardsLoadingMap`, `expandedRowKeys`).

Add after the `expandedRowKeys` ref:
```typescript

// 卡片列表弹窗
const cardListDialogVisible = ref(false)
const currentMemberId = ref<number | null>(null)
const currentMemberName = ref('')
const cardListLoading = ref(false)

// 当前会员的卡片列表
const currentMemberCards = computed(() => {
  if (!currentMemberId.value) return []
  return memberCardsMap.value.get(currentMemberId.value) || []
})
```

- [ ] **Step 2: Verify no TypeScript errors**

Run: `cd boxing-gym-frontend && npm run build 2>&1 | head -50`
Expected: Build succeeds

- [ ] **Step 3: Commit**

```bash
git add boxing-gym-frontend/src/views/member/index.vue
git commit -m "feat(member): add state variables for card list dialog

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

## Task 3: Add New Methods

**Files:**
- Modify: `boxing-gym-frontend/src/views/member/index.vue` (script section, after `loadMemberCards` function)

- [ ] **Step 1: Add new methods**

Find the `loadMemberCards` function. Add after it:
```typescript

/** 打开卡片列表弹窗 */
const openCardListDialog = async (member: Member) => {
  currentMemberId.value = member.id!
  currentMemberName.value = member.name
  cardListDialogVisible.value = true
  cardListLoading.value = true
  await loadMemberCards(member.id!)
  cardListLoading.value = false
}

/** 卡片列表弹窗关闭处理 */
const handleCardListDialogClose = () => {
  cardListLoading.value = false
}

/** 从弹窗内打开购卡 */
const openPurchaseCardFromDialog = async () => {
  purchaseCardForm.memberId = currentMemberId.value!
  purchaseCardForm.cardId = undefined
  purchaseCardForm.payMethod = 3
  purchaseCardForm.paidAmount = 0
  purchaseCardForm.remark = ''
  cardsLoading.value = true
  try {
    availableCards.value = await getAvailableCards()
  } catch (error) {
    console.error('Failed to load available cards:', error)
    availableCards.value = []
  } finally {
    cardsLoading.value = false
  }
  purchaseCardVisible.value = true
}
```

- [ ] **Step 2: Verify no TypeScript errors**

Run: `cd boxing-gym-frontend && npm run build 2>&1 | head -50`
Expected: Build succeeds

- [ ] **Step 3: Commit**

```bash
git add boxing-gym-frontend/src/views/member/index.vue
git commit -m "feat(member): add methods for card list dialog

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

## Task 4: Replace Expand Column with Icon Button Column

**Files:**
- Modify: `boxing-gym-frontend/src/views/member/index.vue` (template section)

- [ ] **Step 1: Remove expand-related props from el-table**

Find the main `<el-table>` component (the one containing `v-loading="loading"` and `:data="memberList"`).

Remove these two props from it:
- `:expand-row-keys="expandedRowKeys"`
- `@expand-change="handleExpandChange"`

- [ ] **Step 2: Replace the expand column with icon button column**

Find the `<el-table-column type="expand" ...>` block. It starts with:
```vue
<el-table-column type="expand" width="50">
```
and ends with:
```vue
</el-table-column>
```
followed by `<el-table-column type="index" ...>`.

Replace the entire expand column block with:
```vue
      <!-- 持卡列 -->
      <el-table-column label="持卡" width="60" align="center">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            @click="openCardListDialog(row)"
          >
            <el-icon><CreditCard /></el-icon>
          </el-button>
        </template>
      </el-table-column>
```

- [ ] **Step 3: Verify the page loads**

Run: `cd boxing-gym-frontend && npm run build 2>&1 | head -50`
Expected: Build succeeds

- [ ] **Step 4: Commit**

```bash
git add boxing-gym-frontend/src/views/member/index.vue
git commit -m "feat(member): replace expand column with card icon button

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

## Task 5: Add Card List Dialog Component

**Files:**
- Modify: `boxing-gym-frontend/src/views/member/index.vue` (template section)

- [ ] **Step 1: Add the card list dialog**

Find the purchase card dialog (the one with `v-model="purchaseCardVisible"` and `title="购买会员卡"`).

Add after its closing `</el-dialog>` tag:
```vue

    <!-- 卡片列表弹窗 -->
    <el-dialog
      v-model="cardListDialogVisible"
      :title="`${currentMemberName} 的会员卡`"
      width="600px"
      @closed="handleCardListDialogClose"
    >
      <!-- Toolbar -->
      <div style="margin-bottom: 16px;">
        <el-button type="primary" @click="openPurchaseCardFromDialog">
          <el-icon><Plus /></el-icon>
          购买会员卡
        </el-button>
      </div>

      <!-- Card Table -->
      <el-table
        :data="currentMemberCards"
        v-loading="cardListLoading"
        size="small"
        border
        :header-cell-style="{ background: '#f5f7fa' }"
      >
        <el-table-column prop="cardName" label="卡片名称" min-width="120" />
        <el-table-column prop="cardCategory" label="类别" width="100">
          <template #default="{ row }">
            {{ CARD_CATEGORY_MAP[row.cardCategory] || row.cardCategoryDesc }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="MEMBER_CARD_STATUS_TAG_TYPE[row.status]" size="small">
              {{ MEMBER_CARD_STATUS_MAP[row.status] || row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="剩余次数/有效期" width="140">
          <template #default="{ row }">
            <template v-if="row.cardCategory === CardCategory.GROUP_TIME">
              {{ row.remainingDays ?? '-' }}天
            </template>
            <template v-else>
              {{ row.remainingSessions ?? 0 }}次
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseTime" label="购卡日期" width="110">
          <template #default="{ row }">
            {{ formatDate(row.purchaseTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="activationTime" label="激活日期" width="110">
          <template #default="{ row }">
            {{ formatDate(row.activationTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="expireDate" label="到期日期" width="110">
          <template #default="{ row }">
            {{ formatDate(row.expireDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.canBeActivated"
              type="success"
              link
              size="small"
              @click="handleActivateCard(row, currentMemberId!)"
            >
              激活
            </el-button>
            <el-button
              v-if="row.status === MemberCardStatus.ACTIVE"
              type="danger"
              link
              size="small"
              @click="handleVoidCard(row, currentMemberId!)"
            >
              作废
            </el-button>
            <el-button type="primary" link size="small" @click="handleViewRecords(row)">
              <el-icon><View /></el-icon>
              记录
            </el-button>
          </template>
        </el-table-column>

        <!-- Empty state -->
        <template #empty>
          <div class="no-cards">暂无持卡信息</div>
        </template>
      </el-table>
    </el-dialog>
```

- [ ] **Step 2: Verify build succeeds**

Run: `cd boxing-gym-frontend && npm run build`
Expected: Build succeeds

- [ ] **Step 3: Commit**

```bash
git add boxing-gym-frontend/src/views/member/index.vue
git commit -m "feat(member): add card list dialog component

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

## Task 6: Remove Unused Expand-Related Code

**Files:**
- Modify: `boxing-gym-frontend/src/views/member/index.vue` (script and style sections)

- [ ] **Step 1: Remove unused refs**

In the script section, find and remove these two refs:
```typescript
const cardsLoadingMap = ref<Map<number, boolean>>(new Map())
const expandedRowKeys = ref<number[]>([])
```

- [ ] **Step 2: Remove `handleExpandChange` method**

Find and remove the entire `handleExpandChange` function:
```typescript
/** 处理行展开 */
const handleExpandChange = async (row: Member, expandedRows: Member[]) => {
  expandedRowKeys.value = expandedRows.map(r => r.id!)

  if (expandedRows.some(r => r.id === row.id) && !memberCardsMap.value.has(row.id!)) {
    await loadMemberCards(row.id!)
  }
}
```

- [ ] **Step 3: Simplify `loadMemberCards` method**

Find the `loadMemberCards` function. Replace it with this simplified version:
```typescript
/** 加载会员卡片 */
const loadMemberCards = async (memberId: number) => {
  try {
    const res = await getMemberCards(memberId)
    memberCardsMap.value.set(memberId, res || [])
  } catch (error) {
    console.error('Failed to load member cards:', error)
    memberCardsMap.value.set(memberId, [])
  }
}
```

- [ ] **Step 4: Remove unused CSS classes**

In the `<style scoped>` section, find and remove these CSS classes:
```css
.expand-content {
  padding: 12px 20px;
  background: #fafafa;
}

.cards-table-wrapper {
  background: #fff;
  border-radius: 4px;
  padding: 12px;
}

.cards-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}
```

Keep `.no-cards` and `.no-records` styles.

- [ ] **Step 5: Verify build succeeds**

Run: `cd boxing-gym-frontend && npm run build`
Expected: Build succeeds with no errors

- [ ] **Step 6: Commit**

```bash
git add boxing-gym-frontend/src/views/member/index.vue
git commit -m "refactor(member): remove unused expand-related code

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

## Task 7: Manual Verification

**Files:**
- Test: `boxing-gym-frontend/src/views/member/index.vue`

- [ ] **Step 1: Start the frontend dev server**

Run: `cd boxing-gym-frontend && npm run dev`

- [ ] **Step 2: Verify card list dialog functionality**

1. Navigate to member list page (http://localhost:3000)
2. Click the CreditCard icon on any member row
3. Verify: Dialog opens with member's name in title
4. Verify: Card table displays with all columns
5. Verify: "购买会员卡" button appears at top
6. Verify: Activate/Void/Records buttons work correctly
7. Verify: Empty state shows "暂无持卡信息" for member with no cards

- [ ] **Step 3: Verify purchase card from dialog**

1. Open card list dialog for a member
2. Click "购买会员卡" button
3. Verify: Purchase dialog opens with correct member context
4. Complete a purchase
5. Verify: Card list refreshes after purchase

- [ ] **Step 4: Final commit**

```bash
git add boxing-gym-frontend/src/views/member/index.vue
git commit -m "feat(member): complete card list dialog implementation

Convert nested expandable table to dialog-based card list with:
- Click CreditCard icon to open dialog
- Purchase Card button inside dialog
- All existing card actions preserved
- Empty state handling

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"
```

---

## Success Criteria Checklist

- [ ] Click CreditCard icon opens dialog (not inline expansion)
- [ ] Dialog displays member's cards with all columns
- [ ] Purchase Card button opens purchase dialog with correct member context
- [ ] Activate button works (shows for inactive cards)
- [ ] Void button works (shows for active cards)
- [ ] Records button opens usage records drawer
- [ ] Empty state displays "暂无持卡信息" when member has no cards
- [ ] Card list refreshes after purchasing a card
