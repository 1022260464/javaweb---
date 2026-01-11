<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElTable, ElMessageBox } from 'element-plus'
import { Search, View, Delete, Plus } from '@element-plus/icons-vue' // 添加Plus图标
import { fetchNewsList, updateNews, batchDeleteNews, addNews } from '@/api/news' // 添加addNews
import { uploadFile } from '@/api/tongyong-api/upload' // 导入上传API
import { Upload } from '@element-plus/icons-vue'
// 定义搜索条件
const searchParams = ref({
  title: '',
  category: '',
  status: '',
  page: 1,
  pageSize: 10
})

// 定义新闻数据
const newsData = ref({
  total: 0,
  rows: []
})

// 定义加载状态
const loading = ref(false)
const editLoading = ref(false)

// 批量操作相关
const selectedNewsIds = ref<string[]>([]) // 选中的新闻ID数组
// const tableRef = ref<InstanceType<typeof ElTable> | null>(null) // 表格引用

// 状态选项
const statusOptions = [
  { label: '全部', value: '' },
  { label: '草稿', value: 0 },
  { label: '发布', value: 1 },
  { label: '隐藏', value: 2 }
]

// 分类选项
const categoryOptions = [
  { label: '全部', value: '' },
  { label: '角色更新', value: '角色更新' },
  { label: '玩法更新', value: '玩法更新' },
  { label: '版本预告', value: '版本预告' },
  { label: '活动公告', value: '活动公告' },
  { label: '区域更新', value: '区域更新' },
  { label: '剧情更新', value: '剧情更新' }
]

// 对话框相关
const dialogVisible = ref(false)  /** 详情对话框是否可见 */
const currentNews = ref<any>(null)/** 当前选中的新闻 */
const editDialogVisible = ref(false) /** 编辑对话框是否可见 */
const adddialogVisible = ref(false) /** 新增对话框是否可见 */

// 编辑表单数据
const editForm = ref({
  id: '',
  title: '',
  category: '',
  status: 0,
  coverImage: '',
  content: '',
  tags: '',
  summary: '',
  author: '' // 添加author字段
})

// 打开详情对话框
const openDetailDialog = (row: any) => {
  currentNews.value = row
  dialogVisible.value = true
}

// 关闭详情对话框
const closeDetailDialog = () => {
  dialogVisible.value = false
  currentNews.value = null
}

// 打开编辑对话框
const openEditDialog = (row: any) => {
  currentNews.value = row
  // 将当前新闻数据复制到编辑表单中，包含所有必需字段
  editForm.value = {
    id: row.newsId, 
    title: row.title,
    category: row.category,
    status: row.status,
    coverImage: row.coverImage, 
    content: row.content,
    tags: row.tags,
    summary: row.summary,
    author: row.author || '' 
  }
  editDialogVisible.value = true
}

// 关闭编辑对话框
const closeEditDialog = () => {
  editDialogVisible.value = false
  // 完全重置编辑表单，不保留任何数据
  editForm.value = {
    id: '',
    title: '',
    category: '',
    status: 0,
    coverImage: '',
    content: '',
    tags: '',
    summary: '',
    author: ''
  }
}

// 打开新增新闻对话框
const openAddNewsDialog = () => {
  // 重置表单
  editForm.value = {
    id: '',
    title: '',
    category: '',
    status: 0,
    coverImage: '',
    content: '',
    tags: '',
    summary: '',
    author: ''
  }
  editDialogVisible.value = true
}

// const openAddDialog = () => {
//   adddialogVisible.value = true
// }

// const closeAddDialog = () => {
//   adddialogVisible.value = false
// }

// 删除单条新闻
const handleDeleteSingle = async (id: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这条新闻吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    // 复用批量删除接口，传入单个 ID 的数组
    await batchDeleteNews([id]);

    ElMessage.success('删除成功');

    // 修复：使用getNewsList()而不是直接调用fetchNewsList()
    getNewsList();
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败，请稍后重试');
    }
  }
};

