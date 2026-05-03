<template>
  <div class="demand-response">
    <el-row :gutter="16" class="summary-row">
      <el-col :span="8">
        <el-card>
          <template #header><span>本月响应次数</span></template>
          <div class="summary-value">{{ stats.monthlyResponseCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>响应总电量(kWh)</span></template>
          <div class="summary-value">{{ Math.round(stats.totalResponseEnergy || 0).toLocaleString() }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>平均响应率</span></template>
          <div class="summary-value">{{ (stats.avgResponseRate || 0).toFixed(1) }}%</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>需求响应活动</span>
          <el-button type="primary" @click="handleCreate">发布响应</el-button>
        </div>
      </template>

      <!-- 发布响应对话框 -->
      <el-dialog v-model="createDialogVisible" title="发布需求响应" width="600px" :close-on-click-modal="false">
        <el-form :model="createForm" :rules="rules" ref="formRef" label-width="120px">
          <el-form-item label="活动名称" prop="activityName">
            <el-input v-model="createForm.activityName" placeholder="请输入活动名称" />
          </el-form-item>
          <el-form-item label="响应类型" prop="responseType">
            <el-radio-group v-model="createForm.responseType">
              <el-radio :label="1">削峰</el-radio>
              <el-radio :label="2">填谷</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker v-model="createForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
          </el-form-item>
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker v-model="createForm.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
          </el-form-item>
          <el-form-item label="目标电量(kWh)" prop="targetEnergy">
            <el-input-number v-model="createForm.targetEnergy" :min="1" :max="100000" style="width: 100%" />
          </el-form-item>
          <el-form-item label="活动说明" prop="remark">
            <el-input v-model="createForm.remark" type="textarea" :rows="3" placeholder="请输入活动说明" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCreate" :loading="submitLoading">发布</el-button>
        </template>
      </el-dialog>

      <!-- 详情对话框 -->
      <el-dialog v-model="detailDialogVisible" title="需求响应详情" width="700px">
        <el-descriptions :column="2" border v-if="currentActivity">
          <el-descriptions-item label="活动名称">{{ currentActivity.activityName }}</el-descriptions-item>
          <el-descriptions-item label="响应类型">
            <el-tag :type="currentActivity.responseType === 1 ? 'danger' : 'warning'" size="small">
              {{ currentActivity.responseType === 1 ? '削峰' : '填谷' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDateTime(currentActivity.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDateTime(currentActivity.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="目标电量">{{ currentActivity.targetEnergy }} kWh</el-descriptions-item>
          <el-descriptions-item label="实际电量">{{ currentActivity.actualEnergy }} kWh</el-descriptions-item>
          <el-descriptions-item label="完成率">
            <span :class="(currentActivity.completionRate || 0) >= 90 ? 'text-success' : 'text-warning'">{{ currentActivity.completionRate || 0 }}%</span>
          </el-descriptions-item>
          <el-descriptions-item label="参与运营商">{{ currentActivity.participatingOperatorCount || 0 }} 家</el-descriptions-item>
          <el-descriptions-item label="活动说明" :span="2">{{ currentActivity.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
        <template #footer>
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <el-table :data="responseList" stripe>
        <el-table-column prop="activityName" label="活动名称" min-width="180" />
        <el-table-column prop="responseType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.responseType === 1 ? 'danger' : 'warning'" size="small">
              {{ row.responseType === 1 ? '削峰' : '填谷' }}
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
        <el-table-column prop="targetEnergy" label="目标电量(kWh)" width="140" align="right" />
        <el-table-column prop="actualEnergy" label="实际电量(kWh)" width="140" align="right" />
        <el-table-column prop="completionRate" label="完成率" width="100" align="center">
          <template #default="{ row }">
            <span :class="(row.completionRate || 0) >= 90 ? 'text-success' : 'text-warning'">{{ row.completionRate || 0 }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="participatingOperatorCount" label="参与运营商" width="120" align="center" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 16px">
      <template #header><span>运营商参与度排名</span></template>
      <el-table :data="rankingList" stripe>
        <el-table-column prop="rank" label="排名" width="80" align="center" />
        <el-table-column prop="operatorName" label="运营商" min-width="150" />
        <el-table-column prop="responseCount" label="响应次数" width="100" align="center" />
        <el-table-column prop="totalPower" label="响应总电量(kWh)" width="140" align="right" />
        <el-table-column prop="responseRate" label="平均响应率" width="120" align="center" />
        <el-table-column prop="rewardAmount" label="奖励金额(元)" width="120" align="right" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDemandResponseList, getDemandResponseStats, getDemandResponseOperatorRanking, createDemandResponse } from '../../api/v2g'

const responseList = ref([])
const rankingList = ref([])
const stats = ref({
  monthlyResponseCount: 0,
  totalResponseEnergy: 0,
  avgResponseRate: 0
})

// 发布响应对话框
const createDialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const createForm = reactive({
  activityName: '',
  responseType: 1,
  startTime: null,
  endTime: null,
  targetEnergy: 5000,
  remark: ''
})
const rules = {
  activityName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  responseType: [{ required: true, message: '请选择响应类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  targetEnergy: [{ required: true, message: '请输入目标电量', trigger: 'blur' }]
}

// 详情对话框
const detailDialogVisible = ref(false)
const currentActivity = ref(null)

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const fetchData = async () => {
  try {
    const [listRes, statsRes, rankingRes] = await Promise.all([
      getDemandResponseList(),
      getDemandResponseStats(),
      getDemandResponseOperatorRanking()
    ])
    if (listRes.data?.records) {
      responseList.value = listRes.data.records
    }
    if (statsRes.data) {
      stats.value = statsRes.data
    }
    if (rankingRes.data) {
      rankingList.value = rankingRes.data
    }
  } catch (e) {
    console.error('获取数据失败', e)
  }
}

onMounted(() => {
  fetchData()
})

const handleCreate = () => {
  createForm.activityName = ''
  createForm.responseType = 1
  createForm.startTime = null
  createForm.endTime = null
  createForm.targetEnergy = 5000
  createForm.remark = ''
  createDialogVisible.value = true
}

const submitCreate = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res = await createDemandResponse({
          ...createForm,
          startTime: createForm.startTime ? new Date(createForm.startTime).toISOString() : null,
          endTime: createForm.endTime ? new Date(createForm.endTime).toISOString() : null
        })
        if (res.code === 200) {
          ElMessage.success('发布需求响应成功')
          createDialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(res.message || '发布失败')
        }
      } catch (e) {
        console.error('发布需求响应失败', e)
        ElMessage.error('发布需求响应失败')
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
</script>

<style scoped lang="scss">
.summary-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-text-primary);
  text-align: center;
  padding: 20px 0;
}
</style>
