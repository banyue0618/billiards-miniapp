<template>
  <div class="app-container">
    <!-- 顶部数据卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <div class="data-header">
            <span class="data-title">今日营收</span>
<!--            <el-tag size="small" effect="plain" type="success">同比 {{ overview.revenueGrowth }}%</el-tag>-->
          </div>
          <div class="data-body">
            <span class="data-value">¥{{ overview.todayRevenue }}</span>
          </div>
          <div class="data-footer">昨日：¥{{ overview.yesterdayRevenue }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <div class="data-header">
            <span class="data-title">今日订单</span>
<!--            <el-tag size="small" effect="plain" type="primary">同比 {{ overview.orderGrowth }}%</el-tag>-->
          </div>
          <div class="data-body">
            <span class="data-value">{{ overview.todayOrderCount }}</span>
          </div>
          <div class="data-footer">昨日：{{ overview.yesterdayOrderCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <div class="data-header">
            <span class="data-title">使用中桌台</span>
            <el-tag size="small" effect="plain" type="warning">使用率 {{ overview.tableUsageRate }}%</el-tag>
          </div>
          <div class="data-body">
            <span class="data-value">{{ overview.activeTables }}</span>
          </div>
          <div class="data-footer">总桌台：{{ overview.totalTables }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <div class="data-header">
            <span class="data-title">平均使用时长</span>
            <el-tag size="small" effect="plain" type="info">分钟/台</el-tag>
          </div>
          <div class="data-body">
            <span class="data-value">{{ overview.avgUsageTime }}</span>
          </div>
          <div class="data-footer">昨日：{{ overview.yesterdayAvgUsageTime }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查询条件 -->
    <el-card class="filter-container" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true">
        <el-form-item label="门店" prop="storeId">
          <el-select v-model="queryParams.storeId" placeholder="选择门店" clearable style="width: 200px">
            <el-option v-for="store in storeOptions" :key="store.id" :label="store.name" :value="store.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围" prop="dateRange">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Download" @click="handleExport">导出数据</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 营收趋势图表 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>营收趋势</span>
              <el-radio-group v-model="revenueChartType" size="small" @change="loadRevenueChart">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="revenueChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8" v-if="false">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>桌台使用率</span>
            </div>
          </template>
          <div ref="usageChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 时段分析和门店排行 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>每日时段分析</span>
            </div>
          </template>
          <div ref="hourlyChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>门店营收排行</span>
            </div>
          </template>
          <el-table :data="storeRanking" style="width: 100%">
            <el-table-column type="index" label="排名" width="80" align="center" />
            <el-table-column prop="storeName" label="门店名称" />
            <el-table-column prop="revenue" label="营收金额">
              <template #default="scope"> ¥{{ parseFloat(scope.row.totalRevenue).toFixed(2) }} </template>
            </el-table-column>
            <el-table-column prop="orderCount" label="订单数" align="center" />
            <el-table-column prop="growth" label="同比增长" align="center">
              <template #default="scope">
                <span :class="scope.row.growth >= 0 ? 'text-success' : 'text-danger'"> {{ scope.row.growth }}% </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门桌台和计费规则分析 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>热门桌台 TOP10</span>
            </div>
          </template>
          <el-table :data="topTables" style="width: 100%">
            <el-table-column type="index" label="排名" width="80" align="center" />
            <el-table-column prop="storeName" label="所属门店" />
            <el-table-column prop="tableNumber" label="桌台编号" align="center" />
            <el-table-column prop="usageCount" label="使用次数" align="center" />
            <el-table-column prop="usageTime" label="使用时长（分钟）" align="center" min-width="120px" />
<!--            <el-table-column prop="totalRevenue" label="收入（元）" align="center" />-->
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>计费规则分析</span>
            </div>
          </template>
          <div ref="priceRuleChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import * as echarts from 'echarts';
import { listStore } from '@/api/billiards/store';
import {
  getDashboardData,
  getRevenueChart,
  getTableUsageChart,
  getHourlyAnalysis,
  getStoreRanking,
  getTopTables,
  getPriceRuleAnalysis,
  exportDashboardData
} from '@/api/billiards/dashboard';

// 图表实例
const revenueChart = ref();
const usageChart = ref();
const hourlyChart = ref();
const priceRuleChart = ref();
let revenueChartInstance: any = null;
let usageChartInstance: any = null;
let hourlyChartInstance: any = null;
let priceRuleChartInstance: any = null;

// 数据概览
const overview = reactive({
  todayRevenue: '0.00',
  yesterdayRevenue: '0.00',
  revenueGrowth: 0,
  todayOrderCount: 0,
  yesterdayOrderCount: 0,
  orderGrowth: 0,
  activeTables: 0,
  totalTables: 0,
  tableUsageRate: 0,
  avgUsageTime: 0,
  yesterdayAvgUsageTime: 0
});

// 门店选项
const storeOptions = ref<Array<any>>([]);
// 营收图表类型
const revenueChartType = ref('day');
// 日期范围
const dateRange = ref<string[]>([]);
// 门店排行数据
const storeRanking = ref<Array<any>>([]);
// 热门桌台数据
const topTables = ref<Array<any>>([]);

// 查询参数
const queryParams = reactive({
  storeId: undefined,
  startDate: undefined,
  endDate: undefined
});

const queryRef = ref();

/** 查询门店列表 */
function getStoreList() {
  listStore({ pageSize: 100 }).then((response) => {
    storeOptions.value = response.data.records;
  });
}

/** 加载概览数据 */
function loadOverview() {
  getDashboardData(queryParams).then((response) => {
    Object.assign(overview, response.data);
  });
}

/** 初始化图表 */
function initCharts() {
  revenueChartInstance = echarts.init(revenueChart.value);
  // usageChartInstance = echarts.init(usageChart.value);
  hourlyChartInstance = echarts.init(hourlyChart.value);
  priceRuleChartInstance = echarts.init(priceRuleChart.value);

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize);
}

/** 加载营收趋势图表 */
function loadRevenueChart() {
  getRevenueChart({ ...queryParams, chartType: revenueChartType.value }).then((response) => {
    const { xAxis, orderSeries, revenueSeries } = response.data;
    revenueChartInstance.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      legend: { data: ['订单数', '营收'] },
      xAxis: {
        type: 'category',
        data: xAxis
      },
      yAxis: [
        { type: 'value', name: '订单数', position: 'left' },
        { type: 'value', name: '营收（元）', position: 'right' }
      ],
      series: [
        {
          name: '订单数',
          type: 'bar',
          data: orderSeries,
          itemStyle: { color: '#409EFF' }
        },
        {
          name: '营收',
          type: 'line',
          yAxisIndex: 1,
          smooth: true,
          data: revenueSeries,
          itemStyle: { color: '#67C23A' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(103,194,58,0.25)' },
              { offset: 1, color: 'rgba(103,194,58,0.05)' }
            ])
          }
        }
      ]
    });
  });
}

