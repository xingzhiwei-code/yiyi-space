<template>
  <div class="article-detail-page" v-if="article">
    <article class="article-content">
      <header class="article-header">
        <h1>{{ article.title }}</h1>
        <div class="article-meta">
          <span class="author">{{ article.author?.username }}</span>
          <span class="time">{{ formatTime(article.createdAt) }}</span>
          <span class="views">{{ article.viewCount }} 浏览</span>
          <span v-if="article.tags?.length" class="tags">
            <span v-for="tag in article.tags" :key="tag.id" class="tag">{{ tag.name }}</span>
          </span>
        </div>
        <div class="article-actions" v-if="isAuthor">
          <el-button v-if="article.status === 'DRAFT'" @click="handlePublish" type="success">发布</el-button>
          <el-button @click="$router.push(`/articles/edit/${article.id}`)">编辑</el-button>
          <el-button @click="handleDelete" type="danger">删除</el-button>
        </div>
      </header>

      <div class="article-body" v-html="article.contentHtml"></div>

      <footer class="article-footer">
        <LikeButton targetType="ARTICLE" :targetId="article.id" :initialCount="article.likeCount" />
        <FavoriteButton targetType="ARTICLE" :targetId="article.id" :initialCount="article.favoriteCount" />
      </footer>
    </article>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getArticleBySlug, publishArticle, deleteArticle } from '@/api/article';
import { useAuthStore } from '@/stores/auth';
import type { Article } from '@/types';
import LikeButton from '@/components/common/LikeButton.vue';
import FavoriteButton from '@/components/common/FavoriteButton.vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const article = ref<Article | null>(null);

const isAuthor = computed(() => {
  return auth.user?.id === article.value?.author?.id;
});

async function fetchArticle() {
  try {
    const slug = route.params.slug as string;
    const { data } = await getArticleBySlug(slug);
    article.value = data.data;
  } catch (e) {
    console.error('Failed to fetch article', e);
  }
}

async function handlePublish() {
  if (!article.value) return;
  try {
    const { data } = await publishArticle(article.value.id);
    article.value = data.data;
    ElMessage.success('发布成功');
  } catch (e) {
    ElMessage.error('发布失败');
  }
}

async function handleDelete() {
  if (!article.value) return;
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '确认删除', { type: 'warning' });
    await deleteArticle(article.value.id);
    ElMessage.success('已删除');
    router.push('/articles');
  } catch (e) {
    if (e !== 'cancel') console.error('Failed to delete article', e);
  }
}

function formatTime(iso: string): string {
  const d = new Date(iso);
  const diff = Date.now() - d.getTime();
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`;
  return d.toLocaleDateString('zh-CN');
}

onMounted(fetchArticle);
</script>

<style scoped>
.article-detail-page {
  max-width: 800px;
  margin: 24px auto;
  padding: 0 16px;
}

.article-content {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
}

.article-header {
  margin-bottom: 32px;
}

.article-header h1 {
  margin: 0 0 12px;
  font-size: 28px;
  color: #303133;
  line-height: 1.3;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
}

.article-meta .author {
  color: #409eff;
  font-weight: 500;
}

.article-meta .tags {
  display: flex;
  gap: 6px;
}

.article-meta .tag {
  background: #f0f5ff;
  color: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.article-actions {
  display: flex;
  gap: 8px;
}

.article-body {
  line-height: 1.8;
  color: #303133;
  font-size: 15px;
}

.article-body :deep(h1), .article-body :deep(h2), .article-body :deep(h3) {
  margin-top: 24px;
  margin-bottom: 12px;
  color: #303133;
}

.article-body :deep(p) {
  margin-bottom: 16px;
}

.article-body :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 14px;
}

.article-body :deep(pre) {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
}

.article-body :deep(pre code) {
  background: none;
  padding: 0;
}

.article-footer {
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  gap: 16px;
}
</style>