// 批量删除函数
const batchDeleteSelectedNews = async () => {
  const ids = selectedNewsIds.value;
  if (ids.length === 0) {
    ElMessage.warning('请先选择要删除的新闻');
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${ids.length} 条新闻吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    // 调用 API：确保传入的是字符串数组
    await batchDeleteNews(ids);

    // 成功后清空选中项
    selectedNewsIds.value = [];
    
    // 刷新新闻列表
    getNewsList();

    ElMessage.success('删除成功');
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error);
      ElMessage.error('批量删除失败，请稍后重试');
    }
  }
};

// 表格选择变化事件处理
const handleSelectionChange = (selection: any[]) => {
  selectedNewsIds.value = selection.map(item => item.newsId)
}

// 保存修改/新增
const saveEdit = async () => {
  // 1. 标题长度校验
  if (editForm.value.title.length > 200) {
    ElMessage.error('标题不能超过200个字符')
    return
  }

  // 2. 传入完整对象，字段名统一用 `newsId`
  editLoading.value = true
  try {
    const newsData = {
      title: editForm.value.title,
      content: editForm.value.content,
      category: editForm.value.category,
      author: editForm.value.author,
      status: editForm.value.status,
      coverImage: formatOssImage(editForm.value.coverImage), // 确保是完整URL,阿里云oss图片地址
      summary: editForm.value.summary,
      tags: editForm.value.tags
    }

    if (editForm.value.id) {
      // 更新操作
      await updateNews({
        ...newsData,
        newsId: editForm.value.id
      })
      ElMessage.success('新闻更新成功')
    } else {
      // 新增操作
      await addNews(newsData)
      ElMessage.success('新闻新增成功')
    }
    
    closeEditDialog()
    getNewsList()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请检查网络或联系管理员')
  } finally {
    editLoading.value = false
  }
}

