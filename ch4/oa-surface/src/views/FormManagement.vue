<template>
  <div class="form-management">
    <div class="header">
      <h2>表单管理</h2>
      <el-button type="primary" @click="showAddDialog">新增表单</el-button>
    </div>

    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="表单标识">
        <el-input v-model="searchForm.key" placeholder="请输入表单标识(key)" clearable />
      </el-form-item>
      <el-form-item label="名称">
        <el-input v-model="searchForm.name" placeholder="请输入表单名称(name)" clearable />
      </el-form-item>
      <el-form-item label="创建时间">
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
        <el-button type="primary" @click="fetchForms">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格展示 -->
    <el-table :data="forms" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="key" label="表单标识 (Key)" align="center" />
      <el-table-column prop="name" label="名称 (Name)" align="center" />
      <el-table-column prop="version" label="版本" width="100" align="center" />
      <el-table-column prop="path" label="路径 (Path)" align="center">
        <template #default="{ row }">
          {{ row.path || '无' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" align="center">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增表单对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增表单" width="500px">
      <el-form :model="addForm" label-width="100px" :rules="rules" ref="addFormRef">
        <el-form-item label="表单标识" prop="key">
          <el-input v-model="addForm.key" placeholder="请输入表单标识" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入表单名称" />
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="addForm.path" placeholder="请输入表单路径" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref } from 'vue';
import request from '@/util/request';
import { ElMessageBox, ElMessage } from 'element-plus';

export default {
  setup() {
    const forms = ref([]);
    const searchForm = ref({
      key: '',
      name: '',
      dateRange: []
    });
    const pageNum = ref(1);
    const pageSize = ref(10);
    const total = ref(0);

    // 新增表单相关
    const addDialogVisible = ref(false);
    const addForm = ref({
      key: '',
      name: '',
      path: ''
    });
    const rules = {
      key: [{ required: true, message: '请输入表单标识', trigger: 'blur' }],
      name: [{ required: true, message: '请输入表单名称', trigger: 'blur' }],
      path: [{ required: true, message: '请输入表单路径', trigger: 'blur' }]
    };
    const addFormRef = ref(null);

    // 获取表单数据
    const fetchForms = async () => {
      try {
        const [startDate, endDate] = searchForm.value.dateRange || [];
        const response = await request.get('/api/apply_form/page', {
          params: {
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            key: searchForm.value.key,
            name: searchForm.value.name,
            startDate,
            endDate
          }
        });
        forms.value = response.data.records;
        total.value = response.data.total;
      } catch (error) {
        console.error('获取表单数据失败:', error);
      }
    };

    // 重置搜索条件
    const resetSearch = () => {
      searchForm.value.key = '';
      searchForm.value.name = '';
      searchForm.value.dateRange = [];
      fetchForms();
    };

    // 格式化时间
    const formatDate = (timestamp) => {
      const date = new Date(timestamp);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ` +
             `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    };

    // 页面大小变化
    const handleSizeChange = (newSize) => {
      pageSize.value = newSize;
      fetchForms();
    };

    // 当前页码变化
    const handleCurrentChange = (newPage) => {
      pageNum.value = newPage;
      fetchForms();
    };

    // 显示新增对话框
    const showAddDialog = () => {
      addForm.value = {
        key: '',
        name: '',
        path: ''
      };
      addDialogVisible.value = true;
    };

    // 提交新增表单
    const handleSubmit = async () => {
      if (!addFormRef.value) return;

      await addFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const response = await request.post('/api/apply_form/add', addForm.value);
            if (response.success) {
              ElMessage.success('新增表单成功');
              addDialogVisible.value = false;
              fetchForms();
            } else {
              ElMessage.error(response.message || '新增表单失败');
            }
          } catch (error) {
            console.error('新增表单失败:', error);
            ElMessage.error('新增表单失败：' + (error.message || '未知错误'));
          }
        }
      });
    };

    // 删除表单
    const handleDelete = async (row) => {
      try {
        // 确认删除
        await ElMessageBox.confirm(
          `确定要删除表单【${row.name}】吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });

        // 调用删除接口
        const response = await request.delete(`/api/apply_form/${row.id}`);
        
        if (response.success) {
          ElMessage.success('删除表单成功');
          fetchForms();
        } else {
          ElMessage.error(response.message || '删除表单失败');
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除表单失败：' + (error.message || '未知错误'));
        }
      }
    };

    // 初始化加载数据
    fetchForms();

    return {
      forms,
      searchForm,
      pageNum,
      pageSize,
      total,
      fetchForms,
      resetSearch,
      formatDate,
      handleSizeChange,
      handleCurrentChange,
      // 新增相关
      addDialogVisible,
      addForm,
      rules,
      addFormRef,
      showAddDialog,
      handleSubmit,
      // 删除相关
      handleDelete
    };
  }
};
</script>

<style scoped>
.form-management {
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