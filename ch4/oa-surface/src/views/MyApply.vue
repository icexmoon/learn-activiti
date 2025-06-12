<template>
  <div class="my-apply">
    <div class="header">
      <h2>我的申请</h2>
    </div>

    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="流程类型">
        <el-select 
        v-model="searchForm.applyProcessId" 
        placeholder="请选择流程类型" 
        size="large"
        style="width: 240px"
        clearable>
          <el-option
            v-for="process in processOptions"
            :key="process.id"
            :label="process.name"
            :value="process.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间">
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

    <!-- 申请列表 -->
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="applyProcess.name" label="流程名称" />
      <el-table-column prop="processKey" label="流程标识" />
      <el-table-column prop="createTime" label="申请时间" width="180" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" @click="viewApplication(row)" plain>查看</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/util/request'
import { useRouter } from 'vue-router'

const router = useRouter();

// 搜索表单数据
const searchForm = reactive({
  applyProcessId: null,
  dateRange: []
})

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 流程选项
const processOptions = ref([])

// 获取我的申请列表数据
const fetchData = async () => {
  try {
    const [start, end] = searchForm.dateRange || []
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      applyProcessId: searchForm.applyProcessId || null,
      beginDate: start || null,
      endDate: end || null
    }

    const response = await request.get('/api/my_apply/page', { params })
    
    if (response.success) {
      tableData.value = response.data.records
      total.value = response.data.total
    } else {
      ElMessage.error(response.message || '获取数据失败')
    }
  } catch (error) {
    ElMessage.error('获取数据失败：' + error.message)
  }
}

// 获取流程选项
const fetchProcessOptions = async () => {
  try {
    const response = await request.get('/api/apply_process/page',{
      params: {
        pageNum: 1,
        pageSize: 100,
        enable: true
      }
    })
    
    if (response.success) {
      processOptions.value = response.data.records.map(item => ({
        id: item.id,
        name: item.name
      }))
      // console.log(processOptions.value)
      // processOptions.value = response.data || []
    } else {
      ElMessage.error('获取流程选项失败')
    }
  } catch (error) {
    ElMessage.error('获取流程选项失败：' + error.message)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.applyProcessId = null
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

// 查看申请详情
const viewApplication = (row) => {
  // 这里可以添加跳转到详细页面的逻辑
  router.push({
    path: '/ViewForm/' + row.id
  });
}

// 页面加载时获取数据
onMounted(() => {
  fetchProcessOptions()
  fetchData()
})
</script>

<style scoped>
.my-apply {
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
</style>