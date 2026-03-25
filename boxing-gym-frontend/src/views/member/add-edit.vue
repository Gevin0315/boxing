<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addMember, updateMember, getMember } from '@/api/member'
import { MEMBER_STATUS, MEMBERSHIP_LEVEL, GENDER } from '@/constants/dict'
import { required, phoneRule, idCardRule } from '@/utils/validate'
import type { MemberForm } from '@/types/member'

interface Props {
  modelValue: boolean
  title: string
  memberId?: number
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const formRef = ref()
const loading = ref(false)

const form = reactive<MemberForm>({
  id: undefined,
  name: '',
  gender: '0',
  phone: '',
  birthday: '',
  idCard: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: '',
  status: '0',
  membershipLevel: '1',
  expiryDate: '',
  remainingBalance: 0,
  remark: ''
})

const rules = {
  name: [required('请输入姓名')],
  gender: [required('请选择性别')],
  phone: [required('请输入手机号'), phoneRule],
  idCard: [idCardRule],
  emergencyPhone: [{ validator: phoneRule, trigger: 'blur' }],
  status: [required('请选择状态')],
  membershipLevel: [required('请选择会员等级')]
}

watch(() => props.modelValue, async (val) => {
  if (val) {
    resetForm()
    if (props.memberId) {
      await loadMemberDetail()
    }
  }
})

const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.gender = '0'
  form.phone = ''
  form.birthday = ''
  form.idCard = ''
  form.address = ''
  form.emergencyContact = ''
  form.emergencyPhone = ''
  form.status = '0'
  form.membershipLevel = '1'
  form.expiryDate = ''
  form.remainingBalance = 0
  form.remark = ''
  formRef.value?.clearValidate()
}

const loadMemberDetail = async () => {
  if (!props.memberId) return
  loading.value = true
  try {
    const res = await getMember(props.memberId)
    Object.assign(form, res)
  } catch (error) {
    console.error('Failed to load member detail:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (form.id) {
      await updateMember(form)
      ElMessage.success('修改成功')
    } else {
      await addMember(form)
      ElMessage.success('新增成功')
    }

    handleSubmitSuccess()
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
}

const handleSubmitSuccess = () => {
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
          <el-form-item label="身份证号">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="地址">
            <el-input v-model="form.address" placeholder="请输入地址" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="紧急联系人">
            <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="紧急联系电话">
            <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
              <el-option
                v-for="item in MEMBER_STATUS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="会员等级" prop="membershipLevel">
            <el-select v-model="form.membershipLevel" placeholder="请选择会员等级" style="width: 100%">
              <el-option
                v-for="item in MEMBERSHIP_LEVEL"
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
          <el-form-item label="到期日期">
            <el-date-picker
              v-model="form.expiryDate"
              type="date"
              placeholder="请选择到期日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="初始余额">
            <el-input-number v-model="form.remainingBalance" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
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
