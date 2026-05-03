<template>
  <div class="dashboard">
    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="--c: var(--color-primary)">
          <el-icon :size="20"><Cpu /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-label">总桩数</span>
          <span class="stat-value">{{ overviewData.deviceStats?.total || 0 }}</span>
        </div>
        <span class="stat-change up">较上月 +12%</span>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="--c: var(--color-success)">
          <el-icon :size="20"><CircleCheck /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-label">空闲桩</span>
          <span class="stat-value">{{ overviewData.realtimeData?.idleCount || 0 }}</span>
        </div>
        <span class="stat-change up">{{ overviewData.deviceStats?.online || 45 }} 台在线</span>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="--c: #7c5ce7">
          <el-icon :size="20"><Lightning /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-label">今日充电量</span>
          <span class="stat-value">{{ Math.round(overviewData.realtimeData?.todayEnergy || 0) }} <span class="unit">kWh</span></span>
        </div>
        <span class="stat-change up">较上月 +8%</span>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="--c: var(--color-warning)">
          <el-icon :size="20"><Coin /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-label">今日营收</span>
          <span class="stat-value">{{ Math.round(overviewData.realtimeData?.todayRevenue || 0) }} <span class="unit">元</span></span>
        </div>
        <span class="stat-change up">较上月 +5%</span>
      </div>
    </div>

    <!-- Charts Row -->
    <div class="charts-row">
      <div class="panel chart-panel">
        <div class="panel-header">
          <div>
            <h3 class="panel-title">24小时负荷趋势</h3>
            <p class="panel-subtitle">预测未来 4 小时负荷平稳</p>
          </div>
          <div class="period-toggle">
            <button :class="{ active: trendType === 'today' }" @click="switchTrend('today')">今日</button>
            <button :class="{ active: trendType === 'yesterday' }" @click="switchTrend('yesterday')">昨日</button>
          </div>
        </div>
        <div ref="loadChartRef" class="chart-container"></div>
      </div>

      <div class="panel alert-panel">
        <div class="panel-header">
          <h3 class="panel-title">实时告警</h3>
          <a class="panel-link" @click="viewAllAlarms">查看全部 →</a>
        </div>
        <div class="alert-list">
          <div class="alert-item" v-for="alarm in alarms" :key="alarm.id" @click="showAlarmDetail(alarm)">
            <div class="alert-level" :class="'level-' + alarm.level">
              <span class="level-dot"></span>
            </div>
            <div class="alert-info">
              <div class="alert-device">{{ alarm.deviceCode }}</div>
              <div class="alert-type">{{ alarm.faultType }}</div>
            </div>
            <span class="alert-time">{{ alarm.time }}</span>
          </div>
          <div v-if="alarms.length === 0" class="empty-alerts">
            <el-icon :size="28"><CircleCheck /></el-icon>
            <span>暂无疑似告警</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Alert Detail Dialog -->
    <el-dialog v-model="alarmDialogVisible" title="告警详情" width="480px" :close-on-click-modal="false">
      <div class="alarm-detail" v-if="currentAlarm.id">
        <div class="detail-item">
          <span class="detail-label">设备编号</span>
          <span class="detail-value code">{{ currentAlarm.deviceCode }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">告警级别</span>
          <el-tag :type="currentAlarm.level === 3 ? 'danger' : currentAlarm.level === 2 ? 'warning' : 'info'" size="small">
            {{ currentAlarm.level === 3 ? '严重' : currentAlarm.level === 2 ? '警告' : '提示' }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="detail-label">故障类型</span>
          <span class="detail-value">{{ currentAlarm.faultType }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">告警时间</span>
          <span class="detail-value">{{ currentAlarm.time }}</span>
        </div>
        <div class="detail-item block">
          <span class="detail-label">详细描述</span>
          <p class="detail-desc">{{ currentAlarm.description || '暂无详细描述' }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="alarmDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleCurrentAlarm">确认处理</el-button>
      </template>
    </el-dialog>

    <!-- Device Monitoring Section -->
    <div class="section-header">
      <h3 class="section-title">实时充电监控</h3>
      <el-button type="primary" size="small" @click="addDevice">
        <el-icon :size="14"><Plus /></el-icon>
        新增设备
      </el-button>
    </div>

    <div class="device-grid">
      <div class="device-card" v-for="device in chargingDevices" :key="device.id">
        <div class="device-top">
          <div class="device-icon-box">
            <el-icon :size="16"><Lightning /></el-icon>
          </div>
          <div class="device-meta">
            <span class="device-id">{{ device.deviceCode || device.name }}</span>
            <span class="device-type">{{ device.typeName || device.type }}</span>
          </div>
          <span class="device-status-tag" :class="getDeviceStatusClass(device.status)">
            {{ getDeviceStatusText(device.status) }}
          </span>
        </div>

        <div class="device-progress-section" v-if="isCharging(device.status)">
          <div class="progress-header">
            <span class="soc">{{ device.soc || 0 }}% <span class="soc-label">SOC</span></span>
            <span class="power">{{ device.power || 0 }} kW</span>
          </div>
          <div class="progress-track">
            <div class="progress-fill" :style="{ width: (device.soc || 0) + '%' }"></div>
          </div>
          <div class="progress-footer">
            <span>
              <el-icon :size="12"><Clock /></el-icon>
              剩余 {{ device.duration || 0 }} 分钟
            </span>
            <span class="battery-ok">
              <el-icon :size="12"><CircleCheck /></el-icon>
              正常
            </span>
          </div>
        </div>
      </div>

      <div v-if="chargingDevices.length === 0" class="empty-devices">
        <el-icon :size="32"><InfoFilled /></el-icon>
        <span>暂无设备数据</span>
      </div>
    </div>

    <!-- Recent Transactions -->
    <div class="panel transaction-panel">
      <div class="panel-header">
        <h3 class="panel-title">最近交易</h3>
        <a class="panel-link" @click="viewMoreTransactions">查看更多 →</a>
      </div>
      <div class="transaction-list">
        <div class="transaction-item" v-for="t in transactions" :key="t.id">
          <div class="tx-left">
            <div class="tx-avatar">{{ t.userName ? t.userName.slice(0, 2).toUpperCase() : 'US' }}</div>
            <div>
              <span class="tx-name">{{ t.userName }}</span>
              <span class="tx-date">{{ t.date }}</span>
            </div>
          </div>
          <span class="tx-amount">+{{ Math.abs(parseFloat(t.amount)).toFixed(2) }}元</span>
        </div>
        <div v-if="transactions.length === 0" class="empty-tx">
          <span>暂无交易记录</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import {
  getDashboardOverview, getLoadTrend, getDeviceAll,
  getTicketList, getOrderList
} from '@/api'

const router = useRouter()
const loadChartRef = ref(null)
let loadChart = null

const overviewData = ref({
  deviceStats: { total: 50, online: 45, offline: 3, fault: 2 },
  realtimeData: { idleCount: 20, todayEnergy: 1250, todayRevenue: 3680, currentLoad: 450.2 }
})
const alarms = ref([])
const chargingDevices = ref([])
const transactions = ref([])
const trendType = ref('today')

const alarmDialogVisible = ref(false)
const currentAlarm = ref({})

const fetchOverview = async () => {
  try { const res = await getDashboardOverview(); if (res.data) overviewData.value = res.data } catch (e) { /* use defaults */ }
}

const fetchLoadTrend = async (type) => {
  try {
    const res = await getLoadTrend(type === 'today' ? 24 : 24)
    if (res.data && loadChart) updateChart(res.data.timestamps, res.data.loads)
  } catch (e) { /* ignore */ }
}

const fetchDevices = async () => {
  try {
    const res = await getDeviceAll()
    if (res.data) {
      chargingDevices.value = res.data.map(device => ({
        id: device.id, name: device.deviceCode, deviceCode: device.deviceCode,
        type: device.typeName || '直流桩', typeName: device.typeName, status: device.status,
        soc: Math.floor(Math.random() * 100), power: (Math.random() * 100).toFixed(1),
        duration: Math.floor(Math.random() * 60)
      }))
    }
  } catch (e) { /* ignore */ }
}

const fetchAlarms = async () => {
  try {
    const res = await getTicketList({ page: 1, size: 5 })
    if (res.data?.records) {
      alarms.value = res.data.records.map(ticket => ({
        id: ticket.id,
        level: ticket.status === 0 ? 3 : ticket.status === 1 ? 2 : 1,
        deviceCode: ticket.deviceCode || `设备${ticket.deviceId}`,
        deviceId: ticket.deviceId,
        faultType: ticket.faultTypeName || ticket.faultDescription?.substring(0, 10) || '故障',
        description: ticket.faultDescription || '',
        time: ticket.createTime || new Date().toLocaleTimeString(),
        status: ticket.status
      }))
    }
  } catch (e) {
    alarms.value = [
      { id: 1, level: 3, deviceCode: 'DC-001', deviceId: 1, faultType: '温度过高', description: '设备温度超75.5℃，请检查散热系统', time: '10:30:15' },
      { id: 2, level: 2, deviceCode: 'AC-003', deviceId: 3, faultType: '通信中断', description: '与服务器通信中断超5分钟', time: '09:45:22' },
      { id: 3, level: 1, deviceCode: 'DC-005', deviceId: 5, faultType: 'SOC偏低', description: '储能设备SOC低于20%', time: '08:20:10' }
    ]
  }
}

const fetchTransactions = async () => {
  try {
    const res = await getOrderList({ page: 1, size: 5 })
    if (res.data?.records) {
      transactions.value = res.data.records.map(order => ({
        id: order.id, userName: `用户_${order.userId || order.id}`,
        date: order.createTime ? order.createTime.split(' ')[0] : new Date().toLocaleDateString(),
        amount: order.actualFee || order.totalFee || 0
      }))
    }
  } catch (e) {
    transactions.value = [
      { id: 1, userName: '用户_1001', date: '2026-04-18', amount: 85.20 },
      { id: 2, userName: '用户_1002', date: '2026-04-18', amount: 62.50 },
      { id: 3, userName: '用户_1003', date: '2026-04-18', amount: 120.00 },
      { id: 4, userName: '用户_1004', date: '2026-04-17', amount: 45.80 },
      { id: 5, userName: '用户_1005', date: '2026-04-17', amount: 98.60 }
    ]
  }
}

const switchTrend = (type) => { trendType.value = type; fetchLoadTrend(type) }

const showAlarmDetail = (alarm) => { currentAlarm.value = { ...alarm }; alarmDialogVisible.value = true }

const handleCurrentAlarm = () => {
  if (!currentAlarm.value.id) return
  ElMessageBox.confirm(`确认处理设备 ${currentAlarm.value.deviceCode} 的告警?`, '处理告警', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' })
    .then(() => {
      ElMessage.success('告警已处理')
      alarms.value = alarms.value.filter(a => a.id !== currentAlarm.value.id)
      alarmDialogVisible.value = false
      currentAlarm.value = {}
    }).catch(() => {})
}

const viewAllAlarms = () => router.push('/fault')
const addDevice = () => { ElMessage.info('跳转到设备管理页面'); router.push('/device') }
const viewMoreTransactions = () => router.push('/charging')

const isCharging = (status) => status === 1 || status === 'charging'
const getDeviceStatusClass = (status) => ({ 0: 'idle', 1: 'charging', 2: 'fault', idle: 'idle', charging: 'charging', fault: 'fault' }[status] || 'idle')
const getDeviceStatusText = (status) => ({ 0: '空闲', 1: '充电中', 2: '故障', idle: '空闲', charging: '充电中', fault: '故障' }[status] || '空闲')

const initLoadChart = () => {
  if (!loadChartRef.value) return
  loadChart = echarts.init(loadChartRef.value)
  updateChart(
    ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'],
    [120, 80, 350, 480, 420, 280]
  )
  window.addEventListener('resize', () => loadChart?.resize())
}

const updateChart = (timestamps, loads) => {
  if (!loadChart) return
  loadChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#fff',
      borderColor: '#e2e5ea',
      textStyle: { color: '#1e2126', fontSize: 12 },
      boxShadow: '0 4px 12px rgba(0,0,0,0.08)'
    },
    grid: { left: 48, right: 16, top: 12, bottom: 24 },
    xAxis: {
      type: 'category', data: timestamps || [],
      axisLine: { lineStyle: { color: '#e2e5ea' } },
      axisTick: { show: false },
      axisLabel: { color: '#9399a5', fontSize: 11 }
    },
    yAxis: {
      type: 'value', name: 'kW', min: 0, max: 600, interval: 150,
      axisLine: { show: false }, axisTick: { show: false },
      axisLabel: { color: '#9399a5', fontSize: 11 },
      splitLine: { lineStyle: { color: '#eef0f3', type: 'dashed' } },
      nameTextStyle: { color: '#9399a5', fontSize: 11 }
    },
    series: [{
      type: 'line', data: loads || [], smooth: true, symbol: 'none',
      lineStyle: { width: 2.5, color: '#0d6b6e' },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(13,107,110,0.12)' },
        { offset: 1, color: 'rgba(13,107,110,0)' }
      ])}
    }]
  })
}

