<template>
  <div class="perm-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>特殊用户管理</span>
          <el-button type="primary" @click="handleAdd">
            <font-awesome-icon :icon="['fas', 'plus']" />
            添加特殊用户
          </el-button>
        </div>
      </template>

      <!-- 用户列表 -->
      <el-table :data="userList" style="width: 100%">
        <el-table-column prop="name" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column label="角色">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role.id" class="role-tag">
              {{ role.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <font-awesome-icon :icon="['fas', 'edit']" />
              编辑角色
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
          :page-sizes="[5, 10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '添加特殊用户' : '编辑角色'" width="500px">
      <template v-if="dialogType === 'add'">
        <el-form :model="searchForm" label-width="80px">
          <el-form-item label="搜索用户">
            <el-select v-model="searchForm.keyword" filterable remote :remote-method="searchUsers"
              :loading="searchLoading" placeholder="请输入用户名或手机号" style="width: 100%">
              <el-option v-for="user in searchResults" :key="user.id" :label="`${user.name} (${user.phone})`"
                :value="user.id" />
            </el-select>
          </el-form-item>
        </el-form>
      </template>

      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="角色">
          <el-select v-model="roleForm.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option v-for="role in roleOptions" :key="role.id" :label="role.name" :value="role.id" />
          </el-select>
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
import { ElMessage } from 'element-plus'
import request from '@/util/request'

// 分页相关
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const userList = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const currentUser = ref(null)

// 搜索相关
const searchForm = ref({
  keyword: ''
})
const searchResults = ref([])
const searchLoading = ref(false)

// 角色表单
const roleForm = ref({
  roleIds: []
})

// 角色选项
const roleOptions = ref([])
const fetchRoleOptions = async () => {
  const response = await request.get('/api/role/list');
  if (response.success) {
    roleOptions.value = response.data;
  }
}

// 获取用户列表
const fetchUserList = async () => {
  try {
    const response = await request.get(`/api/permission/user/page`, {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })
    if (response.success) {
      userList.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
}

// 搜索用户
const searchUsers = async (query) => {
  if (query) {
    searchLoading.value = true
    try {
      const response = await request.get(`/api/user/search`, {
        params: { keyword: query }
      })
      if (response.success) {
        searchResults.value = response.data
      }
    } catch (error) {
      ElMessage.error('搜索用户失败')
    } finally {
      searchLoading.value = false
    }
  } else {
    searchResults.value = []
  }
}

// 处理添加按钮点击
const handleAdd = () => {
  dialogType.value = 'add'
  searchForm.value.keyword = ''
  roleForm.value.roleIds = []
  dialogVisible.value = true
}

// 处理编辑按钮点击
const handleEdit = (row) => {
  dialogType.value = 'edit'
  currentUser.value = row
  roleForm.value.roleIds = row.roleIds
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    const response = await request.post('/api/permission/edit', {
      userId: dialogType.value === 'add' ? searchForm.value.keyword : currentUser.value.id,
      roleIds: roleForm.value.roleIds
    })
    if (response.success) {
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
      dialogVisible.value = false
      fetchUserList()
    }
  } catch (error) {
    ElMessage.error(dialogType.value === 'add' ? '添加失败' : '修改失败')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchUserList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchUserList()
}

// 初始化
onMounted(() => {
  fetchUserList()
  fetchRoleOptions()
})
</script>

<style scoped>
.perm-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.role-tag {
  margin-right: 8px;
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