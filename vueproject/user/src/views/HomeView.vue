<template>
  <div class="homepage-container">
    <el-affix :offset="0">
      <el-menu
        :default-active="activeIndex"
        class="navbar-menu"
        mode="horizontal"
        router
        :ellipsis="false"
        background-color="#1a1a1a" 
        text-color="#ffffff"       
        active-text-color="#00d4ff"
      >
        <div class="nav-logo" @click="$router.push('/')">GAME</div>
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/news">游戏资讯</el-menu-item>
        <el-menu-item index="/products">游戏商城</el-menu-item>
        <el-menu-item index="/community">社区论坛</el-menu-item>

        <div class="flex-grow" />

        <div class="user-section">
          <el-input
            v-model="searchQuery"
            placeholder="搜索..."
            :prefix-icon="Search"
            class="search-bar"
            clearable
          />
          <el-dropdown trigger="hover">
            <div class="user-avatar-wrapper">
              <el-avatar :size="30" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="user-name">管理员</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人空间</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-menu>
    </el-affix>

    <section class="hero-carousel">
      <el-carousel height="500px" >
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-box" :style="{ backgroundImage: `url(${item.img})` }">
            <div class="banner-mask">
              <div class="banner-text">
                <h1>{{ item.title }}</h1>
                <p>{{ item.desc }}</p>
                <el-button type="primary" size="large" @click="$router.push('/news')" round>
                  查看最新资讯
                </el-button>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <main class="main-content">
      <section class="content-section">
        <div class="section-header">
          <div class="title-left">
            <el-icon><HotWater /></el-icon>
            <span>热门资讯</span>
          </div>
          <el-link type="primary" :underline="false" @click="$router.push('/news')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-link>
        </div>

        <el-row :gutter="20">
          <el-col :lg="12" :md="12" :sm="24" :xs="24" v-for="news in newsList" :key="news.newsId">
            <el-card 
              :body-style="{ padding: '0px' }" 
              class="news-card" 
              shadow="hover"
              @click="$router.push(`/news/${news.newsId}`)"
            >
              <div class="news-flex-container">
                <div class="news-cover">
                  <el-image :src="news.coverImage" fit="cover" />
                  <span class="news-tag">{{ news.category }}</span>
                </div>
                <div class="news-info">
                  <h3 class="news-title">{{ news.title }}</h3>
                  <p class="news-summary">{{ news.summary }}</p>
                  <div class="news-meta">
                    <span><el-icon><Calendar /></el-icon> {{ news.publishTime.split('T')[0] }}</span>
                    <el-button link type="primary">阅读详情</el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </section>
    </main>

    <footer class="site-footer">
      <p>GAME HUB &copy; 2025. All Rights Reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import { Search, Calendar, ArrowRight, HotWater } from '@element-plus/icons-vue';

const route = useRoute();
const activeIndex = ref(route.path);
const searchQuery = ref('');

const banners = ref([
  { id: 1, title: '探索无限游戏世界', desc: '最新版本动态一网打尽', img: 'https://my-web-fenghongtao.oss-cn-hangzhou.aliyuncs.com/2025/12/9f076501-3da9-405f-a229-d7f13eb52214.png' },
  { id: 2, title: '', desc: '', img: 'https://my-web-fenghongtao.oss-cn-hangzhou.aliyuncs.com/2025/12/8e5345b5-c22c-4685-ba2f-1f4cc8cb6cc1.png' },
  { id: 3, title: '', desc: '', img: 'https://my-web-fenghongtao.oss-cn-hangzhou.aliyuncs.com/2025/12/905edb58-1467-4cc4-8924-2cd6ded44d6b.jpg' },
  { id: 4, title: '', desc: '', img: "https://my-web-fenghongtao.oss-cn-hangzhou.aliyuncs.com/2025/12/3367fc97-566d-48c4-9c3a-47ce9c0ae760.jpg" }
]);

const newsList = ref([
  {
    newsId: "1",
    title: "《原神》5.4版本更新公告",
    summary: "全新区域星海开放，更多精彩活动等你来战。玩家将体验全新的重力解谜机制与水下探索。内容包含全角色UP池信息及武器更新详情。",
    category: "更新",
    publishTime: "2025-12-15T18:00:00",
    coverImage: "https://via.placeholder.com/400x240?text=Genshin"
  },
  {
    newsId: "2",
    title: "2025 年度电竞外设横评",
    summary: "谁才是今年的性价比之王？我们测试了30款热门鼠标与键盘，为你选出最适合竞技的装备。",
    category: "评测",
    publishTime: "2025-12-14T10:00:00",
    coverImage: "https://via.placeholder.com/400x240?text=Hardware"
  }
]);
</script>

<style scoped>
.homepage-container {
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 1. 导航栏深度修正 */
.navbar-menu {
  padding: 0 10%; /* 两边留白，代替 max-width 防止断层 */
  background-color: #0d0d0d !important;
  border-bottom: 1px solid #131313 !important;
  display: flex;
  align-items: center;
}

.nav-logo {
  color: #00d4ff;
  font-size: 22px;
  font-weight: 800;
  margin-right: 30px;
  cursor: pointer;
  line-height: 60px; /* 与菜单对齐 */
}

.flex-grow { flex-grow: 1; }

.user-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-bar {
  width: 200px;
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  cursor: pointer;
}

/* 2. 轮播图样式 */
.banner-box {
  height: 500px;
  background-size: cover;
  background-position: center;
}
.banner-mask {
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}
.banner-text h1 { font-size: 48px; color: #fff; margin-bottom: 15px; }
.banner-text p { font-size: 18px; color: #ddd; margin-bottom: 30px; }

/* 3. 内容区样式 */
.main-content {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}
.title-left {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  color: #141515;
}

/* 资讯卡片修正：防止挤压 */
.news-card {
  margin-bottom: 20px;
  cursor: pointer;
  border: none;
}
.news-flex-container {
  display: flex;
  height: 180px;
}
.news-cover {
  width: 35%;
  position: relative;
  overflow: hidden;
}
.news-cover .el-image {
  width: 100%;
  height: 100%;
}
.news-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #00d4ff;
  color: white;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 4px;
}
.news-info {
  width: 65%;
  padding: 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.news-title {
  margin: 0;
  font-size: 18px;
  line-height: 1.4;
  /* 标题截断 */
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-summary {
  font-size: 14px;
  color: #666;
  margin: 10px 0;
  /* 摘要截断 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

/* 页脚 */
.site-footer {
  padding: 40px 0;
  background: #1a1a1a;
  color: #666;
  text-align: center;
  margin-top: 60px;
}

/* 针对小屏幕的优化 */
@media (max-width: 768px) {
  .news-flex-container { height: auto; flex-direction: column; }
  .news-cover, .news-info { width: 100%; }
  .news-cover { height: 150px; }
  .navbar-menu { padding: 0 20px; }
  .search-bar { display: none; } /* 手机端隐藏搜索框防挤爆 */
}
</style>