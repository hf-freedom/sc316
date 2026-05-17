<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2>售后单管理</h2>
      <el-button type="primary" @click="openCreateDialog">新建售后单</el-button>
    </div>

    <el-card>
      <el-table :data="orders" border>
        <el-table-column prop="orderNo" label="订单号" width="140" />
        <el-table-column prop="userName" label="用户" width="100">
          <template #default="scope">{{ scope.row.userName || scope.row.userId }}</template>
        </el-table-column>
        <el-table-column prop="problemTypeDesc" label="问题类型" width="110" />
        <el-table-column prop="responsiblePartyDesc" label="责任方" width="100" />
        <el-table-column prop="orderAmount" label="订单金额" width="100">
          <template #default="scope">¥{{ scope.row.orderAmount }}</template>
        </el-table-column>
        <el-table-column prop="compensationAmount" label="赔付金额" width="100">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;" v-if="scope.row.compensationAmount">
              ¥{{ scope.row.compensationAmount }}
            </span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="compensationStatusDesc" label="赔付状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.compensationStatus)" v-if="scope.row.compensationStatusDesc">
              {{ scope.row.compensationStatusDesc }}
            </el-tag>
            <span v-else style="color: #909399;">无需赔付</span>
          </template>
        </el-table-column>
        <el-table-column prop="needApproval" label="审批要求" width="100">
          <template #default="scope">
            <el-tag type="warning" v-if="scope.row.needApproval">需审批</el-tag>
            <el-tag type="success" v-else-if="scope.row.compensationAmount">直接发放</el-tag>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="规则匹配结果" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <div style="font-size: 12px;">
              <div><span style="color: #409EFF;">匹配结果：</span>{{ scope.row.ruleMatchResult }}</div>
              <div style="margin-top: 4px; color: #67c23a;">
                <span>计算过程：</span>{{ scope.row.calculationDetail }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
            <el-button type="danger" size="small" @click="revokeOrder(scope.row.id)">撤销</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="新建售后单" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="form.orderNo" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-select v-model="form.userId" placeholder="请选择用户" style="width: 100%;">
            <el-option v-for="user in users" :key="user.userId" :label="user.userName + '(' + user.userId + ')'" :value="user.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单金额">
          <el-input-number v-model="form.orderAmount" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="问题类型">
          <el-select v-model="form.problemType" placeholder="请选择问题类型" style="width: 100%;">
            <el-option v-for="item in problemTypes" :key="item.name" :label="item.value" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="责任方">
          <el-select v-model="form.responsibleParty" placeholder="请选择责任方" style="width: 100%;">
            <el-option v-for="item in responsibleParties" :key="item.name" :label="item.value" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入问题描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createOrder">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="售后单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ currentOrder.userName || currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="问题类型">{{ currentOrder.problemTypeDesc }}</el-descriptions-item>
        <el-descriptions-item label="责任方">{{ currentOrder.responsiblePartyDesc }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.orderAmount }}</el-descriptions-item>
        <el-descriptions-item label="用户等级">{{ currentOrder.userLevelDesc || '-' }}</el-descriptions-item>
        <el-descriptions-item label="赔付金额" :span="2">
          <span style="color: #f56c6c; font-size: 18px; font-weight: bold;" v-if="currentOrder.compensationAmount">
            ¥{{ currentOrder.compensationAmount }}
          </span>
          <span v-else style="color: #909399;">无需赔付</span>
        </el-descriptions-item>
        <el-descriptions-item label="赔付状态" :span="2">
          <el-tag :type="getStatusType(currentOrder.compensationStatus)" size="large" v-if="currentOrder.compensationStatusDesc">
            {{ currentOrder.compensationStatusDesc }}
          </el-tag>
          <span v-else style="color: #909399;">无需赔付</span>
        </el-descriptions-item>
        <el-descriptions-item label="赔付方式" v-if="currentOrder.compensationTypeDesc">
          {{ currentOrder.compensationTypeDesc }}
        </el-descriptions-item>
        <el-descriptions-item label="审批要求" v-if="currentOrder.compensationAmount">
          <el-tag type="warning" v-if="currentOrder.needApproval">需主管审批</el-tag>
          <el-tag type="success" v-else>可直接发放</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="规则匹配结果" :span="2">
          <div style="padding: 10px; background: #ecf5ff; border-radius: 4px;">
            <div><strong>匹配规则：</strong>{{ currentOrder.ruleMatchResult }}</div>
            <div style="margin-top: 8px;"><strong>计算过程：</strong>{{ currentOrder.calculationDetail }}</div>
            <div style="margin-top: 8px;"><strong>审批建议：</strong>{{ currentOrder.approvalSuggestion }}</div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ currentOrder.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentOrder.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const orders = ref([])
const users = ref([])
const problemTypes = ref([])
const responsibleParties = ref([])
const createDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const form = ref({
  orderNo: '',
  userId: '',
  orderAmount: 0,
  problemType: '',
  responsibleParty: '',
  description: ''
})

const loadData = async () => {
  try {
    const [data, userData, pt, rp] = await Promise.all([
      request.get('/aftersale'),
      request.get('/users'),
      request.get('/enums/problem-types'),
      request.get('/enums/responsible-parties')
    ])
    orders.value = data
    users.value = userData
    problemTypes.value = pt
    responsibleParties.value = rp
  } catch (e) {
    console.error(e)
  }
}

const getStatusType = (status) => {
  const typeMap = {
    PENDING_APPROVAL: 'warning',
    PENDING_ISSUE: 'primary',
    APPROVED: 'success',
    ISSUED: 'success',
    REJECTED: 'danger',
    REVOKED: 'info',
    ROLLBACK: 'info'
  }
  return typeMap[status] || ''
}

const openCreateDialog = () => {
  form.value = { orderNo: '', userId: '', orderAmount: 0, problemType: '', responsibleParty: '', description: '' }
  createDialogVisible.value = true
}

const createOrder = async () => {
  if (!form.value.orderNo || !form.value.userId || !form.value.problemType || !form.value.responsibleParty) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await request.post('/aftersale', form.value)
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const viewDetail = (row) => {
  currentOrder.value = row
  detailDialogVisible.value = true
}

const revokeOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确定要撤销该售后单吗？撤销后将回滚未使用的补偿。', '提示', {
      type: 'warning'
    })
    await request.delete('/aftersale/' + id)
    ElMessage.success('撤销成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(loadData)
</script>
