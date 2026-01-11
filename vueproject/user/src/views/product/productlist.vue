<template>
  <div class="navbar-wrapper">
    <el-affix :offset="0">
      <el-menu
        :default-active="activeIndex"
        class="navbar-menu"
        mode="horizontal"
        router
        :ellipsis="false"
        background-color="#0d0d0d"
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
          <el-input v-model="searchQuery.productName" placeholder="搜索游戏..." :prefix-icon="Search" class="search-bar" clearable />
          
          <el-button class="nav-cart-btn" @click="openCart" round>
            <el-icon :size="18" style="margin-right: 4px"><ShoppingCart /></el-icon>
            购物车
            <el-badge 
              :value="cartCount" 
              :hidden="cartCount === 0" 
              type="danger" 
              class="nav-cart-badge"
            />
          </el-button>

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

  <div class="mall-container">
    <header class="section-header">
       <div class="header-left">
        <h2>游戏商城</h2>
        <div class="tabs">
          <span 
            v-for="tab in categories" 
            :key="tab" 
            :class="{ active: currentTab === tab }"
            @click="handleCategoryChange(tab)"
          >
            {{ tab }}
          </span>
          <div class="header-right">
            <el-select v-model="sortBy" placeholder="排序方式" size="small" effect="dark" @change="fetchProductList">
              <el-option label="默认排序" value="default" />
              <el-option label="价格从低到高" value="price-asc" />
              <el-option label="评分最高" value="rating" />
            </el-select>
          </div>
        </div>
      </div>
    </header>

    <div v-if="productList.length > 0" class="product-grid" v-loading="loading" element-loading-background="rgba(0,0,0,0.8)">
      <article v-for="item in productList" :key="item.productId" class="product-card" @click="handleGoToDetail(item.productId)">
        <div class="cover-wrapper">
          <el-image :src="parseImages(item.productImages)[0]" fit="cover" lazy>
            <template #placeholder>
              <div class="image-slot">加载中...</div>
            </template>
          </el-image>
          <div class="category-tag">{{ item.category }}</div>
          <div class="rating-badge" v-if="item.rating"> {{ item.rating }}</div>
        </div>
        <div class="content">
          <h3 class="title line-clamp-1">{{ item.productName }}</h3>
          <p class="summary line-clamp-2">{{ item.description }}</p>
          <div class="supplier-info"><span>🏢 {{ item.supplierInfo }}</span></div>
          <div class="card-footer">
            <div class="price-box">
              <span class="currency">￥</span><span class="amount">{{ item.price || '398' }}</span>
            </div>
            <div class="actions">
              <el-button type="primary" circle class="add-cart-btn" @click.stop="addToCart(item)">
                <el-icon><Plus /></el-icon>
              </el-button>
              <button class="buy-btn" @click.stop="handleGoToDetail(item.productId)">详情</button>
            </div>
          </div>
        </div>
      </article>
    </div>
    
    <div v-else-if="!loading" class="empty-state">
      <div class="empty-icon">🛒</div><p>暂时没有找到相关游戏商品</p>
    </div>
    
    <div class="pagination-container">
      <el-pagination 
        v-model:current-page="currentPage" 
        v-model:page-size="pageSize" 
        :page-sizes="[12, 24, 36]" 
        background 
        layout="total, sizes, prev, pager, next" 
        :total="total" 
        @size-change="handleSizeChange" 
        @current-change="handleCurrentChange"
      />
    </div>
  </div>

  <div class="fixed-cart-btn" @click="openCart">
    <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99" class="fixed-badge">
      <el-button type="primary" circle size="large" class="float-btn">
        <el-icon :size="24"><ShoppingCart /></el-icon>
      </el-button>
    </el-badge>
  </div>

  <el-drawer
    v-model="cartVisible"
    title="我的购物车"
    direction="rtl"
    size="400px"
    class="dark-drawer"
    :append-to-body="true" 
  >
    <div class="cart-content">
      <div v-if="cartItems.length === 0" class="empty-cart">
        <el-icon :size="60" color="#333"><ShoppingCart /></el-icon>
        <p>购物车是空的，快去选购吧！</p>
      </div>

      <div v-else class="cart-list">
        <div v-for="item in cartItems" :key="item.productId" class="cart-item">
          <el-image :src="parseImages(item.productImages)[0]" class="cart-thumb" fit="cover" />
          <div class="cart-info">
            <h4 class="line-clamp-1">{{ item.productName }}</h4>
            <div class="cart-price">￥{{ item.price }}</div>
            <div class="cart-controls">
              <el-input-number 
                v-model="item.quantity" 
                :min="1" 
                :max="99" 
                size="small" 
                class="dark-input-number"
              />
              <el-button type="danger" link @click="removeFromCart(item.productId)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="cart-footer" v-if="cartItems.length > 0">
        <div class="total-row">
          <span>总计:</span>
          <span class="total-price">￥{{ cartTotal }}</span>
        </div>
        <el-button 
          type="primary" 
          class="checkout-btn" 
          size="large" 
          :loading="submitting"
          @click="submitOrder"
        >
          {{ submitting ? '处理中...' : `立即结算 (${cartCount})` }}
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
// 引入图标
import { Search, ShoppingCart, Plus, Delete } from '@element-plus/icons-vue';
import { ProductList } from '@/api/news-api';
import { ElMessage } from 'element-plus'; 

