/** 通用分页参数 */
export interface PageQuery {
  pageNum: number
  pageSize: number
}

/** 通用分页响应 */
export interface PageResult<T> {
  rows: T[]
  total: number
}

/** 通用响应结构 */
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

/** 字典选项 */
export interface DictOption {
  value: string | number
  label: string
  [key: string]: any
}

/** 树形节点 */
export interface TreeNode {
  id: number | string
  label: string
  children?: TreeNode[]
  [key: string]: any
}

/** 上传文件信息 */
export interface UploadFile {
  name: string
  url: string
  size?: number
  type?: string
}

/** 表格列配置 */
export interface TableColumn {
  prop: string
  label: string
  width?: number | string
  minWidth?: number | string
  align?: 'left' | 'center' | 'right'
  fixed?: boolean | 'left' | 'right'
  sortable?: boolean
}
