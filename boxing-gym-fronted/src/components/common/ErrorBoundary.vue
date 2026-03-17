<script setup lang="ts">
import { onErrorCaptured, type ErrorCapturedVNode } from 'vue'
import { Result, Button, CircleWarning } from '@element-plus/icons-vue'

interface Props {
  fallback?: ErrorCapturedVNode
  stopPropagation?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  stopPropagation: false
})

const error = ref<Error | null>(null)
const hasError = ref(false)

const handleError = (err: unknown, instance: any, info: string) => {
  error.value = err as Error
  hasError.value = true
  console.error('Error caught:', err, info)

  // 向上层传播错误（如果配置）
  if (!props.stopPropagation) {
    onErrorCaptured(err, instance, info)
  }
}

const resetError = () => {
  error.value = null
  hasError.value = false
}
</script>

<template>
  <error-catcher
    :stop-propagation="stopPropagation"
    @error="handleError"
  >
    <template #fallback>
      <div class="error-boundary">
        <el-result
          icon="warning"
          :title="error?.message || '页面出错了'"
          :sub-title="error?.stack?.split('\n')[0]"
        >
          <template #extra>
            <div class="error-actions">
              <el-button type="primary" :icon="Refresh" @click="resetError">
                重新加载
              </el-button>
              <el-button :icon="Result" @click="$router.push('/dashboard')">
                返回首页
              </el-button>
            </div>
            <el-button v-if="!stopPropagation" type="info" text :icon="CircleWarning">
                查看详情
              </el-button>
          </template>
        </el-result>
      </div>
    </template>
    <slot v-if="!hasError" />
  </error-catcher>
</template>

<style scoped>
.error-boundary {
  min-height: 400px;
  padding: 40px 20px;
}

.error-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}
</style>