const router = useRouter();
const activeIndex = ref('/products');

// --- 状态数据 ---
const loading = ref(false);
const productList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(12);
const currentTab = ref('全部');
const sortBy = ref('default');
const searchQuery = ref({ productName: '', category: '', price: undefined, status: '', page: 1, pageSize: 10 });
const categories = ['全部', 'action rpg', 'rpg', 'adventure', 'action', 'co-op'];

// --- 购物车逻辑 ---
const cartVisible = ref(false);
const cartItems = ref([]);
const submitting = ref(false); // 提交状态

// 计算属性：总数量
const cartCount = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0));
// 计算属性：总金额
const cartTotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (parseFloat(item.price) || 0) * item.quantity, 0).toFixed(2);
});

// 打开购物车
const openCart = () => { cartVisible.value = true; };

// 添加商品到购物车
const addToCart = (product) => {
  const existingItem = cartItems.value.find(item => item.productId === product.productId);
  if (existingItem) {
    existingItem.quantity += 1;
  } else {
    // 确保价格存在，如果接口没返回就给默认值
    cartItems.value.push({ 
      ...product, 
      price: product.price || '398', 
      quantity: 1 
    });
  }
  ElMessage.success({ message: '已加入购物车', type: 'success', duration: 1500, offset: 60 });
};

// 移除商品
const removeFromCart = (productId) => {
  cartItems.value = cartItems.value.filter(item => item.productId !== productId);
};

// 提交订单
const submitOrder = async () => {
  if (cartItems.value.length === 0) return;
  
  submitting.value = true;
  try {
    // 模拟API延时
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    // 这里打印要发送给后端的数据
    console.log('Order Data:', {
      items: cartItems.value.map(i => ({ id: i.productId, count: i.quantity })),
      total: cartTotal.value
    });

    ElMessage.success(`支付成功！共消费 ￥${cartTotal.value}`);
    
    // 清空购物车并关闭
    cartItems.value = [];
    cartVisible.value = false;
  } catch (error) {
    ElMessage.error('结算失败');
  } finally {
    submitting.value = false;
  }
};

// --- 商品获取逻辑 ---
const parseImages = (imgStr) => {
  if (!imgStr) return ['https://via.placeholder.com/300x400'];
  try {
    const images = typeof imgStr === 'string' ? JSON.parse(imgStr) : imgStr;
    return Array.isArray(images) ? images : ['https://via.placeholder.com/300x400'];
  } catch (e) { return ['https://via.placeholder.com/300x400']; }
};

