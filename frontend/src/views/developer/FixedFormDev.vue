<template>
  <div class="fixed-form-dev">
    <div class="filter-section">
      <div class="filter-row">
        <div class="filter-item">
          <label>表类型：</label>
          <select v-model="filter.tableType">
            <option value="">全部</option>
            <option value="单表">单表</option>
            <option value="主表">主表</option>
            <option value="从表">从表</option>
          </select>
        </div>
        <div class="filter-item">
          <label>表描述：</label>
          <input type="text" v-model="filter.tableDesc" placeholder="请输入表描述" />
        </div>
      </div>
      <div class="filter-row">
        <button class="btn btn-primary" @click="handleCreateTable">建表</button>
        <button class="btn btn-danger" v-if="selectedTables.length > 0" @click="handleDeleteTables">删除</button>
      </div>
    </div>

    <div class="table-section">
      <table class="data-table">
        <thead>
          <tr>
            <th style="width: 40px;">
              <input type="checkbox" @change="handleSelectAll" :checked="isAllSelected" />
            </th>
            <th>表类型</th>
            <th>表名</th>
            <th>表描述</th>
            <th>版本</th>
            <th>同步状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in filteredTableList" :key="item.tableName">
            <td>
              <input type="checkbox" :value="item.tableName" v-model="selectedTables" />
            </td>
            <td>{{ item.tableType }}</td>
            <td>{{ item.tableName }}</td>
            <td>{{ item.tableDesc }}</td>
            <td>{{ item.version }}</td>
            <td>{{ item.syncStatus }}</td>
            <td>{{ item.createTime }}</td>
            <td>
              <button class="btn btn-link" @click="handleEdit(item)">编辑</button>
            </td>
          </tr>
          <tr v-if="filteredTableList.length === 0">
            <td colspan="8" class="empty-text">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <dialog :open="showDialog" class="edit-dialog" v-if="showDialog">
      <div class="dialog-content">
        <div class="dialog-header">
          <h3>表单编辑</h3>
          <button class="btn-close" @click="handleCloseDialog">&times;</button>
        </div>

        <div class="dialog-body">
          <div class="form-section">
            <h4>基础信息</h4>
            <div class="form-grid">
              <div class="form-item">
                <label>表名：</label>
                <input type="text" v-model="editingForm.tableName" />
              </div>
              <div class="form-item">
                <label>表描述：</label>
                <input type="text" v-model="editingForm.tableDesc" />
              </div>
              <div class="form-item">
                <label>表类型：</label>
                <select v-model="editingForm.tableType">
                  <option value="单表">单表</option>
                  <option value="主表">主表</option>
                  <option value="从表">从表</option>
                </select>
              </div>
              <div class="form-item">
                <label>表单分类：</label>
                <select v-model="editingForm.formCategory">
                  <option value="业务表">业务表</option>
                  <option value="系统表">系统表</option>
                  <option value="临时表">临时表</option>
                </select>
              </div>
              <div class="form-item">
                <label>主键策略：</label>
                <select v-model="editingForm.primaryKeyStrategy">
                  <option value="UUID">UUID</option>
                  <option value="自增">自增</option>
                  <option value="雪花ID">雪花ID</option>
                </select>
              </div>
              <div class="form-item">
                <label>显示复选框：</label>
                <input type="checkbox" v-model="editingForm.showCheckbox" />
              </div>
              <div class="form-item">
                <label>主题模板：</label>
                <select v-model="editingForm.themeTemplate">
                  <option value="default">默认</option>
                  <option value="simple">简约</option>
                  <option value="complex">复杂</option>
                </select>
              </div>
              <div class="form-item">
                <label>表单风格：</label>
                <select v-model="editingForm.formStyle">
                  <option value="default">默认</option>
                  <option value="card">卡片</option>
                  <option value="inline">行内</option>
                </select>
              </div>
              <div class="form-item">
                <label>滚动条：</label>
                <input type="checkbox" v-model="editingForm.scrollBar" />
              </div>
              <div class="form-item">
                <label>是否分页：</label>
                <input type="checkbox" v-model="editingForm.pagination" />
              </div>
              <div class="form-item">
                <label>是否树：</label>
                <input type="checkbox" v-model="editingForm.isTree" />
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-header">
              <h4>字段配置</h4>
              <button class="btn btn-add" @click="handleAddField">新增</button>
              <button class="btn btn-danger" v-if="selectedFields.length > 0" @click="handleDeleteFields">删除</button>
            </div>
            <div class="field-table-wrapper">
              <table class="field-table">
                <thead>
                  <tr>
                    <th style="width: 40px;"></th>
                    <th>#</th>
                    <th>字段名称</th>
                    <th>字段备注</th>
                    <th>字段长度</th>
                    <th>小数点</th>
                    <th>默认值</th>
                    <th>字段类型</th>
                    <th>主键</th>
                    <th>允许空值</th>
                    <th>同步数据库</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(field, index) in editingForm.fields" :key="index">
                    <td>
                      <input type="checkbox" :value="index" v-model="selectedFields" />
                    </td>
                    <td>{{ index + 1 }}</td>
                    <td>
                      <input type="text" v-model="field.fieldName" class="field-input" />
                    </td>
                    <td>
                      <input type="text" v-model="field.fieldComment" class="field-input" />
                    </td>
                    <td>
                      <input type="number" v-model="field.fieldLength" class="field-input small" />
                    </td>
                    <td>
                      <input type="number" v-model="field.decimalPoint" class="field-input small" />
                    </td>
                    <td>
                      <input type="text" v-model="field.defaultValue" class="field-input" />
                    </td>
                    <td>
                      <select v-model="field.fieldType" class="field-input">
                        <option value="varchar">varchar</option>
                        <option value="text">text</option>
                        <option value="int">int</option>
                        <option value="bigint">bigint</option>
                        <option value="decimal">decimal</option>
                        <option value="datetime">datetime</option>
                        <option value="date">date</option>
                        <option value="blob">blob</option>
                      </select>
                    </td>
                    <td>
                      <input type="checkbox" v-model="field.isPrimaryKey" />
                    </td>
                    <td>
                      <input type="checkbox" v-model="field.allowNull" />
                    </td>
                    <td>
                      <input type="checkbox" v-model="field.syncDatabase" />
                    </td>
                  </tr>
                  <tr v-if="editingForm.fields.length === 0">
                    <td colspan="11" class="empty-text">暂无字段，请点击新增添加</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="dialog-footer">
          <button class="btn btn-default" @click="handleCloseDialog">关闭</button>
          <button class="btn btn-primary" @click="handleSave">保存</button>
        </div>
      </div>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { tableDefinitionApi, TableDefinition, TableField } from '../../api/tableDefinition'

