<script setup lang="ts">
import { useRoute } from 'vue-router';
import Navbar from '@/components/layout/Navbar.vue';
import Sidebar from '@/components/layout/Sidebar.vue';

const route = useRoute();
// 不显示导航栏和侧边栏的页面（登录/注册页）
const isAuthPage = route.meta.guest === true;
</script>

<template>
  <div v-if="isAuthPage" class="full-screen">
    <router-view />
  </div>
  <div v-else class="app-layout">
    <Navbar />
    <div class="app-body">
      <Sidebar />
      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: #f5f7fa;
  color: #303133;
}

a {
  text-decoration: none;
  color: inherit;
}

.full-screen {
  min-height: 100vh;
}

.app-layout {
  min-height: 100vh;
}

.app-body {
  display: flex;
  max-width: 1440px;
  margin: 0 auto;
}

.main-content {
  flex: 1;
  padding: 20px 24px;
  min-width: 0;
}
</style>
