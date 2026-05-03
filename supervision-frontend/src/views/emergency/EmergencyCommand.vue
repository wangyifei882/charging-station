<template>
  <div class="emergency-command">
    <el-row :gutter="16" class="summary-row">
      <el-col :span="8">
        <el-card>
          <template #header><span>应急指令统计</span></template>
          <div class="command-stats">
            <el-statistic title="今日下发指令" :value="todayCommands" />
            <el-statistic title="执行中指令" :value="runningCommands" style="margin-top: 15px" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>指令执行状态</span></template>
          <div class="status-chart" ref="statusChartRef"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>最近应急事件</span></template>
          <div class="recent-events">
            <div v-for="event in recentEvents" :key="event.id" class="event-item">
              <el-tag :type="event.level === '1' ? 'danger' : 'warning'" size="small">{{ event.level === '1' ? '严重' : '一般' }}</el-tag>
              <span class="event-name">{{ event.eventName }}</span>
              <span class="event-time">{{ event.time }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>应急指令列表</span>
          <el-button type="danger" @click="handleCreate">下发应急指令</el-button>
        </div>
      </template>

      <el-table :data="commandList" stripe>
        <el-table-column prop="commandNo" label="指令编号" width="140" />
        <el-table-column prop="commandType" label="指令类型" width="100">
          <template #default="{ row }">
            <el-tag :type="commandType(row.commandType)" size="small">
              {{ row.commandType === 'stop' ? '紧急停机' : row.commandType === 'power' ? '功率限制' : '限流' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetStation" label="目标站点" min-width="150" />
        <el-table-column prop="reason" label="下发原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="issueTime" label="下发时间" width="160" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="commandStatusType(row.status)" size="small">
              {{ commandStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executionRate" label="执行率" width="100" align="center">
          <template #default="{ row }">
            <span :class="row.executionRate === 100 ? 'text-success' : 'text-warning'">{{ row.executionRate }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'executing'" type="danger" link @click="handleCancel(row)">撤销</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="指令详情" width="650px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="指令编号">{{ detailData.commandNo }}</el-descriptions-item>
        <el-descriptions-item label="指令类型">
          <el-tag :type="commandType(detailData.commandType)" size="small">
            {{ detailData.commandType === 'stop' ? '紧急停机' : detailData.commandType === 'power' ? '功率限制' : '限流' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="目标站点" :span="2">{{ detailData.targetStation }}</el-descriptions-item>
        <el-descriptions-item label="下发时间">{{ detailData.issueTime }}</el-descriptions-item>
        <el-descriptions-item label="执行率">{{ detailData.executionRate }}%</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="commandStatusType(detailData.status)" size="small">{{ commandStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下发原因" :span="2">{{ detailData.reason }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="createDialogVisible" title="下发应急指令" width="600px">
      <el-form :model="commandForm" label-width="100px">
        <el-form-item label="指令类型" required>
          <el-select v-model="commandForm.commandType" style="width: 100%">
            <el-option label="紧急停机" value="stop" />
            <el-option label="功率限制" value="power" />
            <el-option label="限流控制" value="limit" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标站点" required>
          <el-select v-model="commandForm.targetStation" multiple style="width: 100%">
            <el-option label="南山科技园充电站" :value="28" />
            <el-option label="福田CBD充电站" :value="32" />
            <el-option label="宝安中心充电站" :value="35" />
          </el-select>
        </el-form-item>
        <el-form-item label="下发原因" required>
          <el-input v-model="commandForm.reason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="限制功率(kW)">
          <el-input-number v-model="commandForm.maxPower" :min="0" :max="1000" />
        </el-form-item>
        <el-form-item label="有效时长(分钟)">
          <el-input-number v-model="commandForm.duration" :min="1" :max="1440" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitCommand">确认下发</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCommandList, sendCommand, cancelCommand } from '../../api/emergency'

const statusChartRef = ref(null)
const createDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const detailData = ref(null)

const todayCommands = ref(0)
const runningCommands = ref(0)

let statusChart = null

const recentEvents = ref([])

const commandList = ref([])

const commandForm = reactive({
  commandType: 'stop',
  targetStation: [],
  reason: '',
  maxPower: 0,
  duration: 60
})

const fetchData = async () => {
  try {
    const res = await getCommandList({
      page: 1,
      size: 100
    })
    if (res.data?.records) {
      commandList.value = res.data.records.map(cmd => ({
        id: cmd.id,
        commandNo: cmd.commandNo,
        commandType: cmd.commandType === 1 ? 'stop' : cmd.commandType === 2 ? 'power' : 'limit',
        targetStation: cmd.targetStationName || '未知站点',
        reason: cmd.issueReason || cmd.commandContent,
        issueTime: cmd.issueTimeStr || cmd.issueTime,
        status: cmd.status === 1 ? 'executing' : cmd.status === 2 ? 'completed' : 'cancelled',
        executionRate: Math.round(cmd.executionRate || 0)
      }))
      
      // 统计数据
      const cmds = commandList.value
      const today = new Date().toISOString().slice(0, 10)
      todayCommands.value = cmds.filter(c => c.issueTime?.startsWith(today)).length
      runningCommands.value = cmds.filter(c => c.status === 'executing').length
      
      // 最近应急事件（取最新的5条）
      recentEvents.value = cmds.slice(0, 5).map(cmd => ({
        id: cmd.id,
        level: cmd.commandType === 'stop' ? '1' : '2',
        eventName: cmd.reason || '应急指令',
        time: cmd.issueTime?.split(' ')[1] || ''
      }))
    }
  } catch (e) {
    console.error('获取应急指令失败', e)
  }
}

const commandType = (type) => {
  const map = { stop: 'danger', power: 'warning', limit: 'info' }
  return map[type] || ''
}

const commandStatusType = (status) => {
  const map = { executing: 'success', completed: 'info', cancelled: 'warning' }
  return map[status] || ''
}

const commandStatusText = (status) => {
  const map = { executing: '执行中', completed: '已完成', cancelled: '已撤销' }
  return map[status] || ''
}

const initStatusChart = () => {
  if (!statusChartRef.value) return
  statusChart = echarts.init(statusChartRef.value)
  
  const cmds = commandList.value
  const executing = cmds.filter(c => c.status === 'executing').length
  const completed = cmds.filter(c => c.status === 'completed').length
  const cancelled = cmds.filter(c => c.status === 'cancelled').length
  
  statusChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      name: '执行状态',
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: executing || 0, name: '执行中', itemStyle: { color: '#67c23a' } },
        { value: completed || 0, name: '已完成', itemStyle: { color: '#409eff' } },
        { value: cancelled || 0, name: '已撤销', itemStyle: { color: '#909399' } }
      ]
    }]
  })
}

const handleCreate = () => {
  createDialogVisible.value = true
}

const handleDetail = (row) => {
  detailData.value = row
  detailDialogVisible.value = true
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确认撤销应急指令: ${row.commandName}?`, '确认撤销', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await cancelCommand(row.id)
    if (res.code === 200) {
      ElMessage.success('撤销成功')
      fetchData()
    } else {
      ElMessage.error(res.message || '撤销失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error('撤销失败', e)
      ElMessage.error('撤销失败')
    }
  }
}

const submitCommand = async () => {
  try {
    await sendCommand({
      commandType: commandForm.commandType === 'stop' ? 1 : commandForm.commandType === 'power' ? 2 : 3,
      targetStationIds: commandForm.targetStation.join(','),
      issueReason: commandForm.reason,
      maxPowerLimit: commandForm.maxPower,
      validDuration: commandForm.duration
    })
    ElMessage.success('应急指令已下发')
    createDialogVisible.value = false
    fetchData()
    initStatusChart()
  } catch (e) {
    ElMessage.error('指令下发失败')
  }
}

const handleResize = () => statusChart?.resize()

onMounted(async () => {
  initStatusChart()
  await fetchData()
  initStatusChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  statusChart?.dispose()
})
</script>

<style scoped lang="scss">
.command-stats {
  padding: 20px 0;
  text-align: center;
}

.status-chart {
  height: 150px;
}

.event-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--color-border-light);

  &:last-child {
    border-bottom: none;
  }
}

.event-name {
  flex: 1;
  margin-left: 10px;
  font-size: 14px;
  color: var(--color-text-primary);
}

.event-time {
  color: var(--color-text-secondary);
  font-size: 12px;
}
</style>
