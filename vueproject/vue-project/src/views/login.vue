<template>
  <!-- 强烈黑白对比度波浪背景 -->
  <WavyBackground 
    :colors="['#000000', '#0a0a0a', '#1a1a1a', '#0a0a0a', '#000000']"
    :wave-width="60"
    :background-fill="'rgba(0, 0, 0, 0.98)'"
    :blur="2"
    :speed="'normal'"
    :wave-opacity="0.5"
    class="wavy-bg"
  >
    <!-- 登录表单 -->
    <div class="login-container">
      <div class="login-form">
        <!-- Logo区域 -->
        <div class="logo-section">
          <h1 class="login-title">GAME</h1>
          <p class="login-subtitle">管理系统</p>
        </div>
        
        <!-- 自定义表单 -->
        <form @submit.prevent="handleLogin" class="w-full">
          <div class="form-item">
            <AnimatedInput
              v-model="loginForm.username"
              placeholder="用户名"
              class="w-full"
              container-class="animated-input-container"
            />
          </div>
          
          <div class="form-item">
            <AnimatedInput
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              class="w-full"
              container-class="animated-input-container"
            />
          </div>
          
          <div class="form-item">
            <RainbowButton
              @click="handleLogin"
              class="w-full rainbow-login-button"
              speed="3"
              type="submit"
            >
              登录
            </RainbowButton>
          </div>
          
          <div class="form-footer">
            <el-link type="info" class="footer-link">忘记密码?</el-link>
            <el-link type="info" class="footer-link">立即注册</el-link>
          </div>
        </form>
      </div>
    </div>
  </WavyBackground>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import WavyBackground from '@/components/ui/WavyBackground.vue'
import RainbowButton from '@/components/ui/RainbowButton.vue'
import AnimatedInput from '@/components/ui/AnimatedInput.vue'

const router = useRouter()
const loginForm = ref({
  username: '',
  password: ''
})

// 登录处理函数
const handleLogin = () => {
  // 简单的表单验证
  if (!loginForm.value.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!loginForm.value.password.trim()) {
    ElMessage.warning('请输入密码')
    return
  }
  if (loginForm.value.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  
  // 登录成功，跳转到elements页面
  ElMessage.success('登录成功！')
  router.push('/elements')
}
</script>

<style scoped>
/* 登录页面样式 */
.wavy-bg {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.login-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  max-width: 380px;
  min-width: 320px;
  padding: 0 15px;
  z-index: 10;
}

.logo-section {
  text-align: center;
  margin-bottom: 30px;
}

.login-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 8px;
  line-height: 1.2;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.919);
}

.login-subtitle {
  font-size: 1.1rem;
  color: rgba(129, 125, 125, 0.7);
  font-weight: 400;
}

.login-form {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  padding: 35px 25px;
  box-shadow: 
    0 15px 30px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.05) inset;
}

:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.8) !important;
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 8px;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.12) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 10px !important;
  box-shadow: 
    0 3px 8px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.05) inset !important;
  height: 46px !important;
}

:deep(.el-input__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.3) !important;
  background: rgba(255, 255, 255, 0.15) !important;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: rgba(255, 255, 255, 0.4) !important;
  background: rgba(255, 255, 255, 0.18) !important;
}

:deep(.el-input__inner) {
  color: white !important;
  font-size: 15px;
  font-weight: 400;
}

:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5) !important;
}

.rainbow-login-button {
  margin-top: 10px;
  height: 46px !important;
  font-size: 15px !important;
  font-weight: 500 !important;
  border-radius: 10px !important;
}

.form-item {
  margin-bottom: 20px;
}

.animated-input-container {
  width: 100%;
}

:deep(.animated-input-container input) {
  background: rgba(255, 255, 255, 0.12) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 10px !important;
  box-shadow: 
    0 3px 8px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.05) inset !important;
  height: 46px !important;
  color: white !important;
  font-size: 15px;
  font-weight: 400;
}

:deep(.animated-input-container input::placeholder) {
  color: rgba(255, 255, 255, 0.5) !important;
}

:deep(.animated-input-container input:focus) {
  outline: none;
  border-color: rgba(255, 255, 255, 0.4) !important;
  background: rgba(255, 255, 255, 0.18) !important;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-link {
  color: rgba(255, 255, 255, 0.6) !important;
  text-decoration: none;
  font-size: 13px;
  font-weight: 400;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: rgba(255, 255, 255, 0.8) !important;
}

/* Element组件界面样式 */
.layout-container-demo {
  min-height: 100vh;
}

.layout-container-demo .el-header {
  background-color: #f5f7fa;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #ebeef5;
}

.layout-container-demo .el-aside {
  background-color: #1e2736;
  color: #fff;
  border-right: none;
}

.layout-container-demo .el-menu {
  border-right: none;
}

.layout-container-demo .el-main {
  padding: 20px;
  background-color: #f5f7fa;
}

.layout-container-demo .toolbar {
  display: inline-flex;
  align-items: center;
  height: 100%;
  margin-right: 20px;
}

.content-area {
  background-color: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: center;
}

.content-area h2 {
  color: #333;
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}

.content-area p {
  color: #666;
  font-size: 16px;
  margin-bottom: 30px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-container {
    width: 95%;
    max-width: 340px;
    min-width: unset;
    padding: 0 10px;
  }
  
  .login-form {
    padding: 25px 20px;
    border-radius: 14px;
  }
  
  .logo-section {
    margin-bottom: 25px;
  }
  
  .login-title {
    font-size: 2.2rem;
    margin-bottom: 6px;
  }
  
  .login-subtitle {
    font-size: 1rem;
  }
  
  :deep(.el-input__wrapper) {
    height: 42px !important;
    border-radius: 8px !important;
  }
  
  .rainbow-login-button {
    height: 42px !important;
    font-size: 14px !important;
    border-radius: 8px !important;
  }
  
  .form-footer {
    flex-direction: column;
    gap: 8px;
    margin-top: 20px;
  }
  
  .footer-link {
    display: block;
    text-align: center;
    margin: 0;
    font-size: 12px;
  }
  
  /* 移动端Element界面适配 */
  .layout-container-demo .el-aside {
    width: 100% !important;
  }
  
  .content-area {
    padding: 20px;
  }
  
  .content-area h2 {
    font-size: 20px;
  }
  
  .content-area p {
    font-size: 14px;
  }
}
</style>