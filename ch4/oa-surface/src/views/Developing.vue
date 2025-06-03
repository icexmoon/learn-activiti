<template>
  <div class="developing-page">
    <el-result
      icon="warning"
      title="功能开发中"
      sub-title="该功能正在开发中，敬请期待..."
    >
      <template #extra>
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
</script>

<style scoped>
.developing-page {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style> 