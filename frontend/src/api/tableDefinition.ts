import apiClient from './login'

export interface TableDefinition {
  id?: string
  tableName: string
  tableDesc?: string
  tableType: string
  formCategory?: string
  primaryKeyStrategy?: string
  showCheckbox?: boolean
  themeTemplate?: string
  formStyle?: string
  scrollBar?: boolean
  pagination?: boolean
  isTree?: boolean
  version?: number
  syncStatus?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  sysOrgCode?: string
  fields?: TableField[]
}

export interface TableField {
  id?: string
  tableName: string
  fieldName: string
  fieldComment?: string
  fieldLength?: number
  decimalPoint?: number
  defaultValue?: string
  fieldType: string
  isPrimaryKey?: boolean
  allowNull?: boolean
  syncDatabase?: boolean
  sortOrder?: number
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
}

export interface PageResponse<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

export const tableDefinitionApi = {
  list: () => {
    return apiClient.get<TableDefinition[]>('/table-definition/list')
  },

  page: (current: number, size: number) => {
    return apiClient.get<PageResponse<TableDefinition>>('/table-definition/page', {
      params: { current, size }
    })
  },

  getByTableName: (tableName: string) => {
    return apiClient.get<TableDefinition>(`/table-definition/${encodeURIComponent(tableName)}`)
  },

  create: (data: TableDefinition) => {
    return apiClient.post<boolean>('/table-definition', data)
  },

  update: (data: TableDefinition) => {
    return apiClient.put<boolean>('/table-definition', data)
  },

  delete: (tableName: string) => {
    return apiClient.delete<boolean>(`/table-definition/${encodeURIComponent(tableName)}`)
  },

  getFields: (tableName: string) => {
    return apiClient.get<TableField[]>(`/table-definition/fields/${encodeURIComponent(tableName)}`)
  },

  addField: (field: TableField) => {
    return apiClient.post<boolean>('/table-definition/fields', field)
  },

  deleteFields: (tableName: string, fieldIds: string[]) => {
    return apiClient.delete<boolean>(`/table-definition/fields/${encodeURIComponent(tableName)}`, {
      data: fieldIds
    })
  }
}