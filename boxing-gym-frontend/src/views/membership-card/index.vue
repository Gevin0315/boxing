<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { getCardPage, createCard, updateCardStatus, deleteCard } from '@/api/membershipCard'
import {
  CARD_CATEGORY_OPTIONS,
  CARD_CATEGORY_MAP,
  CARD_STATUS_OPTIONS,
  CARD_STATUS_MAP,
} from '@/types/membershipCard'
import { formatCurrency } from '@/utils/format'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import type { MembershipCard, MembershipCardQuery } from '@/types/membershipCard'

import type { CardCategory, CardStatus } from '@/types/membershipCard'

const loading = ref(false)
const cardList = ref<MembershipCard[]>([])
const total = ref(0)

const queryParams = reactive<MembershipCardQuery>({
  current: 1,
  size: 10,
  cardCategory: undefined,
  status: undefined,
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const cardId = ref<number>()

onMounted(() => {
  getList()
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getCardPage(queryParams)
    // 后端返回 MyBatis-Plus Page 对象，数据在 records 字段
    cardList.value = res?.records || []
    total.value = res?.total || 0
  } catch (error) {
    console.error('Failed to get card list:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.current = 1
  getList()
}

const handleReset = () => {
  Object.assign(queryParams, {
    current: 1,
    size: 10,
    cardCategory: undefined,
    status: undefined,
  })
  getList()
}

const handleAdd = () => {
  dialogTitle.value = '新增卡片'
  cardId.value = undefined
  dialogVisible.value = true
}

const handleEdit = (row: MembershipCard) => {
  dialogTitle.value = '编辑卡片'
  cardId.value = row.id
  dialogVisible.value = true
}

const handleStatusChange = async (row: MembershipCard, status: number) => {
  const statusText = status === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(`确定要${statusText}该卡片吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await updateCardStatus(row.id, status)
    ElMessage.success(`${statusText}成功`)
    getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('Failed to update status:', error)
      ElMessage.error(`${statusText}失败，请重试`)
    }
  }
}

const handleDelete = async (row: MembershipCard) => {
  try {
    await ElMessageBox.confirm('确定要删除该卡片吗？此操作不可恢复!', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteCard(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('Failed to delete card:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const handlePageChange = (page: number) => {
  queryParams.current = page
  getList()
}

const handleSizeChange = (size: number) => {
  queryParams.size = size
  queryParams.current = 1
  getList()
}

const handleDialogClose = () => {
  dialogVisible.value = false
}
</script>

<template>
  <div class="page-container">
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="卡分类">
          <el-select v-model="queryParams.cardCategory" placeholder="请选择卡分类" clearable style="width: 140px">
            <el-option
              v-for="item in CARD_CATEGORY_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option
              v-for="item in CARD_STATUS_OPTIONS"
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
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增卡片</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table :data="cardList" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="cardName" label="卡名称" min-width="120" />
      <el-table-column prop="cardCategoryDesc" label="卡分类" width="110" />
      <el-table-column prop="cardTypeDesc" label="卡类型" width="110" />
      <el-table-column prop="durationDays" label="有效天数" width="90">
        <template #default="{ row }">
          {{ row.durationDays ? `${row.durationDays}天` : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="sessionCount" label="包含次数" width="90">
        <template #default="{ row }">
          {{ row.sessionCount ? `${row.sessionCount}次` : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="price" label="售价" width="100">
        <template #default="{ row }">
          <span style="color: #f56c6c; font-weight: 500">{{ formatCurrency(row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="originalPrice" label="原价" width="100">
        <template #default="{ row }">
          {{ row.originalPrice ? formatCurrency(row.originalPrice) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="activationDeadlineDays" label="激活期限" width="90">
        <template #default="{ row }">
          {{ row.activationDeadlineDays }}天
        </template>
      </el-table-column>
      <el-table-column prop="statusDesc" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.statusDesc }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="70" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button
            :type="row.status === 1 ? 'warning' : 'success'"
            link
            @click="handleStatusChange(row, row.status === 1 ? 0 : 1)"
          >
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      :total="total"
      :page="queryParams.current"
      :page-size="queryParams.size"
      @update:page-change="handlePageChange"
      @update:size-change="handleSizeChange"
    />

    <!-- 弹窗 -->
    <AddEdit
      v-model="dialogVisible"
      :title="dialogTitle"
      :card-id="cardId"
      @close="handleDialogClose"
      @success="getList"
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