onMounted(() => {
  initLoadChart()
  fetchOverview(); fetchLoadTrend('today'); fetchDevices(); fetchAlarms(); fetchTransactions()
})

onUnmounted(() => { loadChart?.dispose() })
</script>

<style lang="scss" scoped>
.dashboard { display: flex; flex-direction: column; gap: 16px; }

// ---- Stats Cards ----
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-xs);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  background: color-mix(in srgb, var(--c) 10%, transparent);
  color: var(--c);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-muted);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  line-height: 1.1;
  font-variant-numeric: tabular-nums;

  .unit { font-size: 12px; font-weight: 400; color: var(--color-text-muted); }
}

.stat-change {
  font-size: 11px;
  font-weight: 500;
  &.up { color: var(--color-success); }
  &.down { color: var(--color-danger); }
}

// ---- Panels ----
.charts-row { display: grid; grid-template-columns: 2fr 1fr; gap: 16px; }

.panel {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-xs);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 20px;
  border-bottom: 1px solid var(--color-border-light);
}

.panel-title { font-size: 15px; font-weight: 600; color: var(--color-text); margin: 0; }
.panel-subtitle { font-size: 12px; color: var(--color-text-muted); margin: 2px 0 0; }
.panel-link { font-size: 12px; color: var(--color-primary); cursor: pointer; text-decoration: none; &:hover { text-decoration: underline; } }

