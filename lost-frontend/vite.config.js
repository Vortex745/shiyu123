import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],
    server: {
        port: 5173, // 前端端口
        open: true, // 启动后自动打开浏览器
        proxy: {
            // 这里的 '/api' 就是我们在后端 application.yml 里配置的 context-path
            '/api': {
                target: 'http://localhost:8088', // 后端接口地址
                changeOrigin: true, // 允许跨域
                // rewrite: (path) => path.replace(/^\/api/, '') // 注意：我们后端配置了 /api 前缀，所以这里不需要 rewrite 去掉它！
            }
        }
    }
})