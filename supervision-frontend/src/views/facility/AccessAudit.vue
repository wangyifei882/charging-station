<template>
  <div class="access-audit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>接入审核</span>
          <el-radio-group v-model="statusFilter" size="small">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="0">待审核</el-radio-button>
            <el-radio-button label="1">已通过</el-radio-button>
            <el-radio-button label="2">已驳回</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="applicationList" v-loading="loading" stripe>
        <el-table-column prop="applicationNo" label="申请编号" width="140" />
        <el-table-column prop="operatorName" label="运营商" width="120" />
        <el-table-column prop="stationName" label="站点名称" min-width="150" />
        <el-table-column prop="submitTime" label="提交时间" width="160" />
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
            <el-button v-if="row.status === '0'" type="success" link @click="handleAudit(row, 1)">通过</el-button>
            <el-button v-if="row.status === '0'" type="danger" link @click="handleAudit(row, 2)">驳回</el-button>
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

    <el-dialog v-model="detailDialogVisible" title="申请详情" width="700px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="申请编号" :span="2">{{ detailData.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="运营商">{{ detailData.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="statusType(detailData.status)" size="small">{{ statusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="站点名称">{{ detailData.stationName }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailData.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="站点地址" :span="2">{{ detailData.stationAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detailData.contactPerson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备总数">{{ detailData.totalDevices || 0 }} 台</el-descriptions-item>
        <el-descriptions-item label="直流快充">{{ detailData.dcFastCount || 0 }} 台</el-descriptions-item>
        <el-descriptions-item label="交流慢充">{{ detailData.acSlowCount || 0 }} 台</el-descriptions-item>
        <el-descriptions-item label="V2G设备">{{ detailData.v2gCount || 0 }} 台</el-descriptions-item>
        <el-descriptions-item label="设备摘要" :span="2">{{ detailData.deviceSummary || '-' }}</el-descriptions-item>
        <template v-if="detailData.status !== '0'">
          <el-descriptions-item label="审核意见" :span="2">{{ detailData.auditOpinion || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核时间" :span="2">{{ detailData.auditTime || '-' }}</el-descriptions-item>
        </template>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="auditDialogVisible" title="审核接入申请" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="申请编号">
          <span>{{ currentApplication.applicationNo }}</span>
        </el-form-item>
        <el-form-item label="运营商">
          <span>{{ currentApplication.operatorName }}</span>
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.auditResult">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="auditForm.auditOpinion" type="textarea" :rows="4" placeholder="请输入审核意见" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAccessApplicationList, auditAccessApplication } from '../../api/facility'

const loading = ref(false)
const statusFilter = ref('')
const auditDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const detailData = ref(null)
const currentApplication = reactive({})
const auditForm = reactive({
  auditResult: 1,
  auditOpinion: ''
})

const applicationList = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const statusType = (status) => {
  const map = { '0': 'warning', '1': 'success', '2': 'danger' }
  return map[status] || 'info'
}

const statusText = (status) => {
  const map = { '0': '待审核', '1': '已通过', '2': '已驳回' }
  return map[status] || '未知'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAccessApplicationList({
      status: statusFilter.value,
      page: pagination.currentPage,
      pageSize: pagination.pageSize
    })
    if (res.data?.records) {
      applicationList.value.splice(0, applicationList.value.length, ...res.data.records)
      pagination.total = res.data.total
    }
  } catch (e) {
    console.error('获取数据失败', e)
  } finally {
    loading.value = false
  }
}

watch(statusFilter, () => {
  pagination.currentPage = 1
  fetchData()
})

watch(() => pagination.currentPage, fetchData)
watch(() => pagination.pageSize, () => {
  pagination.currentPage = 1
  fetchData()
})

const handleDetail = (row) => {
  detailData.value = row
  detailDialogVisible.value = true
}

const handleAudit = (row, result) => {
  Object.assign(currentApplication, row)
  auditForm.auditResult = result
  auditForm.auditOpinion = ''
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  try {
    await auditAccessApplication(currentApplication.id, auditForm)
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('审核失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
</style>
