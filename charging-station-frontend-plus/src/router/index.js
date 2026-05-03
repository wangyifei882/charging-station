import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
      { path: 'device', name: 'Device', component: () => import('@/views/device/DeviceList.vue') },
      { path: 'charging', name: 'Charging', component: () => import('@/views/charging/OrderList.vue') },
      { path: 'finance', name: 'Finance', component: () => import('@/views/finance/RevenueStats.vue') },
      { path: 'analytics', name: 'Analytics', component: () => import('@/views/analytics/DeviceEfficiency.vue') },
      { path: 'fault', name: 'Fault', component: () => import('@/views/fault/TicketList.vue') },
      { path: 'system', name: 'System', component: () => import('@/views/system/StationConfig.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory('/plus/'),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path !== '/plus/login' && !localStorage.getItem('token')) {
    next('/plus/login')
  } else {
    next()
  }
})

export default router
