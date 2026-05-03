<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">充电运营</h2>
    </div>

    <div class="filter-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" :prefix-icon="Search" style="width: 200px" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" style="width: 140px">
            <el-option label="全部状态" value="" />
            <el-option label="充电中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-panel">
      <el-table :data="orderList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" min-width="160">
          <template #default="{ row }">
            <span class="cell-code">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deviceId" label="设备ID" width="100" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">
            <span class="cell-number">{{ formatDateTime(row.startTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">
            <span class="cell-number">{{ formatDateTime(row.endTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">
            <span :class="isCharging(row) ? 'tag-charge' : 'tag-discharge'">
              {{ isCharging(row) ? '充电' : '放电' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="totalEnergy" label="电量(kWh)" width="120" align="right">
          <template #default="{ row }">
            <span class="cell-number" :class="isCharging(row) ? 'positive' : 'negative'">
              {{ row.totalEnergy > 0 ? '+' : '' }}{{ row.totalEnergy || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="actualFee" label="金额(元)" width="110" align="right">
          <template #default="{ row }">
            <span class="cell-number bold" :class="isCharging(row) ? 'positive' : 'negative'">
              {{ row.actualFee > 0 ? '+' : '' }}{{ row.actualFee || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <span :class="statusClass(row.status)">{{ statusText(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 2 && isCharging(row)" size="small" type="warning" link @click="handleRefund(row)">退款</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer">
        <span class="table-count">共 {{ pagination.total }} 条</span>
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          background
          layout="prev, pager, next"
          :page-sizes="[10, 20, 50]"
          small
          @current-change="fetchOrders"
          @size-change="fetchOrders"
        />
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="520px">
      <div class="detail-grid" v-if="currentOrder.id">
        <div class="detail-row"><span class="d-label">订单号</span><span class="d-value code">{{ currentOrder.orderNo }}</span></div>
        <div class="detail-row"><span class="d-label">设备ID</span><span class="d-value">{{ currentOrder.deviceId }}</span></div>
        <div class="detail-row"><span class="d-label">用户ID</span><span class="d-value">{{ currentOrder.userId }}</span></div>
        <div class="detail-row"><span class="d-label">开始时间</span><span class="d-value">{{ formatDateTime(currentOrder.startTime) }}</span></div>
        <div class="detail-row"><span class="d-label">结束时间</span><span class="d-value">{{ formatDateTime(currentOrder.endTime) }}</span></div>
        <div class="detail-row"><span class="d-label">充电电量</span><span class="d-value bold">{{ currentOrder.totalEnergy || 0 }} kWh</span></div>
        <div class="detail-row"><span class="d-label">实际费用</span><span class="d-value bold price">{{ currentOrder.actualFee || 0 }} 元</span></div>
        <div class="detail-row"><span class="d-label">状态</span><el-tag size="small" :type="currentOrder.status === 2 ? 'success' : currentOrder.status === 1 ? 'warning' : 'info'">{{ statusText(currentOrder.status) }}</el-tag></div>
      </div>
      <template #footer><el-button @click="detailVisible = false">关闭</el-button></template>
    </el-dialog>

    <!-- 退款对话框 -->
    <el-dialog v-model="refundVisible" title="退款处理" width="460px">
      <el-form :model="refundForm" label-width="80px">
        <el-form-item label="退款金额"><el-input-number v-model="refundForm.amount" :min="0" :precision="2" style="width: 100%" /></el-form-item>
        <el-form-item label="退款原因"><el-input v-model="refundForm.reason" type="textarea" :rows="3" placeholder="请输入退款原因" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="refundVisible = false">取消</el-button><el-button type="primary" @click="submitRefund">确认退款</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrderList } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const loading = ref(false)
const searchForm = reactive({ orderNo: '', status: '' })
const orderList = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const detailVisible = ref(false)
const currentOrder = ref({})
const refundVisible = ref(false)
const refundForm = reactive({ reason: '', amount: 0 })

const statusText = (s) => ({ 1: '充电中', 2: '已完成', 3: '已取消' }[s] || '未知')
const statusClass = (s) => s === 1 ? 'text-primary' : s === 2 ? 'text-success' : 'text-muted'

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 16)
}

const isCharging = (row) => {
  if (row.totalEnergy !== undefined && row.totalEnergy !== null) return row.totalEnergy >= 0
  if (row.actualFee !== undefined && row.actualFee !== null) return row.actualFee >= 0
  return true
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderList({ ...searchForm, page: pagination.page, size: pagination.size })
    orderList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) { orderList.value = []; pagination.total = 0 }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; fetchOrders() }
const handleReset = () => { Object.assign(searchForm, { orderNo: '', status: '' }); handleSearch() }
const handleDetail = (row) => { currentOrder.value = { ...row }; detailVisible.value = true }
const handleRefund = (row) => {
  currentOrder.value = { ...row }
  refundForm.reason = ''
  refundForm.amount = row.actualFee || 0
  refundVisible.value = true
}
const submitRefund = async () => {
  if (!refundForm.reason.trim()) { ElMessage.warning('请输入退款原因'); return }
  try {
    await ElMessageBox.confirm(`确认退款 ¥${refundForm.amount}？`, '退款确认', { type: 'warning' })
    ElMessage.success('退款处理成功')
    refundVisible.value = false
    fetchOrders()
  } catch (e) { /* cancelled */ }
}

onMounted(fetchOrders)
</script>

<style lang="scss" scoped>
.page { display: flex; flex-direction: column; gap: 14px; }
.page-header .page-title { font-size: 18px; font-weight: 600; color: var(--color-text); margin: 0; }

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
.cell-number { font-variant-numeric: tabular-nums; color: var(--color-text); }
.cell-number.bold { font-weight: 600; }
.positive { color: var(--color-success); }
.negative { color: var(--color-warning); }

.text-primary { color: var(--color-primary); font-weight: 500; }
.text-success { color: var(--color-success); font-weight: 500; }
.text-muted { color: var(--color-text-muted); }

.tag-charge {
  display: inline-block;
  padding: 2px 8px;
  background: var(--color-success-bg);
  color: var(--color-success);
  border-radius: 3px;
  font-size: 12px;
  font-weight: 500;
}
.tag-discharge {
  display: inline-block;
  padding: 2px 8px;
  background: var(--color-warning-bg);
  color: var(--color-warning);
  border-radius: 3px;
  font-size: 12px;
  font-weight: 500;
}

.detail-grid { display: flex; flex-direction: column; gap: 14px; }
.detail-row { display: flex; align-items: center; gap: 12px; }
.d-label { width: 72px; font-size: 13px; color: var(--color-text-muted); flex-shrink: 0; }
.d-value { font-size: 14px; color: var(--color-text); }
.d-value.code { font-variant-numeric: tabular-nums; font-weight: 600; }
.d-value.bold { font-weight: 600; }
.d-value.price { color: var(--color-primary); font-size: 16px; }
</style>
