import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
const instance = axios.create({
    baseURL: ''
});
//请求头使用 application/json
instance.interceptors.request.use(
    config => {
        config.headers['Content-Type'] = 'application/json'
        // 如果本地存储有token，则添加到请求头
        let token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = token
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

instance.interceptors.response.use(
    result => {
        let resp = result.data
        if (!resp.success) {
            // 业务出错
            console.log("业务出错", resp.message)
            ElMessage.error(resp.message)
            return Promise.reject(resp.message)
        }
        return result.data
    },
    error => {
        console.log(error);
        // 如果响应是404，记录日志
        if (error.response.status === 404) {
            console.log(error.response.data.message)
            // 用 element-plus 弹窗展示错误信息
            ElMessage.error(error.response.data.message)
        }
        else if(error.response.status === 403){
            ElMessage.error(error.response.data.message)
        }
        // 如果响应是401，则清除本地存储的token
        else if (error.response.status === 401) {
            localStorage.removeItem('token')
            // 跳转到登录页
            router.push('/login')
        }
        else{
            ;
        }
        return Promise.reject(error);
    }
)
export default instance