<template>
  <div class="business-travel-form">
    <h2 class="form-title">出差申请单</h2>

    <el-form ref="form" :model="formData" label-width="120px" label-position="right">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="申请人">
            <el-input v-model="formData.userName" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系方式">
            <el-input v-model="formData.phone" disabled />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="所属部门">
            <el-input
              v-model="formData.fullDeptName"
              disabled
              class="dept-input"
              :title="formData.fullDeptName"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="职位">
            <el-input v-model="formData.positionName" disabled />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="出发日期">
            <el-date-picker
              v-model="formData.extraData.startDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="返回日期">
            <el-date-picker
              v-model="formData.extraData.endDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              disabled
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="出差天数">
            <el-input-number
              v-model="formData.extraData.days"
              :min="0.5"
              :step="0.5"
              step-strictly
              disabled
            />
            <span class="unit">天</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预算金额">
            <el-input-number v-model="formData.extraData.budget" :min="0" disabled />
            <span class="unit">元</span>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="出差事由">
        <el-input
          v-model="formData.extraData.reason"
          type="textarea"
          :rows="4"
          placeholder="请输入出差事由"
          disabled
        />
      </el-form-item>

      <el-form-item label="备注">
        <el-input
          v-model="formData.extraData.remark"
          type="textarea"
          :rows="3"
          placeholder="如有其他说明请填写在此处"
          disabled
        />
      </el-form-item>
      <el-form-item label="审批">
        <el-table :data="approvalComments" style="width: 100%">
          <el-table-column prop="title" label="审批环节" width="100" />
          <el-table-column prop="userName" label="审批人" width="100" />
          <el-table-column prop="time" label="审批时间" width="180" />
          <el-table-column label="审批意见" width="150">
            <template #default="scope">
              <div v-if="scope.row.canApproval === true">
                <el-input
                  v-model="approvalResult.opinion"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入审批意见"
                />
              </div>
              <div v-else>{{ scope.row.opinion }}</div>
            </template>
          </el-table-column>
          <el-table-column label="审批状态" width="100">
            <template #default="scope">
              <div v-if="scope.row.canApproval === true">
                <el-row
                  ><el-button type="success" @click="approval(scope.row.taskId, true)"
                    >同意</el-button
                  ></el-row
                >
                <el-row
                  ><el-button type="danger" @click="approval(scope.row.taskId, false)"
                    >驳回</el-button
                  ></el-row
                >
              </div>
              <div v-else>{{ scope.row.statusText }}</div>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { inject, ref } from "vue";
import request from "@/util/request";
export default {
  name: "BusinessTravelApply",
  setup() {
    const applyInfo = inject("applyInfo");
    const formData = applyInfo.value.formData;
    const approvalComments = applyInfo.value.approvalDTOS;
    console.log("approvalComments", approvalComments);
    console.log(formData);
    const approvalResult = ref({ opinion: "", agree: false });
    const approval = (taskId, agree) => {
      console.log(taskId, agree);
      approvalResult.value.agree = agree;
      request
        .post("/api/apply/approval", {
          taskId,
          agree,
          opinion: approvalResult.value.opinion,
          applyInstanceId: applyInfo.value.id,
        })
        .then((res) => {
          console.log(res);
          if (res.success) {
            window.location.reload();
          }
        });
    };
    return { formData, approvalComments, approvalResult, approval };
  },
};
</script>

<style scoped>
.business-travel-form {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.el-row {
  margin-bottom: 20px;
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
</style>
