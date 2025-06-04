<template>
  <el-container style="height: 100vh; width: 87vw; background: #f6f8fa;">
    <!-- 顶部 -->
    <el-header class="header">
      <div class="logo">员工管理系统</div>
      <div class="user-info">
        <font-awesome-icon :icon="['fas', 'user']" />
        <span class="username">张三</span>
        <el-link type="danger" @click="logout">退出</el-link>
      </div>
    </el-header>
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="220px" class="aside">
        <el-menu :default-openeds="defaultOpeneds" router>
          <template v-for="menu in menuTree" :key="menu.id">
            <!-- 没有子菜单的情况 -->
            <el-menu-item v-if="!menu.children || menu.children.length === 0" :index="menu.path || '/developing'">
              <font-awesome-icon :icon="getMenuIcon(menu)" />
              <span>{{ menu.name }}</span>
            </el-menu-item>
            <!-- 有子菜单的情况 -->
            <el-sub-menu v-else :index="menu.id.toString()">
              <template #title>
                <font-awesome-icon :icon="getMenuIcon(menu)" />
                <span>{{ menu.name }}</span>
              </template>
              <el-menu-item 
                v-for="subMenu in menu.children" 
                :key="subMenu.id"
                :index="subMenu.path || '/developing'"
              >
                <font-awesome-icon :icon="getMenuIcon(subMenu)" />
                <span>{{ subMenu.name }}</span>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-aside>
      <!-- 内容区 -->
      <el-main class="main">
        <div class="main-inner">
          <router-view />
        </div>
      </el-main>
    </el-container>
    <!-- 底部 -->
    <el-footer class="footer">
      ©2024 企业员工管理系统 - 备案号：粤ICP备xxxx号
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/util/request'

const router = useRouter()
const menuTree = ref([])
const defaultOpeneds = ref([])

const menuIconMap = {
  '1': 'home',
  '2': 'users',
  '3': 'user',
  '4': 'cog',
  '5': 'lock',
  '6': 'bars',
  '7': 'calendar',
  '8': 'check',
  '9': 'file-alt',
  '10': 'plug'
}

// 获取菜单图标
const getMenuIcon = (menu) => {
  // 如果菜单有自定义图标，则使用自定义图标
  if (menu.icon) {
    return ['fas', menu.icon.toLowerCase()]
  }
  // 否则使用默认图标
  return ['fas', 'bars']
}

// 获取菜单数据
const fetchMenuTree = async () => {
  try {
    const response = await request.get('/api/menu/tree?checkPermission=true')
    if (response.success) {
      menuTree.value = response.data.children || []
      // 设置默认展开的菜单
      defaultOpeneds.value = menuTree.value.map(menu => menu.id.toString())
    }
  } catch (error) {
    console.error('获取菜单数据失败', error)
  }
}

const logout = () => {
  // 调用后端退出登录接口
  request.post('/api/logout').then(resp => {
    console.log("退出成功", resp.data)
    // 清除本地存储的token
    localStorage.removeItem('token')
    // 清除登录状态
    router.push('/login')
  }).catch(error => {
    console.log("退出失败", error)
  })
}

// 组件挂载时获取菜单数据
onMounted(() => {
  fetchMenuTree()
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #eaf1fb;
  color: #333;
  height: 60px;
  border-bottom: 1px solid #e0e6ed;
}

.logo {
  font-size: 22px;
  font-weight: bold;
  color: #2d8cf0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.aside {
  background: #f6f8fa;
  color: #333;
  border-right: 1px solid #e0e6ed;
}

.main {
  background: #f6f8fa;
  padding: 24px 0 24px 24px;
  min-height: 600px;
  width: 100%;
  box-sizing: border-box;
  flex: 1;
  display: flex;
  justify-content: flex-start;
}

.main-inner {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  padding: 32px;
  width: 100%;
  min-height: 500px;
  margin-right: 0px;
  /* 右侧预留边框宽度 */
  box-sizing: border-box;
}

.footer {
  text-align: center;
  color: #888;
  background: #f6f8fa;
  padding: 12px 0;
  font-size: 14px;
  border-top: 1px solid #e0e6ed;
}
</style>