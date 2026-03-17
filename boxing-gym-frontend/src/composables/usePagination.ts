import { ref, reactive, computed, watch } from 'vue'

export interface PaginationConfig {
  pageSize?: number
  defaultPage?: number
}

export function usePagination(config?: PaginationConfig) {
  const currentPage = ref(config?.defaultPage || 1)
  const pageSize = ref(config?.pageSize || 10)
  const total = ref(0)

  // 分页相关数据
  const pageSizes = [10, 20, 50, 100]
  const currentSize = computed(() => pageSize.value)

  const offset = computed(() => (currentPage.value - 1) * pageSize.value)

  // 每页数量变化时重置到第一页
  watch(currentSize, () => {
    currentPage.value = 1
  })

  /**
   * 跳转到指定页
   */
  const goToPage = (page: number) => {
    currentPage.value = page
  }

  /**
   * 下一页
   */
  const nextPage = () => {
    if (currentPage.value * pageSize.value < total.value) {
      currentPage.value++
    }
  }

  /**
   * 上一页
   */
  const prevPage = () => {
    if (currentPage.value > 1) {
      currentPage.value--
    }
  }

  /**
   * 每页大小改变
   */
  const handleSizeChange = (size: number) => {
    pageSize.value = size
    currentPage.value = 1
  }

  /**
   * 当前页改变
   */
  const handlePageChange = (page: number) => {
    currentPage.value = page
  }

  /**
   * 更新总数
   */
  const updateTotal = (newTotal: number) => {
    total.value = newTotal
  }

  /**
   * 重置分页
   */
  const reset = () => {
    currentPage.value = 1
    total.value = 0
  }

  return {
    currentPage,
    pageSize,
    total,
    pageSizes,
    currentSize,
    offset,
    goToPage,
    nextPage,
    prevPage,
    handleSizeChange,
    handlePageChange,
    updateTotal,
    reset
  }
}
