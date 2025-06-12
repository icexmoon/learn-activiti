<template>
  <div class="form-container">
    <el-form>
    </el-form>
    <!-- 使用component is动态绑定异步组件 -->
    <component :is="AsyncComp" v-if="AsyncComp && !isLoading" />
    <!-- 添加加载状态提示 -->
    <div v-else class="loading-text">表单加载中...</div>
  </div>
</template>

<script>
import { ref, onMounted,defineAsyncComponent,provide,shallowRef } from "vue";
import { useRoute, useRouter } from "vue-router";
import request from "@/util/request";
import FormloadError from "@/components/form/FormLoadError.vue";
import Formloading from "@/components/form/FormLoading.vue";
import { ElMessage } from "element-plus";
export default {
  setup() {
    const route = useRoute();
    const router = useRouter();
    const applyProcessId = route.params.applyProcessId;
    const AsyncComp = shallowRef(null);
    const isLoading = ref(true);
    const userInfo = ref({});
    const fetchUserInfo = async () => {
      const res = await request.get("/api/user/info");
      if (res.success) {
        userInfo.value = res.data;
      }
    };
    provide("userInfo", userInfo);
    provide("applyProcessId", applyProcessId);


    // 获取表单信息
    onMounted(async () => {
      try {
        const res = await request.get(`/api/apply_process/${applyProcessId}`);
        const applyForm = res.data.applyForm;
        if (!applyForm) throw new Error("表单数据为空");

        let formPath = applyForm.path || "";
        if (formPath.startsWith("/")) {
          formPath = formPath.substring(1);
        }
        fetchUserInfo();
        const componentPath = `../components/form/${formPath}`;
        console.log("componentPath:", componentPath);

        // 定义异步组件
        AsyncComp.value = defineAsyncComponent({
          loader: () => import(componentPath),
          loadingComponent: Formloading,
          errorComponent: FormloadError,
          delay: 200,
          timeout: 3000,
        });
      } catch (error) {
        ElMessage.error("表单加载失败: " + error.message);
        router.push("/apply/list");
      } finally {
        isLoading.value = false;
      }
    });

    return { AsyncComp, isLoading };
  },
};
</script>
