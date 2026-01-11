import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import NewsView from '../views/news/news.vue'
import News from '../views/news/news.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/news',
      name: 'news',
      component: NewsView
    },  
    {
    path: '/news/:id',
    name: 'NewsDetail',
    component: () => import('@/views/news/NewsDetail.vue'), // 详情页组件路径
    props: true                    
    },     
    {
      path: '/products',
      name: 'products',
      component: () => import('../views/product/productlist.vue')
    },
    {
      path:'/products/:id',
      name:'ProductDetail',
      component: () => import('@/views/product/productDetail.vue'),
      props: true
    }
  ]
})

export default router
