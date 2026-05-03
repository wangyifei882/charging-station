<template>
  <div class="dashboard">
    <div class="metrics-grid">
      <el-row :gutter="16">
        <el-col :span="6" v-for="metric in metrics" :key="metric.label">
          <el-card class="metric-card" shadow="hover">
            <div class="metric-content">
              <div class="metric-icon" :style="{ background: metric.color }">
                <el-icon :size="24" color="#fff"><component :is="metric.icon" /></el-icon>
              </div>
              <div class="metric-info">
                <p class="metric-label">{{ metric.label }}</p>
                <h3 class="metric-value">{{ metric.value }}</h3>
                <p class="metric-trend" :class="metric.trendClass">
                  {{ metric.trend }}
                </p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="16" class="charts-row">
      <el-col :span="16">
        <el-card v-loading="trendLoading">
          <template #header>
            <div class="card-header">
              <span>充放电趋势</span>
              <el-radio-group v-model="trendPeriod" size="small">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>站点类型分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>需求响应统计</span>
          </template>
          <div ref="responseChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>实时告警</span>
            <el-button type="primary" link @click="$router.push('/alarm-monitor')">查看全部</el-button>
          </template>
          <el-table :data="alarmList" style="width: 100%" max-height="300" v-loading="alarmLoading">
            <el-table-column prop="alarmLevel" label="级别" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.alarmLevel === 1 ? 'danger' : row.alarmLevel === 2 ? 'warning' : 'info'" size="small">
                  {{ row.alarmLevel === 1 ? '严重' : row.alarmLevel === 2 ? '警告' : '提示' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="alarmContent" label="告警内容" show-overflow-tooltip min-width="150" />
            <el-table-column prop="stationName" label="站点" width="140">
              <template #default="{ row }">
                {{ getStationName(row.stationId) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getDashboardMetrics, getChargeDischargeTrend, getUnreadAlarms } from '../api/dashboard'

const router = useRouter()
const trendPeriod = ref('day')
const trendChartRef = ref(null)
const pieChartRef = ref(null)
const responseChartRef = ref(null)

let trendChart = null
let pieChart = null
let responseChart = null

const metrics = reactive([
  { label: '接入运营商', value: '128', trend: '↑ 12.5%', trendClass: 'trend-up', icon: 'OfficeBuilding', color: '#6366f1' },
  { label: '充电站总数', value: '2,456', trend: '↑ 8.3%', trendClass: 'trend-up', icon: 'Shop', color: '#3b82f6' },
  { label: '充电桩总数', value: '18,932', trend: '↑ 15.2%', trendClass: 'trend-up', icon: 'Cpu', color: '#0891b2' },
  { label: '今日充电量(kWh)', value: '45,678', trend: '↑ 5.1%', trendClass: 'trend-up', icon: 'Lightning', color: '#10b981' },
  { label: '在线设备率', value: '96.8%', trend: '↓ 0.2%', trendClass: 'trend-down', icon: 'Connection', color: '#f59e0b' },
  { label: '今日订单数', value: '8,234', trend: '↑ 3.7%', trendClass: 'trend-up', icon: 'Document', color: '#6366f1' },
  { label: '活跃V2G活动', value: '15', trend: '↑ 25%', trendClass: 'trend-up', icon: 'Switch', color: '#0891b2' },
  { label: '待处理告警', value: '23', trend: '↓ 10%', trendClass: 'trend-down', icon: 'Warning', color: '#ef4444' }
])

const alarmList = ref([])
const alarmLoading = ref(false)

const stationMap = {
  1: '南山科技园充电站',
  2: '福田市民中心充电站',
  3: '宝安中心充电站',
  4: '龙岗大运充电站',
  5: '盐田港充电站'
}

const getStationName = (stationId) => {
  return stationMap[stationId] || `站点${stationId}`
}

const trendLoading = ref(false)

const fetchTrendData = async (period) => {
  trendLoading.value = true
  try {
    const res = await getChargeDischargeTrend({ aggregate: period })
    if (res.data) {
      updateTrendChartWithData(res.data)
    }
  } catch (e) {
    console.error('获取充放电趋势数据失败', e)
  } finally {
    trendLoading.value = false
  }
}

const updateTrendChartWithData = (data) => {
  if (!trendChart) return
  trendChart.setOption({
    xAxis: { data: data.timestamps || [] },
    series: [
      { name: '充电量', type: 'line', smooth: true, data: data.chargeEnergy || [], itemStyle: { color: '#409eff' } },
      { name: '放电量', type: 'line', smooth: true, data: data.dischargeEnergy || [], itemStyle: { color: '#67c23a' } },
      { name: '净充电量', type: 'line', smooth: true, data: data.netChargeEnergy || [], itemStyle: { color: '#e6a23c' } }
    ]
  })
}

const initTrendChart = () => {
  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['充电量', '放电量', '净充电量'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: []
    },
    yAxis: { type: 'value', name: 'kWh' },
    series: [
      { name: '充电量', type: 'line', smooth: true, data: [], itemStyle: { color: '#409eff' } },
      { name: '放电量', type: 'line', smooth: true, data: [], itemStyle: { color: '#67c23a' } },
      { name: '净充电量', type: 'line', smooth: true, data: [], itemStyle: { color: '#e6a23c' } }
    ]
  })
}

const initPieChart = () => {
  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
      name: '站点类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
      data: [
        { value: 1048, name: '公共充电站' },
        { value: 735, name: '专用充电站' },
        { value: 580, name: 'V2G示范站' },
        { value: 484, name: '光储充一体站' }
      ]
    }]
  })
}

