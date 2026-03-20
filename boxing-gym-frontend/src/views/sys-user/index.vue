<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { listSysUser, delSysUser, updateUserStatus, resetUserPassword, getRoleList } from '@/api/sys-user'
import { USER_STATUS } from '@/constants/dict'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import type { SysUser, SysUserQuery, RoleOption } from '@/types/sys-user'

const loading = ref(false)
const userList = ref<SysUser[]>([])
const total = ref(0)
const roleOptions = ref<RoleOption[]>([])

const queryParams = reactive<SysUserQuery>({
  pageNum: 1,
  pageSize: 10,
  username: '',
  realName: '',
  phone: '',
  status: undefined,
  role: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const userId = ref<number>()

onMounted(() => {
  getList()
  getRoles()
})

const getRoles = async () => {
  try {
    roleOptions.value = await getRoleList()
  } catch (error) {
    console.error('Failed to get role list:', error)
  }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await listSysUser(queryParams)
    userList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    // 忽略请求被取消的情况
    if (error?.name === 'CanceledError' || error?.code === 'ERR_CANCELED') {
      return
    }
    console.error('Failed to get user list:', error)
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
    username: '',
    realName: '',
    phone: '',
    status: undefined,
    role: ''
  })
  getList()
}

const handleAdd = () => {
  dialogTitle.value = '新增系统用户'
  userId.value = undefined
  dialogVisible.value = true
}

const handleEdit = (row: SysUser) => {
  dialogTitle.value = '编辑系统用户'
  userId.value = row.id
  dialogVisible.value = true
}

const handleDelete = async (row: SysUser) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${row.nickname}"吗？`, '提示', {
      type: 'warning'
    })
    await delSysUser([row.id!])
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete user:', error)
    }
  }
}

const handleStatusChange = async (row: SysUser) => {
  try {
    await updateUserStatus(row.id!, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    console.error('Failed to update user status:', error)
    getList()
  }
}

const handleResetPassword = async (row: SysUser) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户"${row.nickname}"的密码吗？`, '提示', {
      type: 'warning'
    })
    await resetUserPassword(row.id!, '123456')
    ElMessage.success('密码重置成功，新密码为：123456')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to reset password:', error)
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
      <el-form-item label="用户名">
        <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px" @change="handleSearch">
          <el-option
            v-for="item in USER_STATUS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="queryParams.role" placeholder="请选择角色" clearable style="width: 140px" @change="handleSearch">
          <el-option
            v-for="item in roleOptions"
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
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="userList" stripe border>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="nickname" label="姓名" min-width="120" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
      <el-table-column prop="roleDescription" label="角色" min-width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
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
      :user-id="userId"
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
