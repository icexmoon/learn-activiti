<template>
  <div class="department-container">
    <!-- 左侧部门树 -->
    <el-card class="tree-card">
      <template #header>
        <div class="tree-header">
          <span>组织架构</span>
          <!-- <el-button type="primary" size="small" @click="handleAddRoot">添加根部门</el-button> -->
        </div>
      </template>
      <el-tree :data="treeData" :props="defaultProps" node-key="id" default-expand-all :expand-on-click-node="false">
        <template #default="{ node, data }">
          <div class="custom-tree-node">
            <span>{{ node.label }}</span>
            <span class="node-actions">
              <el-button type="primary" link @click="handleAdd(data)">添加</el-button>
              <el-button type="danger" link @click="handleDelete(node, data)">删除</el-button>
            </span>
          </div>
        </template>
      </el-tree>
    </el-card>

    <!-- 右侧部门列表 -->
    <el-card class="list-card">
      <h2>部门管理</h2>
      <el-table :data="tableData" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="部门名称" />
        <el-table-column prop="fullName" label="完整路径" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleManageVirtualUsers(row)">管理虚拟员工</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[5, 10, 20, 50]"
          :total="total" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 添加部门对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '添加部门' : '添加子部门'" width="30%">
      <el-form :model="departmentForm" label-width="100px">
        <el-form-item label="部门名称">
          <el-input v-model="departmentForm.name" placeholder="请输入部门名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 虚拟员工管理对话框 -->
    <el-dialog v-model="virtualUserDialogVisible" title="管理虚拟员工" width="60%">
      <div class="virtual-user-container">
        <!-- 搜索添加区域 -->
        <div class="search-add-section">
          <el-autocomplete
            v-model="searchKeyword"
            :fetch-suggestions="handleSearchSuggestions"
            placeholder="搜索员工（手机号）"
            class="search-input"
            @select="handleSelectUser"
          >
            <template #append>
              <el-button type="primary" @click="handleAddSelectedUser">添加</el-button>
            </template>
          </el-autocomplete>
        </div>

        <!-- 当前虚拟员工列表 -->
        <div class="current-virtual-users">
          <h4>当前虚拟员工：</h4>
          <el-table :data="currentVirtualUsers" style="width: 100%">
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column label="职位" width="200">
              <template #default="{ row }">
                <el-select v-model="row.position.id" @change="(val) => handlePositionChange(row, val)">
                  <el-option
                    v-for="item in positionOptions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleRemoveVirtualUser(row)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="virtualUserDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitVirtualUsers">保存更改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/util/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const treeData = ref([])
const defaultProps = {
  children: 'children',
  label: 'name'
}

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const departmentForm = ref({
  name: '',
  parentId: null
})

// 虚拟员工管理相关
const virtualUserDialogVisible = ref(false)
const currentDepartmentId = ref(null)
const searchKeyword = ref('')
const positionOptions = ref([])
const currentVirtualUsers = ref([])
const originalVirtualUsers = ref([])
const selectedUser = ref(null)

// 获取部门列表数据
const fetchDepartmentList = async () => {
  try {
    const response = await request.get(`/api/department/pageList?pageNum=${currentPage.value}&pageSize=${pageSize.value}`)
    if (response.success) {
      tableData.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    console.error('获取部门列表失败', error)
  }
}

// 获取部门树数据
const fetchDepartmentTree = async () => {
  try {
    const response = await request.get('/api/department/tree')
    if (response.success) {
      treeData.value = [response.data]
    }
  } catch (error) {
    console.error('获取部门树失败', error)
  }
}

// 处理页码改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchDepartmentList()
}

// 处理每页条数改变
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchDepartmentList()
}

// 添加根部门
const handleAddRoot = () => {
  dialogType.value = 'add'
  departmentForm.value = {
    name: '',
    parentId: 0
  }
  dialogVisible.value = true
}

// 添加子部门
const handleAdd = (data) => {
  dialogType.value = 'add'
  departmentForm.value = {
    name: '',
    parentId: data.id
  }
  dialogVisible.value = true
}

// 删除部门
const handleDelete = (node, data) => {
  ElMessageBox.confirm(
    '确定要删除该部门吗？删除后不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await request.delete(`/api/department/delete/${data.id}`)
      if (response.success) {
        ElMessage.success('删除成功')
        // 刷新数据
        fetchDepartmentTree()
        fetchDepartmentList()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除部门失败', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消删除操作
  })
}

