import request from '@/utils/request'

export function fetchProductList(params) {
  return request({
    url: '/items',
    method: 'get',
    params
  })
//   fetchProductList, updateProduct, batchDeleteProduct, addProduct
}
export function updateProduct(data) {  
  return request({
    url: `/items`,                     
    method: 'put',
    data                               
  })
}
export function batchDeleteProduct(productId) {
   const queryString = productId
       .map(id => `productId=${encodeURIComponent(id)}`)
       .join('&');
     return request({
       url: `/items?${queryString}`,
       method: 'delete'
     })
}
export function addProduct(data) {
  return request({
    url: '/items',
    method: 'post',
    data
  })
}

export function getProductDetail(productId) {
  return request({
    url: `/items/${productId}`,
    method: 'get'
  })
}
export function getPriceHistory(productId) {
  return request({
    url: `/items/price/${productId}`,
    method: 'get'
  })
}