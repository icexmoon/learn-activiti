<template>
  <div class="business-travel-form">
    <!-- 使用新的样式类实现标题居中 -->
    <h2 class="form-title">出差申请单</h2>

    <!-- 表单内容保持不变 -->
    <el-form
      ref="form"
      :model="formData"
      label-width="120px"
      :rules="rules"
      label-position="right"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="申请人">
            <el-input v-model="userInfo.name" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系方式">
            <el-input v-model="userInfo.phone" disabled />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="所属部门">
            <el-input
              v-model="userInfo.fullDeptName"
              disabled
              class="dept-input"
              :title="userInfo.fullDeptName"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="职位">
            <el-input v-model="userInfo.positionName" disabled />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="出发日期">
            <el-date-picker
              v-model="formData.startDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="返回日期">
            <el-date-picker
              v-model="formData.endDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="出差天数">
            <el-input-number
              v-model="formData.days"
              :min="0.5"
              :step="0.5"
              step-strictly
            />
            <span class="unit">天</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预算金额">
            <el-input-number v-model="formData.budget" :min="0" />
            <span class="unit">元</span>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="出差事由">
        <el-input
          v-model="formData.reason"
          type="textarea"
          :rows="4"
          placeholder="请输入出差事由"
        />
      </el-form-item>

      <el-form-item label="备注">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          placeholder="如有其他说明请填写在此处"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm">提交申请</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref, onMounted, inject } from "vue";
import request from "@/util/request";

export default {
  name: "BusinessTravelApply",
  setup() {
    const form = ref(null);
    let formData = ref({
      startDate: "",
      endDate: "",
      days: 0,
      budget: 0,
      reason: "",
      remark: "",
    });
    const userInfo = inject("userInfo");
    formData = userInfo;

    // 表单验证规则
    const rules = {
      // 可以在这里添加验证规则
    };

    // 提交表单
    const submitForm = () => {
      // 这里可以添加表单提交逻辑
      console.log("提交表单数据:", formData.value);
      // 实际应用中，这里应该调用API提交数据
    };

    // 重置表单
    const resetForm = () => {
      if (form.value) {
        form.value.resetFields();
      }
    };

    onMounted(() => {
      // fetchUserInfo();
    });

    return {
      form,
      formData,
      rules,
      submitForm,
      resetForm,
      userInfo,
    };
  },
};
</script>

<style scoped>
/* // 为表单容器添加最大宽度和居中对齐 */
.business-travel-form {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

/* // 使表单项在小屏幕上也能良好显示 */
.el-row {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .el-col {
    width: 100%;
    margin-bottom: 15px;
  }

  .el-form-item__content {
    width: 100%;
  }
}

.unit {
  margin-left: 10px;
  color: #666;
}

.el-form-item__content {
  display: flex;
  align-items: center;
}

.form-title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 20px;
  color: #333;
}

/* 自定义工具提示样式 */
.dept-input .el-input__inner {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 仅当内容被截断时显示tooltip */
.dept-input .el-input {
  display: block;
}
</style>