interface TableInfo {
  tableType: string
  tableName: string
  tableDesc: string
  version: number
  syncStatus: string
  createTime: string
}

interface FormInfo {
  tableName: string
  tableDesc: string
  tableType: string
  formCategory: string
  primaryKeyStrategy: string
  showCheckbox: boolean
  themeTemplate: string
  formStyle: string
  scrollBar: boolean
  pagination: boolean
  isTree: boolean
  fields: TableField[]
}

const tableList = ref<TableDefinition[]>([])

const filter = reactive({
  tableType: '',
  tableDesc: ''
})

const showDialog = ref(false)
const loading = ref(false)
const selectedFields = ref<number[]>([])
const selectedTables = ref<string[]>([])

const editingForm = reactive<FormInfo>({
  tableName: '',
  tableDesc: '',
  tableType: '单表',
  formCategory: '业务表',
  primaryKeyStrategy: 'UUID',
  showCheckbox: false,
  themeTemplate: 'default',
  formStyle: 'default',
  scrollBar: false,
  pagination: true,
  isTree: false,
  fields: []
})

const filteredTableList = computed(() => {
  return tableList.value.filter(item => {
    const matchType = !filter.tableType || item.tableType === filter.tableType
    const matchDesc = !filter.tableDesc || (item.tableDesc && item.tableDesc.includes(filter.tableDesc))
    return matchType && matchDesc
  })
})

const isAllSelected = computed(() => {
  return filteredTableList.value.length > 0 && filteredTableList.value.every(item => selectedTables.value.includes(item.tableName))
})

