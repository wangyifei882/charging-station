<template>
  <div class="status-monitor">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>运行状态监控</span>
          <el-button type="primary" @click="refresh">刷新</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="运营商">
          <el-select v-model="queryForm.operatorId" placeholder="全部" clearable style="width: 140px">
            <el-option label="南方电网" :value="1" />
            <el-option label="特来电" :value="2" />
            <el-option label="星星充电" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryForm.regionCode" placeholder="全部" clearable style="width: 140px">
            <el-option label="深圳市" value="440300" />
            <el-option label="南山区" value="440305" />
            <el-option label="福田区" value="440304" />
          </el-select>
        </el-form-item>
        <el-form-item label="运行状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="在线" :value="1" />
            <el-option label="离线" :value="2" />
            <el-option label="故障" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障类型">
          <el-select v-model="queryForm.faultType" placeholder="全部" clearable style="width: 140px">
            <el-option label="通信故障" :value="1" />
            <el-option label="电源故障" :value="2" />
            <el-option label="硬件故障" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="deviceList" stripe v-loading="loading">
        <el-table-column prop="deviceCode" label="设备编号" width="150"></el-table-column>
        <el-table-column prop="stationName" label="所属站点" width="180"></el-table-column>
        <el-table-column prop="operatorName" label="所属运营商" width="140"></el-table-column>
        <el-table-column prop="deviceType" label="设备类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.deviceType === 1 ? 'primary' : 'success'">
              {{ row.deviceType === 1 ? '充电桩' : 'V2G设备' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="运行状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'danger'" size="small">
              {{ row.status === 1 ? '在线' : row.status === 2 ? '离线' : '故障' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="faultType" label="故障类型" width="120" v-if="queryForm.status === 3">
          <template #default="{ row }">
            <el-tag :type="row.faultType === 1 ? 'danger' : row.faultType === 2 ? 'warning' : 'info'" size="small">
              {{ row.faultType === 1 ? '通信故障' : row.faultType === 2 ? '电源故障' : '硬件故障' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="faultTime" label="故障时间" width="180" v-if="queryForm.status === 3"></el-table-column>
        <el-table-column prop="lastCommTime" label="最后通信时间" width="180"></el-table-column>
        <el-table-column prop="currentPower" label="当前功率(kW)" width="140" align="right"></el-table-column>
        <el-table-column prop="todayCharge" label="今日充电(kWh)" width="140" align="right"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button type="warning" link size="small" v-if="row.status === 3" @click="handleRectify(row)">
              下发整改
            </el-button>
            <el-button type="danger" link size="small" v-if="row.status === 3" @click="handleRepair(row)">
              报修
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </el-card>

    <!-- 整改通知对话框 -->
    <el-dialog v-model="rectifyDialogVisible" title="下发整改通知" width="500px">
      <el-form :model="rectifyForm" label-width="100px">
        <el-form-item label="设备编号">
          <span>{{ currentDevice.deviceCode }}</span>
        </el-form-item>
        <el-form-item label="设备名称">
          <span>{{ currentDevice.deviceName }}</span>
        </el-form-item>
        <el-form-item label="整改类型">
          <el-select v-model="rectifyForm.rectifyType" placeholder="请选择" style="width: 100%">
            <el-option label="设备故障修复" :value="1" />
            <el-option label="安全隐患整改" :value="2" />
            <el-option label="通信问题修复" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="整改要求">
          <el-input v-model="rectifyForm.rectifyContent" type="textarea" :rows="4" placeholder="请输入整改要求" />
        </el-form-item>
        <el-form-item label="整改期限">
          <el-date-picker
            v-model="rectifyForm.deadline"
            type="datetime"
            placeholder="请选择整改期限"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rectifyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRectify">确定下发</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStatusMonitor } from '../../api/facility'

const loading = ref(false)
const queryForm = reactive({
  operatorId: null,
  regionCode: null,
  status: null,
  faultType: null
})

const deviceList = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const rectifyDialogVisible = ref(false)
const currentDevice = reactive({})
const rectifyForm = reactive({
  rectifyType: null,
  rectifyContent: '',
  deadline: null
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getStatusMonitor({
      ...queryForm,
      page: pagination.currentPage,
      pageSize: pagination.pageSize
    })
    if (res.data?.records) {
      deviceList.value.splice(0, deviceList.value.length, ...res.data.records)
      pagination.total = res.data.total
    } else {
      // 模拟数据
      deviceList.value = [
        {
          deviceCode: 'DEV001',
          stationName: '南山区科技园充电站',
          operatorName: '南方电网',
          deviceType: 1,
          status: 1,
          lastCommTime: '2025-01-20 15:30:00',
          currentPower: '45.2',
          todayCharge: '389.5'
        },
        {
          deviceCode: 'DEV002',
          stationName: '南山区科技园充电站',
          operatorName: '南方电网',
          deviceType: 1,
          status: 3,
          faultType: 1,
          faultTime: '2025-01-20 10:15:00',
          lastCommTime: '2025-01-20 10:15:00',
          currentPower: '0',
          todayCharge: '156.2'
        }
      ]
      pagination.total = 2
    }
  } catch (e) {
    console.error('获取设备状态失败', e)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.operatorId = null
  queryForm.regionCode = null
  queryForm.status = null
  queryForm.faultType = null
  fetchData()
}

function handleDetail(row) {
  ElMessage.info(`查看设备详情: ${row.deviceCode}`)
}

function handleRectify(row) {
  Object.assign(currentDevice, row)
  rectifyForm.rectifyType = null
  rectifyForm.rectifyContent = ''
  rectifyForm.deadline = null
  rectifyDialogVisible.value = true
}

function handleRepair(row) {
  ElMessageBox.confirm('确认要报修该设备吗?', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('报修成功')
  })
}

function submitRectify() {
  if (!rectifyForm.rectifyType) {
    ElMessage.warning('请选择整改类型')
    return
  }
  if (!rectifyForm.rectifyContent) {
    ElMessage.warning('请输入整改要求')
    return
  }
  ElMessage.success('整改通知下发成功')
  rectifyDialogVisible.value = false
}

function refresh() {
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
</style>
