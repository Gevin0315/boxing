<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Coin, Wallet, CreditCard, View, ShoppingCart } from '@element-plus/icons-vue'
import { listMember, delMember, updateMemberStatus, memberRecharge, memberDeduct } from '@/api/member'
import { getMemberCards, activateCard, voidCard, getCardUsageRecords, purchaseCard } from '@/api/memberCard'
import { getAvailableCards } from '@/api/membershipCard'
import { MEMBER_STATUS, GENDER } from '@/constants/dict'
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
import { getDictLabel, formatCurrency } from '@/utils/format'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import type { Member, MemberQuery } from '@/types/member'
import type { MembershipCard } from '@/types/membershipCard'

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
const memberCardsMap = ref<Map<number, MemberCard[]>>(new Map())
const cardsLoadingMap = ref<Map<number, boolean>>(new Map())
const expandedRowKeys = ref<number[]>([])

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

// 使用记录抽屉
const drawerVisible = ref(false)
const usageRecords = ref<CardUsageRecord[]>([])
const usageLoading = ref(false)
const selectedCard = ref<MemberCard | null>(null)

// 购卡弹窗
const purchaseCardVisible = ref(false)
const purchaseCardForm = reactive({
  memberId: undefined as number | undefined,
  cardId: undefined as number | undefined,
  payMethod: 3,
  paidAmount: 0,
  remark: ''
})
const availableCards = ref<MembershipCard[]>([])
const cardsLoading = ref(false)
const purchaseCardSubmitting = ref(false)

onMounted(() => {
  getList()
})