const initResponseChart = () => {
  responseChart = echarts.init(responseChartRef.value)
  responseChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value', name: 'kWh' },
    series: [{
      name: '响应电量',
      type: 'bar',
      data: [320, 332, 301, 334, 390, 330, 320],
      itemStyle: { color: '#409eff' },
      label: { show: true, position: 'top' }
    }]
  })
}

const fetchData = async () => {
  try {
    const res = await getDashboardMetrics()
    if (res.data) {
      Object.assign(metrics, res.data.metrics || metrics)
    }
  } catch (e) {
    console.error('获取数据失败', e)
  }
}

const fetchAlarms = async () => {
  alarmLoading.value = true
  try {
    const res = await getUnreadAlarms({ limit: 5 })
    if (res.data?.records && res.data.records.length > 0) {
      alarmList.value = res.data.records.slice(0, 5)
    } else {
      // 如果没有数据，显示模拟数据
      alarmList.value = [
        { alarmLevel: 1, alarmContent: '充电桩设备离线超过30分钟', stationId: 1, createTime: '2025-01-20 14:30:00' },
        { alarmLevel: 2, alarmContent: '充电功率异常波动', stationId: 2, createTime: '2025-01-20 13:15:00' },
        { alarmLevel: 3, alarmContent: '设备温度超过阈值', stationId: 3, createTime: '2025-01-20 12:45:00' },
        { alarmLevel: 2, alarmContent: '通信延迟超过5秒', stationId: 1, createTime: '2025-01-20 11:20:00' },
        { alarmLevel: 3, alarmContent: '电压波动异常', stationId: 4, createTime: '2025-01-20 10:05:00' }
      ]
    }
  } catch (e) {
    console.error('获取告警失败', e)
    // 接口失败时显示模拟数据
    alarmList.value = [
      { alarmLevel: 1, alarmContent: '充电桩设备离线超过30分钟', stationId: 1, createTime: '2025-01-20 14:30:00' },
      { alarmLevel: 2, alarmContent: '充电功率异常波动', stationId: 2, createTime: '2025-01-20 13:15:00' },
      { alarmLevel: 3, alarmContent: '设备温度超过阈值', stationId: 3, createTime: '2025-01-20 12:45:00' }
    ]
  } finally {
    alarmLoading.value = false
  }
}

watch(trendPeriod, (newVal) => {
  fetchTrendData(newVal)
})

onMounted(() => {
  initTrendChart()
  initPieChart()
  initResponseChart()
  fetchData()
  fetchAlarms()
  fetchTrendData(trendPeriod.value)

  window.addEventListener('resize', () => {
    trendChart?.resize()
    pieChart?.resize()
    responseChart?.resize()
  })
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
  responseChart?.dispose()
})
</script>

<style scoped lang="scss">
.metrics-grid {
  margin-bottom: 20px;
}

.metric-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.metric-info {
  flex: 1;
  min-width: 0;
}

.metric-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 6px;
}

.metric-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 4px;
}

.metric-trend {
  font-size: 12px;
  margin: 0;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}
</style>
