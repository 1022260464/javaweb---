import request from '@/utils/request'
// 文件上传接口
export function uploadFile(data) {
  return request({
    url: '/upload',
    method: 'post',
    data
    
  })
}