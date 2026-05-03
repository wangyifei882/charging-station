<template>
  <div class="page">
    <div class="charts-row">
      <div class="panel">
        <div class="panel-header">
          <h3 class="panel-title">充电时段分布</h3>
        </div>
        <div class="panel-body">
          <div ref="pieChartRef" class="chart-container"></div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3 class="panel-title">设备利用率排行</h3>
        </div>
        <div class="panel-body">
          <div ref="barChartRef" class="chart-container"></div>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3 class="panel-title">设备效率排行</h3>
      </div>
      <el-table :data="efficiencyData" style="width: 100%">
        <el-table-column prop="deviceCode" label="设备编号" min-width="120">
          <template #default="{ row }">
            <span class="cell-code">{{ row.deviceCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="utilization" label="利用率" width="110" align="right">
          <template #default="{ row }">
            <span class="cell-number accent">{{ row.utilization }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="chargeCount" label="充电次数" width="110" align="right">
          <template #default="{ row }">
            <span class="cell-number">{{ row.chargeCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="avgDuration" label="平均时长(分)" width="130" align="right">
          <template #default="{ row }">
            <span class="cell-number">{{ row.avgDuration }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="faultCount" label="故障次数" width="110" align="right">
          <template #default="{ row }">
            <span class="cell-number" :class="row.faultCount > 0 ? 'danger' : ''">{{ row.faultCount }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null, barChart = null

const efficiencyData = ref([
  { deviceCode: 'DC-001', utilization: 78.5, chargeCount: 125, avgDuration: 50.2, faultCount: 1 },
  { deviceCode: 'DC-002', utilization: 72.3, chargeCount: 110, avgDuration: 45.8, faultCount: 0 },
  { deviceCode: 'DC-003', utilization: 65.1, chargeCount: 98, avgDuration: 42.5, faultCount: 2 },
  { deviceCode: 'AC-001', utilization: 58.2, chargeCount: 85, avgDuration: 120.5, faultCount: 0 },
  { deviceCode: 'AC-002', utilization: 45.8, chargeCount: 62, avgDuration: 115.2, faultCount: 1 }
])

onMounted(() => {
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: {
        trigger: 'item',
        backgroundColor: '#fff',
        borderColor: '#e2e5ea',
        textStyle: { color: '#1e2126', fontSize: 12 }
      },
      series: [{
        type: 'pie', radius: ['50%', '75%'], center: ['35%', '50%'],
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
        data: [
          { value: 50, name: '普通会员', itemStyle: { color: '#9399a5' } },
          { value: 25, name: '银卡会员', itemStyle: { color: '#0d6b6e' } },
          { value: 15, name: '金卡会员', itemStyle: { color: '#2d8a56' } },
          { value: 5, name: '铂金会员', itemStyle: { color: '#b85c14' } },
          { value: 5, name: '黑卡会员', itemStyle: { color: '#7c5ce7' } }
        ]
      }],
      legend: {
        orient: 'vertical', right: '10%', top: 'center',
        textStyle: { color: '#5b616e', fontSize: 12 },
        itemWidth: 10, itemHeight: 10, itemGap: 12,
        formatter: (name) => {
          const map = { '普通会员': '50%', '银卡会员': '25%', '金卡会员': '15%', '铂金会员': '5%', '黑卡会员': '5%' }
          return `${name}  ${map[name] || ''}`
        }
      }
    })
  }

  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value)
    const devices = ['DC-001', 'DC-002', 'DC-003', 'AC-001', 'AC-002']
    const utilization = [85, 78, 72, 65, 48]
    barChart.setOption({
      tooltip: {
        backgroundColor: '#fff',
        borderColor: '#e2e5ea',
        textStyle: { color: '#1e2126', fontSize: 12 }
      },
      grid: { left: 80, right: 16, top: 8, bottom: 8 },
      xAxis: { type: 'value', axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#9399a5', fontSize: 11 }, splitLine: { lineStyle: { color: '#eef0f3', type: 'dashed' } } },
      yAxis: { type: 'category', data: devices, axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#5b616e', fontSize: 12 } },
      series: [{
        type: 'bar', data: utilization, barWidth: '50%',
        itemStyle: {
          borderRadius: [0, 4, 4, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#8ecacc' },
            { offset: 1, color: '#0d6b6e' }
          ])
        }
      }]
    })
  }

  window.addEventListener('resize', () => { pieChart?.resize(); barChart?.resize() })
})

onUnmounted(() => { pieChart?.dispose(); barChart?.dispose() })
</script>

<style lang="scss" scoped>
.page { display: flex; flex-direction: column; gap: 14px; }

.charts-row { display: grid; grid-template-columns: repeat(2, 1fr); gap: 14px; }

.panel {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}
.panel-header {
  padding: 14px 20px;
  border-bottom: 1px solid var(--color-border-light);
}
.panel-title { font-size: 15px; font-weight: 600; color: var(--color-text); margin: 0; }
.panel-body { padding: 12px 20px 16px; }
.chart-container { height: 300px; }

.cell-code { font-weight: 600; color: var(--color-text); font-variant-numeric: tabular-nums; }
.cell-number { font-variant-numeric: tabular-nums; color: var(--color-text); }
.cell-number.accent { color: var(--color-primary); font-weight: 600; }
.cell-number.danger { color: var(--color-danger); font-weight: 600; }
</style>
