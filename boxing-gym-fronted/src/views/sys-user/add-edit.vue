<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addSysUser, updateSysUser, getSysUser } from '@/api/sys-user'
import { USER_STATUS, USER_ROLE } from '@/constants/dict'
import { required, usernameRule, passwordRule, phoneRule, emailRule } from '@/utils/validate'
import type { SysUserForm } from '@/types/sys-user'

interface Props {
  modelValue: boolean
  title: string
  userId?: number
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const formRef = ref()
const loading = ref(false)

const form = reactive<SysUserForm>({
  id: undefined,
  username: '',
  nickname: '',
  password: '',
  phone: '',
  email: '',
  status: '0',
  role: '',
  remark: ''
})

const rules = {
  username: [required('请输入用户名'), usernameRule],
  nickname: [required('请输入昵称')],
  password: [
    {
      validator: (rule: any, value: string, callback: any) => {
        if (!props.userId && !value) {
          callback(new Error('请输入密码'))
        } else if (value && value.length < 6) {
          callback(new Error('密码长度不能少于6位'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phone: [phoneRule],
  email: [emailRule],
  status: [required('请选择状态')],
  role: [required('请选择角色')]
}

watch(() => props.modelValue, (val) => {
  if (val) {
    resetForm()
    if (props.userId) {
      loadUserDetail()
    }
  }
})

const resetForm = () => {
  form.id = undefined
  form.username = ''
  form.nickname = ''
  form.password = ''
  form.phone = ''
  form.email = ''
  form.status = '0'
  form.role = ''
  form.remark = ''
  formRef.value?.clearValidate()
}

const loadUserDetail = async () => {
  if (!props.userId) return
  loading.value = true
  try {
    const res = await getSysUser(props.userId)
    Object.assign(form, res)
  } catch (error) {
    console.error('Failed to load user detail:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (form.id) {
      await updateSysUser(form)
      ElMessage.success('修改成功')
    } else {
      await addSysUser(form)
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
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!userId" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item v-if="!userId" label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio
            v-for="item in USER_STATUS"
            :key="item.value"
            :label="item.value"
          >
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
          <el-option
            v-for="item in USER_ROLE"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
