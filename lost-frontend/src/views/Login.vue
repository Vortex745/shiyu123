<template>
  <div class="login-container">
    <div class="background-blobs">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
    </div>

    <div class="login-box animate__animated animate__fadeInUp">
      <div class="illustration">
        <div class="flower-wrapper">
          <span class="flower">🌼</span>
        </div>
        <div class="title-group">
          <div class="main-title">失物</div>
          <div class="sub-title">校园失物招领平台</div>
        </div>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" size="large" class="login-form">

        <el-form-item prop="username">
          <el-input
              v-model="form.username"
              placeholder="请输入学号 / 工号"
              :prefix-icon="User"
              class="daisy-input"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              class="daisy-input"
          />
        </el-form-item>

        <div v-if="!isLogin" class="animate__animated animate__fadeIn">
          <el-form-item prop="nickname">
            <el-input
                v-model="form.nickname"
                placeholder="给自己起个好听的昵称"
                :prefix-icon="UserFilled"
                class="daisy-input"
            />
          </el-form-item>
        </div>

        <el-form-item>
          <el-button type="primary" class="submit-btn" @click="handleSubmit" :loading="loading" round>
            {{ isLogin ? '进入平台' : '立即注册' }}
          </el-button>
        </el-form-item>

        <div class="toggle-box">
          <span class="tip-text">{{ isLogin ? '还没有账号？' : '已有账号？' }}</span>
          <span class="toggle-btn" @click="toggleMode">
            {{ isLogin ? '去注册' : '去登录' }}
          </span>
        </div>
      </el-form>
    </div>

    <div class="footer-copyright">
      © 2025 让失物回归温暖
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, UserFilled } from '@element-plus/icons-vue'
import { login, register } from '../api/auth'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const isLogin = ref(true)

const form = reactive({ username: '', password: '', nickname: '' })

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const toggleMode = () => {
  isLogin.value = !isLogin.value
  formRef.value.resetFields()
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (isLogin.value) {
        const token = await login(form)
        localStorage.setItem('token', token)
        ElMessage.success('欢迎回来！')
        router.push('/home')
      } else {
        await register(form)
        ElMessage.success('注册成功，快去登录吧')
        isLogin.value = true
      }
    } catch (error) { console.error(error) }
    finally { loading.value = false }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fffdf5; /* 奶油底色 */
  position: relative;
  overflow: hidden;
}

/* === 1. 背景动态光球 === */
.background-blobs .blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 10s infinite alternate cubic-bezier(0.45, 0.05, 0.55, 0.95);
  z-index: 0;
}
.blob-1 {
  width: 400px; height: 400px;
  background: #ffeaa7; /* 暖黄 */
  top: -10%; left: -10%;
}
.blob-2 {
  width: 350px; height: 350px;
  background: #fab1a0; /* 暖粉 */
  bottom: -5%; right: -5%;
  animation-delay: -5s;
}

@keyframes float {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(30px, 50px) scale(1.1); }
}

/* === 2. 玻璃拟态卡片 === */
.login-box {
  width: 440px;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.75); /* 半透明白 */
  backdrop-filter: blur(20px); /* 磨砂玻璃效果 */
  -webkit-backdrop-filter: blur(20px);
  border-radius: 32px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 20px 50px rgba(93, 64, 55, 0.08); /* 柔和阴影 */
  z-index: 1;
  text-align: center;
  transition: all 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 60px rgba(93, 64, 55, 0.12);
}

/* === 3. 插画与文字 === */
.flower-wrapper {
  margin-bottom: 10px;
  display: inline-block;
  animation: breathe 3s ease-in-out infinite;
}
.flower {
  font-size: 64px;
  display: block;
  /* 摇摆动画 */
  animation: swing 3s ease-in-out infinite;
  transform-origin: bottom center;
}

@keyframes breathe {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}
@keyframes swing {
  0%, 100% { transform: rotate(-5deg); }
  50% { transform: rotate(5deg); }
}

.title-group { margin-bottom: 40px; }
.main-title {
  font-size: 36px;
  font-weight: 800;
  color: #5d4037; /* 暖咖色 */
  letter-spacing: 2px;
  margin-bottom: 5px;
}
.sub-title {
  font-size: 14px;
  color: #a89f91;
  letter-spacing: 1px;
}

/* === 4. 输入框美化 (Daisy Style) === */
/* 穿透修改 Element Plus 样式 */
:deep(.daisy-input .el-input__wrapper) {
  background-color: #f7f5f0;
  box-shadow: none !important;
  padding: 12px 20px;
  border-radius: 50px; /* 胶囊圆角 */
  transition: all 0.3s;
}
:deep(.daisy-input .el-input__wrapper.is-focus) {
  background-color: white;
  box-shadow: 0 0 0 3px rgba(255, 193, 7, 0.25) !important; /* 黄色光晕 */
}
:deep(.daisy-input .el-input__inner) {
  height: 24px;
  color: #5d4037;
  font-weight: 600;
}

/* === 5. 按钮美化 === */
.submit-btn {
  width: 100%;
  height: 50px;
  font-size: 18px;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #ffc107, #ff9f43);
  border: none;
  margin-top: 10px;
  box-shadow: 0 10px 20px rgba(255, 159, 67, 0.25);
  transition: all 0.3s;
}
.submit-btn:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 15px 30px rgba(255, 159, 67, 0.35);
  background: linear-gradient(135deg, #ffca28, #ffb74d);
}
.submit-btn:active {
  transform: scale(0.98);
}

/* === 6. 底部切换 === */
.toggle-box {
  margin-top: 20px;
  font-size: 14px;
  color: #a89f91;
}
.toggle-btn {
  color: #ff9f43;
  font-weight: 800;
  cursor: pointer;
  margin-left: 5px;
  position: relative;
  transition: all 0.3s;
}
.toggle-btn:hover {
  color: #ffc107;
}
.toggle-btn::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 2px;
  bottom: -2px;
  left: 0;
  background-color: #ffc107;
  transform: scaleX(0);
  transition: transform 0.3s;
}
.toggle-btn:hover::after {
  transform: scaleX(1);
}

.footer-copyright {
  position: absolute;
  bottom: 20px;
  font-size: 12px;
  color: #ccc;
  font-family: monospace;
}
</style>