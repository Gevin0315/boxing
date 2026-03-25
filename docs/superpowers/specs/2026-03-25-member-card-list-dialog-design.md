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
├─────────────────────────────────────────────────────────┤
│                                    [< 1 2 3 >]          │
└─────────────────────────────────────────────────────────┘
```

### Columns

| Column | Content | Width |
|--------|---------|-------|
| 卡片名称 | Card name | auto |
| 类别 | Card category (团课时长/团课次卡/私教次卡) | 100px |
| 状态 | Status tag (未激活/正常/已过期/已作废) | 80px |
| 剩余 | Remaining sessions or days | 80px |
| 购买日期 | Purchase date | 110px |
| 操作 | Action buttons | 150px |

### Quick Actions

1. **Purchase Card button** - Top of dialog, opens existing purchase dialog
2. **Card row actions** (existing):
   - 激活 (Activate) - for inactive cards
   - 作废 (Void) - for active cards
   - 记录 (Records) - opens usage records drawer

## Implementation Details

### File to Modify

- `src/views/member/index.vue`

### State Changes

Add new reactive refs:

```typescript
const cardListDialogVisible = ref(false)
const currentMemberId = ref<number | null>(null)
const currentMemberName = ref('')
```

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
>
  <!-- Toolbar -->
  <div class="dialog-toolbar">
    <el-button type="primary" @click="openPurchaseCard">
      <el-icon><Plus /></el-icon>
      购买会员卡
    </el-button>
  </div>

  <!-- Card Table -->
  <el-table :data="currentMemberCards" v-loading="cardListLoading">
    <!-- Card columns (reuse existing) -->
  </el-table>
</el-dialog>
```

### Methods

```typescript
const openCardListDialog = (member: Member) => {
  currentMemberId.value = member.id
  currentMemberName.value = member.name
  cardListDialogVisible.value = true
  loadMemberCards(member.id)
}

const openPurchaseCard = () => {
  purchaseCardVisible.value = true
}
```

### Reused Components/Logic

- `memberCardsMap` - Map storing cards per member
- `loadMemberCards(memberId)` - Load cards for a member
- `handleActivateCard()`, `handleVoidCard()`, `viewUsageRecords()` - Existing action handlers
- `purchaseCardVisible` - Existing purchase dialog visibility
- Usage records drawer - No changes

## Success Criteria

1. Click expand icon opens dialog (not inline expansion)
2. Dialog displays member's cards with all existing columns
3. Purchase Card button opens existing purchase dialog
4. All existing card actions work within dialog
5. Usage records drawer still functions correctly
