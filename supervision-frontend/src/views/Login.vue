<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">
          <el-icon :size="32" color="#fff"><Monitor /></el-icon>
        </div>
        <h1>监管与调度平台</h1>
        <p class="subtitle">Supervision & Dispatch Platform</p>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item prop="regionCode">
          <el-select
            v-model="loginForm.regionCode"
            placeholder="选择监管区域"
            size="large"
            style="width: 100%"
          >
            <el-option label="深圳市" value="440300" />
            <el-option label="南山区" value="440305" />
            <el-option label="福田区" value="440304" />
            <el-option label="罗湖区" value="440303" />
            <el-option label="宝安区" value="440306" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  regionCode: '440300'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  regionCode: [{ required: true, message: '请选择监管区域', trigger: 'change' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await axios.post('/api/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })
    if (res.data.code === 200) {
      const { token, user } = res.data.data
      userStore.setToken(token)
      userStore.setUserInfo({
        ...user,
        regionCode: loginForm.regionCode
      })
      userStore.setCurrentRegion(loginForm.regionCode)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error(res.data.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100vw;
  height: 100vh;
  background: #0f172a;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 420px;
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: 48px 40px 40px;
  box-shadow: var(--shadow-lg);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;

  .logo-icon {
    width: 56px;
    height: 56px;
    margin: 0 auto 16px;
    background: var(--color-primary);
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  h1 {
    font-size: 24px;
    font-weight: 700;
    color: var(--color-text-primary);
    margin: 0 0 6px;
  }
  .subtitle {
    font-size: 13px;
    color: var(--color-text-secondary);
  }
}

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 22px;
  }
  :deep(.el-button--primary) {
    height: 44px;
    font-size: 15px;
    font-weight: 600;
  }
}
</style>
