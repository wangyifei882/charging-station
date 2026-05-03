<template>
  <div class="login-page">
    <div class="login-wrapper">
      <!-- Left: Brand -->
      <div class="login-brand">
        <div class="brand-content">
          <div class="brand-icon">
            <el-icon :size="36"><Lightning /></el-icon>
          </div>
          <h1 class="brand-title">充电站运营管理平台</h1>
          <p class="brand-desc">智能充电站管理 · 实时监控 · 数据分析</p>
          <div class="brand-features">
            <div class="feature">
              <span class="feature-dot"></span>
              设备状态实时监控
            </div>
            <div class="feature">
              <span class="feature-dot"></span>
              充电订单管理与结算
            </div>
            <div class="feature">
              <span class="feature-dot"></span>
              故障智能告警处理
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Form -->
      <div class="login-form-section">
        <div class="form-card">
          <h2 class="form-title">登录</h2>
          <p class="form-subtitle">请输入您的账号和密码</p>

          <el-form
            :model="loginForm"
            @submit.prevent="handleLogin"
            class="login-form"
            size="large"
          >
            <el-form-item>
              <el-input
                v-model="loginForm.username"
                placeholder="用户名"
                :prefix-icon="User"
                class="login-input"
              />
            </el-form-item>

            <el-form-item>
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                :prefix-icon="Lock"
                class="login-input"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </div>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-btn"
                @click="handleLogin"
              >
                {{ loading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <p class="login-footer">© 2026 充电站运营管理平台</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api'
import { ElMessage } from 'element-plus'
import { User, Lock, Lightning } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!loginForm.username) { ElMessage.warning('请输入用户名'); return }
  if (!loginForm.password) { ElMessage.warning('请输入密码'); return }

  loading.value = true
  try {
    const res = await login(loginForm)
    const token = res.data?.token
    const userInfo = res.data?.userInfo || {}

    if (token) {
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      ElMessage.success('登录成功')
      setTimeout(() => router.push('/dashboard'), 300)
    } else {
      ElMessage.error('登录失败：未获取到Token')
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}

.login-wrapper {
  display: flex;
  width: 800px;
  min-height: 460px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06), 0 8px 32px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

// ---- Left Brand Panel ----
.login-brand {
  width: 360px;
  background: linear-gradient(160deg, #0d6b6e 0%, #159196 60%, #1aafb4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.brand-content { text-align: center; }

.brand-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin: 0 auto 20px;
}

.brand-title {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 8px;
  letter-spacing: 0.02em;
}

.brand-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 28px;
}

.brand-features {
  text-align: left;
  display: inline-flex;
  flex-direction: column;
  gap: 12px;
}

.feature {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.feature-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  flex-shrink: 0;
}

// ---- Right Form Panel ----
.login-form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 48px;
}

.form-card { width: 100%; }

.form-title {
  font-size: 22px;
  font-weight: 600;
  color: #1e2126;
  margin: 0 0 4px;
}

.form-subtitle {
  font-size: 13px;
  color: #9399a5;
  margin: 0 0 28px;
}

.login-form {
  :deep(.el-form-item) { margin-bottom: 18px; }
}

.login-input {
  :deep(.el-input__wrapper) {
    background: #f6f7f9 !important;
    border: 1px solid #e2e5ea !important;
    border-radius: 8px !important;
    padding: 10px 14px !important;
    box-shadow: none !important;
    transition: all 0.2s !important;

    &:hover { border-color: #c8cdd5 !important; }
    &.is-focus {
      border-color: #0d6b6e !important;
      background: #fff !important;
      box-shadow: 0 0 0 3px rgba(13, 107, 110, 0.08) !important;
    }
  }
  :deep(.el-input__inner) {
    color: #1e2126 !important;
    font-size: 14px !important;
    &::placeholder { color: #b9bfc8 !important; }
  }
}

.form-options {
  display: flex;
  align-items: center;
  margin-bottom: 20px;

  :deep(.el-checkbox__label) { color: #5b616e; font-size: 13px; }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px !important;
  background: #0d6b6e !important;
  border: none !important;

  &:hover { background: #159196 !important; }
}

.login-footer {
  font-size: 12px;
  color: #b9bfc8;
  margin-top: 24px;
}
</style>
