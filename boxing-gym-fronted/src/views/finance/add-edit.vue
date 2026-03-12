<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addFinanceOrder, updateFinanceOrder, getFinanceOrder, generateOrderNo } from '@/api/finance-order'
import { ORDER_TYPE, PAYMENT_METHOD, PAYMENT_STATUS } from '@/constants/dict'
import { required, numberRule } from '@/utils/validate'
import type { FinanceOrderForm } from '@/types/finance'

interface Props {
  modelValue: boolean
  title: string
  orderId?: number
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

const formRef = ref()
const loading = ref(false)
const generating = ref(false)

const form = reactive<FinanceOrderForm>({
  id: undefined,
  orderNo: '',
  orderType: '0',
  memberId: 0,
  courseId: undefined,
  amount: 0,
  paidAmount: 0,
  paymentMethod: '0',
  paymentStatus: '0',
  remark: ''
})

const rules = {
  orderNo: [required('请输入订单号')],
  orderType: [required('请选择订单类型')],
  memberId: [required('请输入会员ID'), numberRule],
  amount: [required('请输入订单金额'), numberRule],
  paidAmount: [numberRule],
  paymentMethod: [required('请选择支付方式')],
  paymentStatus: [required('请选择支付状态')]
}

watch(() => props.modelValue, async (val) => {
  if (val) {
    resetForm()
    if (props.orderId) {
      await loadOrderDetail()
    } else {
      generateNo()
    }
  }
})

const resetForm = () => {
  form.id = undefined
  form.orderNo = ''
  form.orderType = '0'
  form.memberId = 0
  form.courseId = undefined
  form.amount = 0
  form.paidAmount = 0
  form.paymentMethod = '0'
  form.paymentStatus = '0'
  form.remark = ''
  formRef.value?.clearValidate()
}

const generateNo = async () => {
  generating.value = true
  try {
    const res = await generateOrderNo()
    form.orderNo = res
  } catch (error) {
    console.error('Failed to generate order no:', error)
  } finally {
    generating.value = false
  }
}

const loadOrderDetail = async () => {
  if (!props.orderId) return
  loading.value = true
  try {
    const res = await getFinanceOrder(props.orderId)
    Object.assign(form, res)
  } catch (error) {
    console.error('Failed to load order detail:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (form.id) {
      await updateFinanceOrder(form)
      ElMessage.success('修改成功')
    } else {
      await addFinanceOrder(form)
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
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="订单号" prop="orderNo">
            <el-input v-model="form.orderNo" placeholder="请输入订单号" :disabled="!!orderId">
              <template #append>
                <el-button :icon="Refresh" :loading="generating" @click="generateNo" :disabled="!!orderId" />
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="订单类型" prop="orderType">
            <el-select v-model="form.orderType" placeholder="请选择订单类型" style="width: 100%">
              <el-option
                v-for="item in ORDER_TYPE"
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
          <el-form-item label="会员ID" prop="memberId">
            <el-input-number v-model="form.memberId" :min="1" placeholder="请输入会员ID" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12" />
      </el-row>
      <el-form-item label="课程ID">
        <el-input-number v-model="form.courseId" :min="1" placeholder="请输入课程ID" style="width: 100%" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="订单金额" prop="amount">
            <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="已付金额" prop="paidAmount">
            <el-input-number v-model="form.paidAmount" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="支付方式" prop="paymentMethod">
            <el-select v-model="form.paymentMethod" placeholder="请选择支付方式" style="width: 100%">
              <el-option
                v-for="item in PAYMENT_METHOD"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支付状态" prop="paymentStatus">
            <el-select v-model="form.paymentStatus" placeholder="请选择支付状态" style="width: 100%">
              <el-option
                v-for="item in PAYMENT_STATUS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
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

<script lang="ts">
import { Refresh } from '@element-plus/icons-vue'
export default {
  components: { Refresh }
}
</script>
