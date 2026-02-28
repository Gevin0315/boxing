import { ref, type Ref } from 'vue'

export interface DialogConfig {
  title?: string
  width?: string | number
  beforeClose?: () => boolean | Promise<boolean>
  closeOnClickModal?: boolean
  destroyOnClose?: boolean
}

export function useDialog(config?: DialogConfig) {
  const visible = ref(false)
  const loading = ref(false)
  const {
    title = '',
    width = '500px',
    beforeClose,
    closeOnClickModal = true,
    destroyOnClose = true
  } = config || {}

  const {
    title: currentTitle,
    width: currentWidth
  } = dialogProps

  /**
   * 打开弹窗
   */
  const open = (newTitle?: string) => {
    if (newTitle) {
      currentTitle.value = newTitle
    }
    visible.value = true
  }

  /**
   * 关闭弹窗
   */
  const close = () => {
    visible.value = false
    loading.value = false
    // 重置标题
    setTimeout(() => {
      currentTitle.value = title
    }, 300)
  }

  /**
   * 确认关闭（带loading）
   */
  const confirmClose = async (confirmFn: () => Promise<void>) => {
    loading.value = true
    try {
      await confirmFn()
      close()
    } finally {
      loading.value = false
    }
  }

  /**
   * 弹窗属性
   */
  const dialogProps = reactive({
    title: currentTitle,
    width: currentWidth,
    beforeClose,
    closeOnClickModal,
    destroyOnClose,
    'close-on-click-modal': closeOnClickModal,
    'destroy-on-close': destroyOnClose
  })

  return {
    visible,
    loading,
    open,
    close,
    confirmClose,
    dialogProps
  }
}
