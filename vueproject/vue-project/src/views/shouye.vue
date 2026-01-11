<template>
  <div class="dashboard-home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card" shadow="never">
          <div class="welcome-content">
            <div class="text-area">
              <h1>{{ welcomeText }}，管理员</h1>
              <p class="subtitle">欢迎回到游戏后台管理系统。今天有 {{ pendingTasks }} 条资讯待审核，请及时处理。</p>
              
              <div class="quick-actions">
                <el-button type="primary" @click="$router.push('/elements/news')">发布新资讯</el-button>
                <el-button @click="$router.push('/elements/fenxi')">查看数据报表</el-button>
              </div>
            </div>
            
            <div class="image-area">
              <img src= "https://my-web-fenghongtao.oss-cn-hangzhou.aliyuncs.com/2025/12/3dc1ec7c-3a12-499e-895b-a5fdfa6292ec.png" alt="welcome" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mini-stats">
      <el-col :span="8" v-for="item in statusSummary" :key="item.label">
        <el-card shadow="hover" class="status-item">
          <template #header>
            <div class="status-header">{{ item.label }}</div>
          </template>
          <div class="status-value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
const pendingTasks = ref(5);

const statusSummary = [
  { label: '在线用户', value: '1,284' },
  { label: '今日营收', value: '￥45,200' },
  { label: '系统状态', value: '正常' }
];

// 根据时间生成问候语
const welcomeText = computed(() => {
  const hour = new Date().getHours();
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 18) return '下午好';
  return '晚上好';
});
</script>

<style scoped>
.dashboard-home {
  padding: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #1a1a1a 0%, #0d0d0d 100%) !important;
  border: 1px solid #333 !important;
  border-radius: 12px;
  overflow: hidden;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40px;
  min-height: 300px;
}

.text-area h1 {
  font-size: 2.5rem;
  color: #00d4ff;
  margin-bottom: 15px;
  font-weight: 800;
}

.subtitle {
  color: #888;
  font-size: 1.1rem;
  margin-bottom: 30px;
  max-width: 500px;
  line-height: 1.6;
}

.quick-actions {
  display: flex;
  gap: 15px;
}

.image-area img {
  max-width: 400px;
  /* 增加一个悬浮动画效果 */
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
  100% { transform: translateY(0px); }
}

.mini-stats {
  margin-top: 30px;
}

.status-item {
  background: #161616 !important;
  border: 1px solid #222 !important;
  text-align: center;
}

.status-header {
  color: #888;
  font-size: 14px;
}

.status-value {
  color: #fff;
  font-size: 24px;
  font-weight: bold;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #333 !important;
}
</style>