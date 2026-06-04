<template>
  <div class="article-list-page">
    <div class="page-header">
      <h2>技术文章</h2>
      <el-button type="primary" @click="$router.push('/articles/write')">写文章</el-button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="articles.length === 0" class="empty">
      <p>还没有任何文章</p>
      <el-button type="primary" @click="$router.push('/articles/write')">写第一篇文章</el-button>
    </div>

    <div v-else class="article-items">
      <div
        v-for="a in articles"
        :key="a.id"
        class="article-card"
        @click="$router.push(`/articles/${a.slug}`)"
      >
        <h3 class="article-title">{{ a.title }}</h3>
        <div class="article-meta">
          <span class="author">{{ a.author?.username }}</span>
          <span class="time">{{ formatTime(a.createdAt) }}</span>
          <span class="views">{{ a.viewCount }} 浏览</span>
          <span class="likes">{{ a.likeCount }} 点赞</span>
        </div>
        <div class="article-tags" v-if="a.tags?.length">
          <span v-for="tag in a.tags" :key="tag.id" class="tag">{{ tag.name }}</span>
        </div>
      </div>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <el-pagination
        layout="prev, pager, next"
        :total="totalElements"
        :page-size="pageSize"
        :current-page="currentPage + 1"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { listArticles } from '@/api/article';
import type { Article } from '@/types';

const articles = ref<Article[]>([]);
const loading = ref(true);
const currentPage = ref(0);
const pageSize = 20;
const totalElements = ref(0);
const totalPages = ref(0);

async function fetchArticles() {
  loading.value = true;
  try {
    const { data } = await listArticles({
      page: currentPage.value,
      size: pageSize,
    });
    articles.value = data.data.content;
    totalElements.value = data.data.totalElements;
    totalPages.value = data.data.totalPages;
  } catch (e) {
    console.error('Failed to fetch articles', e);
  } finally {
    loading.value = false;
  }
}

function handlePageChange(page: number) {
  currentPage.value = page - 1;
  fetchArticles();
}

function formatTime(iso: string): string {
  const d = new Date(iso);
  const diff = Date.now() - d.getTime();
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`;
  return d.toLocaleDateString('zh-CN');
}

onMounted(fetchArticles);
</script>

<style scoped>
.article-list-page {
  max-width: 960px;
  margin: 24px auto;
  padding: 0 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.loading,
.empty {
  text-align: center;
  padding: 60px 0;
  color: #909399;
}

.article-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.article-card {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: box-shadow 0.2s, border-color 0.2s;
  background: #fff;
}

.article-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.article-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #303133;
  line-height: 1.4;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.article-meta .author {
  color: #409eff;
}

.article-tags {
  display: flex;
  gap: 8px;
}

.article-tags .tag {
  background: #f0f5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
