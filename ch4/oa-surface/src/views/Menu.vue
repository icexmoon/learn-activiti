<template>
  <div class="menu-container">
    <!-- 左侧菜单树 -->
    <el-card class="tree-card">
      <template #header>
        <div class="tree-header">
          <span>菜单树</span>
        </div>
      </template>
      <el-tree
        :data="menuTree"
        :props="defaultProps"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        @node-click="handleNodeClick"
      >
        <template #default="{ node, data }">
          <div class="custom-tree-node">
            <span>{{ node.label }}</span>
            <span class="node-actions">
              <el-button type="primary" link @click="handleEdit(data)">编辑</el-button>
              <el-button type="primary" link @click="handleAdd(data)">添加</el-button>
              <el-button type="danger" link @click="handleDelete(node, data)">删除</el-button>
            </span>
          </div>
        </template>
      </el-tree>
    </el-card>

    <!-- 右侧菜单列表 -->
    <el-card class="list-card">
      <h2>菜单列表</h2>
      <el-table :data="tableData" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="菜单名称" />
        <el-table-column prop="path" label="路径" />
        <el-table-column label="图标">
          <template #default="{ row }">
            <font-awesome-icon v-if="row.icon" :icon="['fas', row.icon.toLowerCase()]" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleBindInterface(row)">绑定接口</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页组件 -->
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

    <!-- 添加/编辑菜单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加菜单' : '编辑菜单'"
      width="30%"
    >
      <el-form :model="menuForm" label-width="100px">
        <el-form-item label="菜单名称">
          <el-input v-model="menuForm.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="路径">
          <el-input v-model="menuForm.path" placeholder="请输入路径" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="menuForm.icon" placeholder="请输入图标名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 绑定接口对话框 -->
    <el-dialog
      v-model="bindDialogVisible"
      title="绑定接口"
      width="800px"
    >
      <div class="bind-interface-container">
        <!-- 搜索区域 -->
        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="请输入接口名称或路径搜索"
            class="search-input"
            @input="handleSearch"
          >
            <template #prefix>
              <font-awesome-icon :icon="['fas', 'search']" />
            </template>
          </el-input>
        </div>

        <!-- 接口列表区域 -->
        <div class="interface-list">
          <el-table :data="searchResults" style="width: 100%">
            <el-table-column prop="name" label="接口名称" />
            <el-table-column prop="path" label="路径" />
            <el-table-column prop="method" label="请求方法" width="100">
              <template #default="{ row }">
                <el-tag :type="getMethodType(row.method)">{{ row.method.toUpperCase() }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleAddInterface(row)">
                  <font-awesome-icon :icon="['fas', 'plus-circle']" />
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 已绑定接口区域 -->
        <div class="binded-interfaces">
          <h3>已绑定接口</h3>
          <el-table :data="bindedInterfaces" style="width: 100%">
            <el-table-column prop="name" label="接口名称" />
            <el-table-column prop="path" label="路径" />
            <el-table-column prop="method" label="请求方法" width="100">
              <template #default="{ row }">
                <el-tag :type="getMethodType(row.method)">{{ row.method.toUpperCase() }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleUnbind(row)">
                  <font-awesome-icon :icon="['fas', 'minus']" />
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="bindDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleBindSubmit">确定</el-button>
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
const menuTree = ref([])
const defaultProps = {
  children: 'children',
  label: 'name'
}

// 分页相关数据
const pagination = ref({
  current: 1,
  size: 5,
  total: 0
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const menuForm = ref({
  name: '',
  path: '',
  icon: '',
  parentId: null,
  weight: 0
})

// 绑定接口相关
const bindDialogVisible = ref(false)
const searchKeyword = ref('')
const searchResults = ref([])
const bindedInterfaces = ref([])
const selectedInterfaces = ref([])
const currentMenuId = ref(null)

// 获取菜单树数据
const fetchMenuTree = async () => {
  try {
    const response = await request.get('/api/menu/tree')
    if (response.success) {
      menuTree.value = [response.data]
    }
  } catch (error) {
    console.error('获取菜单树失败', error)
  }
}

// 获取菜单列表数据
const fetchMenuList = async () => {
  try {
    const response = await request.get('/api/menu/page', {
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
    console.error('获取菜单列表失败', error)
  }
}

// 处理节点点击
const handleNodeClick = (data) => {
  // 可以在这里处理节点点击事件
  console.log('点击节点:', data)
}

// 添加根菜单
const handleAddRoot = () => {
  dialogType.value = 'add'
  menuForm.value = {
    name: '',
    path: '',
    icon: '',
    parentId: 0,
    weight: 0
  }
  dialogVisible.value = true
}

// 添加子菜单
const handleAdd = (data) => {
  dialogType.value = 'add'
  menuForm.value = {
    name: '',
    path: '',
    icon: '',
    parentId: data.id,
    weight: 0
  }
  dialogVisible.value = true
}

// 编辑菜单
const handleEdit = (row) => {
  dialogType.value = 'edit'
  menuForm.value = {
    id: row.id,
    name: row.name,
    path: row.path,
    icon: row.icon,
    parentId: row.parentId,
    weight: row.weight
  }
  dialogVisible.value = true
}

// 删除菜单
const handleDelete = (node, data) => {
  ElMessageBox.confirm(
    '确定要删除该菜单吗？删除后不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await request.delete(`/api/menu/del/${data.id}`)
      if (response.success) {
        ElMessage.success(response.message || '删除成功')
        // 刷新数据
        fetchMenuTree()
        fetchMenuList()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除菜单失败', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消删除操作
  })
}

// 提交表单
const submitForm = async () => {
  try {
    const url = dialogType.value === 'add' ? '/api/menu/add' : '/api/menu/edit'
    const response = await request.post(url, {
      id: dialogType.value === 'edit' ? menuForm.value.id : undefined,
      name: menuForm.value.name,
      parentId: menuForm.value.parentId,
      icon: menuForm.value.icon,
      path: menuForm.value.path,
      weight: menuForm.value.weight
    })
    if (response.success) {
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
      dialogVisible.value = false
      // 刷新数据
      fetchMenuTree()
      fetchMenuList()
    } else {
      ElMessage.error(response.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
    }
  } catch (error) {
    console.error(dialogType.value === 'add' ? '添加菜单失败' : '更新菜单失败', error)
    ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
  }
}

// 处理页码变化
const handleCurrentChange = (val) => {
  pagination.value.current = val
  fetchMenuList()
}

// 处理每页条数变化
const handleSizeChange = (val) => {
  pagination.value.size = val
  pagination.value.current = 1
  fetchMenuList()
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

// 处理绑定接口按钮点击
const handleBindInterface = async (row) => {
  currentMenuId.value = row.id
  bindDialogVisible.value = true
  await fetchBindedInterfaces(row.id)
}

// 获取已绑定的接口
const fetchBindedInterfaces = async (menuId) => {
  try {
    const response = await request.get(`/api/menu/binded/${menuId}`)
    if (response.success) {
      bindedInterfaces.value = response.data
    }
  } catch (error) {
    console.error('获取已绑定接口失败', error)
    ElMessage.error('获取已绑定接口失败')
  }
}

// 处理搜索
const handleSearch = async () => {
  if (!searchKeyword.value) {
    searchResults.value = []
    return
  }
  try {
    const response = await request.get('/api/interface/search', {
      params: {
        keyWord: searchKeyword.value
      }
    })
    if (response.success) {
      searchResults.value = response.data
    }
  } catch (error) {
    console.error('搜索接口失败', error)
    ElMessage.error('搜索接口失败')
  }
}

// 处理解绑
const handleUnbind = async (interfaceItem) => {
  console.log(interfaceItem)
  // 从已绑定列表中移除
  bindedInterfaces.value = bindedInterfaces.value.filter(i => i.id !== interfaceItem.id)
}

// 处理提交绑定
const handleBindSubmit = async () => {
  try {
    const response = await request.post('/api/menu/bind/interface', {
      menuId: currentMenuId.value,
      interfaceIds: [...bindedInterfaces.value.map(i => i.id)]
    })
    if (response.success) {
      ElMessage.success('绑定成功')
      bindDialogVisible.value = false
      await fetchBindedInterfaces(currentMenuId.value)
    }
  } catch (error) {
    console.error('绑定接口失败', error)
    ElMessage.error('绑定接口失败')
  }
}

// 添加接口
const handleAddInterface = (interfaceItem) => {
  console.log(interfaceItem)  
  if (!bindedInterfaces.value.find(i => i.id === interfaceItem.id)) {
    bindedInterfaces.value.push(interfaceItem)
    // 从搜索结果中移除
    searchResults.value = searchResults.value.filter(i => i.id !== interfaceItem.id)
  }
  else{
    ElMessage.error('接口已绑定')
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchMenuTree()
  fetchMenuList()
})
</script>

<style scoped>
.menu-container {
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

.bind-interface-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-area {
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
}

.interface-list {
  margin-bottom: 20px;
}

.binded-interfaces {
  margin-top: 20px;
}

.binded-interfaces h3 {
  margin-bottom: 10px;
  font-size: 16px;
  color: #333;
}
</style> 