const fetchProductList = async () => {
  loading.value = true;
  try {
    searchQuery.value.page = currentPage.value;
    searchQuery.value.pageSize = pageSize.value;
    searchQuery.value.category = currentTab.value === '全部' ? '' : currentTab.value;
    searchQuery.value.sortBy = sortBy.value;
    const res = await ProductList(searchQuery.value);
    productList.value = res.data.rows || []; 
    total.value = res.data.total || 0;
  } catch (error) {
    console.error('获取商品列表失败:', error);
    productList.value = [];
  } finally { loading.value = false; }
};

const handleCategoryChange = (tab) => { currentTab.value = tab; currentPage.value = 1; fetchProductList(); };
const handleSizeChange = (val) => { pageSize.value = val; fetchProductList(); };
const handleCurrentChange = (val) => { currentPage.value = val; fetchProductList(); window.scrollTo({ top: 0, behavior: 'smooth' }); };
const handleGoToDetail = (productId) => { router.push({ name: 'ProductDetail', params: {id:productId } }); };

onMounted(fetchProductList);
</script>

<style scoped>
/* 导航栏样式增强 */
.nav-cart-btn {
  background-color: #1a1a1a;
  border: 1px solid #333;
  color: #fff;
  margin-right: 15px;
  transition: all 0.3s;
}
.nav-cart-btn:hover {
  background-color: #00d4ff;
  color: #000;
  border-color: #00d4ff;
}
:deep(.nav-cart-badge .el-badge__content) {
  border: none;
  margin-top: -2px;
}

/* 悬浮购物车按钮 */
.fixed-cart-btn {
  position: fixed;
  bottom: 40px;
  right: 40px;
  z-index: 2000;
  cursor: pointer;
}
.float-btn {
  width: 60px;
  height: 60px;
  background-color: #00d4ff;
  border: none;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
  color: #000;
}
.float-btn:hover {
  background-color: #fff;
  transform: scale(1.1);
}

