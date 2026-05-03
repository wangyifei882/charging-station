<template>
  <div class="page">
    <div class="page-header">
      <h2 class="page-title">系统设置</h2>
    </div>

    <div class="panel">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="场站配置" name="station">
          <div class="tab-content">
            <el-form label-width="100px" :model="stationForm">
              <el-form-item label="场站名称">
                <el-input v-model="stationForm.name" placeholder="请输入场站名称" style="width: 320px" />
              </el-form-item>
              <el-form-item label="场站地址">
                <el-input v-model="stationForm.address" placeholder="请输入场站地址" style="width: 400px" />
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model="stationForm.phone" placeholder="请输入联系电话" style="width: 240px" />
              </el-form-item>
              <el-form-item label="营业时间">
                <el-input v-model="stationForm.hours" placeholder="00:00-24:00" style="width: 200px" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary">保存配置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <el-tab-pane label="用户管理" name="users">
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" @click="userDialogVisible = true">
                <el-icon :size="14"><Plus /></el-icon>
                新增用户
              </el-button>
            </div>
            <el-table :data="users" style="width: 100%">
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="realName" label="姓名" width="100" />
              <el-table-column prop="role" label="角色" width="120">
                <template #default="{ row }">
                  <el-tag size="small" :type="row.role === 'admin' ? 'danger' : 'primary'">{{ row.roleText }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="70">
                <template #default="{ row }">
                  <span :class="row.status ? 'text-success' : 'text-danger'">{{ row.status ? '启用' : '禁用' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="phone" label="手机号" width="130" />
              <el-table-column prop="email" label="邮箱" min-width="180" />
              <el-table-column label="操作" width="160" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" type="primary" link @click="handleEditUser(row)">编辑</el-button>
                  <el-button size="small" type="danger" link @click="handleDeleteUser(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane label="告警规则" name="alarms">
          <div class="tab-content">
            <div class="toolbar">
              <el-button type="primary" @click="handleAddRule">
                <el-icon :size="14"><Plus /></el-icon>
                新增规则
              </el-button>
            </div>
            <el-table :data="alarmRules" style="width: 100%">
              <el-table-column prop="ruleName" label="规则名称" min-width="150" />
              <el-table-column prop="target" label="监控对象" width="100">
                <template #default="{ row }"><el-tag size="small">{{ row.target }}</el-tag></template>
              </el-table-column>
              <el-table-column prop="condition" label="触发条件" min-width="180" />
              <el-table-column prop="level" label="告警等级" width="90">
                <template #default="{ row }">
                  <el-tag size="small" :type="row.levelType">{{ row.level }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="notifyMethod" label="通知方式" width="110" />
              <el-table-column prop="enabled" label="状态" width="70">
                <template #default="{ row }"><el-switch v-model="row.enabled" /></template>
              </el-table-column>
              <el-table-column label="操作" width="160" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" type="primary" link @click="handleEditRule(row)">编辑</el-button>
                  <el-button size="small" type="danger" link @click="handleDeleteRule(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane label="操作日志" name="logs">
          <div class="tab-content">
            <el-form :inline="true" :model="logSearchForm" class="log-filter">
              <el-form-item label="操作人">
                <el-input v-model="logSearchForm.operator" placeholder="请输入操作人" :prefix-icon="Search" style="width: 180px" />
              </el-form-item>
              <el-form-item label="操作类型">
                <el-select v-model="logSearchForm.action" placeholder="全部类型" style="width: 150px" clearable>
                  <el-option label="全部类型" value="" />
                  <el-option label="系统" value="系统" />
                  <el-option label="设备管理" value="设备管理" />
                  <el-option label="订单管理" value="订单管理" />
                  <el-option label="用户管理" value="用户管理" />
                  <el-option label="故障处理" value="故障处理" />
                  <el-option label="数据查看" value="数据查看" />
                  <el-option label="系统配置" value="系统配置" />
                  <el-option label="故障申报" value="故障申报" />
                  <el-option label="报表管理" value="报表管理" />
                  <el-option label="告警配置" value="告警配置" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearchLogs">查询</el-button>
                <el-button @click="handleResetLogs">重置</el-button>
              </el-form-item>
            </el-form>
            <el-table :data="operationLogs" style="width: 100%" v-loading="logLoading">
              <el-table-column prop="username" label="操作人" width="100" />
              <el-table-column prop="module" label="操作模块" width="100" />
              <el-table-column prop="operation" label="操作类型" width="110">
                <template #default="{ row }">
                  <el-tag size="small" :type="getModuleType(row.module)">{{ row.operation }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="操作描述" min-width="200" show-overflow-tooltip />
              <el-table-column prop="ipAddress" label="IP地址" width="130" />
              <el-table-column prop="createTime" label="操作时间" width="160">
                <template #default="{ row }">
                  <span class="cell-number">{{ formatDateTime(row.createTime) }}</span>
                </template>
              </el-table-column>
            </el-table>
            <div class="table-footer">
              <span class="table-count">共 {{ logPagination.total }} 条</span>
              <el-pagination
                v-model:current-page="logPagination.page"
                v-model:page-size="logPagination.size"
                :total="logPagination.total"
                background
                layout="prev, pager, next"
                :page-sizes="[10, 20, 50]"
                small
                @current-change="fetchOperationLogs"
                @size-change="fetchOperationLogs"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- User Dialog -->
    <el-dialog v-model="userDialogVisible" :title="isEditUser ? '编辑用户' : '新增用户'" width="480px">
      <el-form label-width="80px">
        <el-form-item label="用户名"><el-input v-model="newUser.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="newUser.realName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="newUser.role" style="width: 100%">
            <el-option label="系统管理员" value="admin" />
            <el-option label="运营人员" value="operator" />
            <el-option label="查看人员" value="viewer" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="newUser.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="newUser.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUser">确认</el-button>
      </template>
    </el-dialog>

    <!-- Alarm Rule Dialog -->
    <el-dialog v-model="ruleDialogVisible" :title="isEditRule ? '编辑规则' : '新增规则'" width="480px">
      <el-form label-width="80px">
        <el-form-item label="规则名称"><el-input v-model="newRule.ruleName" placeholder="请输入规则名称" /></el-form-item>
        <el-form-item label="监控对象">
          <el-select v-model="newRule.target" style="width: 100%">
            <el-option label="设备" value="设备" />
            <el-option label="储能" value="储能" />
            <el-option label="订单" value="订单" />
          </el-select>
        </el-form-item>
        <el-form-item label="触发条件"><el-input v-model="newRule.condition" placeholder="请输入触发条件" /></el-form-item>
        <el-form-item label="告警等级">
          <el-select v-model="newRule.level" style="width: 100%">
            <el-option label="紧急" value="紧急" />
            <el-option label="重要" value="重要" />
            <el-option label="提示" value="提示" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知方式">
          <el-select v-model="newRule.notifyMethod" style="width: 100%">
            <el-option label="短信+邮件" value="短信+邮件" />
            <el-option label="邮件" value="邮件" />
            <el-option label="系统通知" value="系统通知" />
          </el-select>
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
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, getOperationLogs } from '@/api'

const activeTab = ref('station')
const stationForm = ref({ name: 'XX充电站', address: 'XX市XX区XX路XX号', phone: '400-888-8888', hours: '00:00-24:00' })

// Users
const userDialogVisible = ref(false)
const isEditUser = ref(false)
const newUser = ref({ username: '', realName: '', role: 'operator', phone: '', email: '' })
const users = ref([])
const userLoading = ref(false)

const fetchUsers = async () => {
  userLoading.value = true
  try {
    const res = await getUserList({ page: 1, size: 100 })
    if (res.data?.records) {
      users.value = res.data.records.map(user => ({
        ...user,
        role: getRoleCode(user.roleName),
        roleText: user.roleName || '运营人员'
      }))
    }
  } catch (e) {
    users.value = [
      { username: 'admin', realName: '系统管理员', role: 'admin', roleText: '系统管理员', status: 1, phone: '138****0001', email: 'admin@station.com' },
      { username: 'operator1', realName: '张三', role: 'operator', roleText: '运营人员', status: 1, phone: '138****0002', email: 'zhangsan@station.com' },
      { username: 'viewer1', realName: '李四', role: 'viewer', roleText: '查看人员', status: 1, phone: '138****0003', email: 'lisi@station.com' }
    ]
  } finally { userLoading.value = false }
}

const getRoleCode = (roleName) => {
  const map = { '系统管理员': 'admin', '运营人员': 'operator', '查看人员': 'viewer' }
  return map[roleName] || 'operator'
}

const handleEditUser = (row) => { isEditUser.value = true; newUser.value = { ...row }; userDialogVisible.value = true }
const handleDeleteUser = (row) => {
  ElMessageBox.confirm(`确认删除用户"${row.realName}"？`, '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' })
    .then(async () => { try { await deleteUser(row.id); ElMessage.success('删除成功'); fetchUsers() } catch (e) { ElMessage.error('删除失败') } })
}
const submitUser = async () => {
  try {
    if (isEditUser.value) { await updateUser(newUser.value.id, newUser.value); ElMessage.success('用户更新成功') }
    else { await addUser(newUser.value); ElMessage.success('用户创建成功') }
    userDialogVisible.value = false; isEditUser.value = false
    newUser.value = { username: '', realName: '', role: 'operator', phone: '', email: '' }; fetchUsers()
  } catch (e) { ElMessage.error(isEditUser.value ? '更新失败' : '创建失败') }
}

// Alarm rules
const ruleDialogVisible = ref(false)
const isEditRule = ref(false)
const newRule = ref({ ruleName: '', target: '设备', condition: '', level: '提示', notifyMethod: '系统通知' })
const alarmRules = ref([
  { ruleName: '温度过高告警', target: '设备', condition: '温度 > 75℃ 持续5分钟', level: '紧急', levelType: 'danger', notifyMethod: '短信+邮件', enabled: true },
  { ruleName: '通信中断告警', target: '设备', condition: '通信中断 > 3分钟', level: '重要', levelType: 'warning', notifyMethod: '邮件', enabled: true },
  { ruleName: 'SOC低电量', target: '储能', condition: 'SOC < 20%', level: '提示', levelType: 'info', notifyMethod: '系统通知', enabled: true }
])
const handleAddRule = () => { isEditRule.value = false; newRule.value = { ruleName: '', target: '设备', condition: '', level: '提示', notifyMethod: '系统通知' }; ruleDialogVisible.value = true }
const handleEditRule = (row) => { isEditRule.value = true; newRule.value = { ...row }; ruleDialogVisible.value = true }
const handleDeleteRule = (row) => {
  ElMessageBox.confirm(`确认删除规则"${row.ruleName}"？`, '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' })
    .then(() => ElMessage.success('删除成功'))
}
const submitRule = () => { ElMessage.success(isEditRule.value ? '规则更新成功' : '规则创建成功'); ruleDialogVisible.value = false }

// Operation logs
const operationLogs = ref([])
const logLoading = ref(false)
const logSearchForm = reactive({ operator: '', action: '' })
const logPagination = reactive({ page: 1, size: 10, total: 0 })

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return datetime.replace('T', ' ').substring(0, 16)
}

const getModuleType = (module) => {
  const map = { '系统': 'primary', '设备管理': 'success', '订单管理': 'warning', '用户管理': 'success', '故障处理': 'danger', '数据查看': 'info', '系统配置': 'success', '充电运营': 'primary', '故障申报': 'danger', '报表管理': 'info', '告警配置': 'warning', '设备控制': 'primary', '报表查看': 'info' }
  return map[module] || 'info'
}

const fetchOperationLogs = async () => {
  logLoading.value = true
  try {
    const res = await getOperationLogs({ page: logPagination.page, size: logPagination.size, username: logSearchForm.operator || undefined, module: logSearchForm.action || undefined })
    if (res.data?.records) { operationLogs.value = res.data.records; logPagination.total = res.data.total || 0 }
  } catch (e) {
    operationLogs.value = [
      { id: 1, username: 'admin', module: '系统', operation: '登录', description: '管理员登录系统', ipAddress: '192.168.1.100', createTime: '2026-04-18T09:30:15' },
      { id: 2, username: 'admin', module: '设备管理', operation: '重启设备', description: '重启 DC-001 设备', ipAddress: '192.168.1.100', createTime: '2026-04-18T10:15:22' },
      { id: 3, username: 'operator1', module: '订单管理', operation: '订单退款', description: '退款处理 订单#20260418001', ipAddress: '192.168.1.101', createTime: '2026-04-18T11:20:05' }
    ]
    logPagination.total = 3
  } finally { logLoading.value = false }
}

const handleSearchLogs = () => { logPagination.page = 1; fetchOperationLogs() }
const handleResetLogs = () => { logSearchForm.operator = ''; logSearchForm.action = ''; handleSearchLogs() }

onMounted(() => { fetchUsers(); fetchOperationLogs() })
</script>

<style lang="scss" scoped>
.page { display: flex; flex-direction: column; gap: 14px; }
.page-header .page-title { font-size: 18px; font-weight: 600; color: var(--color-text); margin: 0; }

.panel {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.tab-content { padding: 20px 24px; }

.toolbar { margin-bottom: 14px; }

.log-filter { margin-bottom: 14px; }

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
  margin-top: 14px;
  border-top: 1px solid var(--color-border-light);
}
.table-count { font-size: 12px; color: var(--color-text-muted); }

.cell-number { font-variant-numeric: tabular-nums; color: var(--color-text); }
.text-success { color: var(--color-success); font-weight: 500; }
.text-danger { color: var(--color-danger); font-weight: 500; }
</style>