/** 加载使用率图表 */
function loadUsageChart() {
  getTableUsageChart(queryParams).then((response) => {
    const { data } = response;
    usageChartInstance.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}台 ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center'
      },
      series: [
        {
          name: '桌台状态',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '16',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            { value: data.inUse, name: '使用中', itemStyle: { color: '#67C23A' } },
            { value: data.free, name: '空闲', itemStyle: { color: '#909399' } },
            { value: data.maintenance, name: '维修中', itemStyle: { color: '#E6A23C' } },
            { value: data.locked, name: '锁定', itemStyle: { color: '#F56C6C' } }
          ]
        }
      ]
    });
  });
}

/** 加载时段分析图表 */
function loadHourlyChart() {
  getHourlyAnalysis(queryParams).then((response) => {
    const { hours, orders, revenue } = response.data;
    hourlyChartInstance.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' }
      },
      legend: {
        data: ['订单数', '营收']
      },
      xAxis: {
        type: 'category',
        data: hours,
        axisLabel: {
          formatter: '{value}:00'
        }
      },
      yAxis: [
        {
          type: 'value',
          name: '订单数',
          position: 'left'
        },
        {
          type: 'value',
          name: '营收（元）',
          position: 'right'
        }
      ],
      series: [
        {
          name: '订单数',
          type: 'bar',
          data: orders,
          itemStyle: {
            color: '#409EFF'
          }
        },
        {
          name: '营收',
          type: 'line',
          yAxisIndex: 1,
          data: revenue,
          itemStyle: {
            color: '#67C23A'
          }
        }
      ]
    });
  });
}

