<template>
  <div class="operator-manage">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="运营商列表" name="operators">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>运营商信息</span>
              <el-button type="primary" @click="handleAdd">新增运营商</el-button>
            </div>
          </template>

          <el-table :data="operatorList" stripe>
            <el-table-column prop="operatorName" label="运营商名称" min-width="150" />
            <el-table-column prop="contactPerson" label="联系人" width="100" />
            <el-table-column prop="contactPhone" label="联系电话" width="120" />
            <el-table-column prop="stationCount" label="站点数" width="100" align="center" />
            <el-table-column prop="deviceCount" label="设备数" width="100" align="center" />
            <el-table-column prop="rating" label="评级" width="100" align="center">
              <template #default="{ row }">
                <el-rate v-model="row.rating" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 'normal' ? 'success' : 'danger'" size="small">
                  {{ row.status === 'normal' ? '正常' : '违规' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                <el-button type="warning" link @click="handleQualification(row)">资质</el-button>
                <el-button type="danger" link @click="handleViolation(row)">违规</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="资质管理" name="qualification">
        <el-card>
          <template #header><span>资质审核记录</span></template>
          <el-table :data="qualificationList" stripe>
            <el-table-column prop="operatorName" label="运营商" width="150" />
            <el-table-column prop="licenseType" label="资质类型" width="120" />
            <el-table-column prop="licenseNumber" label="资质证书号" min-width="150" />
            <el-table-column prop="issueDate" label="发证日期" width="120" />
            <el-table-column prop="expireDate" label="到期日期" width="120" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 'valid' ? 'success' : 'warning'" size="small">
                  {{ row.status === 'valid' ? '有效' : '即将过期' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="违规记录" name="violations">
        <el-card>
          <template #header><span>违规记录</span></template>
          <el-table :data="violationList" stripe>
            <el-table-column prop="operatorName" label="运营商" width="150" />
            <el-table-column prop="violationType" label="违规类型" width="120" />
            <el-table-column prop="description" label="违规描述" min-width="200" />
            <el-table-column prop="penaltyAmount" label="罚款金额(元)" width="120" align="right" />
            <el-table-column prop="penaltyDate" label="处罚日期" width="120" />
            <el-table-column prop="status" label="处理状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 'resolved' ? 'success' : 'danger'" size="small">
                  {{ row.status === 'resolved' ? '已处理' : '未处理' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="editDialogVisible" title="编辑运营商" width="550px">
      <el-form :model="editForm" label-width="100px" v-if="editData">
        <el-form-item label="运营商名称">
          <el-input v-model="editForm.operatorName" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="editForm.contactPerson" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="editForm.contactPhone" />
        </el-form-item>
        <el-form-item label="站点数">
          <el-input-number v-model="editForm.stationCount" :min="0" />
        </el-form-item>
        <el-form-item label="设备数">
          <el-input-number v-model="editForm.deviceCount" :min="0" />
        </el-form-item>
        <el-form-item label="评级">
          <el-rate v-model="editForm.rating" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="qualDialogVisible" title="资质详情" width="600px">
      <el-table :data="qualData" stripe v-if="qualData.length">
        <el-table-column prop="licenseType" label="资质类型" width="140" />
        <el-table-column prop="licenseNumber" label="证书号" min-width="160" />
        <el-table-column prop="issueDate" label="发证日期" width="120" />
        <el-table-column prop="expireDate" label="到期日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row: r }">
            <el-tag :type="r.status === 'valid' ? 'success' : 'warning'" size="small">
              {{ r.status === 'valid' ? '有效' : '即将过期' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无资质信息" />
      <template #footer>
        <el-button @click="qualDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="violationDialogVisible" title="违规记录" width="700px">
      <el-table :data="violationData" stripe v-if="violationData.length">
        <el-table-column prop="violationType" label="违规类型" width="120" />
        <el-table-column prop="description" label="违规描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="penaltyAmount" label="罚款金额(元)" width="120" align="right" />
        <el-table-column prop="penaltyDate" label="处罚日期" width="120" />
        <el-table-column prop="status" label="处理状态" width="100">
          <template #default="{ row: r }">
            <el-tag :type="r.status === 'resolved' ? 'success' : 'danger'" size="small">
              {{ r.status === 'resolved' ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无违规记录" />
      <template #footer>
        <el-button @click="violationDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getOperatorList, getQualificationList, getViolationList } from '../../api/compliance'

const activeTab = ref('operators')

const operatorList = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const qualificationList = ref([])

const violationList = ref([])

const editDialogVisible = ref(false)
const editData = ref(null)
const editForm = reactive({
  operatorName: '',
  contactPerson: '',
  contactPhone: '',
  stationCount: 0,
  deviceCount: 0,
  rating: 3
})

const qualDialogVisible = ref(false)
const qualData = ref([])

const violationDialogVisible = ref(false)
const violationData = ref([])

const fetchOperatorData = async () => {
  try {
    const res = await getOperatorList({
      page: pagination.currentPage,
      size: pagination.pageSize
    })
    if (res.data?.records) {
      operatorList.value = res.data.records.map(op => ({
        id: op.id,
        operatorName: op.operatorName,
        contactPerson: op.contactPerson,
        contactPhone: op.contactPhone,
        stationCount: op.stationCount || 0,
        deviceCount: op.deviceCount || 0,
        rating: 4,
        status: op.complianceStatus === 1 ? 'normal' : 'violation'
      }))
      pagination.total = res.data.total || 0
    }
  } catch (e) {
    console.error('获取运营商数据失败', e)
  }
}

const fetchQualificationData = async () => {
  try {
    const res = await getQualificationList({
      page: 1,
      size: 100
    })
    if (res.data?.records) {
      qualificationList.value = res.data.records.map(q => ({
        id: q.id,
        operatorName: q.operatorName,
        licenseType: q.qualificationType === 1 ? '电力业务许可证' : '营业执照',
        licenseNumber: q.qualificationNumber,
        issueDate: q.issueDate,
        expireDate: q.expireDate,
        status: q.status === 1 ? 'valid' : 'expiring'
      }))
    }
  } catch (e) {
    console.error('获取资质数据失败', e)
  }
}

const fetchViolationData = async () => {
  try {
    const res = await getViolationList({
      page: 1,
      size: 100
    })
    if (res.data?.records) {
      violationList.value = res.data.records.map(v => ({
        id: v.id,
        operatorName: v.operatorName,
        violationType: v.violationType === 1 ? '价格违规' : '安全隐患',
        description: v.description,
        penaltyAmount: v.penaltyAmount?.toFixed(2) || '0',
        penaltyDate: v.violationDate,
        status: v.rectificationStatus === 1 ? 'resolved' : 'pending'
      }))
    }
  } catch (e) {
    console.error('获取违规记录失败', e)
  }
}

const fetchData = async () => {
  if (activeTab.value === 'operators') {
    fetchOperatorData()
  } else if (activeTab.value === 'qualification') {
    fetchQualificationData()
  } else if (activeTab.value === 'violations') {
    fetchViolationData()
  }
}

watch(activeTab, () => {
  fetchData()
})

onMounted(() => {
  fetchData()
})

const handleAdd = () => {
  editData.value = null
  editForm.operatorName = ''
  editForm.contactPerson = ''
  editForm.contactPhone = ''
  editForm.stationCount = 0
  editForm.deviceCount = 0
  editForm.rating = 3
  editDialogVisible.value = true
}

const handleEdit = (row) => {
  editData.value = row
  editForm.operatorName = row.operatorName
  editForm.contactPerson = row.contactPerson
  editForm.contactPhone = row.contactPhone
  editForm.stationCount = row.stationCount || 0
  editForm.deviceCount = row.deviceCount || 0
  editForm.rating = row.rating || 3
  editDialogVisible.value = true
}

const submitEdit = () => {
  ElMessage.success(editData.value ? '运营商信息更新成功' : '运营商创建成功')
  editDialogVisible.value = false
  fetchOperatorData()
}

const handleQualification = async (row) => {
  if (qualificationList.value.length > 0) {
    qualData.value = qualificationList.value.filter(q => q.operatorName === row.operatorName)
  } else {
    qualData.value = []
  }
  qualDialogVisible.value = true
}

const handleViolation = (row) => {
  violationData.value = violationList.value.filter(v => v.operatorName === row.operatorName)
  violationDialogVisible.value = true
}
</script>

<style scoped lang="scss">
</style>
