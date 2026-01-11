<template>
  <div class="news-detail-wrapper" v-loading="loading">
    <el-card shadow="never" class="news-card" v-if="article">
      <template #header>
        <div class="card-header">
          <el-page-header @back="$router.back()" :content="article.category">
            <template #title> 返回 </template>
          </el-page-header>
        </div>
      </template>

      <header class="article-header">
        <h1>{{ article.productName }}</h1>
        <div class="article-meta">
          <el-space spacer="|">
            <span><el-icon><User /></el-icon> {{ article.supplierInfo }}</span>
            <span><el-icon><Calendar /></el-icon> {{ formatDate(article.updatedAt) }}</span>
            <el-tag size="small" effect="plain">{{ article.category }}</el-tag>
          </el-space>
        </div>
      </header>

      <el-divider />
      
      <el-image
        :src="getMainImage(article.productImages)"
        :preview-src-list="parseImages(article.productImages)"
        fit="cover"
        style="width: 100%; max-height: 400px; margin-bottom: 20px;"
      />
      
      <el-divider content-position="left">商品简介</el-divider>
      <div class="desc-text">{{ article.description }}</div>
      
      <el-divider content-position="left">详细内容</el-divider>
      <div class="article-content" v-html="article.detailContent"></div>

      <template #footer>
        <div class="footer-actions">
          <el-button plain @click="$router.back()">
            返回列表
          </el-button>
          
          <el-button type="danger" size="large" @click="openBuyDialog">
            <el-icon style="margin-right: 5px"><ShoppingCart /></el-icon> 立即购买
          </el-button>
        </div>
      </template>
    </el-card>

    <el-empty 
      v-else-if="!loading" 
      description="商品去火星了..." 
      :image-size="200"
    >
      <el-button type="primary" @click="$router.push('/')">回到首页</el-button>
    </el-empty>

    <el-dialog
      v-model="dialogVisible"
      title="确认订单"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-if="article" class="buy-dialog-content">
        <div class="product-info">
          <p><strong>商品名称：</strong>{{ article.productName }}</p>
          <p><strong>单价：</strong><span class="price">¥{{ article.price || '99.00' }}</span></p>
        </div>
        
        <el-form :model="buyForm" label-width="80px" style="margin-top: 20px;">
          <el-form-item label="购买数量">
            <el-input-number 
              v-model="buyForm.quantity" 
              :min="1" 
              :max="99" 
              @change="calculateTotal"
            />
          </el-form-item>
          <el-form-item label="备注信息">
            <el-input v-model="buyForm.remark" type="textarea" placeholder="请输入您的特殊要求..." />
          </el-form-item>
        </el-form>

        <div class="total-price-area">
          总计金额：<span class="total-price">¥{{ totalPrice }}</span>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrder" :loading="submitting">
            确认支付
          </el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
// 导入图标 (新增 ShoppingCart)
import { User, Calendar, ShoppingCart } from '@element-plus/icons-vue';
// 导入Element Plus组件 (新增 ElDialog, ElInputNumber 等)
import { 
  ElCard, ElPageHeader, ElDivider, ElImage, ElButton, ElEmpty, 
  ElTag, ElSpace, ElDialog, ElForm, ElFormItem, ElInputNumber, 
  ElInput, ElMessage 
} from 'element-plus';
import { getProductDetail } from '@/api/news-api';

const route = useRoute();
const article = ref(null);
const loading = ref(false);

// --- 购买相关状态 ---
const dialogVisible = ref(false);
const submitting = ref(false);
const buyForm = reactive({
  quantity: 1,
  remark: ''
});

// 日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
};

// 获取详情
const getDetail = async () => {
  loading.value = true;
  try {
    const id = route.params.id;
    const response = await getProductDetail(id);
    if (response.code === 1) {
      article.value = response.data;
      // 如果后端没有返回价格，在这里模拟一个价格，防止计算报错
      if (!article.value.price) article.value.price = 199.00; 
    }
  } catch (error) {
    console.error('获取详情失败:', error);
  } finally {
    loading.value = false;
  }
};

// 解析图片字符串为数组
const parseImages = (imgStr) => {
  if (!imgStr) return ['https://via.placeholder.com/300x400'];
  try {
    const images = typeof imgStr === 'string' ? JSON.parse(imgStr) : imgStr;
    return Array.isArray(images) ? images : ['https://via.placeholder.com/300x400'];
  } catch (e) {
    return ['https://via.placeholder.com/300x400'];
  }
};

// 获取主图 (为了解决 :src 需要字符串而不是数组的问题)
const getMainImage = (imgStr) => {
  const imgs = parseImages(imgStr);
  return imgs.length > 0 ? imgs[0] : '';
};

// --- 购买逻辑 ---

// 打开弹窗
const openBuyDialog = () => {
  buyForm.quantity = 1;
  buyForm.remark = '';
  dialogVisible.value = true;
};

// 计算总价 (计算属性)
const totalPrice = computed(() => {
  if (!article.value) return '0.00';
  const price = parseFloat(article.value.price || 0);
  return (price * buyForm.quantity).toFixed(2);
});

// 提交订单
const submitOrder = () => {
  submitting.value = true;
  
  // 模拟后端请求
  setTimeout(() => {
    console.log('提交订单数据:', {
      productId: article.value.id,
      quantity: buyForm.quantity,
      totalPrice: totalPrice.value,
      remark: buyForm.remark
    });
    
    ElMessage.success(`购买成功！共花费 ¥${totalPrice.value}`);
    dialogVisible.value = false;
    submitting.value = false;
  }, 1000);
};

onMounted(() => {
  getDetail();
});
</script>

<style scoped>
.news-detail-wrapper {
  max-width: 900px;
  margin: 24px auto;
  padding: 0 20px;
}

.news-card {
  border: none !important;
}

.article-header {
  text-align: center;
  margin: 20px 0 40px;
}

.article-header h1 {
  font-size: 2rem;
  color: #303133;
  margin-bottom: 20px;
  line-height: 1.4;
}

.article-meta {
  color: #909399;
  font-size: 14px;
}

.article-content {
  font-size: 16px;
  color: #333;
  line-height: 1.8;
  padding: 0 10px;
}

.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 20px auto;
}

/* 底部操作区样式优化 */
.footer-actions {
  display: flex;
  justify-content: center; /* 居中对齐 */
  gap: 20px; /* 按钮之间的间距 */
  padding: 20px 0;
}

/* 购买弹窗样式 */
.product-info {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 15px;
}
.product-info p {
  margin: 5px 0;
  color: #606266;
}
.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}
.total-price-area {
  text-align: right;
  margin-top: 20px;
  font-size: 16px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}
.total-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
</style>