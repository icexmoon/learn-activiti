<template>
  <div class="apply-process-management">
    <div class="header">
      <h2>申请流管理</h2>
      <el-button type="primary" @click="showAddDialog">新增申请流</el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="申请流名称">
        <el-input v-model="searchForm.name" placeholder="请输入申请流名称" clearable />
      </el-form-item>
      <el-form-item label="流程定义">
        <el-input v-model="searchForm.processKey" placeholder="请输入流程定义key" clearable />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.enable" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="true" />
          <el-option label="停用" :value="false" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 申请流列表 -->
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="name" label="申请流名称" />
      <el-table-column prop="processKey" label="流程定义" />
      <el-table-column prop="formKey" label="表单标识" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="enable" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.enable ? 'success' : 'info'">
            {{ row.enable ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleToggleStatus(row)">
            {{ row.enable ? '停用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
        :total="total" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </div>

    <!-- 新增申请流对话框 -->
    <el-dialog v-model="dialogVisible" title="新增申请流" width="500px">
      <el-form :model="addForm" label-width="100px" :rules="rules" ref="addFormRef">
        <el-form-item label="申请流名称" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入申请流名称" />
        </el-form-item>
        <el-form-item label="流程定义" prop="processKey">
          <el-input v-model="addForm.processKey" placeholder="请输入流程定义key" />
        </el-form-item>
        <el-form-item label="表单标识" prop="formKey">
          <el-input v-model="addForm.formKey" placeholder="请输入表单标识" />
        </el-form-item>
        <el-form-item label="适用职位" prop="positionIds">
          <el-checkbox-group v-model="addForm.positionIds">
            <el-checkbox @change="positionChoose($event, 0)" :value="0">任何人</el-checkbox>
            <el-checkbox @change="positionChoose($event, item.id)" v-for="item in positionOptions" :key="item.id"
              :value="item.id">
              {{ item.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="状态" prop="enable">
          <el-switch v-model="addForm.enable" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAdd">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑申请流对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑申请流" width="500px">
      <el-form :model="editForm" label-width="100px" :rules="rules" ref="editFormRef">
        <el-form-item label="申请流名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入申请流名称" />
        </el-form-item>
        <el-form-item label="流程定义" prop="processKey">
          <el-input v-model="editForm.processKey" placeholder="请输入流程定义key" />
        </el-form-item>
        <el-form-item label="表单标识" prop="formKey">
          <el-input v-model="editForm.formKey" placeholder="请输入表单标识" />
        </el-form-item>
        <el-form-item label="适用职位" prop="positionIds">
          <el-checkbox-group v-model="editForm.positionIds">
            <el-checkbox @change="positionChoose($event, 0)" :value="0">任何人</el-checkbox>
            <el-checkbox @change="positionChoose($event, item.id)" v-for="item in positionOptions" :key="item.id"
              :value="item.id">
              {{ item.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="状态" prop="enable">
          <el-switch v-model="editForm.enable" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/util/request'

// 搜索表单数据
const searchForm = reactive({
  name: '',
  processKey: '',
  dateRange: [],
  enable: null
})

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 职位选项
const positionOptions = ref([])

// 新增申请流表单数据
const dialogVisible = ref(false)
const addFormRef = ref(null)
const addForm = reactive({
  name: '',
  processKey: '',
  formKey: '',
  positionIds: [],
  enable: true
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入申请流名称', trigger: 'blur' }],
  processKey: [{ required: true, message: '请输入流程定义key', trigger: 'blur' }],
  formKey: [{ required: true, message: '请输入表单标识', trigger: 'blur' }],
  positionIds: [{ required: true, message: '请选择适用职位', trigger: 'change' }]
}

const positionChoose = (choosed, positionId) => {
  if (positionId == 0 && choosed) {
    // 任何人选中
    addForm.positionIds = [0]
    editForm.positionIds = [0]
  }
  else if (choosed && positionId != 0) {
    // 其它人选中
    addForm.positionIds = addForm.positionIds.filter(item => item != 0)
    editForm.positionIds = editForm.positionIds.filter(item => item != 0)
  }
}

// 编辑申请流表单数据
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  id: null,
  name: '',
  processKey: '',
  formKey: '',
  positionIds: [],
  enable: true
})

// 获取职位列表
const fetchPositions = async () => {
  try {
    const response = await request.get('/api/position')
    if (response.success) {
      positionOptions.value = response.data
    } else {
      ElMessage.error(response.message || '获取职位列表失败')
    }
  } catch (error) {
    ElMessage.error('获取职位列表失败：' + (error.message || '未知错误'))
  }
}

// 获取申请流列表数据
const fetchData = async () => {
  try {
    const [startDate, endDate] = searchForm.dateRange || []
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      name: searchForm.name || null,
      processKey: searchForm.processKey || null,
      startDate: startDate || null,
      endDate: endDate || null,
      enable: searchForm.enable
    }

    const response = await request.get('/api/apply_process/page', { params })
    if (response.success) {
      tableData.value = response.data.records
      total.value = response.data.total
    } else {
      ElMessage.error(response.message || '获取数据失败')
    }
  } catch (error) {
    ElMessage.error('获取数据失败：' + (error.message || '未知错误'))
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.processKey = ''
  searchForm.dateRange = []
  searchForm.enable = null
  handleSearch()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}

// 显示新增对话框
const showAddDialog = () => {
  dialogVisible.value = true
  addForm.name = ''
  addForm.processKey = ''
  addForm.formKey = ''
  addForm.positionIds = []
  addForm.enable = true
}

// 打开编辑对话框
const handleEdit = (row) => {
  Object.assign(editForm, row)
  // 如果没有选中职位，则将任何职位都设置为选中
  if (editForm.positionIds == null || editForm.positionIds.length == 0) {
    editForm.positionIds = [0]
  }
  editDialogVisible.value = true
}

// 新增申请流
const handleAdd = async () => {
  if (!addFormRef.value) return

  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          ...addForm,
          positionIds: addForm.positionIds.includes(0) ? null : addForm.positionIds
        }
        const response = await request.post('/api/apply_process/add', submitData)
        if (response.success) {
          ElMessage.success('新增申请流成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.message || '新增申请流失败')
        }
      } catch (error) {
        ElMessage.error('新增申请流失败：' + (error.message || '未知错误'))
      }
    }
  })
}

// 提交编辑
const submitEdit = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          ...editForm,
          positionIds: editForm.positionIds.includes(0) ? null : editForm.positionIds
        }

        const response = await request.post('/api/apply_process/edit', submitData)
        if (response.success) {
          ElMessage.success('编辑申请流成功')
          editDialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.message || '编辑申请流失败')
        }
      } catch (error) {
        ElMessage.error('编辑申请流失败：' + (error.message || '未知错误'))
      }
    }
  })
}

// 切换申请流状态
const handleToggleStatus = async (row) => {
  try {
    const url = row.enable
      ? `/api/apply_process/disable/${row.id}`
      : `/api/apply_process/enable/${row.id}`

    const response = await request.put(url)
    if (response.success) {
      ElMessage.success(`${row.enable ? '停用' : '启用'}成功`)
      fetchData()
    } else {
      ElMessage.error(response.message || `${row.enable ? '停用' : '启用'}失败`)
    }
  } catch (error) {
    ElMessage.error(`${row.enable ? '停用' : '启用'}失败：` + (error.message || '未知错误'))
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
  fetchPositions()
})
</script>

<style scoped>
.apply-process-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>