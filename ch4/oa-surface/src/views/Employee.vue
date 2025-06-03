<template>
  <el-card>
    <h2>员工管理</h2>
    <el-table :data="tableData" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="deptName" label="部门" />
      <el-table-column prop="position.name" label="职位" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 添加分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 编辑员工对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑员工信息"
      width="30%"
    >
      <el-form :model="employeeForm" label-width="100px">
        <el-form-item label="员工姓名">
          <el-input v-model="employeeForm.name" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-cascader
            v-model="employeeForm.deptIds"
            :options="departmentTree"
            :props="{
              checkStrictly: true,
              value: 'id',
              label: 'name',
              children: 'children'
            }"
            placeholder="请选择部门"
            clearable
          />
        </el-form-item>
        <el-form-item label="职位">
          <el-select v-model="employeeForm.positionId" placeholder="请选择职位" style="width: 100%">
            <el-option
              v-for="item in positionList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
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
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/util/request'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const dialogVisible = ref(false)
const departmentTree = ref([])
const positionList = ref([])

// 员工表单数据
const employeeForm = ref({
  id: null,
  name: '',
  deptIds: null,
  positionId: null,
  roleId: null
})

// 获取员工列表数据
const fetchEmployeeList = async () => {
  try {
    const response = await request.get(`/api/user/pageList?pageNo=${currentPage.value}&pageSize=${pageSize.value}`)
    console.log(response)
    if (response.success) {
      tableData.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    console.error('获取员工列表失败', error)
  }
}

// 获取部门树数据
const fetchDepartmentTree = async () => {
  try {
    const response = await request.get('/api/department/tree')
    if (response.success) {
      departmentTree.value = [response.data]
    }
  } catch (error) {
    console.error('获取部门树失败', error)
  }
}

// 获取职位列表
const fetchPositionList = async () => {
  try {
    const response = await request.get('/api/position')
    if (response.success) {
      positionList.value = response.data
    }
  } catch (error) {
    console.error('获取职位列表失败', error)
  }
}

// 处理页码改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchEmployeeList()
}

// 处理每页条数改变
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchEmployeeList()
}

// 处理编辑按钮点击
const handleEdit = (row) => {
  employeeForm.value = {
    id: row.id,
    name: row.name,
    deptIds: row.deptId,
    positionId: row.positionId,
    roleId: row.roleId
  }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    // 处理部门id，确保它是一个数组
    const deptIds = Array.isArray(employeeForm.value.deptIds) 
      ? employeeForm.value.deptIds 
      : [employeeForm.value.deptIds];
    employeeForm.value.deptId = deptIds[deptIds.length - 1];
    const response = await request.post('/api/user/edit', employeeForm.value)
    if (response.success) {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      fetchEmployeeList()
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新员工信息失败', error)
    ElMessage.error('更新失败')
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchEmployeeList()
  fetchDepartmentTree()
  fetchPositionList()
})
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 