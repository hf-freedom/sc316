<template>
  <div>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2>赔付规则配置</h2>
      <el-button type="primary" @click="loadData" icon="Refresh">刷新数据</el-button>
    </div>

    <el-alert
      title="赔付金额计算公式"
      type="info"
      :closable="false"
      style="margin-bottom: 20px;">
      <p>赔付金额 = 订单金额 × 问题类型比例 × 用户等级系数 × 责任方系数</p>
      <p>计算结果将根据规则的最小/最大金额限制进行调整</p>
    </el-alert>

    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>赔付规则配置</span>
          <el-tag type="info">可实时调整参数</el-tag>
        </div>
      </template>
      <el-table :data="rules" border>
        <el-table-column prop="problemType" label="问题类型" width="150">
          <template #default="scope">{{ getProblemTypeDesc(scope.row.problemType) }}</template>
        </el-table-column>
        <el-table-column prop="ratio" label="赔付比例" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.ratio" :min="0" :max="2" :step="0.1" :precision="2" @change="updateRule(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="minAmount" label="最低赔付(元)" width="150">
          <template #default="scope">
            <el-input-number v-model="scope.row.minAmount" :min="0" :precision="2" @change="updateRule(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="maxAmount" label="最高赔付(元)" width="150">
          <template #default="scope">
            <el-input-number v-model="scope.row.maxAmount" :min="0" :precision="2" @change="updateRule(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="是否启用" width="120">
          <template #default="scope">
            <el-switch v-model="scope.row.enabled" @change="updateRule(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" />
      </el-table>
    </el-card>

    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>同一订单多次赔付累计统计</span>
          <div>
            <el-tag type="success" size="small" style="margin-right: 10px;">
              订单总数：{{ orderStats.length }}
            </el-tag>
            <el-tag type="warning" size="small">
              超限订单：{{ exceedLimitCount }}
            </el-tag>
          </div>
        </div>
      </template>
      <el-table :data="orderStats" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="orderNo" label="订单号" width="140" fixed="left" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="compensationCount" label="赔付次数" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.compensationCount >= 3 ? 'warning' : 'info'">
              {{ scope.row.compensationCount }} 次
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalCompensationAmount" label="累计赔付(元)" width="130" align="right">
          <template #default="scope">
            <span :style="{ color: scope.row.exceedLimit ? '#f56c6c' : '#67c23a', fontWeight: 'bold' }">
              ¥{{ scope.row.totalCompensationAmount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="maxSingleAmount" label="单笔最高" width="110" align="right">
          <template #default="scope">¥{{ scope.row.maxSingleAmount }}</template>
        </el-table-column>
        <el-table-column prop="minSingleAmount" label="单笔最低" width="110" align="right">
          <template #default="scope">¥{{ scope.row.minSingleAmount }}</template>
        </el-table-column>
        <el-table-column prop="avgAmount" label="平均金额" width="110" align="right">
          <template #default="scope">¥{{ scope.row.avgAmount }}</template>
        </el-table-column>
        <el-table-column label="超限状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.exceedLimit ? 'danger' : 'success'" effect="dark" size="small">
              {{ scope.row.exceedLimit ? '已超限' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="firstCompensationTime" label="首次赔付" width="160" />
        <el-table-column prop="lastCompensationTime" label="最近赔付" width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewOrderDetail(scope.row)">明细</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="orderStats.length === 0" description="暂无订单赔付数据" />
    </el-card>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header><span>用户等级系数</span></template>
          <el-table :data="userLevels" border size="small">
            <el-table-column prop="value" label="等级" />
            <el-table-column label="系数">
              <template #default="scope">
                <el-tag>{{ getLevelRatio(scope.row.name) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>责任方系数</span></template>
          <el-table :data="responsibleParties" border size="small">
            <el-table-column prop="value" label="责任方" />
            <el-table-column label="系数">
              <template #default="scope">
                <el-tag>{{ getPartyRatio(scope.row.name) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>系统参数</span></template>
          <ul style="line-height: 2.2;">
            <li><strong>审批阈值：</strong>¥100（超过需主管审批）</li>
            <li><strong>每日赔付上限：</strong>¥500</li>
            <li><strong>订单累计限额：</strong>订单金额 × 2</li>
            <li><strong>高频赔付阈值：</strong>7天3次</li>
            <li><strong>定时扫描间隔：</strong>5分钟</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="detailDialogVisible" title="订单赔付明细" width="700px">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border size="small" style="margin-bottom: 20px;">
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
          <el-descriptions-item label="赔付次数">
            <el-tag :type="currentOrder.compensationCount >= 3 ? 'warning' : 'info'">
              {{ currentOrder.compensationCount }} 次
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="累计赔付金额">
            <span :style="{ color: currentOrder.exceedLimit ? '#f56c6c' : '#67c23a', fontWeight: 'bold', fontSize: '18px' }">
              ¥{{ currentOrder.totalCompensationAmount }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="单笔最高">¥{{ currentOrder.maxSingleAmount }}</el-descriptions-item>
          <el-descriptions-item label="单笔最低">¥{{ currentOrder.minSingleAmount }}</el-descriptions-item>
          <el-descriptions-item label="平均金额">¥{{ currentOrder.avgAmount }}</el-descriptions-item>
          <el-descriptions-item label="超限状态">
            <el-tag :type="currentOrder.exceedLimit ? 'danger' : 'success'" effect="dark">
              {{ currentOrder.exceedLimit ? '已超限' : '正常' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="风险提示" :span="2">
            <el-alert :title="currentOrder.limitTip" :type="currentOrder.exceedLimit ? 'error' : 'success'" :closable="false" show-icon />
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">赔付记录明细</el-divider>
        <el-table :data="currentOrder.compensationRecords" border size="small">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="id" label="赔付记录ID" width="180" show-overflow-tooltip />
          <el-table-column prop="problemTypeDesc" label="问题类型" width="120" />
          <el-table-column prop="amount" label="赔付金额" width="100" align="right">
            <template #default="scope">¥{{ scope.row.amount }}</template>
          </el-table-column>
          <el-table-column prop="statusDesc" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ scope.row.statusDesc }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="申请时间" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const rules = ref([])
const problemTypes = ref([])
const userLevels = ref([])
const responsibleParties = ref([])
const orderStats = ref([])
const detailDialogVisible = ref(false)
const currentOrder = ref(null)

const exceedLimitCount = computed(() => {
  return orderStats.value.filter(o => o.exceedLimit).length
})

const levelRatioMap = {
  NORMAL: 1.0,
  SILVER: 1.1,
  GOLD: 1.2,
  PLATINUM: 1.3,
  DIAMOND: 1.5
}

const partyRatioMap = {
  MERCHANT: 1.0,
  PLATFORM: 1.2,
  LOGISTICS: 0.8,
  CUSTOMER: 0.0
}

const loadData = async () => {
  try {
    const [data, pt, ul, rp, stats] = await Promise.all([
      request.get('/rules'),
      request.get('/enums/problem-types'),
      request.get('/enums/user-levels'),
      request.get('/enums/responsible-parties'),
      request.get('/compensation/order-stats')
    ])
    rules.value = data
    problemTypes.value = pt
    userLevels.value = ul
    responsibleParties.value = rp
    orderStats.value = stats
  } catch (e) {
    console.error(e)
  }
}

const getProblemTypeDesc = (type) => {
  const item = problemTypes.value.find(i => i.name === type)
  return item ? item.value : type
}

const getLevelRatio = (level) => {
  return levelRatioMap[level] || 1.0
}

const getPartyRatio = (party) => {
  return partyRatioMap[party] || 1.0
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

const updateRule = async (rule) => {
  try {
    await request.put('/rules', rule)
    ElMessage.success('更新成功')
  } catch (e) {
    console.error(e)
  }
}

const viewOrderDetail = (row) => {
  currentOrder.value = row
  detailDialogVisible.value = true
}

onMounted(loadData)
</script>
