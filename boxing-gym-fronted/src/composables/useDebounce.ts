import { ref, watch, onUnmounted, type Ref } from 'vue'

export interface DebounceOptions {
  delay?: number
  immediate?: boolean
}

export function useDebounce<T>(valueRef: Ref<T>, callback: (value: T) => void, options?: DebounceOptions) {
  const {
    delay = 300,
    immediate = false
  } = options || {}

  const debounceRef = ref<T>(valueRef.value)
  let timeoutId: number | null = null

  const clearTimeout = () => {
    if (timeoutId !== null) {
      window.clearTimeout(timeoutId)
      timeoutId = null
    }
  }

  const setValue = (value: T) => {
    debounceRef.value = value
  }

  watch(valueRef, (newValue) => {
    clearTimeout()

    if (immediate) {
      callback(newValue)
    }

    timeoutId = window.setTimeout(() => {
      debounceRef.value = newValue
      callback(newValue)
    }, delay)
  })

  onUnmounted(() => {
    clearTimeout()
  })

  return {
    debounceRef,
    setValue
  }
}