.chart-panel { .chart-container { height: 240px; padding: 12px 20px 16px; } }

.period-toggle {
  display: flex;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  overflow: hidden;

  button {
    padding: 4px 12px;
    font-size: 12px;
    border: none;
    background: transparent;
    color: var(--color-text-secondary);
    cursor: pointer;
    transition: all var(--transition-fast);

    &.active { background: var(--color-primary); color: #fff; }
  }
}

// ---- Alert List ----
.alert-panel { display: flex; flex-direction: column; }

.alert-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  cursor: pointer;
  transition: background var(--transition-fast);
  border-bottom: 1px solid var(--color-border-light);

  &:last-child { border-bottom: none; }
  &:hover { background: var(--color-bg); }
}

.alert-level {
  display: flex;
  align-items: center;
  justify-content: center;

  .level-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  &.level-3 .level-dot { background: var(--color-danger); }
  &.level-2 .level-dot { background: var(--color-warning); }
  &.level-1 .level-dot { background: var(--color-primary); }
}

.alert-info { flex: 1; min-width: 0; }
.alert-device {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text);
  font-variant-numeric: tabular-nums;
}
.alert-type { font-size: 12px; color: var(--color-text-muted); margin-top: 1px; }
.alert-time { font-size: 11px; color: var(--color-text-muted); font-variant-numeric: tabular-nums; flex-shrink: 0; }