// 提交表单
const submitForm = async () => {
  try {
    const response = await request.post('/api/department/add', {
      parentId: departmentForm.value.parentId,
      name: departmentForm.value.name
    })
    if (response.success) {
      ElMessage.success(response.message || '添加成功')
      dialogVisible.value = false
      // 重置表单
      departmentForm.value = {
        name: '',
        parentId: null
      }
      // 刷新数据
      fetchDepartmentTree()
      fetchDepartmentList()
    } else {
      ElMessage.error(response.message || '添加失败')
    }
  } catch (error) {
    console.error('添加部门失败', error)
    ElMessage.error('添加失败')
  }
}

// 获取职位列表
const fetchPositions = async () => {
  try {
    const response = await request.get('/api/position')
    if (response.success) {
      positionOptions.value = response.data
    }
  } catch (error) {
    console.error('获取职位列表失败', error)
    ElMessage.error('获取职位列表失败')
  }
}

// 获取部门虚拟员工列表
const fetchDepartmentVirtualUsers = async (deptId) => {
  try {
    const response = await request.get(`/api/department/virtual_user/list/${deptId}`)
    if (response.success) {
      currentVirtualUsers.value = response.data
      originalVirtualUsers.value = JSON.parse(JSON.stringify(response.data))
    }
  } catch (error) {
    console.error('获取部门虚拟员工失败', error)
    ElMessage.error('获取部门虚拟员工失败')
  }
}

// 打开虚拟员工管理对话框
const handleManageVirtualUsers = async (row) => {
  currentDepartmentId.value = row.id
  // 重置搜索相关状态
  searchKeyword.value = ''
  selectedUser.value = null
  virtualUserDialogVisible.value = true
  await fetchDepartmentVirtualUsers(row.id)
  await fetchPositions()
}

// 处理搜索建议
const handleSearchSuggestions = async (query, callback) => {
  if (!query) {
    callback([])
    return
  }
  try {
    const response = await request.get(`/api/user/search?keyword=${query}&type=all`)
    if (response.success) {
      const suggestions = response.data.map(user => ({
        value: `${user.name || '未设置姓名'} - ${user.phone}`,
        user: user
      }))
      callback(suggestions)
    } else {
      callback([])
    }
  } catch (error) {
    console.error('搜索员工失败', error)
    callback([])
  }
}

// 选择用户
const handleSelectUser = (item) => {
  selectedUser.value = item.user
}

// 添加选中的用户
const handleAddSelectedUser = () => {
  if (!selectedUser.value) {
    ElMessage.warning('请先选择用户')
    return
  }
  
  // 检查是否已经存在
  const exists = currentVirtualUsers.value.some(u => u.id === selectedUser.value.id)
  if (exists) {
    ElMessage.warning('该用户已经是虚拟员工')
    return
  }
  
  // 默认设置为员工职位
  const defaultPosition = positionOptions.value.find(p => p.key === 'employee')
  currentVirtualUsers.value.push({
    ...selectedUser.value,
    position: defaultPosition
  })
  
  // 清空选择
  selectedUser.value = null
  searchKeyword.value = ''
  ElMessage.success('添加成功')
}

// 移除虚拟员工
const handleRemoveVirtualUser = (user) => {
  const index = currentVirtualUsers.value.findIndex(u => u.id === user.id)
  if (index !== -1) {
    currentVirtualUsers.value.splice(index, 1)
  }
}

// 提交虚拟员工更改
const submitVirtualUsers = async () => {
  try {
    const response = await request.post(`/api/department/virtual_user/edit/${currentDepartmentId.value}`,
      currentVirtualUsers.value.map(user => ({
        userId: user.id,
        positionId: user.position.id
      }))
    )
    if (response.success) {
      ElMessage.success('更新成功')
      virtualUserDialogVisible.value = false
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新虚拟员工失败', error)
    ElMessage.error('更新失败')
  }
}

// 处理职位变更
const handlePositionChange = (user, positionId) => {
  const position = positionOptions.value.find(p => p.id === positionId)
  if (position) {
    user.position = position
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchDepartmentList()
  fetchDepartmentTree()
  fetchPositions()
})
</script>

<style scoped>
.department-container {
  display: flex;
  gap: 20px;
}

.tree-card {
  width: 300px;
}

.list-card {
  flex: 1;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.node-actions {
  display: none;
}

.custom-tree-node:hover .node-actions {
  display: inline-block;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.virtual-user-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-add-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.search-results,
.current-virtual-users {
  margin-top: 20px;
}

.search-results h4,
.current-virtual-users h4 {
  margin-bottom: 10px;
  color: #606266;
}
</style>