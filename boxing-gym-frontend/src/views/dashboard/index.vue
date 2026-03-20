<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getFinanceStats } from '@/api/finance-order'
import { listMember } from '@/api/member'
import { listTrainingRecord } from '@/api/training-record'
import { listSchedule } from '@/api/course-schedule'
import { listFinanceOrder } from '@/api/finance-order'
import { listCourse } from '@/api/course'
import { formatCurrency, getDictLabel } from '@/utils/format'
import { CHECKIN_STATUS, PAYMENT_STATUS } from '@/constants/dict'

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

let revenueChart: echarts.ECharts | null = null
let courseTypeChart: echarts.ECharts | null = null

onMounted(async () => {
  await loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  revenueChart?.dispose()
  courseTypeChart?.dispose()
})

const handleResize = () => {
  revenueChart?.resize()
  courseTypeChart?.resize()
}

const loadData = async () => {
  loading.value = true
  try {
    const [memberRes, trainingRes, scheduleRes, financeStats, financeRes, courseRes] = await Promise.all([
      listMember({ pageNum: 1, pageSize: 1 }),
      listTrainingRecord({ pageNum: 1, pageSize: 9999 }),
      listSchedule({ pageNum: 1, pageSize: 9999 }),
      getFinanceStats({}),
      listFinanceOrder({ pageNum: 1, pageSize: 9999 }),
      listCourse({ pageNum: 1, pageSize: 9999 })
    ])

    const today = new Date().toISOString().slice(0, 10)
    const todayCheckInCount = (trainingRes.rows || []).filter((item: any) => {
      if (!item.checkinTime) return false;
      const checkinDate = item.checkinTime.slice(0, 10);
      return checkinDate === today;
    }).length
    const todayScheduleCount = (scheduleRes.rows || []).filter((item: any) =>
      item.scheduleDate === today
    ).length

    stats.value[0].value = memberRes.total || 0
    stats.value[1].value = todayCheckInCount
    stats.value[2].value = todayScheduleCount
    stats.value[3].value = formatCurrency(financeStats?.totalIncome || 0)

    recentOrders.value = (financeRes.rows || []).slice(0, 5)
    recentCheckIns.value = (trainingRes.rows || []).slice(0, 5)

    // 初始化图表
    initRevenueChart(financeRes.rows || [])
    initCourseTypeChart(courseRes.rows || [])
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  } finally {
    loading.value = false
  }
}

const initRevenueChart = (orders: any[]) => {
  const chartDom = document.getElementById('revenue-chart')
  if (!chartDom) return

  revenueChart = echarts.init(chartDom)

  // 计算近7天日期
  const dates: string[] = []
  const revenues: number[] = []
  for (let i = 6; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    const dateStr = date.toISOString().slice(0, 10)
    dates.push(dateStr.slice(5)) // MM-DD 格式

    // 计算当天收入
    const dayRevenue = orders
      .filter((o: any) => o.createTime?.slice(0, 10) === dateStr && o.paymentStatus === '1')
      .reduce((sum: number, o: any) => sum + (o.paidAmount || o.amount || 0), 0)
    revenues.push(dayRevenue)
  }

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const data = params[0]
        return `${data.name}<br/>收入: ¥${data.value.toFixed(2)}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: {
        color: '#666',
        formatter: (value: number) => value >= 1000 ? `${value / 1000}k` : value
      },
      splitLine: { lineStyle: { color: '#eee' } }
    },
    series: [{
      name: '收入',
      type: 'line',
      smooth: true,
      data: revenues,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      lineStyle: { color: '#409eff', width: 2 },
      itemStyle: { color: '#409eff' }
    }]
  }

  revenueChart.setOption(option)
}

const initCourseTypeChart = (courses: any[]) => {
  const chartDom = document.getElementById('course-type-chart')
  if (!chartDom) return

  courseTypeChart = echarts.init(chartDom)

  const groupCount = courses.filter((c: any) => c.courseType === 'group').length
  const privateCount = courses.filter((c: any) => c.courseType === 'private').length

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => `${params.name}: ${params.value} (${params.percent}%)`
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center'
    },
    series: [{
      name: '课程类型',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      labelLine: { show: false },
      data: [
        { value: groupCount, name: '团课', itemStyle: { color: '#67c23a' } },
        { value: privateCount, name: '私教课', itemStyle: { color: '#e6a23c' } }
      ]
    }]
  }

  courseTypeChart.setOption(option)
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
          <div id="revenue-chart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <span>课程类型分布</span>
          </template>
          <div id="course-type-chart" class="chart-container"></div>
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
                <el-tag :type="row.paymentStatus === 1 ? 'success' : 'warning'">
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
                <el-tag :type="row.status === 0 ? 'success' : 'info'">
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

.chart-container {
  height: 320px;
  width: 100%;
}

.table-card :deep(.el-card__body) {
  height: 320px;
  overflow-y: auto;
}
</style>
