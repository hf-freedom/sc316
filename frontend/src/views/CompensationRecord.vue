<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2>赔付记录管理</h2>
      <el-button type="primary" @click="loadData" icon="Refresh">刷新数据</el-button>
    </div>

    <el-card style="margin-bottom: 20px;">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态筛选">
          <el-radio-group v-model="activeTab" @change="activeTab = $event">
            <el-radio-button value="all">全部</el-radio-button>
            <el-radio-button value="pendingApproval">待审批</el-radio-button>
            <el-radio-button value="approved">已通过</el-radio-button>
            <el-radio-button value="rejected">已拒绝</el-radio-button>
            <el-radio-button value="pendingIssue">待发放</el-radio-button>
            <el-radio-button value="issued">已发放</el-radio-button>
            <el-radio-button value="rollback">已回滚</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable style="width: 120px;" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="displayRecords" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="orderNo" label="订单号" width="140" fixed="left" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="userLevel" label="用户等级" width="100">
          <template #default="scope">
            <el-tag :type="getLevelTagType(scope.row.userLevel)" size="small">
              {{ getEnumDesc(userLevels, scope.row.userLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderAmount" label="订单金额" width="100" align="right">
          <template #default="scope">¥{{ scope.row.orderAmount }}</template>
        </el-table-column>
        <el-table-column prop="compensationAmount" label="赔付金额" width="110" align="right">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.compensationAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审批流转状态" width="180">
          <template #default="scope">
            <div class="status-timeline">
              <div class="timeline-item" :class="{ active: isStepActive(scope.row, 1) }">
                <div class="timeline-dot"></div>
                <div class="timeline-text">提交申请</div>
              </div>
              <div class="timeline-line" :class="{ active: isStepActive(scope.row, 2) }"></div>
              <div class="timeline-item" :class="{ active: isStepActive(scope.row, 2), rejected: scope.row.status === 'REJECTED' }">
                <div class="timeline-dot"></div>
                <div class="timeline-text">主管审批</div>
              </div>
              <div class="timeline-line" :class="{ active: isStepActive(scope.row, 3) }"></div>
              <div class="timeline-item" :class="{ active: isStepActive(scope.row, 3) }">
                <div class="timeline-dot"></div>
                <div class="timeline-text">发放赔付</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="当前状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="dark" size="small">
              {{ getEnumDesc(compensationStatuses, scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approver" label="审批人" width="100">
          <template #default="scope">{{ scope.row.approver || '-' }}</template>
        </el-table-column>
        <el-table-column prop="approveTime" label="审批时间" width="160">
          <template #default="scope">{{ scope.row.approveTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="compensationType" label="赔付方式" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.compensationType" type="success" size="small">
              {{ getEnumDesc(compensationTypes, scope.row.compensationType) }}
            </el-tag>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
            <el-button v-if="scope.row.status === 'PENDING_APPROVAL'" type="success" size="small" @click="openApproveDialog(scope.row)">审批</el-button>
            <el-button v-if="scope.row.status === 'PENDING_ISSUE'" type="warning" size="small" @click="openIssueDialog(scope.row)">发放</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>补偿券列表</span>
              <el-tag type="success" size="small">共 {{ coupons.length }} 张</el-tag>
            </div>
          </template>
          <el-table :data="coupons" border size="small">
            <el-table-column prop="id" label="券ID" width="180" show-overflow-tooltip />
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="amount" label="金额" width="100" align="right">
              <template #default="scope">¥{{ scope.row.amount }}</template>
            </el-table-column>
            <el-table-column prop="used" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.used ? 'info' : 'success'" size="small">
                  {{ scope.row.used ? '已使用' : '未使用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="expireTime" label="过期时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>余额发放记录</span>
              <el-tag type="warning" size="small">共 {{ balanceRecords.length }} 条</el-tag>
            </div>
          </template>
          <el-table :data="balanceRecords" border size="small">
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="amount" label="发放金额" width="100" align="right">
              <template #default="scope">
                <span style="color: #67c23a;">+¥{{ scope.row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="beforeBalance" label="变动前" width="100" align="right">
              <template #default="scope">¥{{ scope.row.beforeBalance }}</template>
            </el-table-column>
            <el-table-column prop="afterBalance" label="变动后" width="100" align="right">
              <template #default="scope">¥{{ scope.row.afterBalance }}</template>
            </el-table-column>
            <el-table-column prop="createTime" label="发放时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="detailDialogVisible" title="赔付记录详情" width="700px">
      <div v-if="currentRecord">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="订单号">{{ currentRecord.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentRecord.userId }}</el-descriptions-item>
          <el-descriptions-item label="用户等级">
            <el-tag :type="getLevelTagType(currentRecord.userLevel)">{{ getEnumDesc(userLevels, currentRecord.userLevel) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(currentRecord.status)" effect="dark">{{ getEnumDesc(compensationStatuses, currentRecord.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentRecord.orderAmount }}</el-descriptions-item>
          <el-descriptions-item label="赔付金额">
            <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">¥{{ currentRecord.compensationAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="赔付原因" :span="2">{{ currentRecord.reason }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">审批流转</el-divider>
        <el-steps :active="getStepIndex(currentRecord)" finish-status="success" size="small">
          <el-step title="提交申请" :description="currentRecord.createTime">
            <template #icon><el-icon><Check /></el-icon></template>
          </el-step>
          <el-step title="主管审批" :description="currentRecord.approveTime || '待审批'">
            <template #icon>
              <el-icon v-if="currentRecord.status === 'REJECTED'"><Close /></el-icon>
              <el-icon v-else-if="currentRecord.approver"><Check /></el-icon>
              <el-icon v-else><Clock /></el-icon>
            </template>
            <div v-if="currentRecord.approver" style="font-size: 12px; color: #67c23a;">
              审批人：{{ currentRecord.approver }}
              <span v-if="currentRecord.status === 'REJECTED'" style="color: #f56c6c;">（已拒绝）</span>
            </div>
          </el-step>
          <el-step title="发放赔付" :description="currentRecord.issueTime || '待发放'">
            <template #icon>
              <el-icon v-if="currentRecord.status === 'ISSUED'"><Check /></el-icon>
              <el-icon v-else-if="currentRecord.status === 'ROLLBACK'"><RefreshRight /></el-icon>
              <el-icon v-else><Clock /></el-icon>
            </template>
            <div v-if="currentRecord.compensationType" style="font-size: 12px; color: #67c23a;">
              赔付方式：{{ getEnumDesc(compensationTypes, currentRecord.compensationType) }}
            </div>
          </el-step>
        </el-steps>

        <el-divider content-position="left" v-if="currentRecord.status === 'PENDING_APPROVAL'">待审批</el-divider>
        <el-alert v-if="currentRecord.status === 'PENDING_APPROVAL'" type="warning" :closable="false">
          该赔付申请金额为 ¥{{ currentRecord.compensationAmount }}，已超过审批阈值 ¥100，需要主管审批。
          <template #title>待主管审批</template>
        </el-alert>
      </div>
    </el-dialog>

    <el-dialog v-model="approveDialogVisible" title="主管审批" width="500px">
      <div v-if="currentRecord">
        <el-descriptions :column="2" border size="small" style="margin-bottom: 20px;">
          <el-descriptions-item label="订单号">{{ currentRecord.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentRecord.userId }}</el-descriptions-item>
          <el-descriptions-item label="赔付金额">
            <span style="color: #f56c6c; font-weight: bold; font-size: 16px;">¥{{ currentRecord.compensationAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="赔付原因">{{ currentRecord.reason }}</el-descriptions-item>
        </el-descriptions>

        <el-form label-width="100px">
          <el-form-item label="审批人">
            <el-select v-model="approveForm.approver" placeholder="请选择审批人" style="width: 100%;">
              <el-option label="主管A" value="主管A" />
              <el-option label="主管B" value="主管B" />
              <el-option label="主管C" value="主管C" />
            </el-select>
          </el-form-item>
          <el-form-item label="审批意见" v-if="!approveForm.approved">
            <el-input v-model="approveForm.rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="doApprove(false)">拒绝申请</el-button>
        <el-button type="success" @click="doApprove(true)">通过审批</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="issueDialogVisible" title="发放赔付" width="500px">
      <div v-if="currentRecord">
        <el-descriptions :column="2" border size="small" style="margin-bottom: 20px;">
          <el-descriptions-item label="订单号">{{ currentRecord.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentRecord.userId }}</el-descriptions-item>
          <el-descriptions-item label="赔付金额">
            <span style="color: #f56c6c; font-weight: bold; font-size: 16px;">¥{{ currentRecord.compensationAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="赔付原因">{{ currentRecord.reason }}</el-descriptions-item>
        </el-descriptions>

        <el-form label-width="100px">
          <el-form-item label="赔付方式">
            <el-radio-group v-model="issueForm.compensationType">
              <el-radio value="COUPON">
                <el-icon><Discount /></el-icon> 补偿券（有效期30天）
              </el-radio>
              <el-radio value="BALANCE">
                <el-icon><Wallet /></el-icon> 账户余额
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="issueDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doIssue">确认发放</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Clock, Close, RefreshRight, Discount, Wallet } from '@element-plus/icons-vue'
import request from '../utils/request'

const records = ref([])
const coupons = ref([])
const balanceRecords = ref([])
const userLevels = ref([])
const compensationStatuses = ref([])
const compensationTypes = ref([])
const activeTab = ref('all')
const detailDialogVisible = ref(false)
const approveDialogVisible = ref(false)
const issueDialogVisible = ref(false)
const currentRecord = ref(null)
const searchForm = ref({ orderNo: '', userId: '' })
const approveForm = ref({ approver: '', approved: true, rejectReason: '' })
const issueForm = ref({ compensationId: '', compensationType: '' })

const displayRecords = computed(() => {
  let result = records.value

  if (searchForm.value.orderNo) {
    result = result.filter(r => r.orderNo.includes(searchForm.value.orderNo))
  }
  if (searchForm.value.userId) {
    result = result.filter(r => r.userId.includes(searchForm.value.userId))
  }

  const statusMap = {
    pendingApproval: ['PENDING_APPROVAL'],
    approved: ['APPROVED'],
    rejected: ['REJECTED'],
    pendingIssue: ['PENDING_ISSUE'],
    issued: ['ISSUED'],
    rollback: ['ROLLBACK', 'REVOKED']
  }

  if (activeTab.value !== 'all' && statusMap[activeTab.value]) {
    result = result.filter(r => statusMap[activeTab.value].includes(r.status))
  }

  return result
})

const loadData = async () => {
  try {
    const [data, couponData, balanceData, ul, cs, ct] = await Promise.all([
      request.get('/compensation'),
      request.get('/compensation/coupons'),
      request.get('/compensation/balance-records'),
      request.get('/enums/user-levels'),
      request.get('/enums/compensation-statuses'),
      request.get('/enums/compensation-types')
    ])
    records.value = data
    coupons.value = couponData
    balanceRecords.value = balanceData
    userLevels.value = ul
    compensationStatuses.value = cs
    compensationTypes.value = ct
  } catch (e) {
    console.error(e)
  }
}

const getEnumDesc = (list, name) => {
  if (!name) return '-'
  const item = list.find(i => i.name === name)
  return item ? item.value : name
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

const getLevelTagType = (level) => {
  const typeMap = {
    NORMAL: 'info',
    SILVER: '',
    GOLD: 'warning',
    PLATINUM: '',
    DIAMOND: 'danger'
  }
  return typeMap[level] || ''
}

const isStepActive = (row, step) => {
  const statusSteps = {
    PENDING_APPROVAL: 1,
    APPROVED: 2,
    PENDING_ISSUE: 2,
    REJECTED: 2,
    ISSUED: 3,
    ROLLBACK: 3,
    REVOKED: 3
  }
  return (statusSteps[row.status] || 0) >= step
}

const getStepIndex = (row) => {
  const statusSteps = {
    PENDING_APPROVAL: 0,
    APPROVED: 1,
    PENDING_ISSUE: 1,
    REJECTED: 1,
    ISSUED: 2,
    ROLLBACK: 2,
    REVOKED: 2
  }
  return statusSteps[row.status] || 0
}

const viewDetail = (row) => {
  currentRecord.value = row
  detailDialogVisible.value = true
}

const openApproveDialog = (row) => {
  currentRecord.value = row
  approveForm.value = { approver: '', approved: true, rejectReason: '' }
  approveDialogVisible.value = true
}

const doApprove = async (approved) => {
  if (!approveForm.value.approver) {
    ElMessage.warning('请选择审批人')
    return
  }
  if (!approved && !approveForm.value.rejectReason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  try {
    const msg = approved ? '确定要通过该赔付申请吗？' : '确定要拒绝该赔付申请吗？'
    await ElMessageBox.confirm(msg, '确认审批', { type: 'warning' })
    await request.post('/compensation/approve', {
      compensationId: currentRecord.value.id,
      approver: approveForm.value.approver,
      approved,
      rejectReason: approved ? '' : approveForm.value.rejectReason
    })
    ElMessage.success(approved ? '审批通过' : '已拒绝')
    approveDialogVisible.value = false
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const openIssueDialog = (row) => {
  currentRecord.value = row
  issueForm.value = { compensationId: row.id, compensationType: '' }
  issueDialogVisible.value = true
}

const doIssue = async () => {
  if (!issueForm.value.compensationType) {
    ElMessage.warning('请选择赔付方式')
    return
  }
  try {
    await ElMessageBox.confirm('确定要发放该赔付吗？发放后将不可撤销。', '确认发放', { type: 'warning' })
    await request.post('/compensation/issue', issueForm.value)
    ElMessage.success('发放成功')
    issueDialogVisible.value = false
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(loadData)
</script>

<style scoped>
.status-timeline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
}
.timeline-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}
.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #dcdfe6;
  margin-bottom: 4px;
}
.timeline-item.active .timeline-dot {
  background: #67c23a;
}
.timeline-item.rejected .timeline-dot {
  background: #f56c6c;
}
.timeline-text {
  font-size: 11px;
  color: #909399;
  white-space: nowrap;
}
.timeline-item.active .timeline-text {
  color: #67c23a;
}
.timeline-line {
  flex: 1;
  height: 2px;
  background: #dcdfe6;
  margin: 0 -10px 14px -10px;
  z-index: 0;
}
.timeline-line.active {
  background: #67c23a;
}
</style>