// 获取新闻列表
const getNewsList = async () => {
  loading.value = true
  try {
    // 直接调用封装好的API函数，传入参数
    const response = await fetchNewsList(searchParams.value)
   
    newsData.value = response.data
  } catch (error) {
   
    console.error('获取新闻列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索功能
const handleSearch = () => {
  searchParams.value.page = 1
  getNewsList()
}

// 重置搜索条件
const handleReset = () => {
  searchParams.value = {
    title: '',
    category: '',
    status: '',
    page: 1,
    pageSize: 10
  }
  getNewsList()
}

// 分页变化
const handlePageChange = (page: number, pageSize: number) => {
  searchParams.value.page = page
  searchParams.value.pageSize = pageSize
  getNewsList()
}

// 格式化OSS图片地址，确保是完整URL
const formatOssImage = (url: string) => {
  if (!url) return ''
  // 如果已经是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 如果是相对路径，添加API前缀
  return `/api${url.startsWith('/') ? url : `/${url}`}`
}

// 上传前的校验
const beforeCoverImageUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!')
  }
  if (!isLt10M) {
    ElMessage.error('上传图片大小不能超过 10MB!')
  }
  return isJPG && isLt10M
}

// 上传方法
const handleCustomUpload = async (file: any) => {
  // / 使用 file.file 获取实际的 File 对象
  const formData = new FormData()
  formData.append('file', file.file) // 使用 file.file，确保是 File 对象
  try {
    const response = await uploadFile(formData)
    if (response.code === 1) {
      editForm.value.coverImage = response.data
      ElMessage.success('封面图片上传成功')
    } else {
      ElMessage.error(response.msg || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('封面图片上传失败，请重试')
  }
}

// 上传失败处理
const handleCoverImageError = () => {
  ElMessage.error('封面图片上传失败，请重试')
}

// 页面加载时获取新闻列表
onMounted(() => {
  getNewsList()
})
</script>

<template>
  <div class="news-container">
    <div class="news-header">
      <h2>游戏资讯列表</h2>
    </div>
    
    <!-- 搜索条件 -->
    <!-- 搜索条件 -->
    <div class="search-form">
      <el-form :inline="true" size="small">
        <el-form-item label="标题">
          <el-input 
            v-model="searchParams.title" 
            placeholder="请输入标题搜索" 
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        
        <el-form-item label="分类" class="category-select">
          <el-select 
            v-model="searchParams.category"
            placeholder="请选择分类"
            clearable
          >
            <el-option 
              v-for="item in categoryOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" class="status-select">
          <el-select 
            v-model="searchParams.status" 
            placeholder="请选择状态"
            clearable
          >
            <el-option 
              v-for="item in statusOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">
            搜索
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
        </el-form-item>
        
        <!-- 批量操作按钮 -->
        <el-form-item class="batch-actions">
          <el-button 
            type="primary" 
            size="small" 
            @click="openAddNewsDialog" 
            :icon="Plus"
          >
            新增新闻
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="batchDeleteSelectedNews" 
            :icon="Delete"
            :disabled="selectedNewsIds.length === 0"
          >
            批量删除 ({{ selectedNewsIds.length }})
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 新闻列表 -->
    <el-table 
      ref="tableRef"
      v-loading="loading" 
      :data="newsData.rows" 
      stripe 
      border
      class="news-table"
      @selection-change="handleSelectionChange"
    >
      <!-- 多选框列 -->
      <el-table-column type="selection" width="55" />
      <el-table-column prop="newsId" label="ID" width="150" /> <!-- 使用正确的字段名news_id -->
      <el-table-column prop="title" label="标题" min-width="200">
        <template #default="scope">
          <div class="news-title">
            <img 
              v-if="scope.row.coverImage" 
              :src="formatOssImage(scope.row.coverImage)" 
              alt="封面" 
              class="cover-image"
            />
            <span>{{ scope.row.title }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="summary" label="摘要" min-width="250" /> <!-- 摘要列 -->
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="tags" label="标签" min-width="150"> <!-- 标签列 -->
        <template #default="scope">
          <el-tag v-for="tag in (scope.row.tags || []).split(',')" :key="tag" size="small" style="margin-right: 5px;">
            {{ tag }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag 
            :type="scope.row.status === 1 ? 'success' : scope.row.status === 0 ? 'info' : 'danger'"
          >
            {{ scope.row.status === 1 ? '发布' : scope.row.status === 0 ? '草稿' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180" /> <!-- 使用正确的字段名update_time -->
      <el-table-column prop="viewCount" label="浏览量" width="100" /> <!-- 使用正确的字段名view_count -->
      <el-table-column label="操作" width="225" fixed="right">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="openDetailDialog(scope.row)"
            :icon="View"
          >
            查看
          </el-button>
          <el-button 
            type="warning" 
            size="small" 
            @click="openEditDialog(scope.row)"
          >
            修改
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDeleteSingle(scope.row.newsId)"
            :icon="Delete"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="searchParams.page"
        v-model:page-size="searchParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="newsData.total"
        @size-change="handlePageChange"
        @current-change="handlePageChange"
      />
    </div>
    
    <!-- 详情对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="新闻详情" 
      width="800px" 
      center
      class="news-detail-dialog"
    >
      <div v-if="currentNews" class="detail-content">
        <div class="detail-header">
          <h3 class="detail-title">{{ currentNews.title }}</h3>
          <div class="detail-meta">
            <span class="meta-item">
              <i class="el-icon-date"></i>
              <span>{{ currentNews.updateTime }}</span> <!-- 使用正确的字段名update_time -->
            </span>
            <span class="meta-item">
              <i class="el-icon-folder-opened"></i>
              <span>{{ currentNews.category }}</span>
            </span>
            <span class="meta-item">
              <el-tag 
                :type="currentNews.status === 1 ? 'success' : currentNews.status === 0 ? 'info' : 'danger'"
              >
                {{ currentNews.status === 1 ? '发布' : currentNews.status === 0 ? '草稿' : '隐藏' }}
              </el-tag>
            </span>
          </div>
        </div>
        
        <!-- 添加摘要显示 -->
        <div v-if="currentNews.summary" class="detail-summary">
          <h4>摘要</h4>
          <p>{{ currentNews.summary }}</p>
        </div>
        
        <div class="detail-content">
          <div v-if="currentNews.coverImage" class="detail-cover"> <!-- 使用正确的字段名cover_image -->
            <img :src="formatOssImage(currentNews.coverImage)" alt="封面" /> <!-- 使用正确的字段名cover_image -->
          </div>
          
          <div class="detail-body">
            <p v-html="currentNews.content"></p>
          </div>
          
          <div class="detail-footer">
            <div class="detail-tags">
              <el-tag v-for="tag in (currentNews.tags || []).split(',')" :key="tag" size="small">
                {{ tag }}
              </el-tag>
            </div>
            <div class="detail-stats">
              <span>浏览量: {{ currentNews.viewCount }}</span> <!-- 使用正确的字段名view_count -->
              <span>点赞数: {{ currentNews.likeCount }}</span> <!-- 使用正确的字段名like_count -->
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDetailDialog">关闭</el-button>
          <el-button @click="openEditDialog(currentNews)">修改</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 编辑对话框 -->
    <el-dialog 
      v-model="editDialogVisible" 
      title="编辑新闻" 
      width="800px" 
      center
    >
      <el-form label-position="top" size="large">
        <el-form-item label="标题" required>
          <el-input 
            v-model="editForm.title" 
            placeholder="请输入新闻标题" 
            maxlength="200" 
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="分类" required>
          <el-select 
            v-model="editForm.category" 
            placeholder="请选择新闻分类"
            style="width: 100%"
          >
            <el-option 
              v-for="item in categoryOptions.filter(opt => opt.value !== '')" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="作者" required>
          <el-input 
            v-model="editForm.author" 
            placeholder="请输入作者名称"
          />
        </el-form-item>
        
        <el-form-item label="状态" required>
          <el-select 
            v-model="editForm.status" 
            placeholder="请选择新闻状态"
            style="width: 100%"
          >
            <el-option 
              v-for="item in statusOptions.filter(opt => opt.value !== '')" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <!--封面图片输入框 -->
        <el-form-item label="封面图片">
          <el-upload
            :before-upload="beforeCoverImageUpload"
            :on-error="handleCoverImageError"
            :http-request="handleCustomUpload"
            :limit="1"
            :auto-upload="true"
            class="cover-upload"
          >
            <el-button type="primary" size="small" :icon="Upload">
              上传封面
            </el-button>
            <div v-if="editForm.coverImage" class="cover-preview">
              <img :src="editForm.coverImage" alt="封面预览" />
              <div class="cover-remove" @click.stop="editForm.coverImage = ''">
                <i class="el-icon-close"></i>
              </div>
            </div>
          </el-upload>
          <div v-if="!editForm.coverImage" class="cover-hint">点击上传封面图片（建议尺寸：1200x600）</div>
        </el-form-item>
        
        <el-form-item label="摘要" required> <!-- 添加摘要字段 -->
          <el-input 
            v-model="editForm.summary" 
            type="textarea" 
            placeholder="请输入新闻摘要" 
            rows="3"
          />
        </el-form-item>
        
        <el-form-item label="标签" required>
          <el-input 
            v-model="editForm.tags" 
            placeholder="请输入标签，用逗号分隔"
          />
        </el-form-item>
        
        <el-form-item label="内容" required>
          <el-input 
            v-model="editForm.content" 
            type="textarea" 
            placeholder="请输入新闻内容" 
            rows="10"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeEditDialog">取消</el-button>
          <el-button 
            type="primary" 
            @click="saveEdit" 
            :loading="editLoading"
          >
            保存修改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.news-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.news-header {
  margin-bottom: 20px;
}

.news-header h2 {
  color: #333;
  margin: 0;
  font-size: 20px;
}
/* 搜索表单样式 */
.search-form {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
.category-select
 {
  width: 17%;
}
.status-select {
  width: 12.5%;
}

.news-table {
  margin-bottom: 20px;
}

.pagination {
  text-align: right;
}

.news-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cover-image {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

/* 对话框样式 */
.news-detail-dialog {
  font-size: 16px;
  line-height: 1.6;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.detail-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.detail-summary {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.detail-summary h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.detail-content {
  margin-bottom: 20px;
}

.detail-cover {
  margin-bottom: 20px;
  text-align: center;
}

.detail-cover img {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
  border-radius: 4px;
}

.detail-body {
  color: #333;
}

.detail-body p {
  margin-bottom: 10px;
}

.detail-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-stats {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.dialog-footer {
  text-align: center;
}


.cover-upload {
  margin-top: 10px;
}

.cover-preview {
  position: relative;
  margin-top: 10px;
  width: 120px;
  height: 80px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-remove {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.cover-hint {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}
</style>