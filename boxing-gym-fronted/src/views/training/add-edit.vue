<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addTrainingRecord, updateTrainingRecord, getTrainingRecord } from '@/api/training-record'
import { CHECKIN_STATUS } from '@/constants/dict'
import { required } from '@/utils/validate'
import type { TrainingRecord } from '@/api/training-record'

interface Option {
  value: number | string
  label: string
}

interface Props {
  modelValue: boolean
  title: string
  recordId?: number
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

const form = reactive<TrainingRecord>({
  id: undefined,
  scheduleId: 0,
  courseId: 0,
  courseName: '',
  coachId: 0,
  coachName: '',
  classroom: '',
  memberId: 0,
  memberNo: '',
  memberName: '',
  checkInTime: '',
  checkOutTime: '',
  status: '0',
  remark: ''
})

const rules = {
  scheduleId: [required('请选择排课')],
  memberId: [required('请输入会员ID')],
  status: [required('请选择状态')]
}

watch(() => props.modelValue, (val) => {
  if (val) {
    resetForm()
    if (props.recordId) {
      loadRecordDetail()
    }
  }
})

const resetForm = () => {
  form.id = undefined
  form.scheduleId = 0
  form.courseId = 0
  form.courseName = ''
  form.coachId = 0
  form.coachName = ''
  form.classroom = ''
  form.memberId = 0
  form.memberNo = ''
  form.memberName = ''
  form.checkInTime = ''
  form.checkOutTime = ''
  form.status = '0'
  form.remark = ''
  formRef.value?.clearValidate()
}

const loadRecordDetail = async () => {
  if (!props.recordId) return
  loading.value = true
  try {
    const res = await getTrainingRecord(props.recordId)
    Object.assign(form, res)
  } catch (error) {
    console.error('Failed to load record detail:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (form.id) {
      await updateTrainingRecord(form)
      ElMessage.success('修改成功')
    } else {
      await addTrainingRecord(form)
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
    width="600px"
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
      <el-form-item label="排课ID" prop="scheduleId">
        <el-input-number v-model="form.scheduleId" :min="1" placeholder="请输入排课ID" style="width: 100%" />
      </el-form-item>
      <el-form-item label="会员ID" prop="memberId">
        <el-input-number v-model="form.memberId" :min="1" placeholder="请输入会员ID" style="width: 100%" />
      </el-form-item>
      <el-form-item label="签到时间">
        <el-date-picker
          v-model="form.checkInTime"
          type="datetime"
          placeholder="请选择签到时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="签退时间">
        <el-date-picker
          v-model="form.checkOutTime"
          type="datetime"
          placeholder="请选择签退时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="0">已签到</el-radio>
          <el-radio label="1">缺勤</el-radio>
          <el-radio label="2">请假</el-radio>
        </el-radio-group>
      </el-form-item>
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
