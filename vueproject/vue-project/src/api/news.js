import request from '@/utils/request'
import { data } from 'autoprefixer'

// 获取新闻列表
export function fetchNewsList(params) {
  return request({
    url: '/news',
    method: 'get',
    params
  })
}

// 获取新闻详情
export function getNewsDetail(id) {
  return request({
    url: `/news/${id}`,
    method: 'get'
  })
}

// 添加新闻
export function addNews(data) {
  return request({
    url: '/news',
    method: 'post',
    data
  })
}

// 更新新闻
export function updateNews( data) {
  return request({
    url: `/news`,
    method: 'put',
    data
  })
}


// 批量删除新闻
export function batchDeleteNews(newsIds) {
  const queryString = newsIds
    .map(id => `newsId=${encodeURIComponent(id)}`)
    .join('&');
  return request({
    url: `/news?${queryString}`,
    method: 'delete'
  });
}

// // 更新新闻状态
// export function updateNewsStatus(id, status) {
//   return request({
//     url: `/news/${id}/status`,
//     method: 'put',
//     data: { status }
//   })
// }
export function fenxiNews(params) {
  return request({
    url: `/news/newsFenxi`,
    method: 'get',
    params
  })
}