/* 保持原有样式 */
:deep(body) { background-color: #0d0d0d; }
.mall-container { max-width: 1300px; margin: 40px auto; padding: 0 20px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; border-bottom: 1px solid #222; padding-bottom: 20px; }
.header-left h2 { margin: 0 0 15px 0; font-size: 2rem; color: #fff; letter-spacing: 1px; }
.tabs { display: flex; gap: 12px; align-items: center; width: 100%; }
.tabs span { cursor: pointer; padding: 8px 20px; border-radius: 4px; font-size: 14px; background: #1a1a1a; color: #aaa; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); border: 1px solid #333; white-space: nowrap;}
.tabs span.active { background: #00d4ff; color: #000; font-weight: bold; border-color: #00d4ff; box-shadow: 0 0 15px rgba(0, 212, 255, 0.4); }
.header-right { margin-left: auto; } /* 让排序框靠右 */

.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 30px; }
.product-card { background: #161616; border-radius: 12px; overflow: hidden; border: 1px solid #222; transition: all 0.4s ease; cursor: pointer; position: relative; }
.product-card:hover { transform: translateY(-8px); border-color: #00d4ff; box-shadow: 0 12px 30px rgba(0,0,0,0.5); }
.cover-wrapper { position: relative; height: 380px; overflow: hidden; }
.cover-wrapper .el-image { width: 100%; height: 100%; transition: transform 0.5s ease; }
.product-card:hover .el-image { transform: scale(1.1); }
.category-tag { position: absolute; top: 15px; left: 15px; background: rgba(0, 212, 255, 0.9); color: #000; padding: 4px 12px; font-size: 12px; font-weight: bold; border-radius: 4px; z-index: 2; }
.rating-badge { position: absolute; top: 15px; right: 15px; background: rgba(0,0,0,0.7); color: #ffcc00; padding: 4px 8px; border-radius: 4px; font-size: 13px; backdrop-filter: blur(4px); }
.content { padding: 20px; }
.title { font-size: 1.2rem; margin-bottom: 12px; color: #fff; font-weight: 600; }
.summary { font-size: 13px; color: #888; margin-bottom: 15px; line-height: 1.6; }
.supplier-info { font-size: 12px; color: #555; margin-bottom: 20px; }
.card-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #222; padding-top: 15px; }
.price-box { color: #00d4ff; }
.currency { font-size: 14px; font-weight: bold; }
.amount { font-size: 24px; font-weight: 800; font-family: 'Arial'; }
.actions { display: flex; gap: 10px; align-items: center; }
.buy-btn { background: transparent; border: 1px solid #444; color: #fff; padding: 8px 18px; border-radius: 6px; cursor: pointer; font-size: 13px; transition: 0.3s; }
.add-cart-btn { background: #1a1a1a; border: 1px solid #444; color: #00d4ff; transition: 0.3s; }
.add-cart-btn:hover { background: #00d4ff; border-color: #00d4ff; color: #000; }
.product-card:hover .buy-btn { background: transparent; border-color: #00d4ff; color: #00d4ff; }
.product-card:hover .buy-btn:hover { background: #00d4ff; color: #000; }
.pagination-container { margin-top: 60px; display: flex; justify-content: center; padding-bottom: 60px; }
:deep(.el-pagination.is-background .el-pager li:not(.is-active)) { background-color: #1a1a1a; color: #888; }
:deep(.el-pagination.is-background .el-pager li.is-active) { background-color: #00d4ff; color: #000; }
.navbar-menu { padding: 0 10%; border-bottom: 1px solid #1a1a1a !important; }
.flex-grow { flex: 1; }
.user-section { display: flex; align-items: center; gap: 20px; }
.search-bar { width: 220px; }
:deep(.el-input__wrapper) { background-color: #1a1a1a !important; box-shadow: none !important; border: 1px solid #333; }
:deep(.el-input__inner) { color: #fff !important; }
.nav-logo { color: #00d4ff; font-size: 22px; font-weight: 800; margin-right: 30px; cursor: pointer; line-height: 60px; }
.user-avatar-wrapper { display: flex; align-items: center; gap: 10px; color: #fff; cursor: pointer; }

/* 抽屉样式 */
:deep(.dark-drawer) { background-color: #121212 !important; color: #fff !important; }
:deep(.dark-drawer .el-drawer__header) { color: #fff; border-bottom: 1px solid #222; margin-bottom: 0; padding-bottom: 20px; }
:deep(.dark-drawer .el-drawer__title) { font-size: 1.2rem; font-weight: bold; }
.cart-content { height: 100%; display: flex; flex-direction: column; }
.empty-cart { flex: 1; display: flex; flex-direction: column; justify-content: center; align-items: center; color: #666; gap: 15px; }
.cart-list { flex: 1; overflow-y: auto; padding: 20px 0; }
.cart-item { display: flex; gap: 15px; margin-bottom: 20px; background: #1a1a1a; padding: 10px; border-radius: 8px; border: 1px solid #333; }
.cart-thumb { width: 70px; height: 70px; border-radius: 6px; flex-shrink: 0; }
.cart-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.cart-info h4 { margin: 0; font-size: 14px; color: #ddd; }
.cart-price { color: #00d4ff; font-weight: bold; }
.cart-controls { display: flex; justify-content: space-between; align-items: center; }
:deep(.dark-input-number) { width: 90px; }
:deep(.dark-input-number .el-input__wrapper) { background-color: #222 !important; box-shadow: none !important; border: 1px solid #444; }
:deep(.dark-input-number .el-input__inner) { color: #fff; }
:deep(.dark-input-number .el-input-number__decrease), :deep(.dark-input-number .el-input-number__increase) { background-color: #333; color: #fff; border-color: #444; }
.cart-footer { border-top: 1px solid #333; padding-top: 20px; }
.total-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; font-size: 16px; color: #fff; }
.total-price { font-size: 24px; color: #00d4ff; font-weight: bold; }
.checkout-btn { width: 100%; background-color: #00d4ff; border-color: #00d4ff; color: #000; font-weight: bold; font-size: 16px; }
.checkout-btn:hover { background-color: #33e0ff; border-color: #33e0ff; }
.line-clamp-1 { display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.empty-state { text-align: center; padding: 120px 0; color: #444; }
.empty-icon { font-size: 4rem; margin-bottom: 20px; }
</style>