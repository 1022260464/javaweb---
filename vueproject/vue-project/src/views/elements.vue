<script lang="ts" setup>
import { ref } from 'vue'
import {
  View,
  Message,
  Box,
  ChatDotRound,
  User,
  Setting,
  PieChart
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 设置默认激活的菜单项为首页
const activeIndex = ref('/elements')
</script>

<template>
  <el-container class="layout-container-demo">
    <!-- 页边栏 (左侧导航) -->
    <el-aside width="200px">
      <el-scrollbar>
        <el-menu
          :default-active="activeIndex"
          class="el-menu-vertical-demo"
          background-color="#000000"
          text-color="#ffffff"
          active-text-color="#ffffff"
          unique-opened
          router
        >
          <!-- 仪表盘 (首页) -->
          <el-menu-item index="/elements">
            <el-icon><View /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <!-- 游戏资讯 (核心功能) -->
          <el-menu-item index="/elements/news">
            <el-icon><Message /></el-icon>
            <span>游戏资讯</span>
          </el-menu-item>

          <!-- 游戏商品  -->
          <el-menu-item index="/elements/game-items">
            <el-icon><Box /></el-icon>
            <span>游戏商品</span>
          </el-menu-item>

          <!-- 论坛管理 (高频功能) -->
          <el-menu-item index="/elements/forum">
            <el-icon><ChatDotRound /></el-icon>
            <span>论坛管理</span>
          </el-menu-item>

          <!-- 用户管理 (高频操作) -->
          <el-menu-item index="/elements/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>

          <!-- 系统设置 (基础配置) -->
          <el-menu-item index="/elements/settings">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>

          <!-- 数据分析 (高级功能) -->
          <el-menu-item index="/elements/fenxi">
            <el-icon><PieChart /></el-icon>
            <span>数据分析</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <el-header style="text-align: right; font-size: 12px">
        <div class="toolbar">
          <el-dropdown>
            <el-icon style="margin-right: 8px; margin-top: 1px">
              <Setting />
            </el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>查看资料</el-dropdown-item>
                <el-dropdown-item>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <span>管理员</span>
        </div>
      </el-header>

      <el-main style="padding: 20px">
        <!-- 动态内容区域 -->
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container-demo {
  min-height: 100vh;
  background-color: #000000;
}

.layout-container-demo .el-header {
  background-color: #000000;
  color: #ffffff;
  line-height: 60px;
  border-bottom: 1px solid #333333;
}

.layout-container-demo .el-aside {
  background-color: #000000;
  color: #ffffff;
  border-right: 1px solid #333333;
}

.layout-container-demo .el-menu {
  border-right: none;
}

.layout-container-demo .el-main {
  padding: 20px;
  background-color: #000000;
  color: #ffffff;
}

.layout-container-demo .toolbar {
  display: inline-flex;
  align-items: center;
  height: 100%;
  margin-right: 20px;
  color: #ffffff;
}

.content-area {
  background-color: #1a1a1a;
  color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(255, 255, 255, 0.1);
}

.base-content {
  text-align: center;
  padding: 40px;
  color: #ffffff;
}

.base-content h2 {
  color: #ffffff;
  margin-bottom: 20px;
}

.base-content p {
  color: #cccccc;
}

/* 设置下拉菜单样式 */
.layout-container-demo .el-dropdown {
  color: #ffffff;
}

.layout-container-demo .el-dropdown-menu {
  background-color: #1a1a1a;
  border: 1px solid #333333;
}

.layout-container-demo .el-dropdown-menu__item {
  color: #ffffff;
}

.layout-container-demo .el-dropdown-menu__item:hover {
  background-color: #333333;
  color: #ffffff;
}
</style>