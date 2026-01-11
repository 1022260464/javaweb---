<template>
  <div class="analysis-container">
    <header class="analysis-header">
      <h2>数据分析概览</h2>
      <el-button-group>
        <el-button type="primary" plain>最近7天</el-button>
        <el-button type="primary" plain>最近30天</el-button>
      </el-button-group>
    </header>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="stat in coreStats" :key="stat.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">{{ stat.title }}</div>
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
            {{ stat.trend > 0 ? '▲' : '▼' }} {{ Math.abs(stat.trend) }}%
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查询表单 -->
    <el-card class="query-card">
       <div class="header-title">
        <el-icon class="card-header-icon"><Search /></el-icon>
        <span>资讯查询条件</span>
        </div>
      <div class="query-form">
        <el-form :inline="true" :model="queryParams" class="demo-form-inline">
          <el-form-item label="显示数量">
            <el-select v-model="queryParams.topN" placeholder="选择数量">
              <el-option label="5条" value="5"></el-option>
              <el-option label="10条" value="10"></el-option>
              <el-option label="20条" value="20"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="分类">
            <el-select v-model="queryParams.category" placeholder="选择分类">
              <el-option label="全部" value=""></el-option>
              <el-option label="角色更新" value="角色更新"></el-option>
              <el-option label="玩法更新" value="玩法更新"></el-option>
              <el-option label="活动公告" value="活动公告"></el-option>
              <el-option label="版本预告" value="版本预告"></el-option>
              <el-option label="区域更新" value="区域更新"></el-option>
              <el-option label="剧情更新" value="剧情更新"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="排序方式">
            <el-select v-model="queryParams.orderBy" placeholder="选择排序">
              <el-option label="按浏览量" value="viewCount"></el-option>
              <el-option label="按点赞量" value="likeCount"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="chart-card">
      <template #header>
        <div class="card-header"><span>游戏资讯热度对比 (浏览量 vs 点赞量)</span></div>
      </template>
      <div ref="newsChartRef" class="chart-box"></div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header"><span>社区论坛：板块发帖占比</span></div>
          </template>
          <div ref="forumPieRef" class="chart-box"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header"><span>社区论坛：每日活跃用户 (DAU)</span></div>
          </template>
          <div ref="forumLineRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import * as echarts from 'echarts';
import { fenxiNews } from '@/api/news';
import { ElMessage } from 'element-plus';
import {Search} from '@element-plus/icons-vue';
// --- 模拟数据 ---
const coreStats = [
  { title: '总浏览量', value: '128.4k', trend: 12.5 },
  { title: '总点赞数', value: '12,543', trend: 8.2 },
  { title: '论坛新帖', value: '342', trend: -2.4 },
  { title: '活跃用户', value: '1,205', trend: 15.3 },
];

const newsChartRef = ref(null);
const forumPieRef = ref(null);
const forumLineRef = ref(null);

// 定义查询参数响应式变量
const queryParams = ref({
  topN: 5,
  category: "",
  orderBy: "view_count"
});

// 定义更新新闻图表的函数
const updateNewsChart = async () => {
  if (!newsChartRef.value) return;
  
  const newsChart = echarts.init(newsChartRef.value);
  const baseOptions = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['浏览量', '点赞量'], textStyle: { color: '#ccc' } },
    xAxis: {
      type: 'category',
      data: [],
      axisLabel: { 
        color: '#888',
        interval: 0, // 强制显示所有标签
        formatter: (value) => value.length > 8 ? value.slice(0, 8) + '...' : value // 标题过长截断
      }
    },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#333' } } },
    series: [
      {
        name: '浏览量',
        type: 'bar',
        data: [],
        itemStyle: { color: '#00d4ff' }
      },
      {
        name: '点赞量',
        type: 'bar',
        data: [],
        itemStyle: { color: '#fb7299' }
      }
    ]
  };
  
  try {
    // 传递查询参数到API接口
    const response = await fenxiNews(queryParams.value);
    const newsData = response.data;
    
    // 提取数据
    const titles = newsData.map(item => item.title);
    const viewCounts = newsData.map(item => item.viewCount);
    const likeCounts = newsData.map(item => item.likeCount);
    
    // 更新图表
    newsChart.setOption({
      ...baseOptions,
      xAxis: { ...baseOptions.xAxis, data: titles },
      series: [
        { ...baseOptions.series[0], data: viewCounts },
        { ...baseOptions.series[1], data: likeCounts }
      ]
    });
  } catch (error) {
    console.error('获取资讯热度数据失败:', error);
    
    // 使用模拟数据作为备选
    newsChart.setOption({
      ...baseOptions,
      xAxis: { ...baseOptions.xAxis, data: ['游戏攻略1', '游戏攻略2', '游戏攻略3', '游戏攻略4', '游戏攻略5'] },
      series: [
        { ...baseOptions.series[0], data: [1200, 1500, 1800, 1300, 2000] },
        { ...baseOptions.series[1], data: [800, 1200, 900, 1000, 1500] }
      ]
    });
  }
};

