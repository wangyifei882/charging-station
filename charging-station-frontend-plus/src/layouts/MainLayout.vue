<template>
  <div class="layout">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <div class="logo-icon">
            <el-icon :size="20"><Lightning /></el-icon>
          </div>
          <transition name="fade-width">
            <span class="logo-text" v-show="!sidebarCollapsed">充电站运营管理平台</span>
          </transition>
        </div>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
          :title="sidebarCollapsed ? item.name : ''"
        >
          <el-icon :size="18"><component :is="item.icon" /></el-icon>
          <transition name="fade-width">
            <span v-show="!sidebarCollapsed">{{ item.name }}</span>
          </transition>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <button class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed" :title="sidebarCollapsed ? '展开' : '收起'">
          <el-icon :size="16">
            <component :is="sidebarCollapsed ? 'DArrowRight' : 'DArrowLeft'" />
          </el-icon>
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="main-wrapper" :class="{ expanded: sidebarCollapsed }">
      <!-- Top Header -->
      <header class="top-header">
        <div class="header-left">
          <h1 class="page-title">{{ pageTitle }}</h1>
        </div>

        <div class="header-right">
          <div class="header-stats">
            <span class="stat-item">
              <span class="stat-dot online"></span>
              <span class="stat-label">在线</span>
              <span class="stat-value">45</span>
            </span>
            <span class="stat-item">
              <span class="stat-dot fault"></span>
              <span class="stat-label">故障</span>
              <span class="stat-value">2</span>
            </span>
          </div>

          <div class="header-divider"></div>

          <el-badge :value="3" class="header-bell">
            <el-icon :size="18"><Bell /></el-icon>
          </el-badge>

          <el-dropdown trigger="click" @command="handleUserCommand">
            <div class="user-profile">
              <el-avatar :size="32" class="user-avatar">
                <el-icon :size="16"><User /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userInfo.realName || '管理员' }}</span>
              <el-icon :size="14" class="dropdown-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon :size="14"><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- Page Content -->
      <main class="page-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMenus } from '@/api'

const route = useRoute()
const router = useRouter()
const sidebarCollapsed = ref(false)
const userInfo = ref({})

const menuItems = ref([
  { name: '场站总览', path: '/dashboard', icon: 'Odometer' },
  { name: '设备管理', path: '/device', icon: 'Cpu' },
  { name: '充电运营', path: '/charging', icon: 'Lightning' },
  { name: '收益核算', path: '/finance', icon: 'DataLine' },
  { name: '运营分析', path: '/analytics', icon: 'TrendCharts' },
  { name: '故障申报', path: '/fault', icon: 'Warning' },
  { name: '系统设置', path: '/system', icon: 'Setting' }
])

const pageTitle = computed(() => {
  const active = menuItems.value.find(item => route.path.startsWith(item.path))
  return active ? active.name : '场站总览'
})

const isActive = (path) => route.path.startsWith(path)

const handleUserCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}

onMounted(async () => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    userInfo.value = JSON.parse(stored)
  }
  try {
    await getMenus()
  } catch (e) { /* use default menu */ }
})
</script>

<style lang="scss" scoped>
.layout {
  display: flex;
  min-height: 100vh;
  background: var(--color-bg);
}

// ---- Sidebar ----
.sidebar {
  width: var(--sidebar-width);
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  background: var(--color-surface);
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  z-index: 100;
  transition: width 0.2s ease;

  &.collapsed {
    width: 64px;
  }
}

.sidebar-header {
  padding: var(--space-4) var(--space-4);
  border-bottom: 1px solid var(--color-border-light);
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  height: 36px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.logo-text {
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--color-text);
  white-space: nowrap;
}

.sidebar-nav {
  flex: 1;
  padding: var(--space-2) var(--space-2);
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: 10px var(--space-3);
  margin-bottom: 2px;
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: var(--font-size-base);
  font-weight: 500;
  transition: all var(--transition-fast);
  height: 40px;

  .el-icon { flex-shrink: 0; }
  span { white-space: nowrap; }

  &:hover {
    background: var(--color-primary-bg);
    color: var(--color-primary);
  }

  &.active {
    background: var(--color-primary-bg);
    color: var(--color-primary);
    font-weight: 600;
  }
}

.sidebar-footer {
  padding: var(--space-2);
  border-top: 1px solid var(--color-border-light);
}

.collapse-btn {
  width: 100%;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);

  &:hover {
    background: var(--color-bg-secondary);
    color: var(--color-text);
  }
}

// ---- Main ----
.main-wrapper {
  flex: 1;
  margin-left: var(--sidebar-width);
  transition: margin-left 0.2s ease;

  &.expanded {
    margin-left: 64px;
  }
}

// ---- Top Header ----
.top-header {
  position: sticky;
  top: 0;
  z-index: 50;
  height: var(--header-height);
  padding: 0 var(--space-6);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
}

.header-left { display: flex; align-items: center; }

.page-title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-text);
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.header-stats {
  display: flex;
  gap: var(--space-4);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--font-size-sm);
}

.stat-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;

  &.online { background: var(--color-success); }
  &.fault { background: var(--color-danger); }
}

.stat-label { color: var(--color-text-muted); }
.stat-value {
  font-weight: 600;
  color: var(--color-text);
  font-variant-numeric: tabular-nums;
}

.header-divider {
  width: 1px;
  height: 20px;
  background: var(--color-border);
}

.header-bell {
  cursor: pointer;
  color: var(--color-text-muted);
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);

  &:hover { color: var(--color-text); background: var(--color-bg-secondary); }
}

.user-profile {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--transition-fast);

  &:hover { background: var(--color-bg-secondary); }
  .dropdown-arrow { color: var(--color-text-muted); }
}

.user-avatar {
  background: var(--color-primary-bg);
  color: var(--color-primary);
  border: none;
}

.user-name {
  font-size: var(--font-size-base);
  font-weight: 500;
  color: var(--color-text);
}

// ---- Page Content ----
.page-content {
  padding: var(--space-6);
  min-height: calc(100vh - var(--header-height));
}

// ---- Transitions ----
.fade-width-enter-active,
.fade-width-leave-active {
  transition: opacity 0.15s ease, max-width 0.15s ease;
}
.fade-width-enter-from,
.fade-width-leave-to {
  opacity: 0;
  max-width: 0;
}
</style>
