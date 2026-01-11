<template>
  <div class="product-container">
    <div class="header-section">
      <h2>游戏商品管理</h2>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchParams" size="default">
        <el-form-item label="名称">
          <el-input 
            v-model="searchParams.productName" 
            placeholder="输入名称..." 
            clearable 
            @keyup.enter="getProductList"
            style="width: 160px;"
          />
        </el-form-item>
<!-- // 分类下拉 -->
        <el-form-item label="分类">
          <el-select 
            v-model="searchParams.category" 
            placeholder="选择分类" 
            clearable 
            style="width: 140px;"
            @change="getProductList"
          >
            <el-option 
              v-for="item in categoryOptions.filter(opt => opt.value)" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            />
          </el-select>
        </el-form-item>
<!-- // 价格输入 -->
        <el-form-item label="价格≤">
          <el-input-number 
            v-model="searchParams.price" 
            :min="0" 
            :controls="false"
            placeholder="最高预算" 
            style="width: 120px;"
            @keyup.enter="getProductList"
          />
        </el-form-item>
<!-- // 按钮 -->
        <el-form-item>
          <el-button type="primary" @click="getProductList" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch" :icon="Refresh">重置</el-button>
          <el-button type="success" plain @click="openAddProductDialog" :icon="Plus">新增</el-button>
          <el-button 
            type="danger" 
            plain 
            @click="batchDeleteSelected" 
            :disabled="!selectedProductIds.length" 
            :icon="Delete"
          >
            批量删除
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table 
      v-loading="loading" 
      :data="productData.rows" 
      stripe 
      border 
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      
      <el-table-column prop="productId" label="ID" width="100" show-overflow-tooltip />
      
      <el-table-column label="商品信息" min-width="240">
        <template #default="{ row }">
          <div class="info-cell">
            <el-image 
              class="cover" 
              :src="formatOssImage(parseImages(row.productImages)[0])" 
              fit="cover"
              :preview-src-list="parseImages(row.productImages).map(url => formatOssImage(url))"
              preview-teleported
            >
              <template #error>
                <div class="err-slot"><el-icon><Picture /></el-icon></div>
              </template>
            </el-image>
            <div class="text">
              <div class="name">{{ row.productName }}</div>
              <div class="desc" :title="row.description">{{ row.description }}</div> 
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="数据表现" width="140">
        <template #default="{ row }">
          <div class="stats-col">
            <span class="rating"><el-icon><StarFilled /></el-icon> {{ row.avgRating || 'N/A' }}</span>
            <span class="reviews"><el-icon><ChatDotRound /></el-icon> {{ row.totalReviews || 0 }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="supplierInfo" label="供应商" width="130" show-overflow-tooltip />

      <el-table-column prop="category" label="分类" width="100" align="center">
        <template #default="{ row }"><el-tag effect="plain">{{ row.category }}</el-tag></template>
      </el-table-column>
      
      <el-table-column prop="price" label="价格" width="110" sortable align="center">
        <template #default="{ row }">
          <span class="price-text">¥{{ row.price }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="status" label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="dark">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="260" fixed="right" align="center">
        <template #default="{ row }">
          <el-button 
            size="small" 
            type="primary" 
            :icon="View" 
            @click="openDetailDialog(row)"
          >详情</el-button>
          <el-button 
            size="small" 
            type="warning" 
            :icon="Edit" 
            @click="openEditDialog(row)"
          >编辑</el-button>
          <el-button 
            size="small" 
            type="danger" 
            :icon="Delete" 
            @click="handleDeleteSingle(row.productId)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="searchParams.page"
        v-model:page-size="searchParams.pageSize"
        :page-sizes="[5, 10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="productData.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
<!-- // 详情弹窗 -->
    <el-dialog v-model="dialogVisible" title="商品详情" width="600px" center>
      <div v-if="currentProduct" class="detail-box">
        <div class="detail-header">
           <img :src="formatOssImage(parseImages(currentProduct.productImages)[0])" class="detail-img"/>
           <div class="detail-info-right">
              <h3>{{ currentProduct.productName }}</h3>
              <div class="detail-price">¥{{ currentProduct.price }}</div>
              <div class="detail-tags">
                 <el-tag>{{ currentProduct.category }}</el-tag>
                 <el-tag :type="currentProduct.status===1?'success':'info'">{{ currentProduct.status===1?'售卖中':'已下架' }}</el-tag>
              </div>
           </div>
        </div>
        
        <div class="info-grid">
           <div class="info-item"><span>供应商:</span> {{ currentProduct.supplierInfo }}</div>
           <div class="info-item"><span>评分:</span> {{ currentProduct.avgRating }}</div>
           <div class="info-item"><span>评论数:</span> {{ currentProduct.totalReviews }}</div>
           <div class="info-item"><span>更新时间:</span> {{ currentProduct.updatedAt || currentProduct.updated_at }}</div>
        </div>

        <el-divider content-position="left">商品简介</el-divider>
        <div class="desc-text">{{ currentProduct.description }}</div>
        
        <el-divider content-position="left">详细内容</el-divider>
        <div class="content-html" v-html="currentProduct.detailContent || '暂无详细内容'"></div>
        <div ref="chartRef" style="width: 100%; height: 400px;"></div>
      </div>
    </el-dialog>
<!-- 编辑弹窗 -->
    <el-dialog 
      v-model="editDialogVisible" 
      :title="editForm.id ? '编辑商品' : '新增商品'" 
      width="650px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="16">
             <el-form-item label="商品名称" required>
               <el-input v-model="editForm.productName" placeholder="请输入商品名称"/>
             </el-form-item>
          </el-col>
          <el-col :span="8">
             <el-form-item label="分类" required>
                <el-select v-model="editForm.category" style="width:100%">
                  <el-option v-for="c in categoryOptions.filter(o=>o.value)" :key="c.value" :label="c.label" :value="c.value"/>
                </el-select>
             </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格">
              <el-input-number v-model="editForm.price" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
               <el-radio-group v-model="editForm.status">
                  <el-radio :label="1" border>上架</el-radio>
                  <el-radio :label="0" border>下架</el-radio>
               </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
<el-form-item label="供应商">
          <el-input v-model="editForm.supplierInfo" placeholder="请输入供应商/开发商名称"/>
        </el-form-item>

        <el-form-item label="商品封面">
          <el-upload 
            class="avatar-uploader"
            :http-request="handleCustomUpload" 
            :show-file-list="false" 
          >
            <img v-if="editForm.productImages" :src="formatOssImage(editForm.productImages)" class="preview"/>
            <el-icon v-else class="plus-icon"><Plus /></el-icon>
          </el-upload>
          <div class="tips">支持 JPG/PNG 格式，建议尺寸 800x800</div>
        </el-form-item>

        <el-form-item label="简要描述">
           <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="展示在列表页的简短描述"/>
        </el-form-item>
        
        <el-form-item label="详细内容">
           <el-input v-model="editForm.detailContent" type="textarea" :rows="5" placeholder="商品详情页的完整介绍（支持HTML）"/>
        </el-form-item>

      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEdit" :loading="editLoading">保存提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import * as echarts from 'echarts';
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, View, Delete, Plus, Goods, StarFilled, ChatDotRound, 
  Edit, Refresh, Picture 
} from '@element-plus/icons-vue'
import { fetchProductList, updateProduct, batchDeleteProduct, addProduct,getPriceHistory } from '@/api/gameItem' 
import { uploadFile } from '@/api/tongyong-api/upload' 
import { y } from 'vue-router/dist/router-CWoNjPRp.mjs';
import { my } from 'element-plus/es/locale/index.mjs';

// --- 状态定义 ---

// 搜索参数：包含名称、分类、价格
const searchParams = ref({
  productName: '',
  category: '',
  price: undefined, // 传递给后端判断 <= price
  status: '',
  page: 1,
  pageSize: 10
})

const productData = ref({
  total: 0,
  rows: []
})

const loading = ref(false)
const editLoading = ref(false)
const selectedProductIds = ref<string[]>([]) 
// 分类选项
const categoryOptions = [
  { label: '全部', value: '' },
  { label: 'rpg', value: 'rpg' },
  { label: 'action rpg', value: 'action rpg' },
  { label: 'adventure', value: 'adventure' },
  { label: 'action', value: 'action' },
  { label: 'co-op', value: 'co-op' }
]
// 详情与编辑弹窗控制
const dialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentProduct = ref<any>(null)
// 编辑表单
const editForm = ref({
  id: '',
  productName: '',
  description: '',
  productImages: '',
  category: '',
  status: 1,
  supplierInfo: '',
  price: 0,
  detailContent: '',
  
})

// --- 核心方法 ---

// 1. 获取列表
const getProductList = async () => {
  loading.value = true;
  try {
    const res = await fetchProductList(searchParams.value);
    productData.value = res.data;
  } catch (error) {
    console.error('获取列表失败', error);
  } finally {
    loading.value = false;
  }
};

// 2. 重置搜索
const resetSearch = () => {
  searchParams.value = {
    productName: '',
    category: '',
    price: undefined,
    status: '',
    page: 1,
    pageSize: 10
  }
  getProductList()
}

// 3. 多选处理
const handleSelectionChange = (val: any[]) => {
  // 确保取到的是 productId
  selectedProductIds.value = val.map(v => v.productId)
}

// 4. 图片解析
const parseImages = (imgStr: any) => {
  if (!imgStr) return [];
  if (Array.isArray(imgStr)) return imgStr;
  try {
    return JSON.parse(imgStr);
  } catch (e) {
    return imgStr ? [imgStr] : [];
  }
};

// 5. OSS地址格式化
const formatOssImage = (url: string) => {
  if (!url) return '';
  if (url.startsWith('http') || url.startsWith('blob:')) return url;
  return `/api${url.startsWith('/') ? url : `/${url}`}`;
};

// 6. 分页处理
const handleSizeChange = (size: number) => {
  searchParams.value.pageSize = size;
  searchParams.value.page = 1;
  getProductList();
};
const handleCurrentChange = (current: number) => {
  searchParams.value.page = current;
  getProductList();
};

// 7. 图片上传
const handleCustomUpload = async (item: any) => {
  const formData = new FormData();
  formData.append('file', item.file);
  try {
    const res = await uploadFile(formData);
    if(res.code === 1) {
      editForm.value.productImages = res.data;
      ElMessage.success('上传成功');
    } else {
      ElMessage.error(res.msg || '上传失败');
    }
  } catch(e) { 
    ElMessage.error('网络错误，上传失败');
  }
};

// 8. 打开详情
// 添加chartRef
const chartRef = ref(null);
let myChart = null;

// 修改openDetailDialog函数，在打开详情弹窗时初始化图表并加载数据
const openDetailDialog = (row: any) => {
  currentProduct.value = row;
  dialogVisible.value = true;
  // 延迟初始化图表，确保DOM已经渲染
  setTimeout(() => {
    if (chartRef.value) {
      myChart = echarts.init(chartRef.value, 'dark');
      // 加载价格历史数据
      showChart(row.productId);
    }
  }, 100);
};

// showChart函数
const showChart = async (productId: any) => {
  try {
    if (!myChart) return;
    
    const res = await getPriceHistory(productId);
    if (res.code === 1) {
      //实际上应该做逻辑判断，将生效日期与结束日期与当前日期做判断，只显示在生效日期之前的价格 ，此处简化处理用于演示，将effectiveAt作为X轴
      // 后端返回的是effectiveAt字段
      const dates = res.data.map((item: any) => item.effectiveAt);
      const prices = res.data.map((item: any) => item.price);
      
      const options = { ...baseoptions };
      options.xAxis.data = dates;
      options.series[0].data = prices;
      myChart.setOption(options);
    } else {
      ElMessage.error(res.msg || '获取价格历史失败');
    }
  } catch (e) {
    ElMessage.error('网络错误，获取价格历史失败');
  }
};

// 基础配置
const baseoptions = {
  title: { text: '价格趋势图' },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      formatter: '{value} ¥'
    }
  },
  series: [
    {
      name: '价格',
      type: 'line',
      smooth: true,
      data: []
    }
  ]
};

