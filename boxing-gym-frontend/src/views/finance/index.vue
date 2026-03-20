<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Money } from '@element-plus/icons-vue'
import { pageFinanceOrder, delFinanceOrder, payOrder, refundOrder, generateOrderNo } from '@/api/finance-order'
import { ORDER_TYPE, PAYMENT_METHOD, PAYMENT_STATUS } from '@/constants/dict'
import { getDictLabel, formatCurrency } from '@/utils/format'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import type { FinanceOrder, FinanceOrderQuery } from '@/types/finance'

const loading = ref(false)
const orderList = ref<FinanceOrder[]>([])
const total = ref(0)

const queryParams = reactive<FinanceOrderQuery>({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  orderType: '',
  memberNo: '',
  memberName: '',
  paymentStatus: '',
  startTime: '',
  endTime: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const orderId = ref<number>()
const payVisible = ref(false)
const payForm = reactive({
  orderId: undefined as number | undefined,
  paymentMethod: '0',
  amount: 0
})

onMounted(() => {
  getList()
})

const getList = async () => {
  loading.value = true
  try {
    const res = await pageFinanceOrder(queryParams)
    orderList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('Failed to get order list:', error)
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
    orderNo: '',
    orderType: '',
    memberNo: '',
    memberName: '',
    paymentStatus: '',
    startTime: '',
    endTime: ''
  })
  getList()
}

const handleAdd = async () => {
  try {
    const res = await generateOrderNo()
    dialogTitle.value = '新增订单'
    orderId.value = undefined
    dialogVisible.value = true
  } catch (error) {
    console.error('Failed to generate order no:', error)
  }
}

const handleEdit = (row: FinanceOrder) => {
  dialogTitle.value = '编辑订单'
  orderId.value = row.id
  dialogVisible.value = true
}

const handleDelete = async (row: FinanceOrder) => {
  try {
    await ElMessageBox.confirm(`确定要删除订单"${row.orderNo}"吗？`, '提示', {
      type: 'warning'
    })
    await delFinanceOrder([row.id!])
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete order:', error)
    }
  }
}

const handlePay = (row: FinanceOrder) => {
  payForm.orderId = row.id
  payForm.paymentMethod = '0'
  payForm.amount = row.amount - row.paidAmount
  payVisible.value = true
}

const handlePaySubmit = async () => {
  if (!payForm.amount || payForm.amount <= 0) {
    ElMessage.warning('请输入有效的支付金额')
    return
  }
  try {
    await payOrder(payForm.orderId!, payForm.paymentMethod, payForm.amount)
    ElMessage.success('支付成功')
    payVisible.value = false
    getList()
  } catch (error) {
    console.error('Failed to pay order:', error)
  }
}

const handleRefund = async (row: FinanceOrder) => {
  try {
    const reason = await ElMessageBox.prompt('请输入退款原因', '退款', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入退款原因'
    })
    await refundOrder(row.id!, reason.value)
    ElMessage.success('退款成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to refund order:', error)
    }
  }
}

const handleDialogClose = () => {
  dialogVisible.value = false
  getList()
}

const handlePageChange = (page: number, pageSize: number) => {
  queryParams.pageNum = page
  queryParams.pageSize = pageSize
  getList()
}
</script>

<template>
  <div class="page-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" inline class="search-form">
      <el-form-item label="订单号">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="订单类型">
        <el-select v-model="queryParams.orderType" placeholder="请选择订单类型" clearable style="width: 120px">
          <el-option
            v-for="item in ORDER_TYPE"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="会员姓名">
        <el-input v-model="queryParams.memberName" placeholder="请输入会员姓名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="会员姓名">
        <el-input v-model="queryParams.memberName" placeholder="请输入会员姓名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="支付状态">
        <el-select v-model="queryParams.paymentStatus" placeholder="请选择支付状态" clearable style="width: 120px">
          <el-option
            v-for="item in PAYMENT_STATUS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="queryParams.startTime"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="(val: any) => {
            queryParams.startTime = val ? val[0] : ''
            queryParams.endDate = val ? val[1] : ''
          }"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增订单</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="orderList" stripe border>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="orderNo" label="订单号" width="160" />
      <el-table-column prop="orderType" label="订单类型" width="100">
        <template #default="{ row }">
          {{ getDictLabel(ORDER_TYPE, row.orderType) }}
        </template>
      </el-table-column>
      <el-table-column prop="memberName" label="会员姓名" width="120" />
      <el-table-column prop="courseName" label="课程" width="150" show-overflow-tooltip />
      <el-table-column prop="amount" label="订单金额" width="120">
        <template #default="{ row }">
          {{ formatCurrency(row.amount) }}
        </template>
      </el-table-column>
      <el-table-column prop="paidAmount" label="已付金额" width="120">
        <template #default="{ row }">
          {{ formatCurrency(row.paidAmount) }}
        </template>
      </el-table-column>
      <el-table-column prop="paymentMethod" label="支付方式" width="100">
        <template #default="{ row }">
          {{ getDictLabel(PAYMENT_METHOD, row.paymentMethod) }}
        </template>
      </el-table-column>
      <el-table-column prop="paymentStatus" label="支付状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.paymentStatus === '1' ? 'success' : row.paymentStatus === '2' ? 'warning' : 'danger'">
            {{ getDictLabel(PAYMENT_STATUS, row.paymentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.paymentStatus !== '1'" type="success" link :icon="Money" @click="handlePay(row)">支付</el-button>
          <el-button v-if="row.paymentStatus === '1'" type="warning" link @click="handleRefund(row)">退款</el-button>
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
      :order-id="orderId"
      @close="handleDialogClose"
    />

    <!-- 支付弹窗 -->
    <el-dialog
      v-model="payVisible"
      title="订单支付"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="支付方式">
          <el-select v-model="payForm.paymentMethod" placeholder="请选择支付方式" style="width: 100%">
            <el-option
              v-for="item in PAYMENT_METHOD"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="支付金额">
          <el-input-number v-model="payForm.amount" :min="0" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePaySubmit">确定支付</el-button>
      </template>
    </el-dialog>
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
