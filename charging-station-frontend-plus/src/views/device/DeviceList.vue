<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">设备管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon :size="14"><Plus /></el-icon>
        新增设备
      </el-button>
    </div>

    <div class="filter-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="设备编号">
          <el-input v-model="filterForm.deviceCode" placeholder="请输入设备编号" :prefix-icon="Search" style="width: 200px" />
        </el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" style="width: 140px">
            <el-option label="全部状态" value="" />
            <el-option label="在线" :value="1" />
            <el-option label="离线" :value="0" />
            <el-option label="故障" :value="2" />
            <el-option label="维护中" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-panel">
      <el-table :data="deviceList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="deviceCode" label="设备编号" min-width="110">
          <template #default="{ row }">
            <span class="cell-code">{{ row.deviceCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deviceName" label="设备名称" min-width="130">
          <template #default="{ row }">
            <span class="cell-text">{{ row.deviceName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="areaCode" label="区域" width="60" />
        <el-table-column prop="position" label="位置" min-width="110" />
        <el-table-column prop="manufacturer" label="厂家" width="80" />
        <el-table-column prop="powerRating" label="额定功率(kW)" width="110" align="right">
          <template #default="{ row }">
            <span class="cell-number">{{ row.powerRating }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="realTimePower" label="实时功率(kW)" width="110" align="right">
          <template #default="{ row }">
            <span class="cell-number accent">{{ row.realTimePower || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <span class="status-tag" :class="statusClass(row.status)">
              <span class="status-dot"></span>
              {{ statusText(row.status) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button size="small" type="warning" link @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 设备详情对话框 -->
    <el-dialog v-model="detailVisible" title="设备详情" width="520px">
      <div class="detail-grid" v-if="currentDevice.id || currentDevice.deviceCode">
        <div class="d-row"><span class="d-label">设备编号</span><span class="d-value code">{{ currentDevice.deviceCode }}</span></div>
        <div class="d-row"><span class="d-label">设备名称</span><span class="d-value">{{ currentDevice.deviceName }}</span></div>
        <div class="d-row"><span class="d-label">所属区域</span><span class="d-value">{{ currentDevice.areaCode || '-' }}</span></div>
        <div class="d-row"><span class="d-label">位置</span><span class="d-value">{{ currentDevice.position || '-' }}</span></div>
        <div class="d-row"><span class="d-label">厂家</span><span class="d-value">{{ currentDevice.manufacturer || '-' }}</span></div>
        <div class="d-row"><span class="d-label">额定功率</span><span class="d-value bold">{{ currentDevice.powerRating || 0 }} kW</span></div>
        <div class="d-row"><span class="d-label">实时功率</span><span class="d-value bold accent">{{ currentDevice.realTimePower || 0 }} kW</span></div>
        <div class="d-row"><span class="d-label">状态</span><span class="status-tag" :class="statusClass(currentDevice.status)"><span class="status-dot"></span>{{ statusText(currentDevice.status) }}</span></div>
      </div>
      <template #footer><el-button @click="detailVisible = false">关闭</el-button></template>
    </el-dialog>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="设备编号"><el-input v-model="form.deviceCode" /></el-form-item>
        <el-form-item label="设备名称"><el-input v-model="form.deviceName" /></el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="form.typeId" style="width: 100%">
            <el-option label="7kW交流桩" :value="1" />
            <el-option label="11kW交流桩" :value="2" />
            <el-option label="60kW直流桩" :value="3" />
            <el-option label="120kW直流桩" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域"><el-input v-model="form.areaCode" /></el-form-item>
        <el-form-item label="位置"><el-input v-model="form.position" /></el-form-item>
        <el-form-item label="厂家"><el-input v-model="form.manufacturer" /></el-form-item>
        <el-form-item label="额定功率(kW)"><el-input-number v-model="form.powerRating" :min="0" style="width: 100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getDeviceList, getDeviceAll, addDevice, updateDevice, deleteDevice } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const loading = ref(false)
const filterForm = reactive({ deviceCode: '', status: '' })
const deviceList = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const dialogVisible = ref(false)
const dialogTitle = ref('新增设备')
const detailVisible = ref(false)
const currentDevice = ref({})
const form = reactive({ deviceCode: '', deviceName: '', typeId: null, areaCode: '', position: '', manufacturer: '', powerRating: 0 })

const statusText = (s) => ({ 0: '离线', 1: '在线', 2: '故障', 3: '维护中' }[s] || '未知')
const statusClass = (s) => ({ 0: 'offline', 1: 'online', 2: 'fault', 3: 'maintenance' }[s] || 'offline')

const fetchDevices = async () => {
  loading.value = true
  try {
    const res = await getDeviceList({ ...filterForm, page: pagination.page, size: pagination.size })
    deviceList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) {
    try {
      const res = await getDeviceAll()
      deviceList.value = res.data || []
      pagination.total = deviceList.value.length
    } catch (e2) {
      deviceList.value = [
        { deviceCode: 'AC-001', deviceName: '交流桩001', areaCode: 'A', position: 'A区-01号', manufacturer: 'XX科技', powerRating: 7, realTimePower: 0, status: 1 },
        { deviceCode: 'AC-002', deviceName: '交流桩002', areaCode: 'A', position: 'A区-02号', manufacturer: 'XX科技', powerRating: 7, realTimePower: 0, status: 1 },
        { deviceCode: 'AC-003', deviceName: '交流桩003', areaCode: 'A', position: 'A区-03号', manufacturer: 'XX科技', powerRating: 11, realTimePower: 0, status: 1 },
        { deviceCode: 'DC-001', deviceName: '直流桩001', areaCode: 'B', position: 'B区-01号', manufacturer: 'YY电气', powerRating: 60, realTimePower: 0, status: 1 },
        { deviceCode: 'DC-002', deviceName: '直流桩002', areaCode: 'B', position: 'B区-02号', manufacturer: 'YY电气', powerRating: 60, realTimePower: 0, status: 1 },
        { deviceCode: 'DC-003', deviceName: '直流桩003', areaCode: 'B', position: 'B区-03号', manufacturer: 'YY电气', powerRating: 120, realTimePower: 0, status: 1 }
      ]
      pagination.total = 6
    }
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; fetchDevices() }
const handleReset = () => { Object.assign(filterForm, { deviceCode: '', status: '' }); handleSearch() }
const handleAdd = () => { dialogTitle.value = '新增设备'; Object.assign(form, { deviceCode: '', deviceName: '', typeId: null, areaCode: '', position: '', manufacturer: '', powerRating: 0 }); dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑设备'; Object.assign(form, { ...row }); dialogVisible.value = true }
const handleDetail = (row) => { currentDevice.value = { ...row }; detailVisible.value = true }
const handleDelete = async (row) => {
  try { await ElMessageBox.confirm('确定删除设备 ' + row.deviceCode + '？', '提示'); await deleteDevice(row.id); ElMessage.success('删除成功'); fetchDevices() }
  catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}
const submitForm = async () => {
  try { if (form.id) { await updateDevice(form.id, form) } else { await addDevice(form) }; ElMessage.success('保存成功'); dialogVisible.value = false; fetchDevices() }
  catch (e) { ElMessage.error('保存失败') }
}

onMounted(fetchDevices)
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
.cell-text { font-weight: 500; color: var(--color-text); }
.cell-number { font-variant-numeric: tabular-nums; color: var(--color-text); }
.cell-number.accent { color: var(--color-primary); font-weight: 600; }

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  font-weight: 500;
}
.status-dot { width: 6px; height: 6px; border-radius: 50%; }
.status-tag.online { color: var(--color-success); .status-dot { background: var(--color-success); } }
.status-tag.offline { color: var(--color-text-muted); .status-dot { background: var(--color-text-muted); } }
.status-tag.fault { color: var(--color-danger); .status-dot { background: var(--color-danger); } }
.status-tag.maintenance { color: var(--color-warning); .status-dot { background: var(--color-warning); } }

.detail-grid { display: flex; flex-direction: column; gap: 14px; }
.d-row { display: flex; align-items: center; gap: 12px; }
.d-label { width: 72px; font-size: 13px; color: var(--color-text-muted); flex-shrink: 0; }
.d-value { font-size: 14px; color: var(--color-text); }
.d-value.code { font-variant-numeric: tabular-nums; font-weight: 600; }
.d-value.bold { font-weight: 600; }
.d-value.accent { color: var(--color-primary); }
</style>
