# Plan: 添加购卡入口

## 背景

用户需要在前端 Web 界面为会员购买卡片。后端 API 已实现，前端 API 和类型定义已存在，只需在会员页面添加购卡入口。

## 需要修改的文件
- `boxing-gym-frontend/src/views/member/index.vue` - 添加购卡按钮和弹窗

## 实现步骤

### 1. 添加购卡相关状态
```typescript
// 购卡弹窗
const purchaseCardVisible = ref(false)
const purchaseCardForm = reactive({
  memberId: undefined as number | undefined,
  cardId: undefined as number | undefined,
  payMethod: 3, // 默认现金
  paidAmount: 0,
  remark: ''
})
const availableCards = ref<MembershipCard[]>([])
const cardsLoading = ref(false)
```

### 2. 添加购卡按钮处理函数
```typescript
const handlePurchaseCard = async (row: Member) => {
  // 打开弹窗，加载在售卡片列表
}

const handlePurchaseCardSubmit = async () => {
  // 调用 purchaseCard API
}
```

### 3. 在模板中添加
- 购卡按钮（在操作列）
- 购卡弹窗（选择卡片、支付方式、实付金额、备注）

## 验证
1. 启动前后端服务
2. 在会员列表点击"购卡"按钮
3. 选择卡片、支付方式、输入金额
4. 提交后验证会员卡列表中是否出现新卡
