<template>
  <div class="dynamic-apply-form">
    <h1>动态申请单</h1>

    <!-- 加载状态提示 -->
    <div v-if="loading" class="loading">加载中...</div>
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 动态渲染申请单组件 -->
    <component
      :is="dynamicComponent"
      v-if="dynamicComponent"
      :ref="setDynamicComponentRef"
      v-bind="dynamicProps"
      @update="handleUpdate"
    />
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onBeforeUnmount, defineProps } from "vue";
import request from "@/util/request";
import { useRoute } from "vue-router";
import { compileToFunctions } from "vue-template-compiler";

export default defineComponent({
  name: "DynamicApplyForm",
  props: {
    // 支持传递动态props给子组件
    dynamicProps: {
      type: Object,
      default: () => ({}),
    },
  },
  setup(props) {
    // console.log(props.dynamicProps.applyProcessId);
    const applyProcessId = ref(props.dynamicProps.applyProcessId);
    // console.log(props);
    const route = useRoute();
    // const applyProcessId = ref("");
    const dynamicComponent = ref(null);
    const dynamicComponentRef = ref(null);
    const loading = ref(false);
    const error = ref(null);

    // 处理动态组件事件
    const handleUpdate = (value) => {
      // 向父组件传递更新事件
      // 可根据需要扩展更多事件类型
    };

    // 设置动态组件引用
    const setDynamicComponentRef = (el) => {
      dynamicComponentRef.value = el;
    };

    const fetchApplyFormContent = async () => {
      loading.value = true;
      error.value = null;

      try {
        const response = await request.get(
          `/api/apply_form/add_content/apply_process/${applyProcessId.value}`
        );

        if (!response.success) {
          throw new Error(response.message || "获取申请单内容失败");
        }

        // 提取模板内容
        const templateRegex = new RegExp("<template>[\s\S]*?<\\/template>", "i");
        const templateMatch = response.data.match(templateRegex);
        const templateContent = templateMatch
          ? templateMatch[0].replace(/^<template>|<\\\/template>$/gi, "").trim()
          : "";

        // 提取脚本内容
        const scriptRegex = new RegExp("<script>[\s\S]*?<\\/script>", "i");
        const scriptMatch = response.data.match(scriptRegex);
        const scriptContent = scriptMatch
          ? scriptMatch[0].replace(/^<script>|<\\\/script>$/gi, "").trim()
          : "";

        // 提取样式内容
        const styleRegex = new RegExp("<style[^>]*>[\s\S]*?<\\/style>", "gi");
        const styleMatch = response.data.match(styleRegex);
        const styleContent = styleMatch
          ? styleMatch[0].replace(/^<style[^>]*>|<\\\/style>$/gi, "")
          : "";

        // 编译模板
        const { render, staticRenderFns } = compileToFunctions(
          `<div class="dynamic-component-container">${templateContent}</div>`
        );

        // 创建组件定义
        let componentDef = {};
        try {
          // 使用Function构造器替代eval提高安全性
          componentDef = new Function(`return ${scriptContent}`)();
        } catch (scriptError) {
          console.error("脚本解析错误:", scriptError);
          throw new Error("组件脚本解析失败");
        }

        // 创建样式元素
        if (styleContent) {
          const styleElement = document.createElement("style");
          styleElement.textContent = `.dynamic-component-container { ${styleContent} }`;
          document.head.appendChild(styleElement);

          // 组件卸载时移除样式
          onBeforeUnmount(() => {
            document.head.removeChild(styleElement);
          });
        }

        // 创建组件
        dynamicComponent.value = defineComponent({
          ...componentDef,
          render,
          staticRenderFns,
        });
      } catch (err) {
        console.error("加载申请单失败:", err);
        error.value = err.message;
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      // 获取路由参数
      // applyProcessId.value = route.params.id;

      if (applyProcessId.value) {
        fetchApplyFormContent();
      }
    });

    // 组件卸载时清理资源
    onBeforeUnmount(() => {
      dynamicComponent.value = null;
      dynamicComponentRef.value = null;
    });

    return {
      dynamicComponent,
      loading,
      error,
      handleUpdate,
      setDynamicComponentRef,
    };
  },
});
</script>

<style scoped>
.dynamic-apply-form {
  padding: 20px;
}

.loading {
  padding: 20px;
  text-align: center;
  color: #409eff;
}

.error {
  padding: 20px;
  color: #f56c6c;
}
</style>
