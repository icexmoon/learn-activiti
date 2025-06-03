<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>员工管理系统登录</h2>
      <el-form :model="form" :rules="rules" ref="loginForm">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-row :gutter="10">
            <el-col :span="14">
              <el-input v-model="form.code" maxlength="6" />
            </el-col>
            <el-col :span="10">
              <el-button :disabled="countdown > 0" @click="sendCode">
                {{ countdown > 0 ? countdown + 's后重发' : '发送验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../util/request'

const form = ref({ phone: '', code: '' })
const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}
const countdown = ref(0)
let timer = null

const sendCode = async () => {
  if (!form.value.phone) {
    ElMessage.error('请先输入手机号')
    return
  }
  // 调用后端发送验证码接口
  // 配置 accept: application/json
  let resp = await request.put('/api/login/sendCode/' + form.value.phone).catch(error => {
    console.log(error)
  })
  console.log("验证码", resp.data)
  ElMessage.success('验证码已发送')
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

const onLogin = async () => {
  // 这里可以加表单校验
  // 调用后端登录接口
  // await request.post('/api/login', form.value)
  await request.post('/api/login', {
    phone: form.value.phone,
    captcha: form.value.code
  }).then(resp => {
    console.log("登录成功", resp.data)
    let token = resp.data
    // 将token保存到本地存储
    localStorage.setItem('token', token)  
    // 登录成功后跳转
    window.location.href = '/'
  }).catch(error => {
    console.log("登录失败", error)
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 85vw;
  background: #f5f7fa;
}

.login-card {
  width: 400px;
  padding: 40px 30px;
}
</style>