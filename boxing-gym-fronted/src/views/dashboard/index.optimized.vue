<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRequest } from '@/composables/useRequest'
import { getFinanceStats, listMember } from '@/api/finance-order'
import { listTrainingRecord } from '@/api/training-record'
import { listCourseSchedule } from '@/api/course-schedule'
import { formatCurrency } from '@/utils/format'
import { Coin, User, Calendar, Wallet } from '@element-plus/icons-vue'

interface StatCard {
  title: string
  value: number | string
  icon: string
  color: string
  trend?: 'up' | 'down' | 'flat'
}

// 使用 useRequest 管理请求
const { loading: statsLoading, execute: executeStatsRequest } = useRequest()

const stats = ref<StatCard[]>([
  { title: '会员总数', value: 0, icon: 'User', color: '#409eff', trend: 'up' },
  { title: '今日签到', value: 0, icon: 'CircleCheck', color: '#67c23a', trend: 'up' },
  { title: '今日课程', value: 0, icon: 'Calendar', color: '#e6a23c', trend: 'flat' },
  { title: '今日收入', value: '0.00', icon: 'Wallet', color: '#f56c6c', trend: 'up' }
])

const recentOrders = ref<any[]>([])
const recentCheckIns = ref<any[]>([])

const loadDashboardData = async () => {
  try {
    // 并行加载统计数据
    const [statsRes, ordersRes] = await Promise.all([
      // 获取会员总数
      listMember({ pageSize: 1 }).then(res => res.total),
      // 获取今日收入
      executeStatsRequest(() => getFinanceStats({ startDate: getToday(), endDate: getToday() })),
      // 获取最近订单
      getFinanceStats({ pageSize: 5 })
    ])

    // 更新统计数据
    stats.value[0].value = statsRes || 0
    stats.value[3].value = formatCurrency(ordersRes.totalIncome || 0)

    // 获取今日签到
    const checkInRes = await listTrainingRecord({ pageSize: 5 })
    stats.value[1].value = checkInRes.total || 0

    // 获取今日课程
    const scheduleRes = await listCourseSchedule({ startDate: getToday(), endDate: getToday() })
    stats.value[2].value = scheduleRes.total || 0

  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
}

const getToday = (): string => {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  loadDashboardData()
})
</script>

<template>
  <div class="dashboard" v-loading="statsLoading">
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

    <!-- 图表区域 - 占位符（可选：后续可集成 ECharts） -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <span>近7天收入趋势</span>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表功能开发中..." />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <span>课程类型分布</span>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表功能开发中..." />
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
          <el-table :data="recentOrders" stripe max-height="320px">
            <el-table-column prop="orderNo" label="订单号" width="140" />
            <el-table-column prop="memberName" label="会员" />
            <el-table-column prop="amount" label="金额">
              <template #default="{ row }">
                {{ formatCurrency(row.amount) }}
              </template>
            </el-table-column>
            <el-table-column prop="type" label="类型" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.type === 1" type="success">充值</el-tag>
                <el-tag v-else-if="row.type === 2" type="info">退款</el-tag>
                <el-tag v-else-if="row.type === 3" type="warning">扣费</el-tag>
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
          <el-table :data="recentCheckIns" stripe max-height="320px">
            <el-table-column prop="memberName" label="会员" />
            <el-table-column prop="courseName" label="课程" />
            <el-table-column prop="checkinTime" label="签到时间" width="160" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.status === 1" type="success">已签到</el-tag>
                <el-tag v-else type="info">未签到</el-tag>
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
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
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
