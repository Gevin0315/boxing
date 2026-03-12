<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, Calendar } from '@element-plus/icons-vue'
import { listSchedule, delSchedule, cancelSchedule, completeSchedule, getScheduleCalendar } from '@/api/course-schedule'
import { getCourseOptions } from '@/api/course'
import { getCoachOptions } from '@/api/coach-profile'
import { SCHEDULE_STATUS } from '@/constants/dict'
import { getDictLabel } from '@/utils/format'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import type { CourseSchedule } from '@/api/course-schedule'

const loading = ref(false)
const scheduleList = ref<CourseSchedule[]>([])
const total = ref(0)
const courseOptions = ref<Array<{ value: number; label: string; type: number }>>([])
const coachOptions = ref<Array<{ value: number; label: string }>>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined as number | undefined,
  coachId: undefined as number | undefined,
  status: '',
  startDate: '',
  endDate: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const scheduleId = ref<number>()

onMounted(async () => {
  await Promise.all([loadCourseOptions(), loadCoachOptions()])
  await getList()
})

const loadCourseOptions = async () => {
  try {
    const res = await getCourseOptions()
    courseOptions.value = res || []
  } catch (error) {
    console.error('Failed to load course options:', error)
  }
}

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
    const res = await listSchedule(queryParams)
    // 使用已加载的课程和教练选项来丰富数据
    const courseMap = new Map(courseOptions.value.map(item => [item.value, { label: item.label, type: item.type }]))
    const coachMap = new Map(coachOptions.value.map(item => [item.value, item.label]))

    // 后端 type: 1-团课, 2-私教课 -> 前端: 'group', 'private'
    const toFrontendType = (type?: number) => (type === 2 ? 'private' : 'group')

    scheduleList.value = (res.rows || []).map(item => {
      const course = courseMap.get(item.courseId)
      return {
        ...item,
        courseName: course?.label || '',
        courseType: toFrontendType(course?.type),
        coachName: coachMap.get(item.coachId) || ''
      }
    })
    total.value = res.total || 0
  } catch (error) {
    console.error('Failed to get schedule list:', error)
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
    courseId: undefined,
    coachId: undefined,
    status: '',
    startDate: '',
    endDate: ''
  })
  getList()
}

const handleAdd = () => {
  dialogTitle.value = '新增排课'
  scheduleId.value = undefined
  dialogVisible.value = true
}

const handleEdit = (row: CourseSchedule) => {
  dialogTitle.value = '编辑排课'
  scheduleId.value = row.id
  dialogVisible.value = true
}

const handleDelete = async (row: CourseSchedule) => {
  try {
    await ElMessageBox.confirm(`确定要删除这条排课记录吗？`, '提示', {
      type: 'warning'
    })
    await delSchedule([row.id!])
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete schedule:', error)
    }
  }
}

const handleCancel = async (row: CourseSchedule) => {
  try {
    const reason = await ElMessageBox.prompt('请输入取消原因', '取消排课', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await cancelSchedule(row.id!, reason.value)
    ElMessage.success('取消成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to cancel schedule:', error)
    }
  }
}

const handleComplete = async (row: CourseSchedule) => {
  try {
    await ElMessageBox.confirm('确定要完成这条排课记录吗？', '提示', {
      type: 'warning'
    })
    await completeSchedule(row.id!)
    ElMessage.success('操作成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to complete schedule:', error)
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
      <el-form-item label="课程">
        <el-select v-model="queryParams.courseId" placeholder="请选择课程" clearable filterable>
          <el-option
            v-for="item in courseOptions"
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
            v-for="item in SCHEDULE_STATUS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="排课日期">
        <el-date-picker
          v-model="queryParams.startDate"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="(val: any) => {
            queryParams.startDate = val ? val[0] : ''
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
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增排课</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="scheduleList" stripe border>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="courseName" label="课程名称" min-width="120" />
      <el-table-column prop="courseType" label="类型" width="80" />
      <el-table-column prop="coachName" label="教练" width="100" />
      <el-table-column label="排课时间" min-width="200">
        <template #default="{ row }">
          {{ row.scheduleDate }} {{ row.startTime }}-{{ row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="classroom" label="教室" width="100" />
      <el-table-column prop="maxCapacity" label="容量" width="80" align="center" />
      <el-table-column prop="currentCount" label="已报名" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : row.status === '1' ? 'warning' : 'info'">
            {{ getDictLabel(SCHEDULE_STATUS, row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === '0'" type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status === '0'" type="warning" link @click="handleCancel(row)">取消</el-button>
          <el-button v-if="row.status === '0'" type="success" link @click="handleComplete(row)">完成</el-button>
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
      :schedule-id="scheduleId"
      :course-options="courseOptions"
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
