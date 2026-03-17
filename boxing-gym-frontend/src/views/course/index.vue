<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { listCourse, delCourse, updateCourseStatus } from '@/api/course'
import { COURSE_TYPE, COURSE_CATEGORY, COURSE_LEVEL, COURSE_STATUS } from '@/constants/dict'
import { getDictLabel, formatCurrency, formatMinutes } from '@/utils/format'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import { getCoachOptions } from '@/api/coach-profile'
import type { Course, CourseQuery } from '@/types/course'

const loading = ref(false)
const courseList = ref<Course[]>([])
const total = ref(0)
const coachOptions = ref<Array<{ value: number; label: string }>>([])

const queryParams = reactive<CourseQuery>({
  pageNum: 1,
  pageSize: 10,
  courseName: '',
  courseType: '',
  category: '',
  level: '',
  coachId: undefined,
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const courseId = ref<number>()

onMounted(async () => {
  await loadCoachOptions()
  await getList()
})

const loadCoachOptions = async () => {
  try {
    const res = await getCoachOptions()
    coachOptions.value = res || []
  } catch (error) {
    console.error('Failed to load coach options:', error)
  }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await listCourse(queryParams)
    courseList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('Failed to get course list:', error)
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
    courseName: '',
    courseType: '',
    category: '',
    level: '',
    coachId: undefined,
    status: ''
  })
  getList()
}

const handleAdd = () => {
  dialogTitle.value = '新增课程'
  courseId.value = undefined
  dialogVisible.value = true
}

const handleEdit = (row: Course) => {
  dialogTitle.value = '编辑课程'
  courseId.value = row.id
  dialogVisible.value = true
}

const handleDelete = async (row: Course) => {
  try {
    await ElMessageBox.confirm(`确定要删除课程"${row.courseName}"吗？`, '提示', {
      type: 'warning'
    })
    await delCourse([row.id!])
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete course:', error)
    }
  }
}

const handleStatusChange = async (row: Course) => {
  try {
    await updateCourseStatus(row.id!, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    console.error('Failed to update course status:', error)
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
      <el-form-item label="课程名称">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="课程类型">
        <el-select v-model="queryParams.courseType" placeholder="请选择课程类型" clearable>
          <el-option
            v-for="item in COURSE_TYPE"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="课程分类">
        <el-select v-model="queryParams.category" placeholder="请选择课程分类" clearable>
          <el-option
            v-for="item in COURSE_CATEGORY"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="课程难度">
        <el-select v-model="queryParams.level" placeholder="请选择课程难度" clearable>
          <el-option
            v-for="item in COURSE_LEVEL"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="教练">
        <el-select v-model="queryParams.coachId" placeholder="请选择教练" clearable filterable>
          <el-option
            v-for="item in coachOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="item in COURSE_STATUS"
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
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增课程</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="courseList" stripe border>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="courseName" label="课程名称" min-width="150" />
      <el-table-column prop="courseType" label="课程类型" width="100">
        <template #default="{ row }">
          {{ getDictLabel(COURSE_TYPE, row.courseType) }}
        </template>
      </el-table-column>
      <el-table-column prop="category" label="课程分类" width="120">
        <template #default="{ row }">
          {{ getDictLabel(COURSE_CATEGORY, row.category) }}
        </template>
      </el-table-column>
      <el-table-column prop="level" label="难度" width="100">
        <template #default="{ row }">
          {{ getDictLabel(COURSE_LEVEL, row.level) }}
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长" width="100">
        <template #default="{ row }">
          {{ formatMinutes(row.duration) }}
        </template>
      </el-table-column>
      <el-table-column prop="maxCapacity" label="最大人数" width="100" align="center" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">
          {{ formatCurrency(row.price) }}
        </template>
      </el-table-column>
      <el-table-column prop="coachName" label="教练" width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'">
            {{ getDictLabel(COURSE_STATUS, row.status) }}
          </el-tag>
        </template>
      </el-table-column>
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
      :course-id="courseId"
      :coach-options="coachOptions"
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
