<template>
  <div class="news-detail-wrapper" v-loading="loading">
    <el-card shadow="never" class="news-card" v-if="article">
      <template #header>
        <div class="card-header">
          <el-page-header @back="$router.back()" :content="article.category || '详情'">
            <template #title> 返回 </template>
          </el-page-header>
        </div>
      </template>

      <header class="article-header">
        <h1>{{ article.title }}</h1>
        <div class="article-meta">
          <el-space spacer="|">
            <span><el-icon><User /></el-icon> {{ article.author }}</span>
            <span><el-icon><Calendar /></el-icon> {{ formatDate(article.publishTime) }}</span>
            <el-tag size="small" effect="plain">{{ article.category }}</el-tag>
          </el-space>
        </div>
      </header>

      <el-divider />
      
      <el-image
        v-if="article.coverImage"
        :src="article.coverImage"
        fit="cover"
        class="article-cover"
      />
      <div class="article-content" v-html="article.content"></div>

      <template #footer>
        <div class="footer-actions">
          <el-button type="primary" plain @click="$router.back()">阅读结束，点击返回</el-button>
        </div>

        <div class="comment-section">
          <div class="input-area">
            <div class="input-header">
              <span class="title">评论互动 ({{ total }})</span>
            </div>
            
            <div class="input-wrapper">
              <el-avatar :size="40" :src="currentUser.avatar" class="user-avatar" />
              <div class="input-box">
                <el-input
                  v-model="newComment"
                  type="textarea"
                  :rows="3"
                  placeholder="发一条友善的评论..."
                  resize="none"
                />
                <div class="action-bar">
                  <el-button type="primary" @click="handleSubmit" :loading="submitting">发布评论</el-button>
                </div>
              </div>
            </div>
          </div>

          <el-divider />

          <div class="comment-list" v-loading="commentLoading">
            <div v-for="item in commentList" :key="item.id" class="comment-item">
              <div class="main-comment-row">
                <div class="avatar-col">
                  <el-avatar :size="40" :src="item.userAvatar" />
                </div>

                <div class="content-col">
                  <div class="info-row">
                    <span class="username">{{ item.userName }}</span>
                    <span class="date">{{ item.createTime }}</span>
                  </div>

                  <div class="text-content">{{ item.content }}</div>

                  <div class="interaction-row">
                    <span class="action-btn" @click="handleOpenReply(item)">
                      <el-icon><ChatLineRound /></el-icon> 回复
                    </span>
                    <span class="action-btn delete-btn" @click="handleDelete(item.id)">
                      <el-icon><Delete /></el-icon> 删除
                    </span>
                  </div>
                </div>
              </div>

              <div class="sub-comments-box" v-if="item.children && item.children.length > 0">
                <div v-for="child in item.children" :key="child.id" class="sub-item">
                  <el-avatar :size="24" :src="child.userAvatar" class="sub-avatar" />
                  <div class="sub-content-col">
                    <div class="sub-info">
                      <span class="username">{{ child.userName }}</span>
                      <span v-if="child.replyUserName" class="reply-tag">
                         回复 <span class="at-name">@{{ child.replyUserName }}</span>
                      </span>
                      <span class="date">{{ child.createTime }}</span>
                    </div>
                    <div class="sub-text">{{ child.content }}</div>
                    
                    <div class="sub-actions">
                      <span class="action-btn" @click="handleOpenReply(item, child)">回复</span>
                      <span class="action-btn delete-btn" @click="handleDelete(child.id)">删除</span>
                    </div>
                  </div>
                </div>
              </div>

            </div>
            
            <el-empty v-if="commentList.length === 0" description="暂无评论，快来抢沙发！" />
          </div>
        </div>
      </template>
    </el-card>

    <el-empty 
      v-else-if="!loading" 
      description="文章不存在" 
      :image-size="200"
    >
      <el-button type="primary" @click="$router.push('/')">回到首页</el-button>
    </el-empty>

    <el-dialog
      v-model="replyDialogVisible"
      :title="replyPlaceholder"
      width="30%"
      destroy-on-close
    >
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="3"
        placeholder="请输入回复内容..."
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply" :loading="replySubmitting">发送</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { User, Calendar, ChatLineRound, Delete } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
// 引入 API (请确保你 api/news-api.js 里有这些导出)
import { getNewsDetail, getCommentList, addComment, deleteComment } from '@/api/news-api';

const route = useRoute();

// --- 状态定义 ---
const article = ref(null);
const loading = ref(false);
const commentLoading = ref(false);
const commentList = ref([]);
const total = ref(0);

// 顶层评论输入
const newComment = ref('');
const submitting = ref(false);

// 回复弹窗相关
const replyDialogVisible = ref(false);
const replyContent = ref('');
const replyPlaceholder = ref('回复评论');
const replySubmitting = ref(false);
// 暂存当前的回复目标
const currentReplyTarget = ref({
  rootId: 0,
  parentId: 0,
  replyUserName: ''
});

// 模拟当前登录用户头像 (实际应从 Store/Localstorage 获取)
const currentUser = reactive({
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
});

// --- 方法定义 ---

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
};

