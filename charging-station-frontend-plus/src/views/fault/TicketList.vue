<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">故障申报</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon :size="14"><Plus /></el-icon>
        故障申报
      </el-button>
    </div>

    <div class="filter-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="工单状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 140px">
            <el-option label="全部" :value="null" />
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已关闭" :value="3" />
            <el-option label="已驳回" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-panel">
      <el-table :data="ticketList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="ticketNo" label="工单编号" min-width="180">
          <template #default="{ row }">
            <span class="cell-code">{{ row.ticketNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deviceCode" label="设备ID" width="130">
          <template #default="{ row }">
            <span class="cell-code accent">{{ row.deviceCode || `设备${row.deviceId}` }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="handlerCompany" label="处理厂家" width="120" />
        <el-table-column prop="expectedFinishTime" label="预计完成时间" width="160" />
        <el-table-column label="工单状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" size="small" type="warning" link @click="handleProcess(row)">处理</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" link @click="handleComplete(row)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer">
        <span class="table-count">共 {{ pagination.total }} 条</span>
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          background
          layout="total, sizes, prev, pager, next"
          @size-change="handleSearch"
          @current-change="handleSearch"
          small
        />
      </div>
    </div>

    <!-- 工单详情对话框 -->
    <el-dialog v-model="detailVisible" title="工单详情" width="520px">
      <div class="detail-grid" v-if="currentTicket.id">
        <div class="detail-row"><span class="d-label">工单编号</span><span class="d-value code">{{ currentTicket.ticketNo }}</span></div>
        <div class="detail-row"><span class="d-label">设备编号</span><span class="d-value code accent">{{ currentTicket.deviceCode || `设备${currentTicket.deviceId}` }}</span></div>
        <div class="detail-row"><span class="d-label">故障描述</span><span class="d-value">{{ currentTicket.faultDescription || '暂无' }}</span></div>
        <div class="detail-row"><span class="d-label">处理厂家</span><span class="d-value">{{ currentTicket.handlerCompany || '-' }}</span></div>
        <div class="detail-row"><span class="d-label">预计完成</span><span class="d-value">{{ currentTicket.expectedFinishTime || '-' }}</span></div>
        <div class="detail-row"><span class="d-label">当前状态</span><el-tag size="small" :type="statusTag(currentTicket.status)">{{ statusText(currentTicket.status) }}</el-tag></div>
      </div>
      <template #footer><el-button @click="detailVisible = false">关闭</el-button></template>
    </el-dialog>

    <el-dialog v-model="addVisible" title="故障申报" width="520px">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="设备ID"><el-input v-model="addForm.deviceId" placeholder="请输入设备ID" /></el-form-item>
        <el-form-item label="故障描述">
          <el-input v-model="addForm.faultDescription" type="textarea" :rows="4" placeholder="请描述故障现象" />
        </el-form-item>
        <el-form-item label="预计完成时间">
          <el-date-picker v-model="addForm.expectedFinishTime" type="datetime" placeholder="选择时间" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTicketList, createTicket, updateTicketStatus, completeTicket } from '@/api'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const ticketList = ref([])
const addVisible = ref(false)
const detailVisible = ref(false)
const currentTicket = ref({})
const searchForm = reactive({ status: null })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const addForm = reactive({ deviceId: '', faultDescription: '', expectedFinishTime: '' })

const statusText = (s) => ({ 0: '待处理', 1: '处理中', 2: '已完成', 3: '已关闭', 4: '已驳回' }[s] || '未知')
const statusTag = (s) => s === 0 ? 'danger' : s === 1 ? 'warning' : s === 2 ? 'success' : 'info'

const fetchTickets = async () => {
  loading.value = true
  try {
    const res = await getTicketList({ ...searchForm, page: pagination.page, size: pagination.size })
    ticketList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) { ticketList.value = []; pagination.total = 0 }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; fetchTickets() }
const handleReset = () => { Object.assign(searchForm, { status: null }); handleSearch() }
const handleAdd = () => { Object.assign(addForm, { deviceId: '', faultDescription: '', expectedFinishTime: '' }); addVisible.value = true }
const handleDetail = (row) => { currentTicket.value = { ...row }; detailVisible.value = true }
const handleProcess = async (row) => {
  try { await updateTicketStatus(row.id, 1); ElMessage.success('已标记为处理中'); fetchTickets() }
  catch (e) { ElMessage.error('操作失败') }
}
const handleComplete = async (row) => {
  try { await completeTicket(row.id, { solution: '已处理完成', attachmentUrls: '' }); ElMessage.success('工单已完成'); fetchTickets() }
  catch (e) { ElMessage.error('操作失败') }
}
const submitAdd = async () => {
  try { await createTicket({ ...addForm, reporterId: 1, stationId: 1 }); ElMessage.success('故障申报成功'); addVisible.value = false; fetchTickets() }
  catch (e) { ElMessage.error('申报失败') }
}

onMounted(fetchTickets)
</script>

<style lang="scss" scoped>
.page { display: flex; flex-direction: column; gap: 14px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 18px; font-weight: 600; color: var(--color-text); margin: 0; }

.filter-bar {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 14px 20px;
  :deep(.el-form-item) { margin-bottom: 0; }
}

.table-panel {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-top: 1px solid var(--color-border-light);
}
.table-count { font-size: 12px; color: var(--color-text-muted); }

.cell-code { font-weight: 600; color: var(--color-text); font-variant-numeric: tabular-nums; }
.cell-code.accent { color: var(--color-primary); }

.detail-grid { display: flex; flex-direction: column; gap: 14px; }
.detail-row { display: flex; align-items: center; gap: 12px; }
.d-label { width: 72px; font-size: 13px; color: var(--color-text-muted); flex-shrink: 0; }
.d-value { font-size: 14px; color: var(--color-text); }
.d-value.code { font-variant-numeric: tabular-nums; font-weight: 600; }
.d-value.accent { color: var(--color-primary); }
</style>
