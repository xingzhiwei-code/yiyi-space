<template>
  <div class="answer-item" :class="{ 'answer-accepted': answer.accepted }">
    <div class="answer-header">
      <el-tag v-if="answer.accepted" type="success" size="small">✓ 已采纳</el-tag>
      <span class="answer-author">{{ answer.author.username }}</span>
      <span class="answer-time">{{ formatTime(answer.createdAt) }}</span>
    </div>
    <div class="answer-content">
      <MarkdownRenderer :content="answer.contentHtml" />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Answer } from '@/types';
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue';

defineProps<{
  answer: Answer;
}>();

function formatTime(iso: string): string {
  return new Date(iso).toLocaleString('zh-CN');
}
</script>

<style scoped>
.answer-item {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 12px;
  background: #fff;
}

.answer-accepted {
  border-color: #67c23a;
  background: #f0f9ff;
}

.answer-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 13px;
}

.answer-author {
  color: #409eff;
  font-weight: 500;
}

.answer-time {
  color: #909399;
}

.answer-content {
  font-size: 14px;
}
</style>