const loadTableList = async () => {
  loading.value = true
  try {
    const res = await tableDefinitionApi.list()
    tableList.value = res.data || []
  } catch (error) {
    console.error('加载表列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSelectAll = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.checked) {
    selectedTables.value = filteredTableList.value.map(item => item.tableName)
  } else {
    selectedTables.value = []
  }
}

const handleDeleteTables = async () => {
  if (selectedTables.value.length === 0) return
  if (!confirm(`确定要删除选中的 ${selectedTables.value.length} 个表吗？`)) return
  try {
    for (const tableName of selectedTables.value) {
      await tableDefinitionApi.delete(tableName)
    }
    await loadTableList()
    selectedTables.value = []
  } catch (error) {
    console.error('删除表失败:', error)
    alert('删除失败')
  }
}

const handleCreateTable = () => {
  Object.assign(editingForm, {
    tableName: '',
    tableDesc: '',
    tableType: '单表',
    formCategory: '业务表',
    primaryKeyStrategy: 'UUID',
    showCheckbox: false,
    themeTemplate: 'default',
    formStyle: 'default',
    scrollBar: false,
    pagination: true,
    isTree: false,
    fields: []
  })
  selectedFields.value = []
  showDialog.value = true
}

const handleEdit = async (item: TableInfo) => {
  try {
    const res = await tableDefinitionApi.getByTableName(item.tableName)
    const data = res.data
    Object.assign(editingForm, {
      tableName: data.tableName,
      tableDesc: data.tableDesc || '',
      tableType: data.tableType || '单表',
      formCategory: data.formCategory || '业务表',
      primaryKeyStrategy: data.primaryKeyStrategy || 'UUID',
      showCheckbox: data.showCheckbox || false,
      themeTemplate: data.themeTemplate || 'default',
      formStyle: data.formStyle || 'default',
      scrollBar: data.scrollBar || false,
      pagination: data.pagination !== false,
      isTree: data.isTree || false,
      fields: data.fields || []
    })
    selectedFields.value = []
    showDialog.value = true
  } catch (error) {
    console.error('加载表详情失败:', error)
    alert('加载失败')
  }
}

const handleCloseDialog = () => {
  showDialog.value = false
}

const handleAddField = () => {
  editingForm.fields.push({
    fieldName: '',
    fieldComment: '',
    fieldLength: 50,
    decimalPoint: 0,
    defaultValue: '',
    fieldType: 'String',
    isPrimaryKey: false,
    allowNull: true,
    syncDatabase: true
  })
}

const handleDeleteFields = () => {
  const indicesToDelete = [...selectedFields.value].sort((a, b) => b - a)
  indicesToDelete.forEach(index => {
    editingForm.fields.splice(index, 1)
  })
  selectedFields.value = []
}

const handleSave = async () => {
  try {
    const isNew = !tableList.value.some(item => item.tableName === editingForm.tableName)
    const data: TableDefinition = {
      tableName: editingForm.tableName,
      tableDesc: editingForm.tableDesc,
      tableType: editingForm.tableType,
      formCategory: editingForm.formCategory,
      primaryKeyStrategy: editingForm.primaryKeyStrategy,
      showCheckbox: editingForm.showCheckbox,
      themeTemplate: editingForm.themeTemplate,
      formStyle: editingForm.formStyle,
      scrollBar: editingForm.scrollBar,
      pagination: editingForm.pagination,
      isTree: editingForm.isTree,
      fields: editingForm.fields
    }
    if (isNew) {
      await tableDefinitionApi.create(data)
    } else {
      await tableDefinitionApi.update(data)
    }
    showDialog.value = false
    await loadTableList()
  } catch (error) {
    console.error('保存失败:', error)
    alert('保存失败')
  }
}

onMounted(() => {
  loadTableList()
})
</script>

<style scoped>
.fixed-form-dev {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.filter-section {
  background: #fff;
  padding: 16px;
  border-radius: 4px;
  margin-bottom: 16px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  white-space: nowrap;
  color: #333;
  font-weight: 500;
}

.filter-item input[type="text"],
.filter-item select {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  outline: none;
  min-width: 150px;
}

.filter-item input[type="text"]:focus,
.filter-item select:focus {
  border-color: #1890ff;
}

.table-section {
  flex: 1;
  background: #fff;
  border-radius: 4px;
  overflow: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.data-table th {
  background: #fafafa;
  font-weight: 600;
  color: #333;
  position: sticky;
  top: 0;
}

.data-table tbody tr:hover {
  background: #f5f5f5;
}

.btn {
  padding: 6px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-primary {
  background: #1890ff;
  color: #fff;
}

.btn-primary:hover {
  background: #40a9ff;
}

.btn-default {
  background: #fff;
  color: #333;
  border: 1px solid #d9d9d9;
}

.btn-default:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.btn-link {
  background: none;
  color: #1890ff;
  padding: 4px 8px;
}

.btn-link:hover {
  color: #40a9ff;
}

.btn-add {
  background: #52c41a;
  color: #fff;
}

.btn-add:hover {
  background: #73d13d;
}

.btn-danger {
  background: #ff4d4f;
  color: #fff;
}

.btn-danger:hover {
  background: #ff7875;
}

.empty-text {
  text-align: center;
  color: #999;
  padding: 20px !important;
}

.edit-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  max-width: 1200px;
  max-height: 90vh;
  overflow: auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.dialog-content {
  padding: 0;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.btn-close:hover {
  color: #333;
}

.dialog-body {
  padding: 20px;
  max-height: calc(90vh - 130px);
  overflow-y: auto;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

.form-section {
  margin-bottom: 20px;
}

.form-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.section-header h4 {
  margin: 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.form-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-item label {
  white-space: nowrap;
  color: #333;
  font-weight: 500;
  min-width: 90px;
}

.form-item input[type="text"],
.form-item select {
  flex: 1;
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  outline: none;
}

.form-item input[type="text"]:focus,
.form-item select:focus {
  border-color: #1890ff;
}

.form-item input[type="checkbox"] {
  width: 16px;
  height: 16px;
}

.field-table-wrapper {
  overflow-x: auto;
}

.field-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1000px;
}

.field-table th,
.field-table td {
  padding: 8px;
  text-align: left;
  border: 1px solid #f0f0f0;
  font-size: 13px;
}

.field-table th {
  background: #fafafa;
  font-weight: 600;
  color: #333;
}

.field-table tbody tr:hover {
  background: #f5f5f5;
}

.field-input {
  width: 100%;
  padding: 4px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 13px;
}

.field-input.small {
  width: 60px;
}

.field-input:focus {
  border-color: #1890ff;
  outline: none;
}

.field-table td {
  padding: 4px;
}
</style>