<template>
  <div class="process-management">
    <div class="header">
      <h2>流程管理</h2>
      <el-button type="primary" @click="showAddDialog">新增流程</el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="流程标识">
        <el-input v-model="searchForm.key" placeholder="请输入流程标识" clearable />
      </el-form-item>
      <el-form-item label="流程名称">
        <el-input v-model="searchForm.name" placeholder="请输入流程名称" clearable />
      </el-form-item>
      <el-form-item label="部署说明">
        <el-input v-model="searchForm.deploymentName" placeholder="请输入部署说明" clearable />
      </el-form-item>
      <el-form-item label="部署时间">
        <el-date-picker
          v-model="searchForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 流程列表 -->
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="name" label="流程名称" />
      <el-table-column prop="key" label="流程标识" />
      <el-table-column prop="deploymentName" label="部署说明" />
      <el-table-column prop="version" label="版本" width="80" />
      <el-table-column prop="deploymentTime" label="部署时间" width="180" />
      <el-table-column label="资源文件" width="200">
        <template #default="{ row }">
          <el-link type="primary" @click="downloadFile(row.deploymentId, row.resourceName)">
            {{ row.resourceName }}
          </el-link>
          <el-divider direction="vertical" />
          <el-link type="primary" @click="downloadFile(row.deploymentId, row.diagramResourceName)">
            {{ row.diagramResourceName }}
          </el-link>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增流程对话框 -->
    <el-dialog v-model="dialogVisible" title="新增流程" width="500px">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="部署说明" required>
          <el-input v-model="addForm.name" placeholder="请输入部署说明" />
        </el-form-item>
        <el-form-item label="BPMN文件" required>
          <el-upload
            class="upload-demo"
            :action="null"
            :auto-upload="false"
            :on-change="handleBpmnChange"
            :limit="1"
            accept=".xml"
          >
            <template #trigger>
              <el-button type="primary">选择文件</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">请上传BPMN2文件（.xml）</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="流程图" required>
          <el-upload
            class="upload-demo"
            :action="null"
            :auto-upload="false"
            :on-change="handlePngChange"
            :limit="1"
            accept=".png"
          >
            <template #trigger>
              <el-button type="primary">选择文件</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">请上传流程图文件（.png）</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAdd">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/util/request'
import fileRequest from '@/util/file_request'

// 搜索表单数据
const searchForm = reactive({
  key: '',
  name: '',
  dateRange: []
})

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 新增流程表单数据
const dialogVisible = ref(false)
const addForm = reactive({
  name: '',
  bpmnFile: null,
  pngFile: null
})

// 获取流程列表数据
const fetchData = async () => {
  try {
    const [start, end] = searchForm.dateRange || []
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      key: searchForm.key || null,
      name: searchForm.name || null,
      start: start || null,
      end: end || null,
      deploymentName: searchForm.deploymentName || null
    }
    
    const response = await request.get('/api/process_definition/page', { params })
    console.log(response)
    if (response.success) {
      tableData.value = response.data.records
      total.value = response.data.total
    } else {
      ElMessage.error(response.data.message || '获取数据失败')
    }
  } catch (error) {
    ElMessage.error('获取数据失败：' + error.message)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.key = ''
  searchForm.name = ''
  searchForm.deploymentName = ''
  searchForm.dateRange = []
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
  addForm.bpmnFile = null
  addForm.pngFile = null
}

// 处理文件选择
const handleBpmnChange = (file) => {
  addForm.bpmnFile = file.raw
}

const handlePngChange = (file) => {
  addForm.pngFile = file.raw
}

// 新增流程
const handleAdd = async () => {
  if (!addForm.name || !addForm.bpmnFile || !addForm.pngFile) {
    ElMessage.warning('请填写完整信息并上传所需文件')
    return
  }

  try {
    const formData = new FormData()
    formData.append('name', addForm.name)
    formData.append('bpmnFile', addForm.bpmnFile)
    formData.append('pngFile', addForm.pngFile)

    // 使用 fileRequest 而不是 request，因为需要处理文件上传
    const response = await fileRequest.post('/api/process_definition/add', formData)

    if (response.data.success) {
      ElMessage.success('新增流程成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(response.data.message || '新增流程失败')
    }
  } catch (error) {
    console.error('上传文件失败:', error)
    if (error.response?.data instanceof Blob) {
      // 如果是 Blob 类型的错误响应
      const reader = new FileReader()
      reader.onload = () => {
        try {
          const errorData = JSON.parse(reader.result)
          ElMessage.error(errorData.message || '新增流程失败')
        } catch (e) {
          ElMessage.error('新增流程失败')
        }
      }
      reader.readAsText(error.response.data)
    } else {
      ElMessage.error('新增流程失败：' + (error.message || '未知错误'))
    }
  }
}

// 下载文件
const downloadFile = async (deploymentId, fileName) => {
  try {
    const response = await fileRequest.get('/api/process_definition/file', {
      params: { deploymentId, fileName },
      responseType: 'blob'
    })
    
    // 检查响应类型
    const contentType = response.headers?.['content-type']
    if (contentType && contentType.includes('application/json')) {
      // 如果返回的是 JSON，说明可能是错误信息
      const reader = new FileReader()
      reader.onload = () => {
        try {
          const errorData = JSON.parse(reader.result)
          ElMessage.error(errorData.message || '下载文件失败')
        } catch (e) {
          ElMessage.error('下载文件失败')
        }
      }
      reader.readAsText(response.data)
      return
    }

    // 创建下载链接
    const blob = new Blob([response.data], { type: contentType || 'application/octet-stream' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', fileName)
    document.body.appendChild(link)
    link.click()
    
    // 清理
    setTimeout(() => {
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    }, 100)
  } catch (error) {
    console.error('下载文件失败:', error)
    
    // 处理错误响应
    if (error.data instanceof Blob) {
      // 如果是 Blob 类型的错误响应
      const reader = new FileReader()
      reader.onload = () => {
        try {
          const errorData = JSON.parse(reader.result)
          ElMessage.error(errorData.message || '下载文件失败')
        } catch (e) {
          ElMessage.error('下载文件失败')
        }
      }
      reader.readAsText(error.data)
    } else if (typeof error === 'object' && error !== null) {
      // 处理其他类型的错误对象
      const errorMessage = error.message || error.msg || '下载文件失败'
      ElMessage.error(errorMessage)
    } else {
      // 处理未知类型的错误
      ElMessage.error('下载文件失败：未知错误')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.process-management {
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