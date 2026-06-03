<template>
  <header class="navbar">
    <div class="navbar-content">
      <router-link to="/" class="logo">一亩三分学田</router-link>
      <nav class="nav-links">
        <template v-if="auth.isAuthenticated">
          <router-link to="/">问答</router-link>
          <router-link to="/ask" class="ask-btn">提问</router-link>
          <span class="username">{{ auth.user?.username }}</span>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </template>
        <template v-else>
          <router-link to="/">问答</router-link>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const auth = useAuthStore();
const router = useRouter();

function handleLogout() {
  auth.logout();
  router.push('/login');
}
</script>

<style scoped>
.navbar {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 24px;
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
  text-decoration: none;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-links a {
  color: #606266;
  text-decoration: none;
  font-size: 14px;
}

.nav-links a:hover {
  color: #409eff;
}

.username {
  color: #409eff;
  font-weight: 500;
}
</style>
