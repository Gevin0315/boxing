<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addSchedule, updateSchedule, getSchedule } from '@/api/course-schedule'
import { WEEKDAYS } from '@/constants/dict'
import { required, numberRule } from '@/utils/validate'
import type { CourseSchedule } from '@/api/course-schedule'

interface Option {
  value: number | string
  label: string
}

interface Props {
  modelValue: boolean
  title: string
  scheduleId?: number
  courseOptions: Option[]
  coachOptions: Option[]
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const formRef = ref()
const loading = ref(false)
const isRepeat = ref(false)

const form = reactive<CourseSchedule>({
  id: undefined,
  courseId: 0,
  coachId: 0,
  scheduleDate: '',
  startTime: '',
  endTime: '',
  classroom: '',
  maxCapacity: 10,
  currentCount: 0,
  status: '0',
  remark: ''
})

const repeatForm = reactive({
  type: 'once',
  weekdays: [] as number[],
  endDate: ''
})

const rules = {
  courseId: [required('请选择课程')],
  coachId: [required('请选择教练')],
  scheduleDate: [required('请选择排课日期')],
  startTime: [required('请选择开始时间')],
  endTime: [required('请选择结束时间')],
  classroom: [required('请输入教室')],
  maxCapacity: [required('请输入最大人数'), numberRule],
  status: [required('请选择状态')]
}

const repeatRules = {
  type: [required('请选择重复类型')],
  weekdays: [{ type: 'array', required: true, message: '请选择重复日期', trigger: 'change' }],
  endDate: [required('请选择结束日期')]
}

watch(() => props.modelValue, (val) => {
  if (val) {
    resetForm()
    if (props.scheduleId) {
      loadScheduleDetail()
    }
  }
})

const resetForm = () => {
  form.id = undefined
  form.courseId = 0
  form.coachId = 0
  form.scheduleDate = ''
  form.startTime = ''
  form.endTime = ''
  form.classroom = ''
  form.maxCapacity = 10
  form.currentCount = 0
  form.status = '0'
  form.remark = ''
  isRepeat.value = false
  Object.assign(repeatForm, {
    type: 'once',
    weekdays: [],
    endDate: ''
  })
  formRef.value?.clearValidate()
}

const loadScheduleDetail = async () => {
  if (!props.scheduleId) return
  loading.value = true
  try {
    const res = await getSchedule(props.scheduleId)
    Object.assign(form, res)
  } catch (error) {
    console.error('Failed to load schedule detail:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (form.id) {
      await updateSchedule(form)
      ElMessage.success('修改成功')
    } else {
      await addSchedule(form)
      ElMessage.success('新增成功')
    }

    handleClose()
  } catch (error) {
    if (error !== false) {
      console.error('Failed to submit form:', error)
    }
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  emit('update:modelValue', false)
  emit('close')
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="title"
    width="700px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      v-loading="loading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="课程" prop="courseId">
            <el-select v-model="form.courseId" placeholder="请选择课程" filterable style="width: 100%">
              <el-option
                v-for="item in courseOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="教练" prop="coachId">
            <el-select v-model="form.coachId" placeholder="请选择教练" filterable style="width: 100%">
              <el-option
                v-for="item in coachOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="排课日期" prop="scheduleDate">
            <el-date-picker
              v-model="form.scheduleDate"
              type="date"
              placeholder="请选择排课日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="开始时间" prop="startTime">
            <el-time-picker
              v-model="form.startTime"
              format="HH:mm"
              value-format="HH:mm"
              placeholder="开始时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="结束时间" prop="endTime">
            <el-time-picker
              v-model="form.endTime"
              format="HH:mm"
              value-format="HH:mm"
              placeholder="结束时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="教室" prop="classroom">
            <el-input v-model="form.classroom" placeholder="请输入教室" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最大人数" prop="maxCapacity">
            <el-input-number v-model="form.maxCapacity" :min="1" :max="50" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="0">正常</el-radio>
          <el-radio label="1">已取消</el-radio>
          <el-radio label="2">已完成</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="!form.id" label="重复排课">
        <el-checkbox v-model="isRepeat">开启重复排课</el-checkbox>
      </el-form-item>
      <template v-if="isRepeat && !form.id">
        <el-form-item label="重复类型">
          <el-radio-group v-model="repeatForm.type">
            <el-radio value="once">单次</el-radio>
            <el-radio value="weekly">每周</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="repeatForm.type === 'weekly'" label="重复日期">
          <el-checkbox-group v-model="repeatForm.weekdays">
            <el-checkbox
              v-for="item in WEEKDAYS"
              :key="item.value"
              :label="item.value"
            >
              {{ item.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item v-if="repeatForm.type !== 'once'" label="结束日期">
          <el-date-picker
            v-model="repeatForm.endDate"
            type="date"
            placeholder="请选择结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </template>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
