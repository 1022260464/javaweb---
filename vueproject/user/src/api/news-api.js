import request from '@/utils/request'

// ==========================================
//           1. 游戏资讯模块 (News)
// ==========================================

// 获取资讯列表
export function fetchNewsList(params) {
  return request({
    url: '/news',
    method: 'get',
    params
  })
}

// 获取资讯详情
export function getNewsDetail(id) {
  return request({
    url: `/news/${id}`,
    method: 'get'
  })
}

// 【新增】资讯点赞
// 后端是 @PutMapping("/addLike") @RequestBody News news
// 所以这里需要传一个对象，比如 { newsId: "..." }
export function addNewsLike(data) {
  return request({
    url: '/news/addLike', // 假设你的 Controller 路径前缀是 /news
    method: 'put',
    data
  })
}

// ==========================================
//           2. 游戏商品模块 (Products)
// ==========================================

export function ProductList(params) {
  return request({
    url: '/items',
    method: 'get',
    params
  })
}

export function getProductDetail(id) {
  return request({
    url: `/items/${id}`,
    method: 'get'
  })
}

export function getPriceHistory(id) {
  return request({
    url: `/items/price/${id}`,
    method: 'get'
  })
}

// ==========================================
//           3. 评论互动模块 (Comments)
// ==========================================

export function getCommentList(newsId) {
  return request({
    url: '/api/comments/list',
    method: 'get',
    params: { newsId }
  })
}

export function addComment(data) {
  return request({
    url: '/api/comments/add',
    method: 'post',
    data
  })
}

export function deleteComment(id) {
  return request({
    url: `/api/comments/${id}`,
    method: 'delete'
  })
}