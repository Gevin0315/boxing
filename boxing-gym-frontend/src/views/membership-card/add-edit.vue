<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  createCard,
  updateCard,
  getCardDetail,
} from '@/api/membershipCard'
import type {
  MembershipCardForm,
} from '@/types/membershipCard'
import { CardCategory, CardStatus, CARD_CATEGORY_OPTIONS, CARD_TYPE_OPTIONS, CARD_STATUS_OPTIONS } from '@/types/membershipCard'

const props = defineProps<{
  title: string
  cardId?: number
}>()

const emit = defineEmits<{
  close: []
  success: []
}>()

const visible = defineModel<boolean>({ default: false })

const loading = ref(false)
const formRef = ref()

const getDefaultForm = (): MembershipCardForm => ({
  id: undefined,
  cardCode: '',
  cardName: '',
  cardCategory: CardCategory.GROUP_TIME,
  cardType: 'MONTHLY',
  durationDays: 30,
  sessionCount: undefined,
  price: 0,
  originalPrice: undefined,
  activationDeadlineDays: 30,
  validityDaysAfterActivation: undefined,
  description: '',
  status: CardStatus.ENABLED,
  sortOrder: 0,
})

const form = reactive<MembershipCardForm>(getDefaultForm())

const rules = {
  cardCode: [
    { required: true, message: '请输入卡编码', trigger: 'blur' },
  ],
  cardName: [
    { required: true, message: '请输入卡名称', trigger: 'blur' },
  ],
  cardCategory: [
    { required: true, message: '请选择卡分类', trigger: 'change' },
  ],
  cardType: [
    { required: true, message: '请选择卡类型', trigger: 'blur' },
  ],
  price: [
    { required: true, message: '请输入售价' },
  ],
  durationDays: [
    {
      required: true,
      message: '请输入有效期天数',
      trigger: 'blur',
    }
  ],
  sessionCount: [
    {
      required: true,
      message: '请输入包含次数',
      trigger: 'blur'
    }
  ],
  activationDeadlineDays: [
    { required: true, message: '请输入激活期限天数' },
    { type: 'number', min: 1, max: 365, message: '激活期限应在1-365天之间' },
  ],
  validityDaysAfterActivation: [
    {
      required: true,
      message: '请输入激活后有效期',
      trigger: 'blur'
    }
  ],
}

// 根据卡分类过滤卡类型选项
const filteredCardTypes = computed(() => {
  return CARD_TYPE_OPTIONS.filter((option) => option.category === form.cardCategory)
})

// 根据卡分类判断是否显示期限相关字段
const isTimeCard = computed(() => {
  return form.cardCategory === CardCategory.GROUP_TIME
})

// 根据卡分类判断是否显示次卡相关字段
const isSessionCard = computed(() => {
  return form.cardCategory === CardCategory.GROUP_SESSION
})

// 是否为私教卡
const isPrivateCard = computed(() => {
  return form.cardCategory === CardCategory.PRIVATE_SESSION
})

// 卡分类变化时重置卡类型
const handleCategoryChange = () => {
  form.cardType = filteredCardTypes.value[0]?.value || 'MONTHLY'
  if (isTimeCard.value) {
    form.durationDays = 30
    form.sessionCount = undefined
    form.validityDaysAfterActivation = undefined
  } else if (isSessionCard.value) {
    form.durationDays = undefined
    form.sessionCount = 10
    form.validityDaysAfterActivation = 30
  } else if (isPrivateCard.value) {
    form.durationDays = undefined
    form.sessionCount = 5
    form.validityDaysAfterActivation = 90
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, getDefaultForm())
  formRef.value?.clearValidate()
}

// 获取卡片详情
const getDetail = async (id: number) => {
  loading.value = true
  try {
    const res = await getCardDetail(id)
    // 响应拦截器已返回 data，直接使用 res
    Object.assign(form, res)
  } catch (error) {
    console.error('Failed to get card detail:', error)
    ElMessage.error('获取卡片详情失败')
  } finally {
    loading.value = false
  }
}

// 监听 cardId 变化
watch(
  () => props.cardId,
  (newId) => {
    if (newId) {
      getDetail(newId)
    } else {
      resetForm()
    }
  },
  { immediate: true }
)

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true

    if (props.cardId) {
      await updateCard(props.cardId, form)
      ElMessage.success('更新成功')
    } else {
      await createCard(form)
      ElMessage.success('创建成功')
    }

    visible.value = false
    emit('success')
    resetForm()
  } catch (error) {
    console.error('Failed to save card:', error)
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  visible.value = false
  emit('close')
  resetForm()
}
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="600px"
    destroy-on-close
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="卡编码" prop="cardCode">
            <el-input v-model="form.cardCode" placeholder="请输入卡编码" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="卡名称" prop="cardName">
            <el-input v-model="form.cardName" placeholder="请输入卡名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="卡分类" prop="cardCategory">
            <el-select v-model="form.cardCategory" placeholder="请选择卡分类" style="width: 100%" @change="handleCategoryChange">
              <el-option
                v-for="option in CARD_CATEGORY_OPTIONS"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="卡类型" prop="cardType">
            <el-select v-model="form.cardType" placeholder="请选择卡类型" style="width: 100%">
              <el-option
                v-for="option in filteredCardTypes"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="售价" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="原价" prop="originalPrice">
            <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :step="10" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="isTimeCard">
          <el-form-item label="有效期天数" prop="durationDays">
            <el-input-number v-model="form.durationDays" :min="1" :max="365" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="isSessionCard || isPrivateCard">
          <el-form-item label="包含次数" prop="sessionCount">
            <el-input-number v-model="form.sessionCount" :min="1" :max="100" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="激活期限" prop="activationDeadlineDays">
            <el-input-number v-model="form.activationDeadlineDays" :min="1" :max="365" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="isSessionCard || isPrivateCard">
          <el-form-item label="激活后有效期" prop="validityDaysAfterActivation">
            <el-input-number v-model="form.validityDaysAfterActivation" :min="1" :max="365" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="请输入描述"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio
                v-for="option in CARD_STATUS_OPTIONS"
                :key="option.value"
                :label="option.value"
              >
                {{ option.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="排序" prop="sortOrder">
            <el-input-number v-model="form.sortOrder" :min="0" :max="999" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
