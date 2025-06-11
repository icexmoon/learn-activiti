<script>
// DynamicFormLoader.vue
import { defineAsyncComponent, ref } from "vue";
import { compileScript as compile } from "vue/compiler-sfc";
import request from "@/util/request";
import { inject } from "vue";

export default {
  async setup() {
    const form = inject('form')
    const dynamicComponent = ref(null);

    // 从接口获取原始代码
    const resp = await request.get(`/api/apply_form/${form.key}`);
    const rawCode = resp.data.addContent;

    // 安全校验（扩展）
    // if (!/^<template>[\s\S]*<script>/.test(rawCode)) {
    //   throw new Error('无效的Vue组件格式');
    // }

    const { descriptor } = compile(rawCode, {
      filename: `${form.name}.vue`,
      id: `data-v-${Math.random().toString(16).slice(2)}`
    });
    // 编译组件

    // 构造异步组件
    dynamicComponent.value = defineAsyncComponent(() => ({
      components: {
        template: descriptor.template?.content,
        ...eval(`(${descriptor.script?.content.replace("export default", "return")})`),
      },
      timeout: 5000, // 超时时间
    }));
    console.log("here")

    return () => h(dynamicComponent.value);
  },
};
</script>
