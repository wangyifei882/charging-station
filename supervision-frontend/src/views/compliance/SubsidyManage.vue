<template>
  <div class="subsidy-manage">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="补贴申报" name="applications">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>补贴申请列表</span>
              <el-radio-group v-model="auditStatusFilter" size="small">
                <el-radio-button label="">全部</el-radio-button>
                <el-radio-button label="0">待审核</el-radio-button>
                <el-radio-button label="1">已通过</el-radio-button>
                <el-radio-button label="2">已驳回</el-radio-button>
              </el-radio-group>
            </div>
          </template>

          <el-table :data="subsidyList" stripe>
            <el-table-column prop="applicationNo" label="申请编号" width="140" />
            <el-table-column prop="operatorName" label="运营商" width="120" />
            <el-table-column prop="subsidyType" label="补贴类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.subsidyType === 'construction' ? 'primary' : 'success'" size="small">
                  {{ row.subsidyType === 'construction' ? '建设补贴' : '运营补贴' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applyAmount" label="申请金额(元)" width="120" align="right" />
            <el-table-column prop="auditStatus" label="审核状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="auditStatusType(row.auditStatus)" size="small">
                  {{ auditStatusText(row.auditStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="currentStage" label="当前阶段" width="100" align="center">
              <template #default="{ row }">
                {{ row.currentStage === 1 ? '初审' : row.currentStage === 2 ? '复审' : '终审' }}
              </template>
            </el-table-column>
            <el-table-column prop="applyTime" label="申请时间" width="160" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
                <el-button v-if="row.auditStatus === '0'" type="success" link @click="handleAudit(row, 1)">通过</el-button>
                <el-button v-if="row.auditStatus === '0'" type="danger" link @click="handleAudit(row, 2)">驳回</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="补贴发放" name="records">
        <el-card>
          <template #header><span>补贴发放记录</span></template>
          <el-table :data="subsidyRecords" stripe>
            <el-table-column prop="subsidyNo" label="补贴编号" width="140" />
            <el-table-column prop="operatorName" label="运营商" width="120" />
            <el-table-column prop="subsidyType" label="补贴类型" width="100" />
            <el-table-column prop="approveAmount" label="批准金额(元)" width="120" align="right" />
            <el-table-column prop="actualAmount" label="实发金额(元)" width="120" align="right" />
            <el-table-column prop="issueTime" label="发放时间" width="160" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 'issued' ? 'success' : 'warning'" size="small">
                  {{ row.status === 'issued' ? '已发放' : '发放中' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="detailDialogVisible" title="补贴申请详情" width="650px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="申请编号">{{ detailData.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="运营商">{{ detailData.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="补贴类型">
          <el-tag :type="detailData.subsidyType === 'construction' ? 'primary' : 'success'" size="small">
            {{ detailData.subsidyType === 'construction' ? '建设补贴' : '运营补贴' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="auditStatusType(detailData.auditStatus)" size="small">{{ auditStatusText(detailData.auditStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请金额">{{ detailData.applyAmount }} 元</el-descriptions-item>
        <el-descriptions-item label="当前阶段">{{ detailData.currentStage === 1 ? '初审' : detailData.currentStage === 2 ? '复审' : '终审' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ detailData.applyTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="auditDialogVisible" title="补贴审核" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="申请编号">
          <span>{{ currentSubsidy.applicationNo }}</span>
        </el-form-item>
        <el-form-item label="审核阶段">
          <el-select v-model="auditForm.auditStage">
            <el-option label="初审" :value="1" />
            <el-option label="复审" :value="2" />
            <el-option label="终审" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.auditResult">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="auditForm.auditOpinion" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getSubsidyApplications, auditSubsidy } from '../../api/compliance'

const activeTab = ref('applications')
const auditStatusFilter = ref('')
const auditDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const detailData = ref(null)
const currentSubsidy = reactive({})
const auditForm = reactive({
  auditStage: 1,
  auditResult: 1,
  auditOpinion: ''
})

const subsidyList = ref([])

const subsidyRecords = ref([])

const fetchSubsidyData = async () => {
  try {
    const res = await getSubsidyApplications({
      page: 1,
      size: 100,
      auditStatus: auditStatusFilter.value || null
    })
    if (res.data?.records) {
      subsidyList.value = res.data.records.map(s => ({
        id: s.id,
        applicationNo: s.applicationNo,
        operatorName: s.operatorName,
        subsidyType: s.subsidyType === 1 ? 'construction' : 'operation',
        applyAmount: s.applyAmount?.toFixed(2) || '0',
        auditStatus: s.auditStatus?.toString() || '0',
        currentStage: s.currentAuditStage || 1,
        applyTime: s.applyTime
      }))
    }
  } catch (e) {
    console.error('获取补贴数据失败', e)
  }
}

const fetchData = async () => {
  if (activeTab.value === 'applications') {
    fetchSubsidyData()
  }
}

watch(activeTab, () => {
  fetchData()
})

watch(auditStatusFilter, () => {
  if (activeTab.value === 'applications') {
    fetchSubsidyData()
  }
})

onMounted(() => {
  fetchData()
})

const auditStatusType = (status) => {
  const map = { '0': 'warning', '1': 'success', '2': 'danger' }
  return map[status] || ''
}

const auditStatusText = (status) => {
  const map = { '0': '待审核', '1': '已通过', '2': '已驳回' }
  return map[status] || ''
}

const handleDetail = (row) => {
  detailData.value = row
  detailDialogVisible.value = true
}

const handleAudit = (row, result) => {
  Object.assign(currentSubsidy, row)
  auditForm.auditResult = result
  auditForm.auditOpinion = ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  try {
    await auditSubsidy(currentSubsidy.id, {
      auditStage: auditForm.auditStage,
      auditResult: auditForm.auditResult,
      auditOpinion: auditForm.auditOpinion
    })
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    fetchSubsidyData()
  } catch (e) {
    ElMessage.error('审核失败')
  }
}
</script>

<style scoped lang="scss">
</style>
