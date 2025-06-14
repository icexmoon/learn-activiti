<template>
  <div class="approval-history">
    <div class="header">
      <h2>审批记录</h2>
    </div>

    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="流程类型">
        <el-select
          v-model="searchForm.applyProcessId"
          placeholder="请选择流程类型"
          size="large"
          style="width: 240px"
          clearable
        >
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

    <!-- 审批列表 -->
    <el-table :data="tableData.records" border style="width: 100%">
      <el-table-column prop="id" label="申请id" />
      <el-table-column prop="applyProcess.name" label="申请类型" />
      <el-table-column prop="formData.userName" label="申请人" />
      <el-table-column prop="formData.positionName" label="职位" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="statusText" label="状态" width="100" />
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
        :page-sizes="[5, 10, 20, 50]"
        :total="tableData.total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import request from "@/util/request";
import { useRouter } from "vue-router";
import { ref, onMounted, reactive } from "vue";

export default {
  name: "ApprovalHistory",
  setup() {
    const router = useRouter();
    const searchForm = ref({
      pageNum: 1,
      pageSize: 5,
      beginDate: "",
      endDate: "",
      applyProcessId: "",
      dateRange: []
    });
    const currentPage = ref(1);
    const pageSize = ref(5);
    const tableData = ref({ records: [], total: 0 });
    const processOptions = ref([]);

    const fetchData = async () => {
      try {
        const [start, end] = searchForm.value.dateRange || [];
        const params = {
          pageNum: currentPage.value,
          pageSize: pageSize.value,
          applyProcessId: searchForm.value.applyProcessId || null,
          beginDate: start || null,
          endDate: end || null
        };

        const response = await request.get("/api/apply_history/page", {
          params
        });

        if (response.success) {
          tableData.value.records = response.data.records;
          tableData.value.total = response.data.total;
        } else {
          console.error("Failed to fetch data:", response.message);
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    const fetchProcessOptions = async () => {
      try {
        const response = await request.get('/api/apply_process/page',{
          params: {
            pageNum: 1,
            pageSize: 100,
            enable: true
          }
        });

        if (response.success) {
          processOptions.value = response.data.records.map(item => ({
            id: item.id,
            name: item.name
          }));
        } else {
          console.error('获取流程选项失败');
        }
      } catch (error) {
        console.error('获取流程选项失败：' + error.message);
      }
    };

    const onSubmit = () => {
      currentPage.value = 1; // Reset to first page when submitting new search
      fetchData();
    };

    const handleSearch = () => {
      currentPage.value = 1;
      fetchData();
    };

    const resetSearch = () => {
      searchForm.value.applyProcessId = null;
      searchForm.value.dateRange = [];
      handleSearch();
    };

    const handleSizeChange = (val) => {
      pageSize.value = val;
      fetchData();
    };

    const handleCurrentChange = (val) => {
      currentPage.value = val;
      fetchData();
    };

    const viewApplication = (row) => {
      router.push({ path: "/viewForm/" + row.id });
    };

    onMounted(() => {
      fetchProcessOptions();
      fetchData();
    });

    return {
      searchForm,
      tableData,
      onSubmit,
      handleCurrentChange,
      viewApplication,
      currentPage,
      pageSize,
      handleSizeChange,
      handleSearch,
      resetSearch,
      processOptions
    };
  }
};
</script>

<style scoped>
.approval-history {
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
