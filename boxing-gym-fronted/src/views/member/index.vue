<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Coin, Wallet } from '@element-plus/icons-vue'
import { listMember, delMember, updateMemberStatus, memberRecharge, memberDeduct, generateMemberNo } from '@/api/member'
import { MEMBER_STATUS, MEMBERSHIP_LEVEL, GENDER } from '@/constants/dict'
import { getDictLabel, formatCurrency } from '@/utils/format'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import type { Member, MemberQuery } from '@/types/member'

const loading = ref(false)
const memberList = ref<Member[]>([])
const total = ref(0)

const queryParams = reactive<MemberQuery>({
  pageNum: 1,
  pageSize: 10,
  memberNo: '',
  name: '',
  phone: '',
  status: '',
  membershipLevel: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const memberId = ref<number>()
const rechargeVisible = ref(false)
const rechargeForm = reactive({
  memberId: undefined as number | undefined,
  amount: 0
})

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
    memberNo: '',
    name: '',
    phone: '',
    status: '',
    membershipLevel: ''
  })
  getList()
}

const handleAdd = async () => {
  try {
    const res = await generateMemberNo()
    dialogTitle.value = '新增会员'
    memberId.value = undefined
    dialogVisible.value = true
  } catch (error) {
    console.error('Failed to generate member no:', error)
  }
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
  rechargeForm.memberId = row.id
  rechargeForm.amount = 0
  rechargeVisible.value = true
}

const handleRechargeSubmit = async () => {
  if (!rechargeForm.amount || rechargeForm.amount <= 0) {
    ElMessage.warning('请输入有效的充值金额')
    return
  }
  try {
    await memberRecharge(rechargeForm.memberId!, rechargeForm.amount)
    ElMessage.success('充值成功')
    rechargeVisible.value = false
    getList()
  } catch (error) {
    console.error('Failed to recharge:', error)
  }
}

const handleDeduct = (row: Member) => {
  rechargeForm.memberId = row.id
  rechargeForm.amount = 0
  rechargeVisible.value = true
}

const handleDeductSubmit = async () => {
  if (!rechargeForm.amount || rechargeForm.amount <= 0) {
    ElMessage.warning('请输入有效的扣费金额')
    return
  }
  try {
    await memberDeduct(rechargeForm.memberId!, rechargeForm.amount)
    ElMessage.success('扣费成功')
    rechargeVisible.value = false
    getList()
  } catch (error) {
    console.error('Failed to deduct:', error)
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
      <el-form-item label="会员号">
        <el-input v-model="queryParams.memberNo" placeholder="请输入会员号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="item in MEMBER_STATUS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="会员等级">
        <el-select v-model="queryParams.membershipLevel" placeholder="请选择会员等级" clearable>
          <el-option
            v-for="item in MEMBERSHIP_LEVEL"
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
    <el-table v-loading="loading" :data="memberList" stripe border>
      <el-table-column type="index" label="序号" width="60" align="center" />
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
      title="会员充值"
      width="400px"
    >
      <el-form label-width="80px">
        <el-form-item label="充值金额">
          <el-input-number v-model="rechargeForm.amount" :min="0" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRechargeSubmit">确定</el-button>
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
