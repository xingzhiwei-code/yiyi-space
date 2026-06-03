<template>
  <div class="comment-list">
    <div v-if="comments.length === 0" class="comment-empty">暂无评论</div>
    <div v-else class="comment-item" v-for="c in comments" :key="c.id">
      <span class="comment-author">{{ c.author.username }}</span>
      <span class="comment-content">{{ c.content }}</span>
      <span class="comment-time">{{ formatTime(c.createdAt) }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Comment } from '@/types';

defineProps<{
  comments: Comment[];
}>();

function formatTime(iso: string): string {
  const d = new Date(iso);
  const now = new Date();
  const diff = now.getTime() - d.getTime();
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`;
  return d.toLocaleDateString('zh-CN');
}
</script>

<style scoped>
.comment-list {
  font-size: 13px;
  border-top: 1px solid #f0f0f0;
  padding-top: 8px;
}

.comment-empty {
  color: #909399;
  padding: 8px 0;
}

.comment-item {
  padding: 6px 0;
  color: #606266;
}

.comment-author {
  color: #409eff;
  font-weight: 500;
  margin-right: 6px;
}

.comment-content {
  margin-right: 8px;
}

.comment-time {
  color: #909399;
  font-size: 12px;
}
</style>
