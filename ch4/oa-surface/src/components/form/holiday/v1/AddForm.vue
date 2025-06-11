<template>
  <el-form :model="form" label-width="120px" ref="formRef">
    <el-form-item label="申请人">
      <el-input v-model="form.applicant" disabled />
    </el-form-item>
    <el-form-item label="开始时间">
      <el-date-picker v-model="form.startDate" type="datetime" placeholder="选择开始时间" />
    </el-form-item>
    <el-form-item label="结束时间">
      <el-date-picker v-model="form.endDate" type="datetime" placeholder="选择结束时间" />
    </el-form-item>
    <el-form-item label="请假类型">
      <el-select v-model="form.type" placeholder="请选择类型">
        <el-option label="事假" value="personal" />
        <el-option label="病假" value="sick" />
        <el-option label="年假" value="annual" />
      </el-select>
    </el-form-item>
    <el-form-item label="理由">
      <el-input v-model="form.reason" type="textarea" :rows="3" />
    </el-form-item>
    <el-form-item>
      <el-button @click="submitForm">提交申请</el-button>
      <el-button @click="resetForm">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: 'AddForm',
  data() {
    return {
      form: {
        applicant: '张三', // 假设默认申请人
        startDate: '',
        endDate: '',
        type: '',
        reason: ''
      }
    };
  },
  methods: {
    submitForm() {
      // 提交表单逻辑，可结合Activiti流程引擎处理
      console.log('提交的表单数据:', this.form);
      this.$message.success('申请已提交');
    },
    resetForm() {
      this.form.startDate = '';
      this.form.endDate = '';
      this.form.type = '';
      this.form.reason = '';
    }
  }
};
</script>
```
