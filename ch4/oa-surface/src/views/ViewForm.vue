<template>
  <div class="form-container">
    <el-form> </el-form>
    <!-- 使用component is动态绑定异步组件 -->
    <component :is="AsyncView" v-if="AsyncView && !isLoading" />
    <!-- 添加加载状态提示 -->
    <div v-else class="loading-text">表单加载中...</div>
  </div>
</template>
<script>
import { useRoute } from "vue-router";
import { provide, ref, onMounted, shallowRef, defineAsyncComponent } from "vue";
import request from "@/util/request";
import Formloading from "@/components/form/FormLoading.vue";
import FormloadError from "@/components/form/FormLoadError.vue";
// 申请单查看页面
export default {
  setup() {
    const route = useRoute();
    // 获取路由参数
    const applyInstanceId = route.params.applyInstanceId;
    const applyInfo = ref({});
    const AsyncView = shallowRef({});
    const isLoading = ref(true);
    provide("applyInfo", applyInfo);
    const fetchApplyInfo = async () => {
      const res = await request.get("/api/apply/" + applyInstanceId);
      applyInfo.value = res.data;
    };
    onMounted(async () => {
      await fetchApplyInfo();
      const applyForm = applyInfo.value.applyForm;
      let viewPath = applyForm.viewPath;
      if (viewPath.startsWith("/")) {
        viewPath = viewPath.substring(1);
      }
      AsyncView.value = defineAsyncComponent({
        loader: () => import(`../components/form/${viewPath}`),
        loadingComponent: Formloading,
        errorComponent: FormloadError,
        delay: 200,
        timeout: 3000,
      });
      isLoading.value = false;
    });
    return { AsyncView, isLoading };
  },
};
</script>
