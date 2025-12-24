import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router' // 引入路由，用于跳转

// 1. 创建 axios 实例
const request = axios.create({
    baseURL: '/api', // 注意：这里匹配 vite.config.js 中的代理
    timeout: 5000    // 请求超时时间
})

// 2. 请求拦截器 (自动加 Token)
request.interceptors.request.use(config => {
    // 从浏览器缓存中取出 Token
    const token = localStorage.getItem('token')
    if (token) {
        // 如果有 Token，放入请求头 Authorization 中
        config.headers['Authorization'] = token
    }
    return config
}, error => {
    return Promise.reject(error)
})

// 3. 响应拦截器 (统一处理结果)
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 兼容处理：如果返回的是文件流(blob)，直接返回
        if (response.config.responseType === 'blob') {
            return res
        }
        // 如果是字符串(有可能后端返回没转JSON)，尝试转一下
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }

        // 判断业务状态码 (我们在后端定义 200 是成功)
        if (res.code === 200) {
            return res.data; // 直接把 data 里的东西返回去，少写一层 .data
        } else {
            // 报错了，弹窗提示错误信息
            ElMessage.error(res.message || '系统异常')
            // 特殊处理：401 代表未登录或 Token 过期
            if (res.code === 401) {
                localStorage.removeItem('token')
                router.push('/login')
            }
            return Promise.reject(res.message)
        }
    },
    error => {
        // 处理 HTTP 状态码错误 (比如 404, 500)
        console.error('err' + error)
        let { message } = error;
        if (message == "Network Error") {
            message = "后端接口连接异常";
        } else if (message.includes("timeout")) {
            message = "系统接口请求超时";
        } else if (message.includes("Request failed with status code 401")) {
            message = "系统接口401异常";
            localStorage.removeItem('token')
            router.push('/login')
        } else {
            message = "系统接口异常";
        }
        ElMessage.error(message)
        return Promise.reject(error)
    }
)

export default request