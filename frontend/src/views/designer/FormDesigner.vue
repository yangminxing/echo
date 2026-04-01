<template>
  <div class="form-designer-container">
    <div class="header">
      <h2>表单管理</h2>
      <el-button type="primary" @click="handleAdd">新增表单</el-button>
    </div>
    
    <el-table :data="formList" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="formName" label="表单名称" min-width="150" />
      <el-table-column prop="formVersion" label="版本" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '已发布' ? 'success' : 'info'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="syncStatus" label="同步状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.syncStatus === '已同步' ? 'success' : 'warning'">
            {{ row.syncStatus || '未同步' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="tableName" label="物理表名" width="150" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleDesign(row)">表单设计</el-button>
          <el-button type="success" link @click="handleSync(row)" :loading="row.syncing">同步物理表</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFormList, deleteForm, syncToPhysicalTable } from '../../api/formDesigner'
import type { FormDefinition } from '../../types/formDesigner'

const router = useRouter()
const formList = ref<FormDefinition[]>([])
const loading = ref(false)

const loadFormList = async () => {
  loading.value = true
  try {
    const response = await getFormList()
    formList.value = response.data
  } catch (error) {
    ElMessage.error('加载表单列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  router.push('/home/form-editor')
}

const handleDesign = (row: FormDefinition) => {
  router.push(`/home/form-editor/${row.id}`)
}

const handleSync = async (row: FormDefinition) => {
  try {
    await ElMessageBox.confirm(
      `确定要将表单「${row.formName}」同步为物理表吗？\n这将创建或更新数据库表结构。`,
      '同步确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    row.syncing = true
    await syncToPhysicalTable(row.id)
    ElMessage.success('同步成功')
    loadFormList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('同步失败: ' + (error as any).message)
    }
  } finally {
    row.syncing = false
  }
}

const handleDelete = async (row: FormDefinition) => {
  try {
    await ElMessageBox.confirm('确定要删除该表单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteForm(row.id)
    ElMessage.success('删除成功')
    loadFormList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadFormList()
})
</script>

<style scoped>
.form-designer-container {
  height: 100%;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}
</style>