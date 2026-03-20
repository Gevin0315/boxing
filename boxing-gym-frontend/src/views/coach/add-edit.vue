<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addCoach, updateCoach, getCoach } from '@/api/coach-profile'
import { COACH_STATUS, COACH_LEVEL, GENDER } from '@/constants/dict'
import { required, phoneRule, idCardRule, emailRule } from '@/utils/validate'
import type { CoachForm } from '@/types/coach'

interface Props {
  modelValue: boolean
  title: string
  coachId?: number
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const formRef = ref()
const loading = ref(false)

const form = reactive<CoachForm>({
  id: undefined,
  name: '',
  gender: '0',
  phone: '',
  email: '',
  idCard: '',
  birthday: '',
  address: '',
  specialties: '',
  level: '1',
  status: '0',
  hireDate: '',
  imageUrl: '',
  description: ''
})

const rules = {
  name: [required('请输入姓名')],
  gender: [required('请选择性别')],
  phone: [required('请输入手机号'), phoneRule],
  email: [emailRule],
  idCard: [idCardRule],
  specialties: [required('请输入专长')],
  level: [required('请选择等级')],
  status: [required('请选择状态')]
}

watch(() => props.modelValue, async (val) => {
  if (val) {
    resetForm()
    if (props.coachId) {
      await loadCoachDetail()
    }
  }
})

const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.gender = '0'
  form.phone = ''
  form.email = ''
  form.idCard = ''
  form.birthday = ''
  form.address = ''
  form.specialties = ''
  form.level = '1'
  form.status = '0'
  form.hireDate = ''
  form.imageUrl = ''
  form.description = ''
  formRef.value?.clearValidate()
}

const loadCoachDetail = async () => {
  if (!props.coachId) return
  loading.value = true
  try {
    const res = await getCoach(props.coachId)
    Object.assign(form, res)
  } catch (error) {
    console.error('Failed to load coach detail:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (form.id) {
      await updateCoach(form)
      ElMessage.success('修改成功')
    } else {
      await addCoach(form)
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
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="form.gender">
              <el-radio
                v-for="item in GENDER"
                :key="item.value"
                :label="item.value"
              >
                {{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="身份证号">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="生日">
            <el-date-picker
              v-model="form.birthday"
              type="date"
              placeholder="请选择生日"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="入职日期">
            <el-date-picker
              v-model="form.hireDate"
              type="date"
              placeholder="请选择入职日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="地址">
        <el-input v-model="form.address" placeholder="请输入地址" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="专长" prop="specialties">
            <el-input v-model="form.specialties" placeholder="请输入专长，如：拳击、泰拳" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="等级" prop="level">
            <el-select v-model="form.level" placeholder="请选择等级" style="width: 100%">
              <el-option
                v-for="item in COACH_LEVEL"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio
            v-for="item in COACH_STATUS"
            :key="item.value"
            :label="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="头像">
        <el-input v-model="form.imageUrl" placeholder="请输入头像URL" />
      </el-form-item>
      <el-form-item label="个人简介">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入个人简介" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
