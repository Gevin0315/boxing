<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, View } from '@element-plus/icons-vue'
import { getMemberCards, activateCard, voidCard, getCardUsageRecords, purchaseCard } from '@/api/memberCard'
import { getAvailableCards } from '@/api/membershipCard'
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
import { DIALOG_WIDTH } from '@/constants/ui'
import type { MembershipCard } from '@/types/membershipCard'

/**
 * 格式化日期
 */
function formatDate(date: string | undefined): string {
  if (!date) return '-'
  return date.split('T')[0] || date.split(' ')[0]
}

interface Props {
  visible: boolean
  memberId: number | null
  memberName: string
}

interface Emits {
  (e: 'update:visible', value: boolean): void
  (e: 'purchase-card'): void
  (e: 'refresh'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const loading = ref(false)
const cards = ref<MemberCard[]>([])

// 购卡弹窗
const purchaseCardVisible = ref(false)
const purchaseCardForm = ref({
  memberId: undefined as number | undefined,
  cardId: undefined as number | undefined,
  payMethod: 3,
  paidAmount: 0,
  remark: ''
})
const availableCards = ref<MembershipCard[]>([])
const cardsLoading = ref(false)
const purchaseCardSubmitting = ref(false)

// 使用记录抽屉
const drawerVisible = ref(false)
const usageRecords = ref<CardUsageRecord[]>([])
const usageLoading = ref(false)
const selectedCard = ref<MemberCard | null>(null)

/** 计算属性：当前会员的卡片列表 */
const currentMemberCards = computed(() => {
  if (!props.memberId) return []
  return cards.value
})

/** 监听弹窗显示状态和会员ID变化 */
watch(
  () => [props.visible, props.memberId] as const,
  async ([visible, memberId], [oldVisible]) => {
    // 仅在弹窗打开时加载数据（避免关闭时的 memberId 变化触发加载）
    if (visible && memberId && visible !== oldVisible) {
      await loadCards()
    }
  }
)

/**
 * 加载会员卡片列表
 */
async function loadCards() {
  if (!props.memberId) return

  loading.value = true
  try {
    const res = await getMemberCards(props.memberId)
    cards.value = res || []
  } catch (error) {
    console.error('Failed to load member cards:', error)
    ElMessage.error('加载会员卡片失败')
    cards.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 处理弹窗关闭
 */
function handleClose() {
  emit('update:visible', false)
}

/**
 * 打开购卡弹窗
 */
async function handlePurchaseCard() {
  if (!props.memberId) return

  purchaseCardForm.value.memberId = props.memberId
  purchaseCardForm.value.cardId = undefined
  purchaseCardForm.value.payMethod = 3
  purchaseCardForm.value.paidAmount = 0
  purchaseCardForm.value.remark = ''
  cardsLoading.value = true
  purchaseCardVisible.value = true

  try {
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
function handleCardChange(cardId: number) {
  const card = availableCards.value.find(c => c.id === cardId)
  if (card) {
    purchaseCardForm.value.paidAmount = card.price || 0
  }
}

/**
 * 提交购卡
 */
async function handlePurchaseCardSubmit() {
  if (!purchaseCardForm.value.cardId) {
    ElMessage.warning('请选择要购买的卡片')
    return
  }
  if (!purchaseCardForm.value.paidAmount || purchaseCardForm.value.paidAmount <= 0) {
    ElMessage.warning('请输入有效的支付金额')
    return
  }

  purchaseCardSubmitting.value = true
  try {
    const result = await purchaseCard({
      memberId: purchaseCardForm.value.memberId!,
      cardId: purchaseCardForm.value.cardId,
      payMethod: purchaseCardForm.value.payMethod,
      paidAmount: purchaseCardForm.value.paidAmount,
      remark: purchaseCardForm.value.remark || undefined
    })

    purchaseCardVisible.value = false
    if (result.status === 'completed') {
      ElMessage.success('购卡成功')
      emit('refresh')
      await loadCards()
    } else if (result.status === 'pending') {
      ElMessage.info('订单已创建，等待支付')
    }
  } catch (error) {
    console.error('Failed to purchase card:', error)
    ElMessage.error('购卡失败')
  } finally {
    purchaseCardSubmitting.value = false
  }
}

/**
 * 激活卡片
 */
async function handleActivateCard(card: MemberCard) {
  if (!props.memberId) return

  try {
    await ElMessageBox.confirm(`确定要激活卡片 "${card.cardName}" 吗？`, '激活确认', {
      confirmButtonText: '确定激活',
      cancelButtonText: '取消',
      type: 'info',
    })
    await activateCard({ memberCardId: card.id })
    ElMessage.success('激活成功')
    emit('refresh')
    await loadCards()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error('Failed to activate card:', error)
      ElMessage.error('激活失败')
    }
  }
}

/**
 * 作废卡片
 */
async function handleVoidCard(card: MemberCard) {
  if (!props.memberId) return

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
    emit('refresh')
    await loadCards()
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error('Failed to void card:', error)
      ElMessage.error('作废失败')
    }
  }
}

/**
 * 查看使用记录
 */
async function handleViewRecords(card: MemberCard) {
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
  <el-dialog
    :model-value="visible"
    :title="`${memberName} 的会员卡`"
    width="1000px"
    @update:model-value="handleClose"
  >
    <!-- Toolbar -->
    <div style="margin-bottom: 16px;">
      <el-button type="primary" @click="handlePurchaseCard">
        <el-icon><Plus /></el-icon>
        购买会员卡
      </el-button>
    </div>

    <!-- Card Table -->
    <el-table
      :data="currentMemberCards"
      v-loading="loading"
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
            @click="handleActivateCard(row)"
          >
            激活
          </el-button>
          <el-button
            v-if="row.status === MemberCardStatus.ACTIVE"
            type="danger"
            link
            size="small"
            @click="handleVoidCard(row)"
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

    <!-- 购卡弹窗 -->
    <el-dialog
      v-model="purchaseCardVisible"
      title="购买会员卡"
      :width="DIALOG_WIDTH.MEDIUM"
      append-to-body
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
              :label="`${card.cardName} - ¥${(card.price || 0).toFixed(2)}`"
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
  </el-dialog>
</template>

<style scoped>
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
