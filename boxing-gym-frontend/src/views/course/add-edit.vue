<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addCourse, updateCourse, getCourse } from '@/api/course'
import { COURSE_TYPE, COURSE_CATEGORY, COURSE_LEVEL, COURSE_STATUS } from '@/constants/dict'
import { required, numberRule } from '@/utils/validate'
import type { CourseForm } from '@/types/course'

interface CoachOption {
  value: number
  label: string
}

interface Props {
  modelValue: boolean
  title: string
  courseId?: number
  coachOptions: CoachOption[]
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const formRef = ref()
const loading = ref(false)

const form = reactive<CourseForm>({
  id: undefined,
  courseName: '',
  courseType: 'group',
  category: 'boxing',
  level: 'beginner',
  maxCapacity: 10,
  duration: 60,
  price: 0,
  description: '',
  coachId: undefined,
  imageUrl: '',
  status: '0'
})

const rules = {
  courseName: [required('请输入课程名称')],
  courseType: [required('请选择课程类型')],
  category: [required('请选择课程分类')],
  level: [required('请选择课程难度')],
  maxCapacity: [required('请输入最大人数'), numberRule],
  duration: [required('请输入课程时长'), numberRule],
  price: [required('请输入课程价格'), numberRule],
  status: [required('请选择状态')]
}

watch(() => props.modelValue, (val) => {
  if (val) {
    resetForm()
    if (props.courseId) {
      loadCourseDetail()
    }
  }
})

const resetForm = () => {
  form.id = undefined
  form.courseName = ''
  form.courseType = 'group'
  form.category = 'boxing'
  form.level = 'beginner'
  form.maxCapacity = 10
  form.duration = 60
  form.price = 0
  form.description = ''
  form.coachId = undefined
  form.imageUrl = ''
  form.status = '0'
  formRef.value?.clearValidate()
}

const loadCourseDetail = async () => {
  if (!props.courseId) return
  loading.value = true
  try {
    const res = await getCourse(props.courseId)
    Object.assign(form, res)
  } catch (error) {
    console.error('Failed to load course detail:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (form.id) {
      await updateCourse(form)
      ElMessage.success('修改成功')
    } else {
      await addCourse(form)
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
          <el-form-item label="课程名称" prop="courseName">
            <el-input v-model="form.courseName" placeholder="请输入课程名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程类型" prop="courseType">
            <el-select v-model="form.courseType" placeholder="请选择课程类型" style="width: 100%">
              <el-option
                v-for="item in COURSE_TYPE"
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
          <el-form-item label="课程分类" prop="category">
            <el-select v-model="form.category" placeholder="请选择课程分类" style="width: 100%">
              <el-option
                v-for="item in COURSE_CATEGORY"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程难度" prop="level">
            <el-select v-model="form.level" placeholder="请选择课程难度" style="width: 100%">
              <el-option
                v-for="item in COURSE_LEVEL"
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
          <el-form-item label="课程时长" prop="duration">
            <el-input-number v-model="form.duration" :min="15" :max="180" :step="15" style="width: 100%" />
            <span class="unit">分钟</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最大人数" prop="maxCapacity">
            <el-input-number v-model="form.maxCapacity" :min="1" :max="50" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="课程价格" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
            <span class="unit">元</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio
                v-for="item in COURSE_STATUS"
                :key="item.value"
                :label="item.value"
              >
                {{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="教练">
        <el-select v-model="form.coachId" placeholder="请选择教练" clearable filterable style="width: 100%">
          <el-option
            v-for="item in coachOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="封面图片">
        <el-input v-model="form.imageUrl" placeholder="请输入图片URL" />
      </el-form-item>
      <el-form-item label="课程描述">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入课程描述" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.unit {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}
</style>
