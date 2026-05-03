<template>
  <div class="station-detail">
    <el-page-header @back="goBack" content="返回设施台账">
      <template #extra>
        <el-button type="primary" @click="refresh">刷新</el-button>
      </template>
    </el-page-header>

    <el-card class="basic-info-card" shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>站点基本信息</span>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="站点名称">{{ stationInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="所属运营商">{{ stationInfo.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="所属区域">{{ stationInfo.regionName }}</el-descriptions-item>
        <el-descriptions-item label="站点地址">{{ stationInfo.address }}</el-descriptions-item>
        <el-descriptions-item label="站点状态">
          <el-tag :type="stationInfo.status === 1 ? 'success' : 'warning'">
            {{ stationInfo.status === 1 ? '运营中' : '建设中' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="设备总数">{{ stationInfo.totalDevices || 0 }}</el-descriptions-item>
        <el-descriptions-item label="在线设备">{{ stationInfo.onlineDevices || 0 }}</el-descriptions-item>
        <el-descriptions-item label="今日充电量">{{ stationInfo.todayCharge || 0 }} kWh</el-descriptions-item>
        <el-descriptions-item label="是否支持V2G">
          <el-tag :type="stationInfo.isV2g ? 'success' : 'info'">
            {{ stationInfo.isV2g ? '支持' : '不支持' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="device-list-card" shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>设备清单</span>
        </div>
      </template>
      <el-table :data="deviceList" stripe v-loading="loading">
        <el-table-column prop="deviceCode" label="设备编号" width="150"></el-table-column>
        <el-table-column prop="deviceName" label="设备名称" width="150"></el-table-column>
        <el-table-column prop="deviceType" label="设备类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.deviceType === 1 ? 'primary' : 'success'">
              {{ row.deviceType === 1 ? '充电桩' : 'V2G设备' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="运行状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'danger'" size="small">
              {{ row.status === 1 ? '在线' : row.status === 2 ? '离线' : '故障' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="power" label="额定功率" width="100" align="right"></el-table-column>
        <el-table-column prop="todayCharge" label="今日充电量" width="120" align="right">
          <template #default="{ row }">
            {{ row.todayCharge || 0 }} kWh
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small">详情</el-button>
            <el-button type="warning" link size="small" v-if="row.status === 3">报修</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近30天充电量趋势</span>
            </div>
          </template>
          <div ref="chargeTrendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>近30天设备在线率</span>
            </div>
          </template>
          <div ref="onlineRateChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="alarm-list-card" shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>近90天告警记录</span>
        </div>
      </template>
      <el-table :data="alarmList" stripe>
        <el-table-column prop="alarmTime" label="告警时间" width="180"></el-table-column>
        <el-table-column prop="alarmLevel" label="告警级别" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.alarmLevel === 1 ? 'danger' : row.alarmLevel === 2 ? 'warning' : 'info'" size="small">
              {{ row.alarmLevel === 1 ? '严重' : row.alarmLevel === 2 ? '警告' : '提示' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alarmType" label="告警类型" width="120"></el-table-column>
        <el-table-column prop="alarmContent" label="告警内容" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="deviceName" label="关联设备" width="150"></el-table-column>
        <el-table-column prop="status" label="处理状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'primary' : 'success'" size="small">
              {{ row.status === 0 ? '未处理' : row.status === 1 ? '处理中' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getStationDetail } from '../../api/facility'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const stationId = route.query.id

const stationInfo = reactive({
  id: null,
  name: '',
  operatorName: '',
  regionName: '',
  address: '',
  status: 1,
  totalDevices: 0,
  onlineDevices: 0,
  todayCharge: 0,
  isV2g: false
})

const deviceList = ref([])
const alarmList = ref([])

let chargeTrendChart = null
let onlineRateChart = null

const chargeTrendChartRef = ref(null)
const onlineRateChartRef = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const res = await getStationDetail(stationId)
    if (res.data) {
      Object.assign(stationInfo, res.data)
    }
    
    // 模拟设备数据
    deviceList.value = [
      { deviceCode: 'DEV001', deviceName: '充电桩A1', deviceType: 1, status: 1, power: '60', todayCharge: 452.5 },
      { deviceCode: 'DEV002', deviceName: '充电桩A2', deviceType: 1, status: 1, power: '60', todayCharge: 389.2 },
      { deviceCode: 'DEV003', deviceName: 'V2G设备B1', deviceType: 2, status: 1, power: '120', todayCharge: 245.8 },
      { deviceCode: 'DEV004', deviceName: '充电桩A3', deviceType: 1, status: 2, power: '60', todayCharge: 0 },
      { deviceCode: 'DEV005', deviceName: '充电桩A4', deviceType: 1, status: 3, power: '60', todayCharge: 0 }
    ]
    
    // 模拟告警数据
    alarmList.value = [
      { alarmTime: '2025-01-20 14:30:00', alarmLevel: 2, alarmType: '设备离线', alarmContent: '设备DEV004离线', deviceName: '充电桩A4', status: 1 },
      { alarmTime: '2025-01-19 09:15:00', alarmLevel: 3, alarmType: '充电异常', alarmContent: '充电功率波动异常', deviceName: '充电桩A2', status: 2 },
      { alarmTime: '2025-01-18 22:45:00', alarmLevel: 1, alarmType: '设备故障', alarmContent: '设备DEV005出现故障', deviceName: '充电桩A4', status: 2 }
    ]
    
    initCharts()
  } catch (e) {
    ElMessage.error('获取站点详情失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

function initCharts() {
  // 充电量趋势图
  if (chargeTrendChartRef.value) {
    chargeTrendChart = echarts.init(chargeTrendChartRef.value)
    const dates = []
    const chargeData = []
    for (let i = 29; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      dates.push(`${d.getMonth() + 1}/${d.getDate()}`)
      chargeData.push(300 + Math.random() * 300)
    }
    chargeTrendChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', boundaryGap: false, data: dates },
      yAxis: { type: 'value', name: 'kWh' },
      series: [{
        name: '充电量',
        type: 'line',
        smooth: true,
        data: chargeData,
        itemStyle: { color: '#409eff' }
      }]
    })
  }

  // 设备在线率图
  if (onlineRateChartRef.value) {
    onlineRateChart = echarts.init(onlineRateChartRef.value)
    const dates = []
    const onlineData = []
    for (let i = 29; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      dates.push(`${d.getMonth() + 1}/${d.getDate()}`)
      onlineData.push(85 + Math.random() * 15)
    }
    onlineRateChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', boundaryGap: false, data: dates },
      yAxis: { type: 'value', name: '%', min: 0, max: 100 },
      series: [{
        name: '在线率',
        type: 'line',
        smooth: true,
        data: onlineData,
        itemStyle: { color: '#67c23a' }
      }]
    })
  }
}

function goBack() {
  router.back()
}

function refresh() {
  fetchData()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (chargeTrendChart) chargeTrendChart.dispose()
  if (onlineRateChart) onlineRateChart.dispose()
  window.removeEventListener('resize', handleResize)
})

function handleResize() {
  if (chargeTrendChart) chargeTrendChart.resize()
  if (onlineRateChart) onlineRateChart.resize()
}
</script>

<style scoped lang="scss">
.chart-container {
  height: 300px;
}
</style>
