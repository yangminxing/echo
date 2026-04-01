export interface FormDefinition {
  id: string
  formName: string
  formVersion: number
  formSchema: string
  tableName: string
  syncStatus: string
  syncTime: string
  status: string
  description: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}