// 1. 加载文章详情
const getDetail = async () => {
  loading.value = true;
  try {
    const res = await getNewsDetail(route.params.id);
    if (res.code === 1) {
      article.value = res.data;
    }
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 2. 加载评论列表 (树形)
const loadComments = async () => {
  commentLoading.value = true;
  try {
    const res = await getCommentList(route.params.id);
    if (res.code === 1) {
      commentList.value = res.data;
      // 简单计算一下总数 (实际后端可以在外面包一层 total)
      // 这里暂时只显示顶层数量，或者你自己写个递归算总数
      total.value = res.data.length; 
    }
  } catch (e) {
    console.error(e);
  } finally {
    commentLoading.value = false;
  }
};

// 3. 发布顶层评论
const handleSubmit = async () => {
  if (!newComment.value.trim()) return ElMessage.warning('请输入内容');
  
  submitting.value = true;
  try {
    const res = await addComment({
      newsId: route.params.id,
      content: newComment.value,
      rootId: 0,   // 顶层
      parentId: 0
    });
    
    if (res.code === 1) {
      ElMessage.success('发布成功');
      newComment.value = '';
      loadComments(); // 刷新列表
    }
  } catch(e) {
    ElMessage.error('发布失败');
  } finally {
    submitting.value = false;
  }
};

// 4. 打开回复弹窗
// rootItem: 所在的根评论对象
// childItem: (可选) 如果是回复楼中楼，这里是被回复的子对象
const handleOpenReply = (rootItem, childItem = null) => {
  if (childItem) {
    // 回复子评论 -> rootId 不变，parentId 变成子评论id
    currentReplyTarget.value = {
      rootId: rootItem.id,
      parentId: childItem.id,
      replyUserName: childItem.userName
    };
    replyPlaceholder.value = `回复 @${childItem.userName}`;
  } else {
    // 回复根评论 -> rootId 是它自己，parentId 也是它自己
    currentReplyTarget.value = {
      rootId: rootItem.id,
      parentId: rootItem.id,
      replyUserName: rootItem.userName
    };
    replyPlaceholder.value = `回复 @${rootItem.userName}`;
  }
  replyContent.value = '';
  replyDialogVisible.value = true;
};

// 5. 提交回复
const submitReply = async () => {
  if (!replyContent.value.trim()) return ElMessage.warning('请输入内容');
  
  replySubmitting.value = true;
  try {
    const res = await addComment({
      newsId: route.params.id,
      content: replyContent.value,
      rootId: currentReplyTarget.value.rootId,
      parentId: currentReplyTarget.value.parentId,
      replyUserName: currentReplyTarget.value.replyUserName
    });
    
    if (res.code === 1) {
      ElMessage.success('回复成功');
      replyDialogVisible.value = false;
      loadComments(); // 刷新
    }
  } catch (e) {
    ElMessage.error('回复失败');
  } finally {
    replySubmitting.value = false;
  }
};

// 6. 删除评论
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条评论吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteComment(id);
    ElMessage.success('已删除');
    loadComments();
  });
};

onMounted(() => {
  getDetail();
  loadComments();
});
</script>

<style scoped>
.news-detail-wrapper { max-width: 900px; margin: 24px auto; padding: 0 20px; }
.news-card { border: none !important; }
.article-header { text-align: center; margin: 20px 0 40px; }
.article-header h1 { font-size: 2rem; color: #303133; margin-bottom: 20px; line-height: 1.4; }
.article-meta { color: #909399; font-size: 14px; }
.article-cover { width: 100%; max-height: 400px; margin-bottom: 20px; border-radius: 4px; }
.article-content { font-size: 16px; color: #333; line-height: 1.8; padding: 0 10px; }
.footer-actions { text-align: center; padding: 20px 0; }

/* 评论区核心样式 */
.comment-section { margin-top: 40px; }
.input-header { margin-bottom: 15px; }
.title { font-size: 18px; font-weight: bold; }
.input-wrapper { display: flex; gap: 15px; }
.input-box { flex: 1; }
.action-bar { margin-top: 10px; text-align: right; }

.comment-list { margin-top: 20px; }
.comment-item { padding: 20px 0; border-bottom: 1px solid #f0f2f5; }

/* 一级评论布局 */
.main-comment-row { display: flex; gap: 15px; }
.content-col { flex: 1; }
.info-row { display: flex; justify-content: space-between; margin-bottom: 8px; }
.username { font-weight: 600; font-size: 14px; color: #303133; }
.date { color: #909399; font-size: 12px; }
.text-content { margin-bottom: 10px; font-size: 14px; color: #606266; line-height: 1.6; }
.interaction-row { display: flex; gap: 20px; font-size: 13px; }
.action-btn { cursor: pointer; color: #909399; display: flex; align-items: center; gap: 4px; transition: color 0.2s; }
.action-btn:hover { color: #409EFF; }
.delete-btn:hover { color: #F56C6C; }

/* 二级评论 (楼中楼) 容器 */
.sub-comments-box {
  background-color: #f9fafc;
  border-radius: 4px;
  padding: 15px;
  margin-top: 15px;
  margin-left: 55px; /* 与一级评论内容对齐 */
}

/* 子评论项 */
.sub-item { display: flex; gap: 10px; margin-bottom: 15px; }
.sub-item:last-child { margin-bottom: 0; }
.sub-avatar { margin-top: 2px; }
.sub-content-col { flex: 1; }
.sub-info { font-size: 13px; margin-bottom: 5px; color: #606266; }
.reply-tag { margin: 0 5px; color: #909399; }
.at-name { color: #409EFF; }
.sub-text { font-size: 13px; color: #303133; margin-bottom: 5px; line-height: 1.5; }
.sub-actions { display: flex; gap: 15px; font-size: 12px; }

</style>