<template>
  <div class="home">
    <h1>欢迎，{{ auth.user?.username }} 👋</h1>
    <p class="subtitle">一亩三分学田 — 你的技术交流学习社区</p>

    <el-row :gutter="20" class="stats">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ auth.user?.reputation || 0 }}</div>
            <div class="stat-label">积分</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">0</div>
            <div class="stat-label">提问</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">0</div>
            <div class="stat-label">文章</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="coming-soon">
      <h3>🚧 更多功能开发中</h3>
      <p>问答、文章、标签、评论... 敬请期待！</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';

const auth = useAuthStore();

onMounted(() => {
  if (!auth.user) {
    auth.fetchMe();
  }
});
</script>

<style scoped>
.home {
  padding: 24px;
}

h1 {
  color: #303133;
  margin-bottom: 8px;
}

.subtitle {
  color: #909399;
  margin-bottom: 32px;
}

.stats {
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
  padding: 12px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.coming-soon {
  text-align: center;
  padding: 40px;
}

.coming-soon h3 {
  color: #303133;
  margin-bottom: 8px;
}

.coming-soon p {
  color: #909399;
}
</style>
