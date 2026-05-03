<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户权限管理</span>
          <el-button type="primary" @click="handleCreate" class="add-btn">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable style="width: 200px" />
        </el-form-item>
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

      <el-table :data="userList" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="realName" label="真实姓名" width="140" />
        <el-table-column prop="roleName" label="角色" width="140" />
        <el-table-column prop="regionName" label="所属区域" width="140" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
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
            <el-button type="warning" size="small" class="action-btn" @click="handleAssignRole(row)">
              <el-icon><UserFilled /></el-icon>
              分配角色
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

    <el-dialog v-model="userDialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="600px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="userForm.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option label="超级管理员" :value="1" />
            <el-option label="区域管理员" :value="2" />
            <el-option label="普通用户" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属区域" prop="regionCode">
          <el-select v-model="userForm.regionCode" placeholder="请选择区域" clearable style="width: 100%">
            <el-option label="深圳市" value="440300" />
            <el-option label="福田区" value="440304" />
            <el-option label="南山区" value="440305" />
            <el-option label="宝安区" value="440306" />
            <el-option label="龙岗区" value="440307" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUser">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-form :model="roleForm" label-width="100px">
        <el-form-item label="当前用户">
          <span>{{ currentUser?.realName }}</span>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="roleForm.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option label="超级管理员" :value="1" />
            <el-option label="区域管理员" :value="2" />
            <el-option label="普通用户" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限列表">
          <el-checkbox-group v-model="roleForm.permissions">
            <el-checkbox label="数据总览" value="dashboard" />
            <el-checkbox label="设施台账" value="facility" />
            <el-checkbox label="接入审核" value="access-audit" />
            <el-checkbox label="V2G活动" value="v2g-activity" />
            <el-checkbox label="智能调度" value="smart-dispatch" />
            <el-checkbox label="运营商管理" value="operator-manage" />
            <el-checkbox label="补贴管理" value="subsidy-manage" />
            <el-checkbox label="告警监控" value="alarm-monitor" />
            <el-checkbox label="应急指令" value="emergency-command" />
            <el-checkbox label="事故追溯" value="accident-trace" />
            <el-checkbox label="系统管理" value="system-manage" />
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRole">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Search, RefreshLeft, Edit, UserFilled, Delete } from '@element-plus/icons-vue';

const loading = ref(false);
const queryForm = reactive({
  username: '',
  status: null
});
const userList = ref([]);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const userDialogVisible = ref(false);
const roleDialogVisible = ref(false);
const isEdit = ref(false);
const userFormRef = ref(null);
const currentUser = ref(null);
const userForm = reactive({
  username: '',
  password: '',
  realName: '',
  roleId: null,
  regionCode: null,
  phone: '',
  email: '',
  status: 1
});
const roleForm = reactive({
  roleId: null,
  permissions: []
});
const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
};

const fetchData = () => {
  loading.value = true;
  setTimeout(() => {
    userList.value = [
      {
        id: 1,
        username: 'admin',
        realName: '管理员',
        roleName: '超级管理员',
        regionName: '深圳市',
        phone: '13800138000',
        email: 'admin@example.com',
        status: 1,
        createTime: '2024-12-01 09:00:00'
      },
      {
        id: 2,
        username: 'ns_user',
        realName: '南山管理员',
        roleName: '区域管理员',
        regionName: '南山区',
        phone: '13800138001',
        email: 'ns@example.com',
        status: 1,
        createTime: '2024-12-05 14:30:00'
      },
      {
        id: 3,
        username: 'ft_user',
        realName: '福田管理员',
        roleName: '区域管理员',
        regionName: '福田区',
        phone: '13800138002',
        email: 'ft@example.com',
        status: 1,
        createTime: '2024-12-10 10:15:00'
      }
    ];
    pagination.total = 3;
    loading.value = false;
  }, 500);
};

const handleReset = () => {
  queryForm.username = '';
  queryForm.status = null;
  fetchData();
};

const handleCreate = () => {
  isEdit.value = false;
  userForm.username = '';
  userForm.password = '';
  userForm.realName = '';
  userForm.roleId = null;
  userForm.regionCode = null;
  userForm.phone = '';
  userForm.email = '';
  userForm.status = 1;
  userDialogVisible.value = true;
};

const handleEdit = (row) => {
  isEdit.value = true;
  Object.assign(userForm, {
    username: row.username,
    realName: row.realName,
    roleId: row.roleId,
    regionCode: row.regionCode,
    phone: row.phone,
    email: row.email,
    status: row.status
  });
  userDialogVisible.value = true;
};

const handleAssignRole = (row) => {
  currentUser.value = row;
  roleForm.roleId = row.roleId;
  roleForm.permissions = ['dashboard', 'facility', 'v2g-activity', 'alarm-monitor'];
  roleDialogVisible.value = true;
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除用户"${row.realName}"？`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功');
    fetchData();
  });
};

const submitUser = async () => {
  try {
    await userFormRef.value.validate();
    ElMessage.success(isEdit.value ? '用户更新成功' : '用户创建成功');
    userDialogVisible.value = false;
    fetchData();
  } catch {
    console.log('验证失败');
  }
};

const submitRole = () => {
  if (!roleForm.roleId) {
    ElMessage.warning('请选择角色');
    return;
  }
  ElMessage.success('角色分配成功');
  roleDialogVisible.value = false;
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
</style>
