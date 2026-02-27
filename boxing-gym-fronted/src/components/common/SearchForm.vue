<script setup lang="ts">
import { reactive, watch } from 'vue'

export interface SearchFormItem {
  prop: string
  label: string
  type?: 'input' | 'select' | 'date' | 'daterange'
  options?: Array<{ value: string | number; label: string }>
  placeholder?: string
  dateType?: 'date' | 'month' | 'year'
}

interface Props {
  items: SearchFormItem[]
  modelValue: Record<string, any>
}

const props = defineProps<Props>()
const emit = defineEmits<{
  search: []
  reset: []
  'update:modelValue': [value: Record<string, any>]
}>()

const form = reactive<Record<string, any>>({ ...props.modelValue })

watch(() => props.modelValue, (val) => {
  Object.assign(form, val)
}, { deep: true })

watch(form, (val) => {
  emit('update:modelValue', { ...val })
}, { deep: true })

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  Object.keys(form).forEach(key => {
    form[key] = undefined
  })
  emit('reset')
}
</script>

<template>
  <el-form :model="form" inline class="search-form">
    <el-form-item
      v-for="item in items"
      :key="item.prop"
      :label="item.label"
    >
      <el-input
        v-if="!item.type || item.type === 'input'"
        v-model="form[item.prop]"
        :placeholder="item.placeholder || `请输入${item.label}`"
        clearable
        @keyup.enter="handleSearch"
      />
      <el-select
        v-else-if="item.type === 'select'"
        v-model="form[item.prop]"
        :placeholder="item.placeholder || `请选择${item.label}`"
        clearable
      >
        <el-option
          v-for="opt in item.options"
          :key="opt.value"
          :label="opt.label"
          :value="opt.value"
        />
      </el-select>
      <el-date-picker
        v-else-if="item.type === 'date'"
        v-model="form[item.prop]"
        :type="item.dateType || 'date'"
        :placeholder="item.placeholder || `请选择${item.label}`"
        value-format="YYYY-MM-DD"
      />
      <el-date-picker
        v-else-if="item.type === 'daterange'"
        v-model="form[item.prop]"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleSearch">
        搜索
      </el-button>
      <el-button @click="handleReset">
        重置
      </el-button>
    </el-form-item>
  </el-form>
</template>

<style scoped>
.search-form {
  background: #f5f7fa;
  padding: 18px 18px 0;
  border-radius: 4px;
  margin-bottom: 20px;
}
</style>
