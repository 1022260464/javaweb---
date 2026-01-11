import { createRouter, createWebHistory } from 'vue-router'

// 导入视图组件
import elements from '../views/elements.vue'
import login from '../views/login.vue'
import news from '../views/news/news.vue'
import gameItems from '../views/game-items/game-items.vue'
import fenxi from '../views/fenxi/fenxi.vue'
import shouye from '../views/shouye.vue'
// 创建子路由组件,zhanwei
// const Dashboard = { template: '<div class="content-area"><h2>仪表盘</h2><p>这里是系统仪表盘页面</p></div>' }
// // const GameItems = { template: '<div class="content-area"><h2>游戏商品管理</h2><p>这里是游戏商品管理页面</p></div>' }
const Forum = { template: '<div class="content-area"><h2>论坛管理</h2><p>这里是论坛管理页面</p></div>' }
const Users = { template: '<div class="content-area"><h2>用户管理</h2><p>这里是用户管理页面</p></div>' }
const Settings = { template: '<div class="content-area"><h2>系统设置</h2><p>这里是系统设置页面</p></div>' }
// // const Analytics = { template: '<div class="content-area"><h2>数据分析</h2><p>这里是数据分析页面</p></div>' }

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: login
  },
  {
    path: '/elements',
    name: 'elements',
    component: elements,
    children: [
      {
        path: '',
        name: 'shouye',
        component: shouye
      },
      {
        path: 'news',
        name: 'news',
        component: news
      },
      {
        path: 'game-items',
        name: 'game-items',
        component: gameItems
      },
      {
        path: 'forum',
        name: 'forum',
        component: Forum
      },
      {
        path: 'users',
        name: 'users',
        component: Users
      },
      {
        path: 'settings',
        name: 'settings',
        component: Settings
      },
      {
        path: 'fenxi',
        name: 'fenxi',
        component: fenxi
      }
    ]
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router