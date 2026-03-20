<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  total: number
  page?: number
  pageSize?: number
  pageSizes?: number[]
}

const props = withDefaults(defineProps<Props>(), {
  page: 1,
  pageSize: 10,
  pageSizes: () => [10, 20, 30, 50, 100]
})

const emit = defineEmits<{
  update: [page: number, pageSize: number]
}>()

const currentPage = computed({
  get: () => props.page,
  set: (val) => emit('update', val, props.pageSize)
})

const currentPageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update', 1, val)
})
</script>

<template>
  <div class="pagination-container">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="currentPageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      background
    />
  </div>
</template>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
}
</style>
