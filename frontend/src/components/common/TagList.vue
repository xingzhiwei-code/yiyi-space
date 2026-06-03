<template>
  <div class="tag-list">
    <router-link
      v-for="tag in tags"
      :key="tag.id"
      :to="`/tags/${tag.id}`"
      class="tag-item"
    >
      {{ tag.name }}
      <span v-if="showCount" class="tag-count">x{{ tag.useCount }}</span>
    </router-link>
  </div>
</template>

<script setup lang="ts">
import type { Tag } from '@/types';

defineProps<{
  tags: Tag[];
  showCount?: boolean;
}>();
</script>

<style scoped>
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  display: inline-block;
  padding: 4px 10px;
  background: #e8f4ff;
  color: #409eff;
  border-radius: 4px;
  font-size: 12px;
  text-decoration: none;
  transition: background 0.2s;
}

.tag-item:hover {
  background: #d4e9ff;
}

.tag-count {
  color: #909399;
  margin-left: 4px;
}
</style>