const getList = async () => {
  loading.value = true
  try {
    const res = await listMember(queryParams)
    memberList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('Failed to get member list:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

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

const handleAdd = () => {
  dialogTitle.value = '新增会员'
  memberId.value = undefined
  dialogVisible.value = true
}

const handleEdit = (row: Member) => {
  dialogTitle.value = '编辑会员'
  memberId.value = row.id
  dialogVisible.value = true
}

const handleDelete = async (row: Member) => {
  try {
    await ElMessageBox.confirm(`确定要删除会员"${row.name}"吗？`, '提示', {
      type: 'warning'
    })
    await delMember([row.id!])
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete member:', error)
    }
  }
}

const handleStatusChange = async (row: Member) => {
  try {
    await updateMemberStatus(row.id!, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    console.error('Failed to update member status:', error)
    getList()
  }
}

const handleRecharge = (row: Member) => {
  rechargeMode.value = 'recharge'
  rechargeForm.memberId = row.id
  rechargeForm.amount = 0
  rechargeVisible.value = true
}

const handleRechargeSubmit = async () => {
  if (!rechargeForm.amount || rechargeForm.amount <= 0) {
    ElMessage.warning(rechargeMode.value === 'recharge' ? '请输入有效的充值金额' : '请输入有效的扣费金额')
    return
  }
  try {
    if (rechargeMode.value === 'recharge') {
      await memberRecharge(rechargeForm.memberId!, rechargeForm.amount)
      ElMessage.success('充值成功')
    } else {
      await memberDeduct(rechargeForm.memberId!, rechargeForm.amount)
      ElMessage.success('扣费成功')
    }
    rechargeVisible.value = false
    getList()
  } catch (error) {
    console.error('Failed to submit:', error)
  }
}

const handleDeduct = (row: Member) => {
  rechargeMode.value = 'deduct'
  rechargeForm.memberId = row.id
  rechargeForm.amount = 0
  rechargeVisible.value = true
}

/** 打开购卡弹窗 */
const handlePurchaseCard = async (row: Member) => {
  purchaseCardForm.memberId = row.id
  purchaseCardForm.cardId = undefined
  purchaseCardForm.payMethod = 3
  purchaseCardForm.paidAmount = 0
  purchaseCardForm.remark = ''
  cardsLoading.value = true
  purchaseCardVisible.value = true
  try {
    availableCards.value = await getAvailableCards()
  } catch (error) {
    console.error('Failed to load available cards:', error)
    availableCards.value = []
  } finally {
    cardsLoading.value = false
  }
}

/** 选择卡片时更新金额 */
const handleCardChange = (cardId: number) => {
  const card = availableCards.value.find(c => c.id === cardId)
  if (card) {
    purchaseCardForm.paidAmount = card.price || 0
  }
}

/** 提交购卡 */
const handlePurchaseCardSubmit = async () => {
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
    const result = await purchaseCard({
      memberId: purchaseCardForm.memberId!,
      cardId: purchaseCardForm.cardId,
      payMethod: purchaseCardForm.payMethod,
      paidAmount: purchaseCardForm.paidAmount,
      remark: purchaseCardForm.remark || undefined
    })
    purchaseCardVisible.value = false
    if (result.status === 'completed') {
      ElMessage.success('购卡成功')
      memberCardsMap.value.delete(purchaseCardForm.memberId!)
      await loadMemberCards(purchaseCardForm.memberId!)
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

const handleDialogClose = async () => {
  dialogVisible.value = false
  await new Promise(resolve => setTimeout(resolve, 100))
  getList()
}

const handlePageChange = (page: number, pageSize: number) => {
  queryParams.pageNum = page
  queryParams.pageSize = pageSize
  getList()
}

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
    memberCardsMap.value.set(memberId, res || [])
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
    await ElMessageBox.confirm(`确定要激活卡片 "${card.cardName}" 吗？`, '激活确认', {
      confirmButtonText: '确定激活',
      cancelButtonText: '取消',
      type: 'info',
    })
    await activateCard({ memberCardId: card.id })
    ElMessage.success('激活成功')
    memberCardsMap.value.delete(memberId)
    await loadMemberCards(memberId)
  } catch (error: unknown) {
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
  } catch (error: unknown) {
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
    usageRecords.value = res || []
  } catch (error) {
    console.error('Failed to load usage records:', error)
    usageRecords.value = []
  } finally {
    usageLoading.value = false
  }
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
      :expand-row-keys="expandedRowKeys"
      @expand-change="handleExpandChange"
    >
      <!-- 展开列 -->
      <el-table-column type="expand" width="50">
        <template #default="{ row }">
          <div class="expand-content" v-loading="cardsLoadingMap.get(row.id!)">
            <div v-if="memberCardsMap.get(row.id!)?.length" class="cards-table-wrapper">
              <div class="cards-header">
                <el-icon><CreditCard /></el-icon>
                <span>持卡信息 ({{ memberCardsMap.get(row.id!)?.length || 0 }}张)</span>
              </div>
              <el-table :data="memberCardsMap.get(row.id!)" size="small" border>
                <el-table-column prop="cardName" label="卡名称" min-width="120" />
                <el-table-column prop="cardCategory" label="卡分类" width="100">
                  <template #default="{ row: card }">
                    {{ CARD_CATEGORY_MAP[card.cardCategory] || card.cardCategoryDesc }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="90">
                  <template #default="{ row: card }">
                    <el-tag :type="MEMBER_CARD_STATUS_TAG_TYPE[card.status]" size="small">
                      {{ MEMBER_CARD_STATUS_MAP[card.status] || card.statusDesc }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="剩余次数/有效期" width="140">
                  <template #default="{ row: card }">
                    <template v-if="card.cardCategory === CardCategory.GROUP_TIME">
                      {{ card.remainingDays ?? '-' }}天
                    </template>
                    <template v-else>
                      {{ card.remainingSessions ?? 0 }}次
                    </template>
                  </template>
                </el-table-column>
                <el-table-column prop="purchaseTime" label="购卡日期" width="110">
                  <template #default="{ row: card }">
                    {{ formatDate(card.purchaseTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="activationTime" label="激活日期" width="110">
                  <template #default="{ row: card }">
                    {{ formatDate(card.activationTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="expireDate" label="到期日期" width="110">
                  <template #default="{ row: card }">
                    {{ formatDate(card.expireDate) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                  <template #default="{ row: card }">
                    <el-button
                      v-if="card.canBeActivated"
                      type="success"
                      link
                      size="small"
                      @click="handleActivateCard(card, row.id)"
                    >
                      激活
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
                    <el-button type="primary" link size="small" @click="handleViewRecords(card)">
                      <el-icon><View /></el-icon>
                      记录
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-else-if="!cardsLoadingMap.get(row.id!)" class="no-cards">
              暂无持卡信息
            </div>
          </div>
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
          <el-button type="primary" link :icon="ShoppingCart" @click="handlePurchaseCard(row)">购卡</el-button>
          <el-button type="warning" link :icon="Wallet" @click="handleDeduct(row)">扣费</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
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
      width="400px"
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
      width="500px"
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
            <el-option label="现金" :value="3" />
            <el-option label="刷卡" :value="4" />
            <el-option label="微信" :value="1" />
            <el-option label="支付宝" :value="2" />
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

    <!-- 使用记录抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      :title="`卡片使用记录 - ${selectedCard?.cardNo || ''}`"
      size="600px"
    >
      <el-table v-loading="usageLoading" :data="usageRecords" stripe border size="small">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="usageType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.usageType === 1 ? 'success' : row.usageType === 2 ? 'primary' : row.usageType === 3 ? 'info' : 'danger'" size="small">
              {{ CARD_USAGE_TYPE_MAP[row.usageType] || row.usageTypeDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程" min-width="120">
          <template #default="{ row }">
            {{ row.courseName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="次数变化" width="100">
          <template #default="{ row }">
            <template v-if="row.sessionsBefore !== undefined && row.sessionsAfter !== undefined">
              {{ row.sessionsBefore }} -> {{ row.sessionsAfter }}
            </template>
            <template v-else>-</template>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="100">
          <template #default="{ row }">
            {{ row.operatorName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="160" />
        <el-table-column prop="remark" label="备注" min-width="120">
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!usageLoading && !usageRecords.length" class="no-records">
        暂无使用记录
      </div>
    </el-drawer>
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

.no-cards {
  text-align: center;
  color: #909399;
  padding: 20px;
  font-size: 14px;
}

.no-records {
  text-align: center;
  color: #909399;
  padding: 40px;
  font-size: 14px;
}
</style>
