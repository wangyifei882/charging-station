<template>
  <div class="alarm-monitor">
    <el-row :gutter="16" class="summary-row">
      <el-col :span="6">
        <el-card class="alarm-stat-card" shadow="hover">
          <div class="alarm-stat">
            <el-statistic title="今日告警总数" :value="totalAlarms" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="alarm-stat-card" shadow="hover">
          <div class="alarm-stat">
            <el-statistic title="严重告警" :value="criticalAlarms" value-style="color: #f56c6c" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="alarm-stat-card" shadow="hover">
          <div class="alarm-stat">
            <el-statistic title="警告" :value="warningAlarms" value-style="color: #e6a23c" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="alarm-stat-card" shadow="hover">
          <div class="alarm-stat">
            <el-statistic title="未处理" :value="pendingAlarms" value-style="color: #909399" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>实时告警</span>
          <div>
            <el-radio-group v-model="levelFilter" size="small" style="margin-right: 10px">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button label="1">严重</el-radio-button>
              <el-radio-button label="2">警告</el-radio-button>
              <el-radio-button label="3">提示</el-radio-button>
            </el-radio-group>
            <el-button type="primary" :icon="Refresh" @click="fetchData">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="alarmList" stripe>
        <el-table-column prop="alarmLevel" label="级别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="levelType(row.alarmLevel)" size="small">
              {{ levelText(row.alarmLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alarmType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.alarmType === 1 ? 'danger' : row.alarmType === 2 ? 'warning' : 'info'" size="small">
              {{ row.alarmType === 1 ? '设备' : row.alarmType === 2 ? '网络' : '其他' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stationId" label="站点ID" width="100" />
        <el-table-column prop="deviceId" label="设备ID" width="100" />
        <el-table-column prop="alarmContent" label="告警内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="发生时间" width="160" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'danger' : row.status === 1 ? 'warning' : 'success'" size="small">
              {{ row.status === 0 ? '未处理' : row.status === 1 ? '处理中' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status !== 2" type="success" link @click="handleProcess(row)">处理</el-button>
            <el-button v-if="row.alarmLevel === 1" type="danger" link @click="handleEscalate(row)">升级</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        class="pagination"
      />
    </el-card>

    <el-dialog v-model="processDialogVisible" title="处理告警" width="500px">
      <el-form :model="processForm" label-width="80px">
        <el-form-item label="处理结果">
          <el-radio-group v-model="processForm.handleResult">
            <el-radio label="resolved">已解决</el-radio>
            <el-radio label="ignored">已忽略</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明">
          <el-input v-model="processForm.handleNote" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProcess">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="告警详情" width="650px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="告警级别">
          <el-tag :type="levelType(detailData.alarmLevel)" size="small">{{ levelText(detailData.alarmLevel) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="告警类型">
          <el-tag :type="detailData.alarmType === 1 ? 'danger' : detailData.alarmType === 2 ? 'warning' : 'info'" size="small">
            {{ detailData.alarmType === 1 ? '设备' : detailData.alarmType === 2 ? '网络' : '其他' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="站点ID">{{ detailData.stationId }}</el-descriptions-item>
        <el-descriptions-item label="设备ID">{{ detailData.deviceId }}</el-descriptions-item>
        <el-descriptions-item label="发生时间" :span="2">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="告警内容" :span="2">{{ detailData.alarmContent }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="detailData.status === 0 ? 'danger' : detailData.status === 1 ? 'warning' : 'success'" size="small">
            {{ detailData.status === 0 ? '未处理' : detailData.status === 1 ? '处理中' : '已处理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="确认人">{{ detailData.confirmBy || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAlarmList } from '../../api/emergency'

const levelFilter = ref('')
const processDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const detailData = ref(null)
const processForm = reactive({
  handleResult: 'resolved',
  handleNote: ''
})

const totalAlarms = ref(0)
const criticalAlarms = ref(0)
const warningAlarms = ref(0)
const pendingAlarms = ref(0)

const alarmList = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const levelType = (level) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'info' }
  return map[level] || ''
}

const levelText = (level) => {
  const map = { 1: '严重', 2: '警告', 3: '提示' }
  return map[level] || ''
}

const fetchData = async () => {
  try {
    const res = await getAlarmList({ level: levelFilter.value })
    if (res.data?.records) {
      alarmList.value.splice(0, alarmList.value.length, ...res.data.records)
      pagination.total = res.data.total
      totalAlarms.value = res.data.total || 0
      criticalAlarms.value = (res.data.records || []).filter(a => a.alarmLevel === '1' || a.alarmLevel === 1).length
      warningAlarms.value = (res.data.records || []).filter(a => a.alarmLevel === '2' || a.alarmLevel === 2).length
      pendingAlarms.value = (res.data.records || []).filter(a => a.status === 'pending' || a.status === 0).length
    }
  } catch (e) {
    console.error('获取告警失败', e)
  }
}

const handleDetail = (row) => {
  detailData.value = row
  detailDialogVisible.value = true
}

const handleProcess = (row) => {
  processForm.handleNote = ''
  processDialogVisible.value = true
}

const handleEscalate = (row) => {
  ElMessage.warning(`升级告警: ${row.message}`)
}

const submitProcess = () => {
  ElMessage.success('处理成功')
  processDialogVisible.value = false
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.alarm-stat-card {
  text-align: center;
}
</style>