const initCharts = async () => {
  // 更新新闻图表
  await updateNewsChart();

  // 2. 论坛饼图
  const forumPie = echarts.init(forumPieRef.value);
  forumPie.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: [
        { value: 1048, name: '游戏攻略' },
        { value: 735, name: '联机找人' },
        { value: 580, name: '硬件外设' },
        { value: 484, name: '二手交易' }
      ],
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
    }]
  });

  // 3. 论坛折线图
  const forumLine = echarts.init(forumLineRef.value);
  forumLine.setOption({
    xAxis: { type: 'category', data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'] },
    yAxis: { type: 'value' },
    series: [{
      data: [820, 932, 901, 934, 1290, 1330, 1320],
      type: 'line',
      smooth: true,
      lineStyle: { color: '#00d4ff' },
      areaStyle: { color: 'rgba(0, 212, 255, 0.2)' }
    }]
  });
};

// 查询按钮点击事件
const handleQuery = () => {
  updateNewsChart();
};

onMounted(() => {
  nextTick(() => {
    initCharts();
  });
});
</script>

<style scoped>
.analysis-container {
  background-color: #0d0d0d;
  min-height: 100vh;
  padding: 30px;
  color: #fff;
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

/* 查询卡片样式 */
.query-card {
  background: #161616 !important;
  border: 1px solid #222 !important;
  margin-bottom: 30px;
}

.query-form {
  padding: 15px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.query-form .el-form-item {
  margin-right: 20px;
  width: 150px;
}
.demo-form-inline {
  display: flex;
  align-items: center;
 
}

/* 统计卡片样式 */
.stat-cards { margin-bottom: 30px; }
.stat-card {
  background: #161616 !important;
  border: 1px solid #222 !important;
  color: #fff;
  text-align: center;
}

.stat-title { color: #888; font-size: 14px; }
.stat-value { font-size: 28px; font-weight: bold; margin: 10px 0; color: #00d4ff; }
.stat-trend { font-size: 12px; }
.stat-trend.up { color: #4caf50; }
.stat-trend.down { color: #f44336; }

/* 图表卡片样式 */
.chart-card {
  background: #161616 !important;
  border: 1px solid #222 !important;
  margin-bottom: 20px;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #222 !important;
  color: #fff;
  font-weight: bold;
}

.chart-box {
  width: 100%;
  height: 350px;
}

/* 表单样式 */
:deep(.el-form-item__label) {
  color: #ccc !important;
}

:deep(.el-input__inner),
:deep(.el-select__input) {
  background-color: #222 !important;
  border-color: #333 !important;
  color: #fff !important;
}

:deep(.el-select-dropdown__item.selected) {
  color: #00d4ff !important;
}

:deep(.el-select-dropdown) {
  background-color: #222 !important;
  border-color: #333 !important;
}

:deep(.el-select-dropdown__item) {
  color: #ccc !important;
}

:deep(.el-select-dropdown__item:hover) {
  background-color: #333 !important;
}
.card-header-icon {
font-size: 18px;
  color: #00d4ff;
  filter: drop-shadow(0 0 5px rgba(0, 212, 255, 0.5));
}
.header-title {
  display: flex;
  align-items: center;
  gap: 10px; /* 图标和文字的间距 */
  color: #f2f4f5; 
  font-weight: 600;
  letter-spacing: 1px;
}

</style>