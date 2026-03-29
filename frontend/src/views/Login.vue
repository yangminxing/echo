<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="title">Echo 系统登录</h1>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-item">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="loginForm.username"
            type="text"
            placeholder="请输入用户名"
            autocomplete="username"
          />
        </div>

        <div class="form-item">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </div>

        <div class="form-item captcha-item">
          <label for="captcha">验证码</label>
          <div class="captcha-wrapper">
            <input
              id="captcha"
              v-model="loginForm.captcha"
              type="text"
              placeholder="请输入验证码"
              maxlength="4"
            />
            <canvas
              ref="captchaCanvas"
              class="captcha-canvas"
              @click="refreshCaptcha"
            ></canvas>
          </div>
        </div>

        <div v-if="errorMsg" class="error-message">{{ errorMsg }}</div>

        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { generateCaptcha, type CaptchaData } from '../utils/captcha'
import { login } from '../api/login'

const router = useRouter()

const captchaCanvas = ref<HTMLCanvasElement | null>(null)
const loading = ref(false)
const errorMsg = ref('')
const captchaData = ref<CaptchaData | null>(null)

const loginForm = reactive({
  username: '',
  password: '',
  captcha: ''
})

const drawCaptcha = (data: CaptchaData) => {
  const canvas = captchaCanvas.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const img = new Image()
  img.onload = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    ctx.drawImage(img, 0, 0)
  }
  img.src = data.dataUrl
}

const refreshCaptcha = () => {
  captchaData.value = generateCaptcha(120, 40)
  if (captchaData.value) {
    drawCaptcha(captchaData.value)
  }
}

const handleLogin = async () => {
  errorMsg.value = ''

  if (!loginForm.username.trim()) {
    errorMsg.value = '请输入用户名'
    return
  }

  if (!loginForm.password) {
    errorMsg.value = '请输入密码'
    return
  }

  if (!loginForm.captcha.trim()) {
    errorMsg.value = '请输入验证码'
    return
  }

  if (captchaData.value && loginForm.captcha.toLowerCase() !== captchaData.value.text.toLowerCase()) {
    errorMsg.value = '验证码错误'
    refreshCaptcha()
    loginForm.captcha = ''
    return
  }

  loading.value = true

  try {
    const response = await login({
      username: loginForm.username,
      password: loginForm.password
    })

    if (response.code === 200) {
      localStorage.setItem('token', response.token || '')
      localStorage.setItem('username', loginForm.username)
      router.push('/home')
    } else {
      errorMsg.value = response.msg || '登录失败'
      refreshCaptcha()
    }
  } catch (error) {
    errorMsg.value = '网络错误，请稍后重试'
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box {
  background: white;
  border-radius: 12px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: 600;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.form-item input {
  height: 44px;
  padding: 0 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-item input:focus {
  outline: none;
  border-color: #667eea;
}

.captcha-item .captcha-wrapper {
  display: flex;
  gap: 12px;
}

.captcha-item input {
  flex: 1;
}

.captcha-canvas {
  cursor: pointer;
  border-radius: 4px;
  height: 40px;
}

.error-message {
  color: #f56c6c;
  font-size: 13px;
  text-align: center;
  padding: 8px;
  background: #fef0f0;
  border-radius: 4px;
}

.login-btn {
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.3s;
}

.login-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>