.empty-alerts {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  color: var(--color-text-muted);
  gap: 8px;

  .el-icon { color: var(--color-success); }
}

// ---- Section Header ----
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0;
}

// ---- Device Grid ----
.device-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }

.device-card {
  padding: 16px 20px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-xs);
  transition: box-shadow var(--transition-fast);

  &:hover { box-shadow: var(--shadow-sm); }
}

.device-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.device-icon-box {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  background: var(--color-primary-bg);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.device-meta { flex: 1; min-width: 0; }
.device-id { display: block; font-size: 13px; font-weight: 600; color: var(--color-text); font-variant-numeric: tabular-nums; }
.device-type { display: block; font-size: 11px; color: var(--color-text-muted); }

.device-status-tag {
  font-size: 11px;
  font-weight: 500;
  padding: 3px 10px;
  border-radius: 12px;
  flex-shrink: 0;

  &.charging { background: var(--color-primary-bg); color: var(--color-primary); }
  &.idle { background: var(--color-bg-secondary); color: var(--color-text-muted); }
  &.fault { background: var(--color-danger-bg); color: var(--color-danger); }
}

.device-progress-section {
  .progress-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
  }
  .soc { font-size: 18px; font-weight: 700; color: var(--color-text); }
  .soc-label { font-size: 11px; font-weight: 400; color: var(--color-text-muted); margin-left: 2px; }
  .power { font-size: 13px; font-weight: 500; color: var(--color-primary); }

  .progress-track {
    height: 5px;
    background: var(--color-bg-secondary);
    border-radius: 3px;
    overflow: hidden;
    margin-bottom: 10px;
  }
  .progress-fill {
    height: 100%;
    border-radius: 3px;
    background: var(--color-primary);
    transition: width 0.8s ease;
  }
  .progress-footer {
    display: flex;
    justify-content: space-between;
    font-size: 11px;
    color: var(--color-text-muted);

    .el-icon { vertical-align: -1px; }
    .battery-ok { color: var(--color-success); }
  }
}