// 9. 打开编辑
const openEditDialog = (row: any) => {
  const imgs = parseImages(row.productImages);
  editForm.value = {
    id: row.productId,
    productName: row.productName,
    description: row.description,
    category: row.category,
    status: row.status,
    supplierInfo: row.supplierInfo,
    productImages: imgs.length > 0 ? imgs[0] : '',
    price: row.price || 0,
    detailContent: row.detailContent || '',
    
  };
  editDialogVisible.value = true;
};

// 10. 打开新增
const openAddProductDialog = () => {
  editForm.value = {
    id: '',
    productName: '',
    description: '',
    productImages: '',
    category: '',
    status: 1,
    supplierInfo: '',
    price: 0,
    detailContent: '',
    
  };
  editDialogVisible.value = true;
};

// 11. 保存逻辑
const saveEdit = async () => {
  if (!editForm.value.productName) return ElMessage.error('商品名称不能为空');
  
  editLoading.value = true;
  try {
    // 构造提交数据：将单张图片转为 JSON 数组字符串
    const finalImages = editForm.value.productImages 
      ? JSON.stringify([editForm.value.productImages]) 
      : '[]';

    const submitData = {
      productName: editForm.value.productName,
      description: editForm.value.description,
      productImages: finalImages,
      category: editForm.value.category,
      status: editForm.value.status,
      supplierInfo: editForm.value.supplierInfo,
      price: editForm.value.price,
      detailContent: editForm.value.detailContent,
      // 如果后端没有这两个字段，可以移除
      avgRating: 0, 
      totalReviews: 0
    };
    
    if (editForm.value.id) {
      // 更新操作 - 使用项目统一的API设计模式
      await updateProduct({ 
        ...submitData, 
        productId: editForm.value.id  // 使用后端期望的字段名productId
      });
      ElMessage.success('更新成功');
    } else {
      // 新增操作 - 调用addProduct函数
      await addProduct(submitData);
      ElMessage.success('新增成功');
    }
    
    editDialogVisible.value = false;
    getProductList();
  } catch (e) {
    console.error('保存失败:', e);
    ElMessage.error('保存失败，请检查数据格式');
  } finally {
    editLoading.value = false;
  }
};

