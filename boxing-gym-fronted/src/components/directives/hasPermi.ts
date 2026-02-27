import { useUserStore } from '@/store/modules/user'
import type { Directive, DirectiveBinding } from 'vue'

/**
 * 权限指令
 * 用法: v-hasPermi="'user:add'" 或 v-hasPermi="['user:add', 'user:edit']"
 */
export const hasPermi: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
    const userStore = useUserStore()
    const { value } = binding

    if (value) {
      const permissions = Array.isArray(value) ? value : [value]
      const hasAuth = permissions.some(permission => userStore.hasPermission(permission))

      if (!hasAuth) {
        el.parentNode?.removeChild(el)
      }
    }
  }
}

export default hasPermi
