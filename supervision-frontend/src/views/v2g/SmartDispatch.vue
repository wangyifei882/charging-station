<template>
  <div class="smart-dispatch">
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card>
          <template #header><span>当前调度任务</span></template>
          <div class="dispatch-status">
            <el-statistic title="进行中任务" :value="activeTasks" />
            <el-progress :percentage="dispatchProgress" :status="dispatchProgress < 50 ? '' : 'success'" style="margin-top: 20px" />
            <p class="status-text">当前调度进度: {{ dispatchProgress }}%</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>总功率分配</span></template>
          <div class="power-allocation">
            <div class="power-item">
              <span class="label">分配功率</span>
              <span class="value">{{ allocatedPower }} kW</span>
            </div>
            <div class="power-item">
              <span class="label">使用功率</span>
              <span class="value">{{ usedPower }} kW</span>
            </div>
            <div class="power-item">
              <span class="label">剩余功率</span>
              <span class="value">{{ remainingPower }} kW</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>调度效果</span></template>
          <div class="dispatch-effect">
            <el-statistic title="降低峰值(kW)" :value="peakReduction" />
            <el-statistic title="节约电费(元)" :value="costSaving" style="margin-top: 15px" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>调度任务列表</span>
          <el-button type="primary" @click="handleCreate">新建调度</el-button>
        </div>
      </template>

      <el-table :data="taskList" stripe>
        <el-table-column prop="taskName" label="任务名称" min-width="180" />
        <el-table-column prop="taskType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.taskType === 'peak' ? 'danger' : 'primary'" size="small">
              {{ row.taskType === 'peak' ? '削峰' : '均衡' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stationCount" label="涉及站点" width="100" align="center" />
        <el-table-column prop="targetPower" label="目标功率(kW)" width="120" align="right" />
        <el-table-column prop="actualPower" label="实际功率(kW)" width="120" align="right" />
        <el-table-column prop="progress" label="进度" width="120" align="center">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :status="row.progress === 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button type="warning" link @click="handleAdjust(row)">调整</el-button>
            <el-button v-if="row.status === 'running'" type="danger" link @click="handleStop(row)">终止</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="调度任务详情" width="650px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="任务名称" :span="2">{{ detailData.taskName }}</el-descriptions-item>
        <el-descriptions-item label="任务类型">
          <el-tag :type="detailData.taskType === 'peak' ? 'danger' : 'primary'" size="small">
            {{ detailData.taskType === 'peak' ? '削峰' : '均衡' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detailData.status)" size="small">{{ statusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="涉及站点">{{ detailData.stationCount }} 个</el-descriptions-item>
        <el-descriptions-item label="执行进度">{{ detailData.progress }}%</el-descriptions-item>
        <el-descriptions-item label="目标功率">{{ detailData.targetPower }} kW</el-descriptions-item>
        <el-descriptions-item label="实际功率">{{ detailData.actualPower }} kW</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDispatchTasks, getDispatchStats } from '../../api/v2g'

const detailDialogVisible = ref(false)
const detailData = ref(null)
const activeTasks = ref(0)
const dispatchProgress = ref(0)
const allocatedPower = ref(0)
const usedPower = ref(0)
const remainingPower = ref(0)
const peakReduction = ref(0)
const costSaving = ref(0)

const taskList = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const fetchStats = async () => {
  try {
    const res = await getDispatchStats()
    if (res.data) {
      activeTasks.value = res.data.activeTasks || 0
      dispatchProgress.value = res.data.dispatchProgress || 0
      allocatedPower.value = res.data.allocatedPower || 0
      usedPower.value = res.data.usedPower || 0
      remainingPower.value = res.data.remainingPower || 0
      peakReduction.value = res.data.peakReduction || 0
      costSaving.value = res.data.costSaving || 0
    }
  } catch (e) {
    console.error('获取统计数据失败', e)
  }
}

const fetchData = async () => {
  try {
    const res = await getDispatchTasks({
      page: pagination.currentPage,
      size: pagination.pageSize
    })
    if (res.data?.records) {
      taskList.value = res.data.records.map(task => ({
        id: task.id,
        taskName: `调度任务-${task.taskNo}`,
        taskType: task.dispatchType === 1 ? 'peak' : 'balance',
        stationCount: task.availableStationCount,
        targetPower: task.totalChargePower?.toFixed(0) || '0',
        actualPower: task.actualChargePower?.toFixed(0) || '0',
        progress: Math.round(task.executionRate || 0),
        status: task.status === 1 ? 'running' : task.status === 2 ? 'completed' : 'stopped'
      }))
      pagination.total = res.data.total || 0
    }
  } catch (e) {
    console.error('获取调度任务失败', e)
  }
}

onMounted(() => {
  fetchStats()
  fetchData()
})

const statusType = (status) => {
  const map = { running: 'success', completed: 'info', stopped: 'warning' }
  return map[status] || ''
}

const statusText = (status) => {
  const map = { running: '执行中', completed: '已完成', stopped: '已终止' }
  return map[status] || ''
}

const handleCreate = () => {
  ElMessage.info('创建调度任务')
}

const handleDetail = (row) => {
  detailData.value = row
  detailDialogVisible.value = true
}

const handleAdjust = (row) => {
  ElMessage.warning(`调整调度参数: ${row.taskName}`)
}

const handleStop = async (row) => {
  try {
    await ElMessageBox.confirm('确认终止该调度任务?', '警告', { type: 'warning' })
    ElMessage.error(`终止调度任务: ${row.taskName}`)
  } catch (e) {}
}
</script>

<style scoped lang="scss">
.dispatch-status, .power-allocation, .dispatch-effect {
  padding: 20px 0;
  text-align: center;
}

.status-text {
  margin-top: 15px;
  font-size: 14px;
  color: var(--color-text-regular);
}

.power-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border-light);

  &:last-child {
    border-bottom: none;
  }

  .label {
    color: var(--color-text-secondary);
  }
  .value {
    font-weight: 700;
    color: var(--color-text-primary);
  }
}
</style>
