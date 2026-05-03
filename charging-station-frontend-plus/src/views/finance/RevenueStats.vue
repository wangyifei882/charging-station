<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">收益核算</h2>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="--b: var(--color-primary-bg); --c: var(--color-primary)">
          <el-icon :size="20"><Coin /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-label">总营收</span>
          <span class="stat-val">52,380 <span class="stat-unit">元</span></span>
          <span class="stat-trend up">较上月 +8%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="--b: var(--color-success-bg); --c: var(--color-success)">
          <el-icon :size="20"><Lightning /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-label">总充电量</span>
          <span class="stat-val">18,560 <span class="stat-unit">kWh</span></span>
          <span class="stat-trend up">较上月 +5%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="--b: #f0ebff; --c: #7c5ce7">
          <el-icon :size="20"><Document /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-label">总订单数</span>
          <span class="stat-val">1,250 <span class="stat-unit">单</span></span>
          <span class="stat-trend up">较上月 +12%</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="--b: var(--color-warning-bg); --c: var(--color-warning)">
          <el-icon :size="20"><TrendCharts /></el-icon>
        </div>
        <div class="stat-body">
          <span class="stat-label">平均客单价</span>
          <span class="stat-val">41.9 <span class="stat-unit">元</span></span>
          <span class="stat-trend down">较上月 -2%</span>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3 class="panel-title">营收趋势图</h3>
      </div>
      <div class="panel-body">
        <div ref="revenueChartRef" class="chart-container"></div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-header">
        <h3 class="panel-title">费率配置</h3>
        <el-button type="primary" size="small" @click="saveRates">保存配置</el-button>
      </div>
      <el-table :data="rateData" style="width: 100%">
        <el-table-column prop="period" label="时段" min-width="260" />
        <el-table-column prop="electricityPrice" label="电价(元/kWh)" width="180" align="right">
          <template #default="{ row }">
            <el-input-number v-model="row.electricityPrice" :min="0" :precision="2" :step="0.1" size="small" controls-position="right" style="width: 140px" />
          </template>
        </el-table-column>
        <el-table-column prop="servicePrice" label="服务费(元/kWh)" width="180" align="right">
          <template #default="{ row }">
            <el-input-number v-model="row.servicePrice" :min="0" :precision="2" :step="0.1" size="small" controls-position="right" style="width: 140px" />
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const revenueChartRef = ref(null)
let revenueChart = null

const rateData = ref([
  { period: '尖峰时段 (10:00-12:00, 14:00-17:00)', electricityPrice: 1.2, servicePrice: 0.8 },
  { period: '高峰时段 (08:00-10:00, 17:00-20:00)', electricityPrice: 1.0, servicePrice: 0.7 },
  { period: '平段时段 (06:00-08:00, 12:00-14:00)', electricityPrice: 0.7, servicePrice: 0.5 },
  { period: '低谷时段 (00:00-06:00, 20:00-24:00)', electricityPrice: 0.4, servicePrice: 0.3 }
])

const saveRates = () => { ElMessage.success('费率配置已保存') }

onMounted(() => {
  if (!revenueChartRef.value) return
  revenueChart = echarts.init(revenueChartRef.value)

  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  const data = [3200, 3500, 3800, 4200, 3900, 4100, 3700, 4500, 4800, 5100, 4900, 5238]

  revenueChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#fff',
      borderColor: '#e2e5ea',
      textStyle: { color: '#1e2126', fontSize: 12 }
    },
    grid: { left: 48, right: 16, top: 12, bottom: 24 },
    xAxis: {
      type: 'category', data: months,
      axisLine: { lineStyle: { color: '#e2e5ea' } },
      axisTick: { show: false },
      axisLabel: { color: '#9399a5', fontSize: 11 }
    },
    yAxis: {
      type: 'value', min: 3000, max: 6000,
      axisLine: { show: false }, axisTick: { show: false },
      axisLabel: { color: '#9399a5', fontSize: 11 },
      splitLine: { lineStyle: { color: '#eef0f3', type: 'dashed' } }
    },
    series: [{
      type: 'bar', data, barWidth: '50%',
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#159196' },
          { offset: 1, color: '#0d6b6e' }
        ])
      }
    }]
  })

  window.addEventListener('resize', () => revenueChart?.resize())
})

onUnmounted(() => { revenueChart?.dispose() })
</script>

<style lang="scss" scoped>
.page { display: flex; flex-direction: column; gap: 14px; }
.page-header .page-title { font-size: 18px; font-weight: 600; color: var(--color-text); margin: 0; }

.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; }

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}
.stat-icon {
  width: 44px; height: 44px; border-radius: var(--radius-md);
  background: var(--b); color: var(--c);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.stat-body { flex: 1; }
.stat-label { display: block; font-size: 12px; color: var(--color-text-muted); margin-bottom: 2px; }
.stat-val { font-size: 22px; font-weight: 700; color: var(--color-text); font-variant-numeric: tabular-nums; }
.stat-unit { font-size: 12px; font-weight: 400; color: var(--color-text-muted); }
.stat-trend { display: block; font-size: 11px; margin-top: 4px; font-weight: 500; }
.stat-trend.up { color: var(--color-success); }
.stat-trend.down { color: var(--color-danger); }

.panel {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  border-bottom: 1px solid var(--color-border-light);
}
.panel-title { font-size: 15px; font-weight: 600; color: var(--color-text); margin: 0; }
.panel-body { padding: 12px 20px 16px; }
.chart-container { height: 280px; }
</style>
