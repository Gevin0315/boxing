<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Coin, Wallet, CreditCard, ShoppingCart } from '@element-plus/icons-vue'
import { listMember, delMember, updateMemberStatus, memberRecharge, memberDeduct } from '@/api/member'
import { MEMBER_STATUS, GENDER } from '@/constants/dict'
import { DIALOG_WIDTH, DELAY, PAYMENT_METHOD } from '@/constants/ui'
import { getDictLabel, formatCurrency, debounce } from '@/utils/format'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import CardListDialog from './CardListDialog.vue'
import type { Member, MemberQuery } from '@/types/member'

const loading = ref(false)
const memberList = ref<Member[]>([])
const total = ref(0)

const queryParams = reactive<MemberQuery>({
  pageNum: 1,
  pageSize: 10,
  name: '',
  phone: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const memberId = ref<number>()
const rechargeVisible = ref(false)
const rechargeMode = ref<'recharge' | 'deduct'>('recharge')
const rechargeForm = reactive({
  memberId: undefined as number | undefined,
  amount: 0
})

// 展开行相关
const memberCardsMap = ref<Map<number, any[]>>(new Map())
const cardsLoadingMap = ref<Map<number, boolean>>(new Map())
const expandedRowKeys = ref<number[]>([])

// 卡片列表弹窗
const cardListDialogVisible = ref(false)
const currentMemberId = ref<number | null>(null)
const currentMemberName = ref('')

// 购卡弹窗
const purchaseCardVisible = ref(false)
const purchaseCardForm = reactive({
  memberId: undefined as number | undefined,
  cardId: undefined as number | undefined,
  payMethod: PAYMENT_METHOD.CASH,
  paidAmount: 0,
  remark: ''
})
const availableCards = ref<any[]>([])
const cardsLoading = ref(false)
const purchaseCardSubmitting = ref(false)

/**
 * 组件挂载时初始化数据
 */
onMounted(() => {
  getList()
})

/**
 * 获取会员列表
 */
const getList = async () => {
  loading.value = true
  try {
    const res = await listMember(queryParams)
    memberList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('Failed to get member list:', error)
    ElMessage.error('获取会员列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

/**
 * 处理重置
 */
const handleReset = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    name: '',
    phone: '',
    status: ''
  })
  getList()
}

/**
 * 处理新增会员
 */
const handleAdd = () => {
  dialogTitle.value = '新增会员'
  memberId.value = undefined
  dialogVisible.value = true
}

/**
 * 处理编辑会员
 */
const handleEdit = (row: Member) => {
  dialogTitle.value = '编辑会员'
  memberId.value = row.id
  dialogVisible.value = true
}

/**
 * 处理删除会员
 */
const handleDelete = async (row: Member) => {
  if (!row.id) {
    ElMessage.error('会员ID不存在')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除会员"${row.name}"吗？`, '提示', {
      type: 'warning'
    })
    await delMember([row.id])
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete member:', error)
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 处理会员状态变更
 */
const handleStatusChange = async (row: Member) => {
  if (!row.id) {
    ElMessage.error('会员ID不存在')
    return
  }

  try {
    await updateMemberStatus(row.id, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    console.error('Failed to update member status:', error)
    ElMessage.error('状态修改失败')
    getList()
  }
}

/**
 * 处理会员充值
 */
const handleRecharge = (row: Member) => {
  rechargeMode.value = 'recharge'
  rechargeForm.memberId = row.id
  rechargeForm.amount = 0
  rechargeVisible.value = true
}

/**
 * 处理充值/扣费提交
 */
const handleRechargeSubmit = async () => {
  if (!rechargeForm.memberId) {
    ElMessage.error('会员ID不存在')
    return
  }

  if (!rechargeForm.amount || rechargeForm.amount <= 0) {
    ElMessage.warning(rechargeMode.value === 'recharge' ? '请输入有效的充值金额' : '请输入有效的扣费金额')
    return
  }

  try {
    if (rechargeMode.value === 'recharge') {
      await memberRecharge(rechargeForm.memberId, rechargeForm.amount)
      ElMessage.success('充值成功')
    } else {
      await memberDeduct(rechargeForm.memberId, rechargeForm.amount)
      ElMessage.success('扣费成功')
    }
    rechargeVisible.value = false
    getList()
  } catch (error) {
    console.error('Failed to submit:', error)
    ElMessage.error(rechargeMode.value === 'recharge' ? '充值失败' : '扣费失败')
  }
}

/**
 * 处理会员扣费
 */
const handleDeduct = (row: Member) => {
  rechargeMode.value = 'deduct'
  rechargeForm.memberId = row.id
  rechargeForm.amount = 0
  rechargeVisible.value = true
}

/**
 * 打开购卡弹窗
 */
const handlePurchaseCard = async (row: Member) => {
  if (!row.id) {
    ElMessage.error('会员ID不存在')
    return
  }

  // 防止重复打开
  if (purchaseCardVisible.value) {
    return
  }

  purchaseCardForm.memberId = row.id
  purchaseCardForm.cardId = undefined
  purchaseCardForm.payMethod = PAYMENT_METHOD.CASH
  purchaseCardForm.paidAmount = 0
  purchaseCardForm.remark = ''
  cardsLoading.value = true
  purchaseCardVisible.value = true

  try {
    const { getAvailableCards } = await import('@/api/membershipCard')
    availableCards.value = await getAvailableCards()
  } catch (error) {
    console.error('Failed to load available cards:', error)
    ElMessage.error('加载可用卡片失败')
    availableCards.value = []
  } finally {
    cardsLoading.value = false
  }
}

/**
 * 选择卡片时更新金额
 */
const handleCardChange = (cardId: number) => {
  const card = availableCards.value.find(c => c.id === cardId)
  if (card) {
    purchaseCardForm.paidAmount = card.price || 0
  }
}

/**
 * 提交购卡
 */
const handlePurchaseCardSubmit = async () => {
  if (!purchaseCardForm.memberId) {
    ElMessage.error('会员ID不存在')
    return
  }

  if (!purchaseCardForm.cardId) {
    ElMessage.warning('请选择要购买的卡片')
    return
  }

  if (!purchaseCardForm.paidAmount || purchaseCardForm.paidAmount <= 0) {
    ElMessage.warning('请输入有效的支付金额')
    return
  }

  purchaseCardSubmitting.value = true
  try {
    const { purchaseCard } = await import('@/api/memberCard')
    const result = await purchaseCard({
      memberId: purchaseCardForm.memberId,
      cardId: purchaseCardForm.cardId,
      payMethod: purchaseCardForm.payMethod,
      paidAmount: purchaseCardForm.paidAmount,
      remark: purchaseCardForm.remark || undefined
    })

    purchaseCardVisible.value = false
    if (result.status === 'completed') {
      ElMessage.success('购卡成功')
      memberCardsMap.value.delete(purchaseCardForm.memberId)
      await loadMemberCards(purchaseCardForm.memberId)
    } else if (result.status === 'pending') {
      ElMessage.info('订单已创建，等待支付')
    }
    getList()
  } catch (error) {
    console.error('Failed to purchase card:', error)
    ElMessage.error('购卡失败')
  } finally {
    purchaseCardSubmitting.value = false
  }
}

/**
 * 处理弹窗关闭
 */
const handleDialogClose = async () => {
  dialogVisible.value = false
  await new Promise(resolve => setTimeout(resolve, DELAY.DIALOG_CLOSE))
  getList()
}

/**
 * 处理分页变化
 */
const handlePageChange = (page: number, pageSize: number) => {
  queryParams.pageNum = page
  queryParams.pageSize = pageSize
  getList()
}

/**
 * 处理行展开
 */
const handleExpandChange = async (row: Member, expandedRows: Member[]) => {
  if (!row.id) return

  expandedRowKeys.value = expandedRows.map(r => r.id!).filter(Boolean)

  if (expandedRows.some(r => r.id === row.id) && !memberCardsMap.value.has(row.id)) {
    await loadMemberCards(row.id)
  }
}

/**
 * 加载会员卡片
 */
const loadMemberCards = async (memberId: number) => {
  cardsLoadingMap.value.set(memberId, true)
  try {
    const { getMemberCards } = await import('@/api/memberCard')
    const res = await getMemberCards(memberId)
    memberCardsMap.value.set(memberId, res || [])
  } catch (error) {
    console.error('Failed to load member cards:', error)
    ElMessage.error('加载会员卡片失败')
    memberCardsMap.value.set(memberId, [])
  } finally {
    cardsLoadingMap.value.set(memberId, false)
  }
}

/**
 * 打开卡片列表弹窗（带防抖）
 */
const debouncedOpenCardListDialog = debounce(async (member: Member) => {
  if (!member.id) {
    ElMessage.error('会员ID不存在')
    return
  }

  currentMemberId.value = member.id
  currentMemberName.value = member.name
  cardListDialogVisible.value = true
}, 300)

/**
 * 打开卡片列表弹窗
 */
const openCardListDialog = (member: Member) => {
  debouncedOpenCardListDialog(member)
}

/**
 * 处理卡片列表弹窗刷新
 */
const handleCardListRefresh = () => {
  getList()
}

/**
 * 格式化日期
 */
const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return date.split('T')[0] || date.split(' ')[0]
}
</script>

<template>
  <div class="page-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px" @change="handleSearch">
          <el-option
            v-for="item in MEMBER_STATUS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增会员</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="memberList"
      stripe
      border
      :row-key="(row) => row.id"
    >
      <el-table-column prop="status" label="状态" width="100" fixed="left">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="'1'"
            :inactive-value="'0'"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
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
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="name" label="姓名" min-width="100" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="{ row }">
          {{ getDictLabel(GENDER, row.gender) }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="remainingBalance" label="余额" width="120">
        <template #default="{ row }">
          {{ formatCurrency(row.remainingBalance || 0) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" link :icon="Coin" @click="handleRecharge(row)">充值</el-button>
          <el-button type="primary" link :icon="ShoppingCart" @click="handlePurchaseCard(row)">购卡</el-button>
          <el-button type="warning" link :icon="Wallet" @click="handleDeduct(row)">扣费</el-button>
          <el-tooltip v-if="row.hasActiveCard" content="该会员有有效卡，不可删除" placement="top">
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </el-tooltip>
          <el-button v-else type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      :total="total"
      :page="queryParams.pageNum"
      :page-size="queryParams.pageSize"
      @update="handlePageChange"
    />

    <!-- 弹窗 -->
    <AddEdit
      v-model="dialogVisible"
      :title="dialogTitle"
      :member-id="memberId"
      @close="handleDialogClose"
    />

    <!-- 充值/扣费弹窗 -->
    <el-dialog
      v-model="rechargeVisible"
      :title="rechargeMode === 'recharge' ? '会员充值' : '会员扣费'"
      :width="DIALOG_WIDTH.SMALL"
    >
      <el-form label-width="80px">
        <el-form-item :label="rechargeMode === 'recharge' ? '充值金额' : '扣费金额'">
          <el-input-number v-model="rechargeForm.amount" :min="0" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeVisible = false">取消</el-button>
        <el-button :type="rechargeMode === 'recharge' ? 'primary' : 'warning'" @click="handleRechargeSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 购卡弹窗 -->
    <el-dialog
      v-model="purchaseCardVisible"
      title="购买会员卡"
      :width="DIALOG_WIDTH.MEDIUM"
    >
      <el-form label-width="80px">
        <el-form-item label="选择卡片">
          <el-select
            v-model="purchaseCardForm.cardId"
            placeholder="请选择卡片"
            style="width: 100%"
            :loading="cardsLoading"
            @change="handleCardChange"
          >
            <el-option
              v-for="card in availableCards"
              :key="card.id"
              :label="`${card.cardName} - ${formatCurrency(card.price)}`"
              :value="card.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="purchaseCardForm.payMethod" placeholder="请选择支付方式" style="width: 100%">
            <el-option label="微信" :value="PAYMENT_METHOD.WECHAT" />
            <el-option label="支付宝" :value="PAYMENT_METHOD.ALIPAY" />
            <el-option label="现金" :value="PAYMENT_METHOD.CASH" />
            <el-option label="刷卡" :value="PAYMENT_METHOD.BANK_CARD" />
          </el-select>
        </el-form-item>
        <el-form-item label="实付金额">
          <el-input-number
            v-model="purchaseCardForm.paidAmount"
            :min="0"
            :precision="2"
            :step="10"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="purchaseCardForm.remark" placeholder="请输入备注（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="purchaseCardVisible = false">取消</el-button>
        <el-button type="primary" :loading="purchaseCardSubmitting" @click="handlePurchaseCardSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 卡片列表弹窗 -->
    <CardListDialog
      v-model:visible="cardListDialogVisible"
      :member-id="currentMemberId"
      :member-name="currentMemberName"
      @refresh="handleCardListRefresh"
    />
  </div>
</template>

<style scoped>
.page-container {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}

.search-form {
  background: #f5f7fa;
  padding: 18px 18px 0;
  border-radius: 4px;
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
