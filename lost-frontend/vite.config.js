import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],
    server: {
        port: 5173,
        open: true,
        proxy: {
            '/api': {
                target: 'http://localhost:9090', // 👈 4. 修改这里：必须和后端 application.yml 的端口保持一致！
                changeOrigin: true,
                // rewrite: (path) => path.replace(/^\/api/, '') // 👈 保持注释状态，不要打开！因为你的后端 Controller 里写了 /api/auth
            }
        }
    }
})