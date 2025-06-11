import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router/index.js'
// import { StagewiseToolbar } from '@stagewise/toolbar-vue'
import FontAwesomeIcon from './plugins/fontawesome'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.component('font-awesome-icon', FontAwesomeIcon)

// 仅在开发模式下初始化 stagewise 工具栏
if (import.meta.env.DEV) {
  const stagewiseConfig = {
    plugins: []
  }
  // app.component('StagewiseToolbar', StagewiseToolbar)
  app.provide('stagewiseConfig', stagewiseConfig)
}

app.mount('#app')