// 12. 删除逻辑
const handleDeleteSingle = async (id: string) => {
  try {
    await ElMessageBox.confirm('确认删除该商品?', '警告', { type: 'warning' });
    await batchDeleteProduct([id]);
    ElMessage.success('删除成功');
    getProductList();
  } catch (e) { if(e!=='cancel') console.error(e) }
};

const batchDeleteSelected = async () => {
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedProductIds.value.length} 个商品?`, '警告', { type: 'warning' });
    await batchDeleteProduct(selectedProductIds.value);
    selectedProductIds.value = []; // 清空选中
    getProductList();
    ElMessage.success('批量删除成功');
  } catch (e) { if(e!=='cancel') console.error(e) }
};

onMounted(getProductList);
</script>

<style scoped>
.product-container { padding: 20px; background: #fff; border-radius: 8px; }
.header-section h2 { margin: 0 0 20px 0; color: #333; font-size: 20px; }

/* 搜索栏样式优化 */
.search-form { 
  background-color: #f8f9fa; 
  padding: 18px 18px 0 18px; 
  border-radius: 6px; 
  margin-bottom: 20px; 
  border: 1px solid #ebeef5;
}

/* 表格内容样式 */
.info-cell { display: flex; gap: 12px; align-items: center; }
.cover { width: 60px; height: 60px; border-radius: 6px; flex-shrink: 0; background: #f5f5f5; border: 1px solid #eee; }
.text { display: flex; flex-direction: column; justify-content: center; overflow: hidden; }
.text .name { font-weight: bold; font-size: 14px; color: #333; margin-bottom: 4px; }
.text .desc { font-size: 12px; color: #999; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 200px; }

.stats-col { display: flex; flex-direction: column; gap: 4px; font-size: 12px; color: #666; }
.rating { color: #ff9900; display: flex; align-items: center; gap: 4px; }
.price-text { color: #f56c6c; font-weight: bold; }

/* 详情弹窗样式 */
.detail-header { display: flex; gap: 20px; margin-bottom: 20px; }
.detail-img { width: 120px; height: 120px; border-radius: 8px; object-fit: cover; border: 1px solid #eee; }
.detail-info-right { flex: 1; display: flex; flex-direction: column; justify-content: space-around; }
.detail-info-right h3 { margin: 0; font-size: 20px; color: #333; }
.detail-price { font-size: 24px; color: #f56c6c; font-weight: bold; }
.detail-tags { display: flex; gap: 10px; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 20px; background: #f9f9f9; padding: 15px; border-radius: 6px; }
.info-item { font-size: 13px; color: #666; }
.info-item span { font-weight: bold; color: #333; margin-right: 8px; }

.desc-text { line-height: 1.6; color: #555; margin-bottom: 20px; }
.content-html { padding: 10px; background: #fafafa; border-radius: 4px; min-height: 100px; }

/* 上传组件样式 */
.avatar-uploader { 
  border: 1px dashed #d9d9d9; 
  border-radius: 6px; 
  cursor: pointer; 
  position: relative; 
  overflow: hidden; 
  width: 100px; 
  height: 100px; 
  display: flex; 
  justify-content: center; 
  align-items: center; 
  transition: .3s; 
}
.avatar-uploader:hover { border-color: #409eff; }
.preview { width: 100%; height: 100%; object-fit: cover; }
.plus-icon { font-size: 28px; color: #8c939d; }
.tips { font-size: 12px; color: #999; margin-top: 6px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>