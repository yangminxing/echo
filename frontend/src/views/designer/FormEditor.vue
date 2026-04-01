<template>
  <div class="form-editor-container">
    <div class="editor-header">
      <div class="left">
        <el-button @click="handleBack">返回</el-button>
        <span class="title">{{ isEdit ? '编辑表单' : '新建表单' }}</span>
      </div>
      <div class="right">
        <el-input 
          v-model="formName" 
          placeholder="请输入表单名称" 
          style="width: 200px; margin-right: 10px"
        />
        <el-input 
          v-model="description" 
          placeholder="请输入表单描述" 
          style="width: 250px; margin-right: 10px"
        />
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </div>
    </div>
    <div class="editor-content">
      <EDesigner ref="designerRef" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { EDesigner } from 'epic-designer'
import { getFormById, saveForm, updateForm } from '../../api/formDesigner'
import type { FormDefinition } from '../../types/formDesigner'

const router = useRouter()
const route = useRoute()
const designerRef = ref()
const formName = ref('')
const description = ref('')
const saving = ref(false)
const formId = computed(() => route.params.id as string)
const isEdit = computed(() => !!formId.value)

const loadFormData = async () => {
  if (formId.value) {
    try {
      const response = await getFormById(formId.value)
      const form = response.data
      formName.value = form.formName
      description.value = form.description || ''
      if (form.formSchema) {
        const schema = JSON.parse(form.formSchema)
        designerRef.value?.setData(schema)
      }
    } catch (error) {
      ElMessage.error('加载表单数据失败')
    }
  }
}

const handleSave = async () => {
  if (!formName.value.trim()) {
    ElMessage.warning('请输入表单名称')
    return
  }

  const schema = designerRef.value?.getData()
  if (!schema) {
    ElMessage.warning('请设计表单内容')
    return
  }

  saving.value = true
  try {
    const data: FormDefinition = {
      id: formId.value || '',
      formName: formName.value,
      formVersion: 0,
      formSchema: JSON.stringify(schema),
      status: '草稿',
      description: description.value,
      createBy: '',
      createTime: '',
      updateBy: '',
      updateTime: ''
    }

    if (isEdit.value) {
      await updateForm(data)
      ElMessage.success('更新成功')
    } else {
      await saveForm(data)
      ElMessage.success('保存成功')
    }
    handleBack()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleBack = () => {
  router.push('/home/form-designer')
}

onMounted(() => {
  loadFormData()
})
</script>

<style scoped>
.form-editor-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.editor-header {
  height: 50px;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e4e7ed;
  background: #f5f7fa;
}

.editor-header .left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.editor-header .title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.editor-header .right {
  display: flex;
  align-items: center;
}

.editor-content {
  flex: 1;
  overflow: hidden;
}
</style>
