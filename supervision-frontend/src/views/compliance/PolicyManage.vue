<template>
  <div class="policy-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>政策管理</span>
          <el-button type="primary" @click="handleCreate">发布新政策</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下线" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="请输入关键词" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="policyList" stripe v-loading="loading">
        <el-table-column prop="policyTitle" label="政策标题" min-width="200"></el-table-column>
        <el-table-column prop="policyType" label="政策类型" width="140">
          <template #default="{ row }">
            <el-tag :type="row.policyType === 1 ? 'primary' : row.policyType === 2 ? 'success' : 'warning'">
              {{ row.policyType === 1 ? '行业规范' : row.policyType === 2 ? '补贴政策' : '安全要求' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180"></el-table-column>
        <el-table-column prop="publisher" label="发布人" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'info' : 'warning'" size="small">
              {{ row.status === 0 ? '草稿' : row.status === 1 ? '已发布' : '已下线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="readRate" label="阅读率" width="100" align="center" v-if="queryForm.status === 1">
          <template #default="{ row }">
            {{ row.readRate || '0%' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
            <el-button type="warning" link size="small" @click="handleEdit(row)" v-if="row.status === 0">编辑</el-button>
            <el-button type="success" link size="small" @click="handlePush(row)" v-if="row.status === 1">推送</el-button>
            <el-button type="danger" link size="small" @click="handleOffline(row)" v-if="row.status === 1">下线</el-button>
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

    <!-- 发布/编辑政策对话框 -->
    <el-dialog v-model="policyDialogVisible" :title="isEdit ? '编辑政策' : '发布新政策'" width="700px">
      <el-form :model="policyForm" :rules="policyRules" ref="policyFormRef" label-width="100px">
        <el-form-item label="政策标题" prop="policyTitle">
          <el-input v-model="policyForm.policyTitle" placeholder="请输入政策标题" />
        </el-form-item>
        <el-form-item label="政策类型" prop="policyType">
          <el-select v-model="policyForm.policyType" placeholder="请选择政策类型" style="width: 100%">
            <el-option label="行业规范" :value="1" />
            <el-option label="补贴政策" :value="2" />
            <el-option label="安全要求" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="政策内容" prop="policyContent">
          <el-input
            v-model="policyForm.policyContent"
            type="textarea"
            :rows="8"
            placeholder="请输入政策内容"
          />
        </el-form-item>
        <el-form-item label="附件上传">
          <el-upload
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            multiple
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持上传pdf、doc、docx文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="policyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPolicy">确认发布</el-button>
      </template>
    </el-dialog>

    <!-- 推送政策对话框 -->
    <el-dialog v-model="pushDialogVisible" title="推送政策" width="600px">
      <el-form :model="pushForm" label-width="100px">
        <el-form-item label="政策标题">
          <span>{{ currentPolicy.policyTitle }}</span>
        </el-form-item>
        <el-form-item label="选择运营商">
          <el-checkbox-group v-model="pushForm.selectedOperators">
            <el-checkbox label="1">南方电网</el-checkbox>
            <el-checkbox label="2">特来电</el-checkbox>
            <el-checkbox label="3">星星充电</el-checkbox>
            <el-checkbox label="4">云快充</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pushDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPush">确认推送</el-button>
      </template>
    </el-dialog>

    <!-- 推送记录对话框 -->
    <el-dialog v-model="pushRecordDialogVisible" title="推送记录" width="800px">
      <el-table :data="pushRecordList" stripe>
        <el-table-column prop="operatorName" label="运营商" width="180"></el-table-column>
        <el-table-column prop="pushTime" label="推送时间" width="180"></el-table-column>
        <el-table-column prop="readStatus" label="阅读状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.readStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.readStatus === 1 ? '已阅读' : '未阅读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="readTime" label="阅读时间" width="180"></el-table-column>
      </el-table>
      <template #footer>
        <div class="read-stats">
          <span>总推送数: {{ pushRecordList.length }}</span>
          <span>已阅读: {{ pushRecordList.filter(r => r.readStatus === 1).length }}</span>
          <span>阅读率: {{ pushRecordList.length ? Math.round(pushRecordList.filter(r => r.readStatus === 1).length / pushRecordList.length * 100) : 0 }}%</span>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getPolicyList,
  publishPolicy,
  offlinePolicy,
  getPushRecords,
  pushPolicy
} from '../../api/compliance'

const loading = ref(false)
const queryForm = reactive({
  status: null,
  keyword: ''
})
const policyList = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const policyDialogVisible = ref(false)
const pushDialogVisible = ref(false)
const pushRecordDialogVisible = ref(false)
const isEdit = ref(false)
const policyFormRef = ref(null)
const currentPolicy = reactive({})
const policyForm = reactive({
  policyTitle: '',
  policyType: null,
  policyContent: ''
})
const pushForm = reactive({
  selectedOperators: []
})
const pushRecordList = ref([])
const policyRules = {
  policyTitle: [{ required: true, message: '请输入政策标题', trigger: 'blur' }],
  policyType: [{ required: true, message: '请选择政策类型', trigger: 'change' }],
  policyContent: [{ required: true, message: '请输入政策内容', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getPolicyList({
      ...queryForm,
      page: pagination.currentPage,
      pageSize: pagination.pageSize
    })
    if (res.data?.records) {
      policyList.value.splice(0, policyList.value.length, ...res.data.records)
      pagination.total = res.data.total
    } else {
      // 模拟数据
      policyList.value = [
        {
          id: 1,
          policyTitle: '关于加强充电设施安全管理的通知',
          policyType: 3,
          publishTime: '2025-01-15 10:00:00',
          publisher: '监管平台管理员',
          status: 1,
          readRate: '85%'
        },
        {
          id: 2,
          policyTitle: '2025年第一季度补贴政策说明',
          policyType: 2,
          publishTime: '2025-01-10 09:00:00',
          publisher: '监管平台管理员',
          status: 1,
          readRate: '92%'
        }
      ]
      pagination.total = 2
    }
  } catch (e) {
    console.error('获取政策列表失败', e)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.status = null
  queryForm.keyword = ''
  fetchData()
}

function handleCreate() {
  isEdit.value = false
  policyForm.policyTitle = ''
  policyForm.policyType = null
  policyForm.policyContent = ''
  policyDialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(policyForm, row)
  policyDialogVisible.value = true
}

function handleDetail(row) {
  ElMessage.info(`查看政策详情: ${row.policyTitle}`)
}

function handlePush(row) {
  Object.assign(currentPolicy, row)
  pushForm.selectedOperators = []
  pushDialogVisible.value = true
}

function handleOffline(row) {
  ElMessageBox.confirm('确认要下线该政策吗?', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await offlinePolicy(row.id)
      ElMessage.success('政策已下线')
      fetchData()
    } catch (e) {
      console.error('下线政策失败', e)
    }
  })
}

async function submitPolicy() {
  try {
    await policyFormRef.value.validate()
    await publishPolicy(policyForm)
    ElMessage.success(isEdit.value ? '政策更新成功' : '政策发布成功')
    policyDialogVisible.value = false
    fetchData()
  } catch (e) {
    console.error('发布政策失败', e)
  }
}

async function submitPush() {
  try {
    await pushPolicy(currentPolicy.id, pushForm.selectedOperators)
    ElMessage.success('政策推送成功')
    pushDialogVisible.value = false
    // 获取推送记录
    const res = await getPushRecords(currentPolicy.id)
    if (res.data) {
      pushRecordList.value = res.data
      pushRecordDialogVisible.value = true
    }
  } catch (e) {
    console.error('推送政策失败', e)
  }
}

function handleFileChange(file) {
  ElMessage.info(`文件 ${file.name} 已选择`)
}

fetchData()
</script>

<style scoped lang="scss">
.read-stats {
  display: flex;
  gap: 40px;
  justify-content: center;
  color: var(--color-text-secondary);
}
</style>
