<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Picture } from '@element-plus/icons-vue'
import { listCoach, delCoach, updateCoachStatus } from '@/api/coach-profile'
import { COACH_STATUS, COACH_LEVEL, GENDER } from '@/constants/dict'
import { getDictLabel } from '@/utils/format'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import type { Coach, CoachQuery } from '@/types/coach'

const loading = ref(false)
const coachList = ref<Coach[]>([])
const total = ref(0)

const queryParams = reactive<CoachQuery>({
  pageNum: 1,
  pageSize: 10,
  coachNo: '',
  name: '',
  phone: '',
  specialties: '',
  level: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const coachId = ref<number>()

onMounted(() => {
  getList()
})

const getList = async () => {
  loading.value = true
  try {
    const res = await listCoach(queryParams)
    coachList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('Failed to get coach list:', error)
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
    coachNo: '',
    name: '',
    phone: '',
    specialties: '',
    level: '',
    status: ''
  })
  getList()
}

const handleAdd = () => {
  dialogTitle.value = '新增教练'
  coachId.value = undefined
  dialogVisible.value = true
}

const handleEdit = (row: Coach) => {
  dialogTitle.value = '编辑教练'
  coachId.value = row.id
  dialogVisible.value = true
}

const handleDelete = async (row: Coach) => {
  try {
    await ElMessageBox.confirm(`确定要删除教练"${row.name}"吗？`, '提示', {
      type: 'warning'
    })
    await delCoach([row.id!])
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete coach:', error)
    }
  }
}

const handleStatusChange = async (row: Coach) => {
  try {
    await updateCoachStatus(row.id!, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    console.error('Failed to update coach status:', error)
    getList()
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
      <el-form-item label="教练号">
        <el-input v-model="queryParams.coachNo" placeholder="请输入教练号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="专长">
        <el-input v-model="queryParams.specialties" placeholder="请输入专长" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="等级">
        <el-select v-model="queryParams.level" placeholder="请选择等级" clearable>
          <el-option
            v-for="item in COACH_LEVEL"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="item in COACH_STATUS"
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
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增教练</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="coachList" stripe border>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="coachNo" label="教练号" width="120" />
      <el-table-column label="头像" width="80" align="center">
        <template #default="{ row }">
          <el-avatar v-if="row.imageUrl" :src="row.imageUrl" :size="40" />
          <el-avatar v-else :size="40">{{ row.name?.charAt(0) }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="姓名" min-width="100" />
      <el-table-column prop="gender" label="性别" width="60">
        <template #default="{ row }">
          {{ getDictLabel(GENDER, row.gender) }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="specialties" label="专长" min-width="150" show-overflow-tooltip />
      <el-table-column prop="level" label="等级" width="100">
        <template #default="{ row }">
          {{ getDictLabel(COACH_LEVEL, row.level) }}
        </template>
      </el-table-column>
      <el-table-column prop="hireDate" label="入职日期" width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : row.status === '1' ? 'warning' : 'info'">
            {{ getDictLabel(COACH_STATUS, row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
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
      :coach-id="coachId"
      @close="handleDialogClose"
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
