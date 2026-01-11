<template>
  <div>
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
  </div>

  <div class="news-container">
    <header class="section-header">
      <div class="header-left">
        <h2>🔥 游戏情报站</h2>
        <div class="tabs">
          <span 
            v-for="tab in categories" 
            :key="tab" 
            :class="{ active: currentTab === tab }"
            @click="handleTabChange(tab)"
          >
            {{ tab }}
          </span>
        </div>
      </div>
      <span v-if="loading" class="loading-text">
        <i class="loading-icon"></i> 数据同步中...
      </span>
    </header>

    <div v-if="newsList.length > 0" class="news-grid" :class="{ 'is-loading': loading }">
      <article v-for="item in newsList" :key="item.newsId" class="news-card">
        <div class="cover-wrapper">
          <el-image :src="item.coverImage" fit="cover" />
          <span class="category-tag">{{ item.category }}</span>
        </div>

        <div class="content">
          <h3 class="title line-clamp-1">{{ item.title }}</h3>
          <p class="summary line-clamp-2">{{ item.summary }}</p>
          
          <div class="meta">
            <!-- <el-icon size="16"><User/></el-icon> -->
            <span class="author">{{ item.author }}</span>
            <span class="time">{{ formatDate(item.publishTime) }}</span>
          </div>

          <div class="card-footer">
            <div class="stats">
              <el-icon><View /></el-icon>
              <span> {{ item.viewCount }}</span>
            </div>
            
            <div class="actions-right">
              <el-button 
                class="like-btn-list"
                :class="{ 'is-active': item.isLiked }"
                link
                @click.stop="handleListLike(item)"
              > 
                 <el-icon><Star/></el-icon>
                <span class="like-count">{{ item.likeCount || 0 }}</span>
              </el-button>

              <button class="read-more" @click="handleReadMore(item.newsId)">阅读详情</button>
            </div>
          </div>
        </div>
      </article>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <div class="empty-icon">📂</div>
      <p>暂无该分类下的资讯内容</p>
    </div>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[8, 12, 24, 48]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
// 【修正】引入 CaretTop
import { Search, CaretTop, Star ,View,User} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { fetchNewsList, addNewsLike } from '@/api/news-api';

const router = useRouter();

// --- 响应式数据 ---
const newsList = ref([]);
const total = ref(0);
const loading = ref(false);
const currentTab = ref('全部');
const categories = ['全部', '角色更新', '版本预告', '区域更新', '活动公告', '剧情更新'];
const searchQuery = ref('');
const activeIndex = ref('/news');

const currentPage = ref(1);
const pageSize = ref(12);

const getNewsData = async () => {
  loading.value = true;
  try {
    const params = { 
      page: currentPage.value, 
      pageSize: pageSize.value,
      category: currentTab.value === '全部' ? '' : currentTab.value 
    }; 
    
    const response = await fetchNewsList(params);
    
    if (response.code === 1) {
      newsList.value = response.data.rows;
      total.value = response.data.total;
    }
  } catch (error) {
    console.error('API请求异常:', error);
  } finally {
    loading.value = false;
  }
};

const handleListLike = async (item) => {
  if (item.isLiked) {
    ElMessage.warning('这条资讯你已经点过赞啦');
    return;
  }

  const oldLikeCount = item.likeCount;
  
  // 乐观更新
  item.isLiked = true; 
  item.likeCount = (item.likeCount || 0) + 1;

  try {
    await addNewsLike({ newsId: item.newsId });
  } catch (error) {
    item.isLiked = false;
    item.likeCount = oldLikeCount;
    ElMessage.error('点赞失败，请稍后重试');
    console.error('点赞错误:', error);
  }
};

const handleTabChange = (tab) => {
  currentTab.value = tab;
  currentPage.value = 1;
  getNewsData();
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
  getNewsData();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  getNewsData();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

const handleReadMore = (newsId) => {
  router.push({ 
    name: 'NewsDetail', 
    params: { id: newsId } 
  });
};

onMounted(() => {
  getNewsData();
});
</script>

<style scoped>
/* 保持原有样式不变 */
.news-container {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 25px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}
.header-left h2 { margin: 0 0 15px 0; font-size: 1.8rem; color: #333; }
.tabs { display: flex; gap: 15px; }
.tabs span {
  cursor: pointer;
  padding: 6px 18px;
  border-radius: 20px;
  font-size: 14px;
  background: #f5f5f5;
  transition: all 0.3s;
}
.tabs span.active {
  background: #409eff;
  color: white;
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
}

.loading-text { font-size: 14px; color: #999; }

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
  transition: opacity 0.3s;
}
.news-grid.is-loading { opacity: 0.5; pointer-events: none; }

.news-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
}
.news-card:hover { 
  transform: translateY(-5px); 
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}
.cover-wrapper { position: relative; height: 180px; }
.cover-wrapper .el-image { width: 100%; height: 100%; }

.category-tag {
  position: absolute; top: 10px; left: 10px;
  background: rgba(0,0,0,0.6); color: white;
  padding: 3px 10px; font-size: 11px; border-radius: 4px;
}

.content { 
  padding: 15px; 
  flex: 1; 
  display: flex;
  flex-direction: column;
}
.title { font-size: 1.1rem; margin-bottom: 10px; font-weight: bold; color: #333; }
.summary { font-size: 13px; color: #666; margin-bottom: 15px; height: 40px; }
.meta { display: flex; justify-content: space-between; font-size: 12px; color: #999; margin-bottom: 12px; }

.card-footer {
  display: flex; 
  justify-content: space-between; 
  align-items: center;
  border-top: 1px solid #f5f5f5; 
  padding-top: 12px;
  margin-top: auto; 
}

.stats { font-size: 12px; color: #888; }

.actions-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.like-btn-list {
  color: #909399;
  transition: all 0.3s;
  padding: 0 5px;
}
.like-btn-list:hover {
  color: #F56C6C;
  transform: scale(1.1);
}
.like-btn-list.is-active {
  color: #F56C6C;
  font-weight: bold;
}
.like-count {
  margin-left: 4px;
  font-size: 13px;
}

.read-more {
  background: transparent; border: 1px solid #409eff; color: #409eff;
  padding: 4px 12px; border-radius: 4px; cursor: pointer; font-size: 13px;
  transition: all 0.3s;
}
.read-more:hover { background: #409eff; color: white; }

.pagination-container {
  margin-top: 50px;
  display: flex;
  justify-content: center;
  padding-bottom: 40px;
}

.line-clamp-1 { display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

.empty-state { text-align: center; padding: 100px 0; color: #999; }
.empty-icon { font-size: 3rem; margin-bottom: 10px; opacity: 0.5; }

.navbar-menu {
  padding: 0 10%;
  background-color: #0d0d0d !important;
  border-bottom: 1px solid #131313 !important;
  display: flex;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
}

.flex-grow { flex: 1; }

.user-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-bar { width: 200px; }

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  cursor: pointer;
}
.nav-logo {
  color: #00d4ff;
  font-size: 22px;
  font-weight: 800;
  margin-right: 30px;
  cursor: pointer;
  line-height: 60px;
}
</style>