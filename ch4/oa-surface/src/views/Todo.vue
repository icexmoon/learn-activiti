<template>
  <div class="todo-approval">
    <div class="header">
      <h2>审批待办</h2>
    </div>

    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <!-- 申请流下拉框 -->
      <el-form-item label="申请流">
        <el-select 
        v-model="searchForm.applyProcessId" 
        placeholder="请选择申请流" 
        style="width: 240px"
        clearable>
          <el-option
            v-for="process in applyProcessList"
            :key="process.id"
            :label="process.name"
            :value="process.id"
          />
        </el-select>
      </el-form-item>

      <!-- 审批状态选择框 -->
      <el-form-item label="审批状态">
        <el-select 
        v-model="searchForm.status" 
        placeholder="请选择审批状态"
        style="width: 100px" 
        clearable>
          <el-option
            v-for="status in approvalStatusOptions"
            :key="status.key"
            :label="status.name"
            :value="status.key"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="fetchTodoApprovals">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格展示 -->
    <el-table :data="todoApprovals" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="processKey" label="流程标识" align="center" />
      <el-table-column prop="formData.userName" label="申请人" align="center" />
      <el-table-column prop="createTime" label="申请时间" align="center">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" align="center">
        <template #default="{ row }">
          {{ getApprovalStatusName(row.status) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="viewDetail(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import request from '@/util/request';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const router = useRouter();
    const todoApprovals = ref([]);
    const searchForm = ref({
      applyProcessId: '',
      status: ''
    });
    const pageNum = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const applyProcessList = ref([]);
    const approvalStatusOptions = ref([]);

    // 获取审批待办数据
    const fetchTodoApprovals = async () => {
      try {
        const response = await request.get('/api/my_todo/page', {
          params: {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            applyProcessId: searchForm.value.applyProcessId,
            status: searchForm.value.status
          }
        });
        todoApprovals.value = response.data.records;
        total.value = response.data.total;
      } catch (error) {
        console.error('获取审批待办数据失败:', error);
      }
    };

    // 获取申请流列表
    const fetchApplyProcessList = async () => {
      try {
        // 这里需要替换为实际的申请流列表接口
        // const response = await request.get('/api/apply_process/list');
        // applyProcessList.value = response.data.records;
        
        // 模拟数据
        applyProcessList.value = [
          { id: 7, name: '差旅申请' },
          { id: 8, name: '请假申请' }
        ];
      } catch (error) {
        console.error('获取申请流列表失败:', error);
      }
    };

    // 获取审批状态选项
    const fetchApprovalStatusOptions = async () => {
      try {
        const response = await request.get('/api/apply_process/status/list');
        approvalStatusOptions.value = response.data;
      } catch (error) {
        console.error('获取审批状态选项失败:', error);
      }
    };

    // 格式化时间
    const formatDate = (timestamp) => {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ` +
             `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    };

    // 获取状态名称
    const getApprovalStatusName = (statusKey) => {
      const status = approvalStatusOptions.value.find(s => s.key === statusKey);
      return status ? status.name : statusKey;
    };

    // 重置搜索条件
    const resetSearch = () => {
      searchForm.value.applyProcessId = '';
      searchForm.value.status = '';
      fetchTodoApprovals();
    };

    // 页面大小变化
    const handleSizeChange = (newSize) => {
      pageSize.value = newSize;
      fetchTodoApprovals();
    };

    // 当前页码变化
    const handleCurrentChange = (newPage) => {
      pageNum.value = newPage;
      fetchTodoApprovals();
    };

    // 查看详情
    const viewDetail = (row) => {
      // 这里可以实现跳转到详情页的逻辑
      console.log('查看详情:', row);
      router.push({ path: '/ViewForm/'+row.id })
    };

    // 初始化加载数据
    fetchApplyProcessList();
    fetchApprovalStatusOptions();
    fetchTodoApprovals();

    return {
      todoApprovals,
      searchForm,
      pageNum,
      pageSize,
      total,
      applyProcessList,
      approvalStatusOptions,
      fetchTodoApprovals,
      resetSearch,
      formatDate,
      handleSizeChange,
      handleCurrentChange,
      getApprovalStatusName,
      viewDetail
    };
  }
};
</script>

<style scoped>
.todo-approval {
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