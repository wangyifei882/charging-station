<template>
  <div class="main-layout">
    <el-header class="header">
      <div class="header-left">
        <el-icon :size="24" color="var(--color-primary)"><Monitor /></el-icon>
        <h2>监管与调度平台</h2>
      </div>
      <div class="header-right">
        <el-badge :value="alarmCount" :hidden="alarmCount === 0" class="alarm-badge">
          <el-icon :size="20" @click="$router.push('/alarm-monitor')" style="cursor: pointer"><Bell /></el-icon>
        </el-badge>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-icon><User /></el-icon>
            <span>{{ userStore.userInfo.realName || '管理员' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container class="main-container">
      <el-aside width="220px" class="sidebar">
        <el-menu
          :default-active="$route.path"
          router
          background-color="transparent"
          text-color="var(--sidebar-text)"
          active-text-color="var(--color-primary-light)"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>数据总览</span>
          </el-menu-item>
          <el-sub-menu index="facility">
            <template #title>
              <el-icon><Grid /></el-icon>
              <span>设施监管</span>
            </template>
            <el-menu-item index="/facility">设施台账</el-menu-item>
            <el-menu-item index="/access-audit">接入审核</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="v2g">
            <template #title>
              <el-icon><Connection /></el-icon>
              <span>车网互动与调度</span>
            </template>
            <el-menu-item index="/v2g-activity">V2G活动</el-menu-item>
            <el-menu-item index="/demand-response">需求响应</el-menu-item>
            <el-menu-item index="/smart-dispatch">智能调度</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="compliance">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>运营与合规管理</span>
            </template>
            <el-menu-item index="/operator-manage">运营商管理</el-menu-item>
            <el-menu-item index="/subsidy-manage">补贴管理</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="emergency">
            <template #title>
              <el-icon><Warning /></el-icon>
              <span>安全与应急管理</span>
            </template>
            <el-menu-item index="/alarm-monitor">告警监控</el-menu-item>
            <el-menu-item index="/emergency-command">应急指令</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getUnreadAlarmCount } from '../api/dashboard'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const alarmCount = ref(0)

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

const fetchAlarmCount = async () => {
  try {
    const res = await getUnreadAlarmCount()
    alarmCount.value = res.data.totalCount || 0
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchAlarmCount()
  setInterval(fetchAlarmCount, 30000)
})
</script>

<style scoped lang="scss">
.main-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  height: var(--header-height);
  background: var(--color-bg-card);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 0 var(--color-border-light);
  z-index: 100;

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
    h2 {
      font-size: 18px;
      font-weight: 700;
      color: var(--color-text-primary);
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 24px;

    .alarm-badge {
      cursor: pointer;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 6px;
      cursor: pointer;
      color: var(--color-text-regular);
      padding: 6px 12px;
      border-radius: var(--radius-sm);
      transition: background-color var(--transition-fast), color var(--transition-fast);
      font-weight: 500;

      &:hover {
        background: var(--color-bg-page);
        color: var(--color-primary);
      }
    }
  }
}

.main-container {
  flex: 1;
  overflow: hidden;
}

.sidebar {
  background: var(--sidebar-bg);
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar :deep(.el-menu-item),
.sidebar :deep(.el-sub-menu__title) {
  margin: 2px 8px;
  border-radius: var(--radius-sm);
  transition: background-color var(--transition-fast), color var(--transition-fast);
}

.sidebar :deep(.el-menu-item:hover),
.sidebar :deep(.el-sub-menu__title:hover) {
  background: var(--sidebar-bg-hover);
}

.sidebar :deep(.el-menu-item.is-active) {
  background: var(--sidebar-item-active-bg);
}

.sidebar :deep(.el-sub-menu .el-menu) {
  background: rgba(0, 0, 0, 0.15);
}

.main-content {
  background: var(--color-bg-page);
  overflow-y: auto;
  padding: 24px;
}
</style>
