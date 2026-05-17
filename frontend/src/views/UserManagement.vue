<template>
  <div>
    <h2 style="margin-bottom: 20px;">用户管理</h2>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header><span>用户列表</span></template>
          <el-table :data="users" border>
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="userName" label="用户名" width="120" />
            <el-table-column prop="level" label="用户等级" width="120">
              <template #default="scope">
                <el-tag :type="getLevelTagType(scope.row.level)">{{ getEnumDesc(userLevels, scope.row.level) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="balance" label="账户余额">
              <template #default="scope">¥{{ scope.row.balance }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>风控限制用户</span>
              <el-button type="primary" size="small" @click="loadData">刷新</el-button>
            </div>
          </template>
          <el-table :data="riskUsers" border v-if="riskUsers.length > 0">
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="compensationCount" label="赔付次数" width="100" />
            <el-table-column prop="reason" label="限制原因" />
            <el-table-column prop="endTime" label="限制到期" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button type="primary" size="small" @click="removeRestriction(scope.row.userId)">解除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无风控限制用户" />
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header><span>风控说明</span></template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="risk-item">
            <div class="risk-title">高频赔付检测</div>
            <div class="risk-desc">7天内赔付次数≥3次</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="risk-item">
            <div class="risk-title">限制周期</div>
            <div class="risk-desc">自动限制30天</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="risk-item">
            <div class="risk-title">限制内容</div>
            <div class="risk-desc">禁止申请新的赔付</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="risk-item">
            <div class="risk-title">人工干预</div>
            <div class="risk-desc">可手动解除限制</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const users = ref([])
const riskUsers = ref([])
const userLevels = ref([])

const loadData = async () => {
  try {
    const [userData, riskData, ul] = await Promise.all([
      request.get('/users'),
      request.get('/users/risk'),
      request.get('/enums/user-levels')
    ])
    users.value = userData
    riskUsers.value = riskData
    userLevels.value = ul
  } catch (e) {
    console.error(e)
  }
}

const getEnumDesc = (list, name) => {
  const item = list.find(i => i.name === name)
  return item ? item.value : name
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

const removeRestriction = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要解除该用户的风控限制吗？', '提示', { type: 'warning' })
    await request.post('/users/risk/' + userId + '/remove')
    ElMessage.success('解除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(loadData)
</script>

<style scoped>
.risk-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}
.risk-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #303133;
}
.risk-desc {
  color: #909399;
  font-size: 14px;
}
</style>
