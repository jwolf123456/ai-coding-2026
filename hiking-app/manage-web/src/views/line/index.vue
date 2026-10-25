<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>线路列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增线路
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="160" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="distance" label="距离(km)" width="100" />
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="difficultyType(row.difficulty)">{{ difficultyLabel(row.difficulty) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startPoint" label="起点" width="120" show-overflow-tooltip />
        <el-table-column prop="endPoint" label="终点" width="120" show-overflow-tooltip />
        <el-table-column prop="coverImage" label="封面" width="100">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" style="width:40px;height:40px" fit="cover" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上线' : '下线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除?" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        v-model:current-page="page.current"
        v-model:page-size="page.size"
        :total="page.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @change="fetchData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑线路' : '新增线路'" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="距离(km)" prop="distance">
          <el-input-number v-model="form.distance" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="form.difficulty">
            <el-option :value="1" label="简单" />
            <el-option :value="2" label="中等" />
            <el-option :value="3" label="困难" />
            <el-option :value="4" label="极难" />
          </el-select>
        </el-form-item>
        <el-form-item label="起点" prop="startPoint">
          <el-input v-model="form.startPoint" />
        </el-form-item>
        <el-form-item label="终点" prop="endPoint">
          <el-input v-model="form.endPoint" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-input v-model="form.coverImage" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上线</el-radio>
            <el-radio :value="0">下线</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue'
import type {FormInstance, FormRules} from 'element-plus'
import {ElMessage} from 'element-plus'
import {createLine, deleteLine, getLinePage, updateLine} from '../../api/line'
import type {Line} from '../../types'

const loading = ref(false)
const tableData = ref<Line[]>([])
const page = reactive({ current: 1, size: 10, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Partial<Line>>({
  title: '',
  description: '',
  distance: 0,
  difficulty: 1,
  startPoint: '',
  endPoint: '',
  coverImage: '',
  status: 1,
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
}

function difficultyLabel(d: number) {
  const map: Record<number, string> = { 1: '简单', 2: '中等', 3: '困难', 4: '极难' }
  return map[d] || '未知'
}

function difficultyType(d: number) {
  const map: Record<number, string> = { 1: 'success', 2: '', 3: 'warning', 4: 'danger' }
  return map[d] || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getLinePage(page.current, page.size)
    tableData.value = res.data.records
    page.total = res.data.total
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  dialogVisible.value = true
}

function handleEdit(row: Line) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleDelete(id: number) {
  await deleteLine(id)
  ElMessage.success('删除成功')
  fetchData()
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (isEdit.value) {
    await updateLine(form as Line)
  } else {
    await createLine(form as Line)
  }
  ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
  dialogVisible.value = false
  fetchData()
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { title: '', description: '', distance: 0, difficulty: 1, startPoint: '', endPoint: '', coverImage: '', status: 1 })
}

onMounted(() => fetchData())
</script>

<style scoped>
.page-container {
  height: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>