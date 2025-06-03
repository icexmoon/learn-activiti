<template>
  <div class="interface-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>接口管理</span>
          <el-button type="primary" @click="handleAdd">新增接口</el-button>
        </div>
      </template>

      <!-- 接口列表 -->
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="接口名称" />
        <el-table-column prop="desc" label="描述" />
        <el-table-column prop="path" label="路径" />
        <el-table-column prop="method" label="请求方法" width="100">
          <template #default="{ row }">
            <el-tag :type="getMethodType(row.method)">{{ row.method.toUpperCase() }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="权限" width="300">
          <template #default="{ row }">
            <el-space>
              <el-tag :type="row.modeView ? 'success' : 'info'" effect="plain">查看</el-tag>
              <el-tag :type="row.modeAdd ? 'success' : 'info'" effect="plain">新增</el-tag>
              <el-tag :type="row.modeEdit ? 'success' : 'info'" effect="plain">修改</el-tag>
              <el-tag :type="row.modeDel ? 'success' : 'info'" effect="plain">删除</el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[5, 10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增接口' : '编辑接口'"
      width="500px"
    >
      <el-form :model="interfaceForm" label-width="100px">
        <el-form-item label="接口名称">
          <el-input v-model="interfaceForm.name" placeholder="请输入接口名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="interfaceForm.desc" placeholder="请输入接口描述" />
        </el-form-item>
        <el-form-item label="路径">
          <el-input v-model="interfaceForm.path" placeholder="请输入接口路径" />
        </el-form-item>
        <el-form-item label="请求方法">
          <el-select v-model="interfaceForm.method" placeholder="请选择请求方法">
            <el-option label="GET" value="get" />
            <el-option label="POST" value="post" />
            <el-option label="PUT" value="put" />
            <el-option label="DELETE" value="delete" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限设置">
          <el-space direction="vertical">
            <el-checkbox v-model="interfaceForm.modeView">查看权限</el-checkbox>
            <el-checkbox v-model="interfaceForm.modeAdd">新增权限</el-checkbox>
            <el-checkbox v-model="interfaceForm.modeEdit">修改权限</el-checkbox>
            <el-checkbox v-model="interfaceForm.modeDel">删除权限</el-checkbox>
          </el-space>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/util/request'
import { ElMessage } from 'element-plus'

// 表格数据
const tableData = ref([])

// 分页数据
const pagination = ref({
  current: 1,
  size: 5,
  total: 0
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const interfaceForm = ref({
  name: '',
  desc: '',
  path: '',
  method: 'get',
  modeView: false,
  modeAdd: false,
  modeEdit: false,
  modeDel: false
})

// 获取接口列表
const fetchInterfaceList = async () => {
  try {
    const response = await request.get('/api/interface/page', {
      params: {
        pageNum: pagination.value.current,
        pageSize: pagination.value.size
      }
    })
    if (response.success) {
      tableData.value = response.data.records
      pagination.value.total = response.data.total
    }
  } catch (error) {
    console.error('获取接口列表失败', error)
    ElMessage.error('获取接口列表失败')
  }
}

// 处理页码变化
const handleCurrentChange = (val) => {
  pagination.value.current = val
  fetchInterfaceList()
}

// 处理每页条数变化
const handleSizeChange = (val) => {
  pagination.value.size = val
  pagination.value.current = 1
  fetchInterfaceList()
}

// 获取请求方法的标签类型
const getMethodType = (method) => {
  const typeMap = {
    get: 'success',
    post: 'primary',
    put: 'warning',
    delete: 'danger'
  }
  return typeMap[method.toLowerCase()] || 'info'
}

// 新增接口
const handleAdd = () => {
  dialogType.value = 'add'
  interfaceForm.value = {
    name: '',
    desc: '',
    path: '',
    method: 'get',
    modeView: false,
    modeAdd: false,
    modeEdit: false,
    modeDel: false
  }
  dialogVisible.value = true
}

// 编辑接口
const handleEdit = (row) => {
  dialogType.value = 'edit'
  interfaceForm.value = { ...row }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    const url = dialogType.value === 'add' ? '/api/interface/add' : '/api/interface/edit'
    const response = await request.post(url, interfaceForm.value)
    if (response.success) {
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
      dialogVisible.value = false
      fetchInterfaceList()
    } else {
      ElMessage.error(response.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
    }
  } catch (error) {
    console.error(dialogType.value === 'add' ? '添加接口失败' : '更新接口失败', error)
    ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchInterfaceList()
})
</script>

<style scoped>
.interface-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-select) {
  width: 100%;
}
</style> 