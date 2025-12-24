import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/home' // 默认跳到首页
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('../views/Home.vue')
    },
    {
        path: '/user',
        name: 'UserCenter',
        component: () => import('../views/UserCenter.vue')
    },
    // ... 在 routes 数组中添加
    {
        path: '/post/:id', // :id 是动态参数
        name: 'PostDetail',
        component: () => import('../views/PostDetail.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 简单的路由守卫：没登录不准看首页 (除了 /login 其他都要检查)
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    // 如果去的地方不是 login，且没有 token，就强制踢回 login
    if (to.path !== '/login' && !token) {
        next('/login')
    } else {
        next()
    }
})

export default router