<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, CircleCheck } from '@element-plus/icons-vue'
import { listTrainingRecord, delTrainingRecord, checkIn, checkOut } from '@/api/training-record'
import { getCourseOptions } from '@/api/course'
import { getCoachOptions } from '@/api/coach-profile'
import { CHECKIN_STATUS } from '@/constants/dict'
import { getDictLabel } from '@/utils/format'
import Pagination from '@/components/common/Pagination.vue'
import AddEdit from './add-edit.vue'
import type { TrainingRecord } from '@/api/training-record'

const loading = ref(false)
const trainingList = ref<TrainingRecord[]>([])
const total = ref(0)
const courseOptions = ref<Array<{ value: number; label: string }>>([])
const coachOptions = ref<Array<{ value: number; label: string }>>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  memberId: undefined as number | undefined,
  scheduleId: undefined as number | undefined,
  status: '',
  startDate: '',
  endDate: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const recordId = ref<number>()
const checkInVisible = ref(false)
const checkInForm = reactive({
  scheduleId: undefined as number | undefined,
  memberId: undefined as number | undefined
})

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
    const res = await listTrainingRecord(queryParams)
    trainingList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('Failed to get training list:', error)
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
    memberId: undefined,
    scheduleId: undefined,
    status: '',
    startDate: '',
    endDate: ''
  })
  getList()
}

const handleAdd = () => {
  dialogTitle.value = '新增签到记录'
  recordId.value = undefined
  dialogVisible.value = true
}

const handleEdit = (row: TrainingRecord) => {
  dialogTitle.value = '编辑签到记录'
  recordId.value = row.id
  dialogVisible.value = true
}

const handleDelete = async (row: TrainingRecord) => {
  try {
    await ElMessageBox.confirm(`确定要删除这条签到记录吗？`, '提示', {
      type: 'warning'
    })
    await delTrainingRecord([row.id!])
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete training record:', error)
    }
  }
}

const handleCheckIn = () => {
  checkInForm.scheduleId = undefined
  checkInForm.memberId = undefined
  checkInVisible.value = true
}

const handleCheckInSubmit = async () => {
  if (!checkInForm.scheduleId || !checkInForm.memberId) {
    ElMessage.warning('请完善签到信息')
    return
  }
  try {
    await checkIn(checkInForm.scheduleId, checkInForm.memberId)
    ElMessage.success('签到成功')
    checkInVisible.value = false
    getList()
  } catch (error) {
    console.error('Failed to check in:', error)
  }
}

const handleCheckOut = async (row: TrainingRecord) => {
  try {
    await checkOut(row.id!)
    ElMessage.success('签退成功')
    getList()
  } catch (error) {
    console.error('Failed to check out:', error)
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
      <el-form-item label="会员ID">
        <el-input-number v-model="queryParams.memberId" :min="1" placeholder="请输入会员ID" style="width: 150px" />
      </el-form-item>
      <el-form-item label="排课ID">
        <el-input-number v-model="queryParams.scheduleId" :min="1" placeholder="请输入排课ID" style="width: 150px" />
      </el-form-item>
      <el-form-item label="签到状态">
        <el-select v-model="queryParams.status" placeholder="请选择签到状态" clearable style="width: 120px">
          <el-option
            v-for="item in CHECKIN_STATUS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围">
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
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增记录</el-button>
      <el-button type="success" :icon="CircleCheck" @click="handleCheckIn">快速签到</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="trainingList" stripe border>
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="memberNo" label="会员号" width="120" />
      <el-table-column prop="memberName" label="会员姓名" min-width="100" />
      <el-table-column prop="courseName" label="课程" min-width="120" />
      <el-table-column prop="coachName" label="教练" width="100" />
      <el-table-column label="排课时间" min-width="200">
        <template #default="{ row }">
          {{ row.scheduleDate }}
        </template>
      </el-table-column>
      <el-table-column prop="classroom" label="教室" width="100" />
      <el-table-column prop="checkinTime" label="签到时间" width="180" />
      <el-table-column prop="checkoutTime" label="签退时间" width="180" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : row.status === 1 ? 'primary' : 'warning'">
            {{ getDictLabel(CHECKIN_STATUS, row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 1 && !row.checkoutTime" type="warning" link @click="handleCheckOut(row)">签退</el-button>
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
      :record-id="recordId"
      :course-options="courseOptions"
      :coach-options="coachOptions"
      @close="handleDialogClose"
    />

    <!-- 快速签到弹窗 -->
    <el-dialog
      v-model="checkInVisible"
      title="快速签到"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="排课ID">
          <el-input-number v-model="checkInForm.scheduleId" :min="1" placeholder="请输入排课ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="会员ID">
          <el-input-number v-model="checkInForm.memberId" :min="1" placeholder="请输入会员ID" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCheckInSubmit">确定签到</el-button>
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