.empty-devices {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px;
  color: var(--color-text-muted);
  gap: 8px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

// ---- Transactions ----
.transaction-panel {
  .panel-header { border-bottom: 1px solid var(--color-border-light); }
}

.transaction-list { padding: 0; }

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid var(--color-border-light);

  &:last-child { border-bottom: none; }
}

.tx-left { display: flex; align-items: center; gap: 10px; }

.tx-avatar {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  background: var(--color-bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
  color: var(--color-text-muted);
  flex-shrink: 0;
}

.tx-name { display: block; font-size: 13px; font-weight: 500; color: var(--color-text); }
.tx-date { display: block; font-size: 11px; color: var(--color-text-muted); }
.tx-amount { font-size: 14px; font-weight: 600; color: var(--color-success); font-variant-numeric: tabular-nums; }

.empty-tx { text-align: center; padding: 40px; color: var(--color-text-muted); font-size: 13px; }

// ---- Alert Detail Dialog ----
.alarm-detail {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;

  &.block { flex-direction: column; align-items: flex-start; gap: 6px; }
}

.detail-label { font-size: 13px; color: var(--color-text-muted); width: 72px; flex-shrink: 0; }
.detail-value { font-size: 14px; color: var(--color-text); }
.detail-value.code {
  font-variant-numeric: tabular-nums;
  font-weight: 600;
  background: var(--color-primary-bg);
  color: var(--color-primary);
  padding: 2px 10px;
  border-radius: var(--radius-sm);
}

.detail-desc {
  margin: 0;
  padding: 10px 12px;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  width: 100%;
}
</style>
