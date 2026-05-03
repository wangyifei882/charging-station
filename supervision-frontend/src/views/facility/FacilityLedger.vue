<template>
  <div class="facility-ledger">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设施台账</span>
          <el-button type="primary" :icon="Plus" @click="handleExport">导出报表</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="运营商">
          <el-select v-model="queryForm.operatorId" placeholder="全部" clearable style="width: 140px" @change="handleOperatorChange">
            <el-option label="南方电网" :value="1" />
            <el-option label="特来电" :value="2" />
            <el-option label="星星充电" :value="3" />
            <el-option label="云快充" :value="4" />
            <el-option label="国家电网" :value="5" />
            <el-option label="小鹏汽车" :value="6" />
            <el-option label="蔚来汽车" :value="7" />
            <el-option label="比亚迪" :value="8" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryForm.regionCode" placeholder="全部" clearable style="width: 140px" :disabled="!queryForm.operatorId">
            <el-option
              v-for="region in regionOptions"
              :key="region.code"
              :label="region.name"
              :value="region.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="站点状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="运营中" :value="1" />
            <el-option label="建设中" :value="2" />
            <el-option label="暂停运营" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="stationList" v-loading="loading" stripe>
        <el-table-column prop="stationName" label="站点名称" min-width="150" />
        <el-table-column prop="operatorName" label="运营商" width="120" />
        <el-table-column prop="regionName" label="区域" width="100" />
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="totalDevices" label="设备总数" width="100" align="center" />
        <el-table-column prop="onlineDevices" label="在线设备" width="100" align="center" />
        <el-table-column prop="todayCharge" label="今日充电(kWh)" width="120" align="right" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'info'" size="small">
              {{ row.status === 1 ? '运营中' : row.status === 2 ? '建设中' : '暂停运营' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button type="warning" link @click="handleRectify(row)">整改</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getStationList, getRegionsByOperator } from '../../api/facility'

const router = useRouter()

const loading = ref(false)
const queryForm = reactive({
  operatorId: null,
  regionCode: null,
  status: null
})

const stationList = ref([])

const regionOptions = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const handleOperatorChange = async (value) => {
  queryForm.regionCode = null
  if (!value) {
    regionOptions.value = []
    return
  }
  try {
    const res = await getRegionsByOperator(value)
    if (res.data) {
      regionOptions.value = res.data
    }
  } catch (e) {
    console.error('获取区域列表失败', e)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStationList({
      ...queryForm,
      page: pagination.currentPage,
      pageSize: pagination.pageSize
    })
    if (res.data?.records) {
      stationList.value.splice(0, stationList.value.length, ...res.data.records)
      pagination.total = res.data.total
    }
  } catch (e) {
    console.error('获取数据失败', e)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  pagination.currentPage = 1
  fetchData()
}

const handleReset = () => {
  queryForm.operatorId = null
  queryForm.regionCode = null
  queryForm.status = null
  regionOptions.value = []
  handleQuery()
}

const handleDetail = (row) => {
  router.push({
    path: '/station-detail',
    query: { id: row.id }
  })
}

const handleRectify = (row) => {
  ElMessage.warning(`发送整改通知: ${row.stationName}`)
}

const handleExport = () => {
  // 准备导出数据
  const exportData = stationList.value.map(item => ({
    '站点名称': item.stationName,
    '运营商': item.operatorName,
    '区域': item.regionName,
    '地址': item.address,
    '设备总数': item.totalDevices,
    '在线设备': item.onlineDevices,
    '今日充电(kWh)': item.todayCharge,
    '状态': item.status === 1 ? '运营中' : item.status === 2 ? '建设中' : '暂停运营'
  }))

  if (exportData.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }

  // 转换为 CSV
  const headers = Object.keys(exportData[0])
  const csvContent = [
    headers.join(','),
    ...exportData.map(row => headers.map(h => {
      const val = row[h]
      // 处理包含逗号或换行符的值
      if (typeof val === 'string' && (val.includes(',') || val.includes('\n'))) {
        return `"${val.replace(/"/g, '""')}"`
      }
      return val
    }).join(','))
  ].join('\n')

  // 添加 BOM 以支持中文
  const BOM = '\uFEFF'
  const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' })

  // 下载文件
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.href = url
  link.download = `设施台账_${new Date().toLocaleDateString()}.csv`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)

  ElMessage.success('导出成功')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
</style>