/** 加载计费规则分析图表 */
function loadPriceRuleChart() {
  getPriceRuleAnalysis(queryParams).then((response) => {
    const { data } = response;
    priceRuleChartInstance.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}元 ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center'
      },
      series: [
        {
          name: '计费规则',
          type: 'pie',
          radius: '50%',
          data: data.map((item: any) => ({
            name: item.ruleName,
            value: item.totalRevenue
          })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    });
  });
}

/** 加载门店排行数据 */
function loadStoreRanking() {
  getStoreRanking(queryParams).then((response) => {
    storeRanking.value = response.data;
  });
}

/** 加载热门桌台数据 */
function loadTopTables() {
  getTopTables(queryParams).then((response) => {
    topTables.value = response.data;
  });
}

/** 处理窗口大小变化 */
function handleResize() {
  revenueChartInstance?.resize();
  usageChartInstance?.resize();
  hourlyChartInstance?.resize();
  priceRuleChartInstance?.resize();
}

/** 日期工具与默认范围 */
function formatDate(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function getDefaultDateRange(): string[] {
  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);
  return [formatDate(today), formatDate(tomorrow)];
}

/** 搜索按钮操作 */
function handleQuery() {
  let start: string;
  let end: string;

  if (dateRange.value && dateRange.value.length === 2) {
    [start, end] = dateRange.value;
  } else {
    [start, end] = getDefaultDateRange();
    dateRange.value = [start, end];
  }

  if (start > end) {
    ElMessage.error('开始日期不能晚于结束日期');
    return;
  }

  queryParams.startDate = start;
  queryParams.endDate = end;

  loadData();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryRef.value.resetFields();
  handleQuery();
}

/** 导出数据 */
function handleExport() {
  const exportParams = { ...queryParams };
  if (dateRange.value && dateRange.value.length === 2) {
    exportParams.startDate = dateRange.value[0];
    exportParams.endDate = dateRange.value[1];
  }

  ElMessageBox.confirm('是否确认导出仪表盘数据?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      return exportDashboardData(exportParams);
    })
    .then((response) => {
      const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = '营业数据报表.xlsx';
      link.click();
      URL.revokeObjectURL(link.href);
    });
}

/** 加载所有数据 */
function loadData() {
  loadOverview();
  loadRevenueChart();
  // loadUsageChart();
  loadHourlyChart();
  loadStoreRanking();
  loadTopTables();
  loadPriceRuleChart();
}

onMounted(() => {
  getStoreList();
  initCharts();
  loadData();
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  revenueChartInstance?.dispose();
  usageChartInstance?.dispose();
  hourlyChartInstance?.dispose();
  priceRuleChartInstance?.dispose();
});
</script>

<style scoped>
.mb-4 {
  margin-bottom: 16px;
}

.filter-container {
  margin-bottom: 16px;
}

.data-card {
  .data-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .data-title {
      font-size: 14px;
      color: #606266;
    }
  }

  .data-body {
    margin-bottom: 16px;

    .data-value {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
    }
  }

  .data-footer {
    font-size: 12px;
    color: #909399;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-success {
  color: #67c23a;
}

.text-danger {
  color: #f56c6c;
}
</style>
