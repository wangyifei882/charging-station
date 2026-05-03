<template>
  <div class="alarm-config">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>告警规则配置</span>
          <el-button type="primary" @click="handleCreate" class="add-btn">
            <el-icon><Plus /></el-icon>
            新增规则
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 160px">
            <el-option label="全部" :value="null" />
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData" class="search-btn">
            <el-icon style="margin-right: 4px"><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset" class="reset-btn">
            <el-icon style="margin-right: 4px"><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="ruleList" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="ruleName" label="规则名称" min-width="200" />
        <el-table-column prop="alarmLevel" label="告警级别" width="120">
          <template #default="{ row }">
            <el-tag :type="row.alarmLevel === 1 ? 'danger' : row.alarmLevel === 2 ? 'warning' : 'info'" size="small">
              {{ row.alarmLevel === 1 ? '严重' : row.alarmLevel === 2 ? '较大' : '一般' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="triggerCount" label="触发次数" width="120" />
        <el-table-column prop="timeWindow" label="时间窗口(分钟)" width="160" />
        <el-table-column prop="escalationLevel" label="升级级别" width="120">
          <template #default="{ row }">
            <el-tag :type="row.escalationLevel === 'severe' ? 'danger' : row.escalationLevel === 'important' ? 'warning' : 'info'" size="small">
              {{ row.escalationLevel === 'severe' ? '严重告警' : row.escalationLevel === 'important' ? '重要告警' : '普通告警' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="notificationType" label="通知类型" width="140">
          <template #default="{ row }">
            {{ row.notificationType === 'sms' ? '短信' : row.notificationType === 'email' ? '邮件' : '系统消息' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" class="action-btn" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              :type="row.status === 0 ? 'success' : 'warning'" 
              size="small" 
              class="action-btn" 
              @click="handleToggle(row)"
            >
              <el-icon><component :is="row.status === 0 ? 'CircleCheck' : 'CircleClose'" /></el-icon>
              {{ row.status === 0 ? '启用' : '禁用' }}
            </el-button>
            <el-button type="danger" size="small" class="action-btn" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
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

    <el-dialog v-model="ruleDialogVisible" :title="isEdit ? '编辑规则' : '新增规则'" width="600px">
      <el-form :model="ruleForm" :rules="ruleRules" ref="ruleFormRef" label-width="120px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="ruleForm.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="告警级别" prop="alarmLevel">
          <el-select v-model="ruleForm.alarmLevel" placeholder="请选择告警级别" style="width: 100%">
            <el-option label="一般告警" :value="3" />
            <el-option label="较大告警" :value="2" />
            <el-option label="严重告警" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="触发次数" prop="triggerCount">
          <el-input-number v-model="ruleForm.triggerCount" :min="1" :max="100" placeholder="触发次数" />
        </el-form-item>
        <el-form-item label="时间窗口(分钟)" prop="timeWindow">
          <el-input-number v-model="ruleForm.timeWindow" :min="1" :max="1440" placeholder="时间窗口" />
        </el-form-item>
        <el-form-item label="升级级别" prop="escalationLevel">
          <el-select v-model="ruleForm.escalationLevel" placeholder="请选择升级级别" style="width: 100%">
            <el-option label="普通告警" value="normal" />
            <el-option label="重要告警" value="important" />
            <el-option label="严重告警" value="severe" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知类型" prop="notificationType">
          <el-select v-model="ruleForm.notificationType" placeholder="请选择通知类型" style="width: 100%">
            <el-option label="系统消息" value="system" />
            <el-option label="邮件" value="email" />
            <el-option label="短信" value="sms" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知目标" prop="notificationTarget">
          <el-input v-model="ruleForm.notificationTarget" type="textarea" :rows="3" placeholder="多个目标用逗号分隔" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="ruleForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ruleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRule">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Search, RefreshLeft, Edit, CircleCheck, CircleClose, Delete } from '@element-plus/icons-vue';

const loading = ref(false);
const queryForm = reactive({
  status: null
});
const ruleList = ref([]);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const ruleDialogVisible = ref(false);
const isEdit = ref(false);
const ruleFormRef = ref(null);
const ruleForm = reactive({
  ruleName: '',
  alarmLevel: null,
  triggerCount: 3,
  timeWindow: 60,
  escalationLevel: '',
  notificationType: '',
  notificationTarget: '',
  status: 1
});
const ruleRules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  alarmLevel: [{ required: true, message: '请选择告警级别', trigger: 'change' }],
  triggerCount: [{ required: true, message: '请输入触发次数', trigger: 'blur' }],
  timeWindow: [{ required: true, message: '请输入时间窗口', trigger: 'blur' }],
  escalationLevel: [{ required: true, message: '请选择升级级别', trigger: 'change' }],
  notificationType: [{ required: true, message: '请选择通知类型', trigger: 'change' }]
};

const fetchData = () => {
  loading.value = true;
  setTimeout(() => {
    ruleList.value = [
      {
        id: 1,
        ruleName: '设备离线告警升级',
        alarmLevel: 2,
        triggerCount: 5,
        timeWindow: 60,
        escalationLevel: 'important',
        notificationType: 'sms',
        notificationTarget: '13800138000,13800138001',
        status: 1,
        createTime: '2024-12-01 09:00:00'
      },
      {
        id: 2,
        ruleName: '充电异常频繁告警',
        alarmLevel: 1,
        triggerCount: 3,
        timeWindow: 30,
        escalationLevel: 'severe',
        notificationType: 'sms',
        notificationTarget: '13800138000',
        status: 1,
        createTime: '2024-12-05 14:30:00'
      }
    ];
    pagination.total = 2;
    loading.value = false;
  }, 500);
};

const handleReset = () => {
  queryForm.status = null;
  fetchData();
};

const handleCreate = () => {
  isEdit.value = false;
  ruleForm.ruleName = '';
  ruleForm.alarmLevel = null;
  ruleForm.triggerCount = 3;
  ruleForm.timeWindow = 60;
  ruleForm.escalationLevel = '';
  ruleForm.notificationType = '';
  ruleForm.notificationTarget = '';
  ruleForm.status = 1;
  ruleDialogVisible.value = true;
};

const handleEdit = (row) => {
  isEdit.value = true;
  Object.assign(ruleForm, {
    ruleName: row.ruleName,
    alarmLevel: row.alarmLevel,
    triggerCount: row.triggerCount,
    timeWindow: row.timeWindow,
    escalationLevel: row.escalationLevel,
    notificationType: row.notificationType,
    notificationTarget: row.notificationTarget,
    status: row.status
  });
  ruleDialogVisible.value = true;
};

const handleToggle = (row) => {
  const action = row.status === 1 ? '禁用' : '启用';
  ElMessageBox.confirm(`确认${action}规则"${row.ruleName}"？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    row.status = row.status === 1 ? 0 : 1;
    ElMessage.success(`${action}成功`);
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除规则"${row.ruleName}"？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功');
    fetchData();
  });
};

const submitRule = async () => {
  try {
    await ruleFormRef.value.validate();
    ElMessage.success(isEdit.value ? '规则更新成功' : '规则创建成功');
    ruleDialogVisible.value = false;
    fetchData();
  } catch {
    console.log('验证失败');
  }
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
</style>
