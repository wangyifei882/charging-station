<template>
  <div class="accident-trace">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>事故追溯</span>
          <el-button type="primary" @click="handleExport">导出报告</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="事故类型">
          <el-select v-model="queryForm.incidentType" placeholder="全部" clearable style="width: 140px">
            <el-option label="设备故障" :value="1" />
            <el-option label="安全事故" :value="2" />
            <el-option label="通信中断" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="incidentList" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="incidentNo" label="事故编号" width="160" />
        <el-table-column prop="incidentType" label="事故类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getIncidentTypeTag(row.incidentType)">
              {{ getIncidentTypeName(row.incidentType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stationName" label="关联站点" width="180" />
        <el-table-column prop="deviceName" label="关联设备" width="160" />
        <el-table-column prop="incidentTime" label="发生时间" width="180" />
        <el-table-column prop="incidentLevel" label="严重等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getIncidentLevelTag(row.incidentLevel)" size="small">
              {{ getIncidentLevelName(row.incidentLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="处理状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : row.status === 1 ? 'primary' : 'warning'" size="small">
              {{ row.status === 2 ? '已完成' : row.status === 1 ? '处理中' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button type="success" link size="small" @click="handleProcess(row)" v-if="row.status !== 2">
              处理
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

    <el-dialog v-model="detailDialogVisible" title="事故详情" width="800px">
      <el-descriptions :column="2" border v-if="currentIncident">
        <el-descriptions-item label="事故编号">{{ currentIncident.incidentNo }}</el-descriptions-item>
        <el-descriptions-item label="事故类型">{{ getIncidentTypeName(currentIncident.incidentType) }}</el-descriptions-item>
        <el-descriptions-item label="发生时间">{{ currentIncident.incidentTime }}</el-descriptions-item>
        <el-descriptions-item label="严重等级">
          <el-tag :type="getIncidentLevelTag(currentIncident.incidentLevel)">{{ getIncidentLevelName(currentIncident.incidentLevel) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="关联站点">{{ currentIncident.stationName }}</el-descriptions-item>
        <el-descriptions-item label="关联设备">{{ currentIncident.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="影响范围" :span="2">{{ currentIncident.impactScope }}</el-descriptions-item>
        <el-descriptions-item label="事故描述" :span="2">{{ currentIncident.incidentDesc }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider>处理记录</el-divider>
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in currentIncident.processRecords"
          :key="index"
          :timestamp="item.processTime"
          placement="top">
          {{ item.processContent }}
        </el-timeline-item>
      </el-timeline>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="processDialogVisible" title="处理事故" width="600px">
      <el-form :model="processForm" label-width="100px">
        <el-form-item label="处理方案">
          <el-input v-model="processForm.processContent" type="textarea" :rows="4" placeholder="请输入处理方案" />
        </el-form-item>
        <el-form-item label="完成状态">
          <el-radio-group v-model="processForm.completed">
            <el-radio :value="true">已完成</el-radio>
            <el-radio :value="false">继续处理中</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProcess">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const loading = ref(false);
const queryForm = reactive({
  incidentType: null,
  status: null
});
const dateRange = ref(null);
const incidentList = ref([]);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const detailDialogVisible = ref(false);
const processDialogVisible = ref(false);
const currentIncident = ref(null);
const processForm = reactive({
  processContent: '',
  completed: false
});

const getIncidentTypeName = (type) => {
  const map = { 1: '设备故障', 2: '安全事故', 3: '通信中断', 4: '其他' };
  return map[type] || '未知';
};

const getIncidentTypeTag = (type) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'info', 4: '' };
  return map[type] || '';
};

const getIncidentLevelName = (level) => {
  const map = { 1: '严重', 2: '较大', 3: '一般', 4: '轻微' };
  return map[level] || '未知';
};

const getIncidentLevelTag = (level) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'info', 4: '' };
  return map[level] || '';
};

const fetchData = () => {
  loading.value = true;
  setTimeout(() => {
    incidentList.value = [
      {
        id: 1,
        incidentNo: 'IN20250120001',
        incidentType: 1,
        stationName: '南山区科技园充电站',
        deviceName: '充电桩A4',
        incidentTime: '2025-01-20 10:15:00',
        incidentLevel: 2,
        impactScope: '影响该充电桩及相邻2台充电桩',
        incidentDesc: '设备电源模块异常，导致充电中断',
        status: 0,
        processRecords: []
      },
      {
        id: 2,
        incidentNo: 'IN20250119001',
        incidentType: 3,
        stationName: '福田区市民中心充电站',
        deviceName: '充电站通信网关',
        incidentTime: '2025-01-19 22:30:00',
        incidentLevel: 3,
        impactScope: '全站设备通信中断',
        incidentDesc: '通信网关故障，导致所有设备无法上报数据',
        status: 2,
        processRecords: [
          { processTime: '2025-01-19 22:45:00', processContent: '发现故障，通知运维人员' },
          { processTime: '2025-01-20 01:30:00', processContent: '更换通信网关，恢复通信' },
          { processTime: '2025-01-20 02:15:00', processContent: '验证设备通信正常' }
        ]
      }
    ];
    pagination.total = 2;
    loading.value = false;
  }, 500);
};

const handleReset = () => {
  queryForm.incidentType = null;
  queryForm.status = null;
  dateRange.value = null;
  fetchData();
};

const handleDetail = (row) => {
  currentIncident.value = row;
  detailDialogVisible.value = true;
};

const handleProcess = (row) => {
  currentIncident.value = row;
  processForm.processContent = '';
  processForm.completed = false;
  processDialogVisible.value = true;
};

const submitProcess = () => {
  if (!processForm.processContent.trim()) {
    ElMessage.warning('请输入处理方案');
    return;
  }
  ElMessage.success('处理记录已提交');
  processDialogVisible.value = false;
  fetchData();
};

const handleExport = () => {
  ElMessageBox.confirm('确认导出事故报告？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage.success('报告导出成功');
  });
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
</style>
