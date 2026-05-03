import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '数据总览' }
      },
      {
        path: 'facility',
        name: 'Facility',
        component: () => import('../views/facility/FacilityLedger.vue'),
        meta: { title: '设施台账' }
      },
      {
        path: 'station-detail',
        name: 'StationDetail',
        component: () => import('../views/facility/StationDetail.vue'),
        meta: { title: '站点详情' }
      },
      {
        path: 'status-monitor',
        name: 'StatusMonitor',
        component: () => import('../views/facility/StatusMonitor.vue'),
        meta: { title: '运行状态监控' }
      },
      {
        path: 'access-audit',
        name: 'AccessAudit',
        component: () => import('../views/facility/AccessAudit.vue'),
        meta: { title: '接入审核' }
      },
      {
        path: 'v2g-activity',
        name: 'V2gActivity',
        component: () => import('../views/v2g/V2gActivity.vue'),
        meta: { title: 'V2G活动' }
      },
      {
        path: 'demand-response',
        name: 'DemandResponse',
        component: () => import('../views/v2g/DemandResponse.vue'),
        meta: { title: '需求响应' }
      },
      {
        path: 'smart-dispatch',
        name: 'SmartDispatch',
        component: () => import('../views/v2g/SmartDispatch.vue'),
        meta: { title: '智能调度' }
      },
      {
        path: 'operator-manage',
        name: 'OperatorManage',
        component: () => import('../views/compliance/OperatorManage.vue'),
        meta: { title: '运营商管理' }
      },
      {
        path: 'subsidy-manage',
        name: 'SubsidyManage',
        component: () => import('../views/compliance/SubsidyManage.vue'),
        meta: { title: '补贴管理' }
      },
      {
        path: 'policy-manage',
        name: 'PolicyManage',
        component: () => import('../views/compliance/PolicyManage.vue'),
        meta: { title: '政策管理' }
      },
      {
        path: 'alarm-monitor',
        name: 'AlarmMonitor',
        component: () => import('../views/emergency/AlarmMonitor.vue'),
        meta: { title: '告警监控' }
      },
      {
        path: 'emergency-command',
        name: 'EmergencyCommand',
        component: () => import('../views/emergency/EmergencyCommand.vue'),
        meta: { title: '应急指令' }
      },
      {
        path: 'accident-trace',
        name: 'AccidentTrace',
        component: () => import('../views/emergency/AccidentTrace.vue'),
        meta: { title: '事故追溯' }
      },
      {
        path: 'alarm-config',
        name: 'AlarmConfig',
        component: () => import('../views/emergency/AlarmConfig.vue'),
        meta: { title: '告警规则配置' }
      },
      {
        path: 'user-manage',
        name: 'UserManage',
        component: () => import('../views/system/UserManagement.vue'),
        meta: { title: '用户权限管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 监管与调度平台` : '监管与调度平台'
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
