import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例对象
const request = axios.create({
  baseURL: '/api',
  timeout: 10000, // 超时时间为10秒
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等请求头信息
    // const token = localStorage.getItem('token')
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`
    // }
    
    // 如果是FormData类型（文件上传），移除默认的Content-Type，让浏览器自动设置
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 根据业务需求处理响应
    if (res.code !== 1) {
      // 错误提示
      ElMessage.error(res.msg || '操作失败')
      
      // 可以在这里处理特定错误码
      // if (res.code === 401) {
      //   // 未登录处理
      //   localStorage.removeItem('token')
      //   window.location.href = '/login'
      // }
      
      return Promise.reject(new Error(res.msg || '操作失败'))
    } else {
      return res
    }
  },
  (error) => {
    // 网络错误处理
    let msg = '网络错误，请稍后重试'
    if (error.response) {
      // 根据HTTP状态码处理错误
      switch (error.response.status) {
        case 400:
          msg = '请求参数错误'
          break
        case 401:
          msg = '未授权，请重新登录'
          // 未登录处理
          break
        case 403:
          msg = '拒绝访问'
          break
        case 404:
          msg = '请求地址错误'
          break
        case 500:
          // 获取更详细的错误信息
          msg = error.response.data?.msg || error.response.data?.message || '服务器内部错误'
          // 打印完整的错误响应数据，帮助调试
          console.error('500错误详情:', error.response.data)
          break
        default:
          msg = error.response.data.msg || '请求失败'
      }
    }
    
    ElMessage.error(msg)
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

export default request