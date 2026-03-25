# Member Card List Dialog Design

## Overview

Convert the member card list from a nested expandable table to a dialog-based display with quick actions.

## Current State

- Member cards are displayed in a nested table when expanding a member row
- Uses `el-table-column type="expand"` pattern
- Cards load on-demand when row expands

## Target State

- Click expand icon opens a dialog instead of inline expansion
- Dialog contains card list table with quick action buttons
- Purchase Card button available inside dialog

## UI Specification

### Dialog Layout (600px width)

```
┌─────────────────────────────────────────────────────────┐
│  [Member Name] 的会员卡                           [X]   │
├─────────────────────────────────────────────────────────┤
│  [+ 购买会员卡]                                         │
├─────────────────────────────────────────────────────────┤
│  卡片名称 │ 类别 │ 状态 │ 剩余 │ 购买日期 │ 操作       │
│  ─────────────────────────────────────────────────────  │
│  年卡     │ 团课 │ 正常 │ 30天 │ 2024-01-01│ 激活/作废/记录│
│  ...                                                     │
│                                                          │
│  (暂无持卡信息 - shown when no cards)                   │
├─────────────────────────────────────────────────────────┘
└─────────────────────────────────────────────────────────┘
```

### Columns

Keep all existing columns for feature parity:

| Column | Content | Width |
|--------|---------|-------|
| 卡片名称 | Card name | auto |
| 类别 | Card category (团课时长/团课次卡/私教次卡) | 100px |
| 状态 | Status tag (未激活/正常/已过期/已作废) | 80px |
| 剩余 | Remaining sessions or days | 140px |
| 购卡日期 | Purchase date (formatted) | 110px |
| 激活日期 | Activation date (formatted) | 110px |
| 到期日期 | Expiry date (formatted) | 110px |
| 操作 | Action buttons | 180px |

### Quick Actions

1. **Purchase Card button** - Top of dialog, opens existing purchase dialog

2. **Card row actions** (existing):
   - 激活 (Activate) - `type="success"`, shown when `card.canBeActivated` is true
   - 作废 (Void) - `type="danger"`, shown when `card.status === MemberCardStatus.ACTIVE`
   - 记录 (Records) - `type="primary"`, always visible with View icon

### Empty State

When member has no cards, display:
```
暂无持卡信息
```

## Implementation Details

### File to Modify

- `src/views/member/index.vue`

### State Changes

Add new reactive refs:

```typescript
const cardListDialogVisible = ref(false)
const currentMemberId = ref<number | null>(null)
const currentMemberName = ref('')
const cardListLoading = ref(false)

// Computed for current member's cards
const currentMemberCards = computed(() => {
  if (!currentMemberId.value) return []
  return memberCardsMap.value.get(currentMemberId.value) || []
})
```

### Code to Remove (Cleanup)

Remove the expand-related code:

```typescript
// Remove these refs:
const cardsLoadingMap = ref<Map<number, boolean>>(new Map())
const expandedRowKeys = ref<number[]>([])

// Remove this method:
const handleExpandChange = async (row: Member, expandedRows: Member[]) => { ... }
```

Remove from el-table:
- `:expand-row-keys="expandedRowKeys"`
- `@expand-change="handleExpandChange"`
- `<el-table-column type="expand">...</el-table-column>`

### Template Changes

1. Replace `el-table-column type="expand"` with icon button column:

```vue
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

2. Add new dialog component:

```vue
<el-dialog
  v-model="cardListDialogVisible"
  :title="`${currentMemberName} 的会员卡`"
  width="600px"
  @closed="handleCardListDialogClose"
>
  <!-- Toolbar -->
  <div class="dialog-toolbar" style="margin-bottom: 16px;">
    <el-button type="primary" @click="openPurchaseCard">
      <el-icon><Plus /></el-icon>
      购买会员卡
    </el-button>
  </div>

  <!-- Card Table -->
  <el-table
    :data="currentMemberCards"
    v-loading="cardListLoading"
    size="small"
    :header-cell-style="{ background: '#f5f7fa' }"
  >
    <el-table-column prop="cardName" label="卡片名称" />
    <el-table-column prop="cardCategory" label="类别" width="100">
      <template #default="{ row }">
        {{ CARD_CATEGORY_MAP[row.cardCategory] }}
      </template>
    </el-table-column>
    <el-table-column prop="status" label="状态" width="80">
      <template #default="{ row }">
        <el-tag :type="MEMBER_CARD_STATUS_TAG_TYPE[row.status]">
          {{ MEMBER_CARD_STATUS_MAP[row.status] }}
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

### Methods

```typescript
const openCardListDialog = async (member: Member) => {
  currentMemberId.value = member.id
  currentMemberName.value = member.name
  cardListDialogVisible.value = true
  cardListLoading.value = true
  await loadMemberCards(member.id)
  cardListLoading.value = false
}

const handleCardListDialogClose = () => {
  // Keep cached cards for faster reopen, just reset loading state
  cardListLoading.value = false
}

const openPurchaseCard = async () => {
  // Set member context for purchase
  purchaseCardForm.memberId = currentMemberId.value!
  purchaseCardForm.cardId = undefined
  purchaseCardForm.payMethod = 3
  purchaseCardForm.paidAmount = 0
  purchaseCardForm.remark = ''

  // Load available cards using existing pattern
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

### Reused Components/Logic

- `memberCardsMap` - Map storing cards per member
- `loadMemberCards(memberId)` - Load cards for a member
- `handleActivateCard(card, memberId)` - Existing action handler
- `handleVoidCard(card, memberId)` - Existing action handler
- `handleViewRecords(card)` - Existing action handler for viewing usage records
- `handlePurchaseCardSubmit()` - Existing purchase submit (already refreshes card list)
- `purchaseCardVisible`, `purchaseCardForm` - Existing purchase dialog state
- `availableCards`, `cardsLoading` - Existing refs for available cards loading
- `getAvailableCards()` - Existing API call from `@/api/membershipCard`
- `formatDate(date)` - Existing local helper for date formatting
- Usage records drawer - No changes
- `MEMBER_CARD_STATUS_MAP`, `MEMBER_CARD_STATUS_TAG_TYPE`, `CARD_CATEGORY_MAP`, `CardCategory` - Existing constants/types
- `MemberCardStatus` - Existing enum
- `Plus`, `CreditCard`, `View` icons - Already imported from `@element-plus/icons-vue`

## Success Criteria

1. Click expand icon opens dialog (not inline expansion)
2. Dialog displays member's cards with all existing columns
3. Purchase Card button opens existing purchase dialog with correct member context
4. All existing card actions work within dialog
5. Usage records drawer still functions correctly
6. After purchasing a card from within the dialog, the card list refreshes automatically
7. Empty state displays "暂无持卡信息" when member has no cards
