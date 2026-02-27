<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getFinanceStats } from '@/api/finance-order'
import { formatCurrency, getDictLabel } from '@/utils/format'
import { MEMBER_STATUS, COACH_STATUS, PAYMENT_STATUS } from '@/constants/dict'

interface StatCard {
  title: string
  value: string | number
  icon: string
  color: string
}

const loading = ref(false)
const stats = ref<StatCard[]>([
  { title: '会员总数', value: 0, icon: 'UserFilled', color: '#409eff' },
  { title: '今日签到', value: 0, icon: 'CircleCheck', color: '#67c23a' },
  { title: '今日课程', value: 0, icon: 'Reading', color: '#e6a23c' },
  { title: '今日收入', value: 0, icon: 'Wallet', color: '#f56c6c' }
])

const recentOrders = ref<any[]>([])
const recentCheckIns = ref<any[]>([])

onMounted(async () => {
  await loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    // 模拟数据，实际应从API获取
    stats.value[0].value = 156
    stats.value[1].value = 42
    stats.value[2].value = 8
    stats.value[3].value = formatCurrency(5680)

    recentOrders.value = [
      { id: 1, orderNo: 'ORD202401001', memberName: '张三', amount: 599, paymentStatus: '1', createTime: '2024-01-15 10:30:00' },
      { id: 2, orderNo: 'ORD202401002', memberName: '李四', amount: 299, paymentStatus: '1', createTime: '2024-01-15 11:20:00' },
      { id: 3, orderNo: 'ORD202401003', memberName: '王五', amount: 1999, paymentStatus: '0', createTime: '2024-01-15 14:15:00' }
    ]

    recentCheckIns.value = [
      { id: 1, memberName: '张三', courseName: '拳击基础', checkInTime: '2024-01-15 09:00:00', status: '0' },
      { id: 2, memberName: '李四', courseName: '泰拳入门', checkInTime: '2024-01-15 10:30:00', status: '0' },
      { id: 3, memberName: '赵六', courseName: '体能训练', checkInTime: '2024-01-15 14:00:00', status: '1' }
    ]
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="dashboard" v-loading="loading">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="stat in stats" :key="stat.title">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="40" :color="stat.color">
              <component :is="stat.icon" />
            </el-icon>
            <div class="stat-info">
              <div class="stat-title">{{ stat.title }}</div>
              <div class="stat-value">{{ stat.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <span>近7天收入趋势</span>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表区域" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <span>课程类型分布</span>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表区域" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-row :gutter="20" class="tables-row">
      <el-col :xs="24" :lg="12">
        <el-card class="table-card">
          <template #header>
            <span>最近订单</span>
          </template>
          <el-table :data="recentOrders" stripe>
            <el-table-column prop="orderNo" label="订单号" width="120" />
            <el-table-column prop="memberName" label="会员" />
            <el-table-column prop="amount" label="金额">
              <template #default="{ row }">
                {{ formatCurrency(row.amount) }}
              </template>
            </el-table-column>
            <el-table-column prop="paymentStatus" label="支付状态">
              <template #default="{ row }">
                <el-tag :type="row.paymentStatus === '1' ? 'success' : 'warning'">
                  {{ getDictLabel(PAYMENT_STATUS, row.paymentStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="table-card">
          <template #header>
            <span>最近签到</span>
          </template>
          <el-table :data="recentCheckIns" stripe>
            <el-table-column prop="memberName" label="会员" />
            <el-table-column prop="courseName" label="课程" />
            <el-table-column prop="checkInTime" label="签到时间" width="160" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === '0' ? 'success' : 'info'">
                  {{ getDictLabel(CHECKIN_STATUS, row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.charts-row,
.tables-row {
  margin-bottom: 20px;
}

.chart-card,
.table-card {
  height: 400px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.table-card :deep(.el-card__body) {
  height: 320px;
  overflow-y: auto;
}
</style>
