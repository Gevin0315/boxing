import { ref, computed, type Ref } from 'vue'
import type { ElTable } from 'element-plus'

export interface TableConfig<T> {
  data: Ref<T[]>
  loading?: Ref<boolean>
  stripe?: boolean
  border?: boolean
  height?: string | number
  maxHeight?: string | number
}

export function useTable<T = any>(config: TableConfig<T>) {
  const tableRef = ref<InstanceType<typeof ElTable>>()

  const {
    data,
    loading = ref(false),
    stripe = true,
    border = true,
    height,
    maxHeight
  } = config

  /**
   * 选中所有
   */
  const selectAll = () => {
    tableRef.value?.toggleAllSelection()
  }

  /**
   * 清空选择
   */
  const clearSelection = () => {
    tableRef.value?.clearSelection()
  }

  /**
   * 获取选中数据
   */
  const getSelectionRows = () => {
    return tableRef.value?.getSelectionRows() || []
  }

  /**
   * 切换选择状态
   */
  const toggleRowSelection = (row: T) => {
    tableRef.value?.toggleRowSelection(row, undefined)
  }

  /**
   * 清空过滤和排序
   */
  const clearFilter = () => {
    tableRef.value?.clearFilter()
    tableRef.value?.clearSort()
  }

  /**
   * 导出数据为 CSV
   */
  const exportCSV = (filename: string = 'export') => {
    if (!data.value || data.value.length === 0) {
      ElMessage.warning('暂无数据可导出')
      return
    }

    const headers = Object.keys(data.value[0])
    const csvContent = [
      headers.join(','),
      ...data.value.map(row =>
        headers.map(header => `"${(row as any)[header]}"`).join(',')
      )
    ].join('\n')

    const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${filename}_${Date.now()}.csv`
    link.click()
    URL.revokeObjectURL(url)
  }

  return {
    tableRef,
    data,
    loading,
    selectAll,
    clearSelection,
    getSelectionRows,
    toggleRowSelection,
    clearFilter,
    exportCSV,
    tableProps: {
      ref: tableRef,
      data: data,
      loading,
      stripe,
      border,
      height,
      maxHeight
    }
  }
}
