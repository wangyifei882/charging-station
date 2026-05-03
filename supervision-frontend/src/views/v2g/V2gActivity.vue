<template>
  <div class="v2g-activity">
    <el-row :gutter="16">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card shadow="hover">
          <div class="stat-card">
            <h4>{{ stat.label }}</h4>
            <p class="stat-value">{{ stat.value }}</p>
            <p class="stat-trend" :class="stat.trendClass">{{ stat.trend }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>V2G活动列表</span>
          <el-button type="primary" :icon="Plus" @click="handleCreate">创建活动</el-button>
        </div>
      </template>

      <!-- 创建活动对话框 -->
      <el-dialog
        v-model="dialogVisible"
        title="创建V2G活动"
        width="600px"
        :close-on-click-modal="false"
      >
        <el-form :model="createForm" :rules="rules" ref="formRef" label-width="120px">
          <el-form-item label="活动名称" prop="activityName">
            <el-input v-model="createForm.activityName" placeholder="请输入活动名称" />
          </el-form-item>
          <el-form-item label="活动类型" prop="activityType">
            <el-radio-group v-model="createForm.activityType">
              <el-radio label="v2g">V2G放电</el-radio>
              <el-radio label="ordered">有序充电</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker
              v-model="createForm.startTime"
              type="datetime"
              placeholder="选择开始时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker
              v-model="createForm.endTime"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="目标功率(kW)" prop="targetPower">
            <el-input-number v-model="createForm.targetPower" :min="1" :max="10000" style="width: 100%" />
          </el-form-item>
          <el-form-item label="目标电量(kWh)" prop="targetEnergy">
            <el-input-number v-model="createForm.targetEnergy" :min="1" :max="100000" style="width: 100%" />
          </el-form-item>
          <el-form-item label="活动说明" prop="description">
            <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="请输入活动说明" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitCreate" :loading="submitLoading">创建</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 活动详情对话框 -->
      <el-dialog
        v-model="detailDialogVisible"
        title="活动详情"
        width="700px"
      >
        <el-descriptions :column="2" border v-if="currentActivity">
          <el-descriptions-item label="活动名称">{{ currentActivity.activityName }}</el-descriptions-item>
          <el-descriptions-item label="活动编号">{{ currentActivity.activityNo }}</el-descriptions-item>
          <el-descriptions-item label="活动类型">
            <el-tag :type="currentActivity.activityType === 'v2g' ? 'success' : 'warning'" size="small">
              {{ currentActivity.activityType === 'v2g' ? 'V2G放电' : '有序充电' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="活动状态">
            <el-tag :type="statusType(currentActivity.status)" size="small">
              {{ statusText(currentActivity.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(currentActivity.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDateTime(currentActivity.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="目标功率">{{ currentActivity.targetPower }} kW</el-descriptions-item>
          <el-descriptions-item label="目标电量">{{ currentActivity.targetEnergy }} kWh</el-descriptions-item>
          <el-descriptions-item label="参与车辆数">{{ currentActivity.participatingVehicleCount || 0 }} 辆</el-descriptions-item>
          <el-descriptions-item label="实际放电量">{{ currentActivity.actualDischargeEnergy || 0 }} kWh</el-descriptions-item>
          <el-descriptions-item label="活动说明" :span="2">{{ currentActivity.description || '无' }}</el-descriptions-item>
        </el-descriptions>
        <template #footer>
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="活动状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable>
            <el-option label="未开始" value="0" />
            <el-option label="进行中" value="1" />
            <el-option label="已结束" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="activityList" v-loading="loading" stripe>
        <el-table-column prop="activityName" label="活动名称" min-width="180" />
        <el-table-column prop="activityType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.activityType === 'v2g' ? 'success' : 'warning'" size="small">
              {{ row.activityType === 'v2g' ? 'V2G' : '有序充电' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="participantCount" label="参与数" width="100" align="center" />
        <el-table-column prop="targetPower" label="目标功率(kW)" width="120" align="right" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === '0'" type="success" link @click="handleStart(row)">启动</el-button>
            <el-button v-if="row.status === '1'" type="danger" link @click="handleStop(row)">结束</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getV2gActivityList, getV2gActivityStats, createV2gActivity } from '../../api/v2g'

const loading = ref(false)
const submitLoading = ref(false)
const queryForm = reactive({ status: '' })

const stats = ref([
  { label: '进行中活动', value: '0', trend: '--', trendClass: '' },
  { label: '参与运营商', value: '0', trend: '--', trendClass: '' },
  { label: '响应总电量(kWh)', value: '0', trend: '--', trendClass: '' },
  { label: '参与设备数', value: '0', trend: '--', trendClass: '' }
])

const activityList = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 创建活动对话框
const dialogVisible = ref(false)
const formRef = ref(null)
const createForm = reactive({
  activityName: '',
  activityType: 'v2g',
  startTime: null,
  endTime: null,
  targetPower: 1000,
  targetEnergy: 5000,
  description: ''
})

const rules = {
  activityName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  activityType: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  targetPower: [{ required: true, message: '请输入目标功率', trigger: 'blur' }],
  targetEnergy: [{ required: true, message: '请输入目标电量', trigger: 'blur' }]
}

// 详情对话框
const detailDialogVisible = ref(false)
const currentActivity = ref(null)

const statusType = (status) => {
  const map = { '0': 'info', '1': 'success', '2': 'info' }
  return map[status] || ''
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const statusText = (status) => {
  const map = { '0': '未开始', '1': '进行中', '2': '已结束' }
  return map[status] || ''
}

const fetchData = async () => {
  loading.value = true
  try {
    const [listRes, statsRes] = await Promise.all([
      getV2gActivityList({
        ...queryForm,
        page: pagination.currentPage,
        pageSize: pagination.pageSize
      }),
      getV2gActivityStats()
    ])
    if (listRes.data?.records) {
      activityList.value.splice(0, activityList.value.length, ...listRes.data.records)
      pagination.total = listRes.data.total
    }
    if (statsRes.data) {
      stats.value[0].value = statsRes.data.activeActivities || 0
      stats.value[1].value = statsRes.data.participatingOperators || 0
      stats.value[2].value = Math.round(statsRes.data.totalResponseEnergy || 0).toLocaleString()
      stats.value[3].value = statsRes.data.participatingDevices || 0
    }
  } catch (e) {
    console.error('获取数据失败', e)
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  // 重置表单
  createForm.activityName = ''
  createForm.activityType = 'v2g'
  createForm.startTime = null
  createForm.endTime = null
  createForm.targetPower = 1000
  createForm.targetEnergy = 5000
  createForm.description = ''
  dialogVisible.value = true
}

const submitCreate = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res = await createV2gActivity({
          ...createForm,
          startTime: createForm.startTime ? new Date(createForm.startTime).toISOString() : null,
          endTime: createForm.endTime ? new Date(createForm.endTime).toISOString() : null
        })
        if (res.code === 200) {
          ElMessage.success('创建活动成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(res.message || '创建失败')
        }
      } catch (e) {
        console.error('创建活动失败', e)
        ElMessage.error('创建活动失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDetail = (row) => {
  currentActivity.value = row
  detailDialogVisible.value = true
}

const handleStart = (row) => {
  ElMessage.success(`启动活动: ${row.activityName}`)
}

const handleStop = (row) => {
  ElMessage.success(`结束活动: ${row.activityName}`)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.stat-card {
  h4 {
    font-size: 13px;
    color: var(--color-text-secondary);
    margin: 0 0 8px;
  }
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 6px;
}

.stat-trend {
  font-size: 12px;
  margin: 0;
}
</style>
