<template>
  <div class="apply-list-container">
    <h2>提交申请</h2>
    <div class="apply-grid">
      <a 
        v-for="apply in applies" 
        :key="apply.id"
        :href="`/apply/form/${apply.id}`" 
        class="apply-card"
        :class="{ disabled: !apply.enable }"
        @click.prevent="handleApplyClick(apply)"
      >
        <div class="apply-name">{{ apply.name }}</div>
      </a>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/util/request';

export default {
  name: 'ApplyList',
  setup() {
    const router = useRouter();
    const applies = ref([]);

    // 获取可提交的申请列表
    const fetchApplyList = async () => {
      try {
        const response = await request.get('/api/apply/list');
        
        if (response.success) {
          applies.value = response.data;
        } else {
          console.error('获取申请列表失败:', response.message);
        }
      } catch (error) {
        console.error('请求异常:', error);
      }
    };

    // 处理申请点击
    const handleApplyClick = (apply) => {
      if (!apply.enable) return;
      
      // 跳转到对应的申请表单页面
      router.push({
        path: `/applyForm/${apply.id}`
        // path: '/businessTravelApply?applyProcessId=${apply.id}'
      });
    };

    onMounted(() => {
      fetchApplyList();
    });

    return {
      applies,
      handleApplyClick
    };
  }
};
</script>

<style scoped>
.apply-list-container {
  padding: 20px;
}

.apply-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.apply-card {
  flex: 1 1 calc(33.333% - 7px);
  min-width: 120px;
  max-width: 160px;
  padding: 12px 8px;
  border-radius: 4px;
  background-color: #f0f0f0;
  color: #333;
  text-decoration: none;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  text-align: center;
}

.apply-card:hover {
  background-color: #e0e0e0;
}

.apply-card.disabled {
  background-color: #fafafa;
  color: #999;
  cursor: not-allowed;
}

.apply-name {
  font-size: 14px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .apply-card {
    flex: 1 1 100%;
    max-width: 100%;
    margin-bottom: 10px;
  }
}
</style>