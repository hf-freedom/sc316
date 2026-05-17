<template>
  <div>
    <h2>数据概览</h2>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">待审批</div>
            <div class="stat-value" style="color: #e6a23c;">{{ stats.pendingApproval }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">待发放</div>
            <div class="stat-value" style="color: #67c23a;">{{ stats.pendingIssue }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">异常高额</div>
            <div class="stat-value" style="color: #f56c6c;">{{ stats.highAmount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">风控用户</div>
            <div class="stat-value" style="color: #909399;">{{ stats.riskUsers }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>售后单列表</span>
              <el-button type="primary" size="small" @click="loadData">刷新</el-button>
            </div>
          </template>
          <el-table :data="afterSaleOrders" border size="small">
            <el-table-column prop="orderNo" label="订单号" width="150" />
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="problemType" label="问题类型" width="120">
              <template #default="scope">{{ getProblemTypeDesc(scope.row.problemType) }}</template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>赔付记录</span>
              <el-button type="primary" size="small" @click="loadData">刷新</el-button>
            </div>
          </template>
          <el-table :data="compensationRecords" border size="small">
            <el-table-column prop="orderNo" label="订单号" width="150" />
            <el-table-column prop="compensationAmount" label="赔付金额" width="100">
              <template #default="scope">¥{{ scope.row.compensationAmount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">{{ getStatusDesc(scope.row.status) }}</template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const stats = ref({ pendingApproval: 0, pendingIssue: 0, highAmount: 0, riskUsers: 0 })
const afterSaleOrders = ref([])
const compensationRecords = ref([])
const problemTypes = ref([])
const compensationStatuses = ref([])

const loadData = async () => {
  try {
    const [pendingApproval, pendingIssue, highAmount, riskUsers, orders, records, pt, cs] = await Promise.all([
      request.get('/compensation/pending-approval'),
      request.get('/compensation/pending-issue'),
      request.get('/compensation/high-amount'),
      request.get('/users/risk'),
      request.get('/aftersale'),
      request.get('/compensation'),
      request.get('/enums/problem-types'),
      request.get('/enums/compensation-statuses')
    ])
    stats.value = {
      pendingApproval: pendingApproval.length,
      pendingIssue: pendingIssue.length,
      highAmount: highAmount.length,
      riskUsers: riskUsers.length
    }
    afterSaleOrders.value = orders.slice(0, 5)
    compensationRecords.value = records.slice(0, 5)
    problemTypes.value = pt
    compensationStatuses.value = cs
  } catch (e) {
    console.error(e)
  }
}

const getProblemTypeDesc = (type) => {
  const item = problemTypes.value.find(i => i.name === type)
  return item ? item.value : type
}

const getStatusDesc = (status) => {
  const item = compensationStatuses.value.find(i => i.name === status)
  return item ? item.value : status
}

onMounted(loadData)
</script>

<style scoped>
.stat-item {
  text-align: center;
  padding: 10px 0;
}
.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
}
</style>
