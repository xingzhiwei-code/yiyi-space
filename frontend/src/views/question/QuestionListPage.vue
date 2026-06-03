<template>
  <div class="question-list-page">
    <div class="page-header">
      <h2>全部问题</h2>
      <el-button type="primary" @click="$router.push('/ask')">提问</el-button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="questions.length === 0" class="empty">
      <p>还没有任何问题</p>
      <el-button type="primary" @click="$router.push('/ask')">提出第一个问题</el-button>
    </div>

    <div v-else class="question-items">
      <div
        v-for="q in questions"
        :key="q.id"
        class="question-card"
        @click="$router.push(`/questions/${q.id}`)"
      >
        <div class="question-stats">
          <span class="stat">{{ q.viewCount }}<br><small>浏览</small></span>
          <span class="stat" :class="{ hasAnswers: q.answerCount > 0 }">
            {{ q.answerCount }}<br><small>回答</small>
          </span>
        </div>
        <div class="question-main">
          <h3 class="question-title">{{ q.title }}</h3>
          <div class="question-meta">
            <TagList :tags="q.tags" />
            <span class="author">{{ q.author?.username }}</span>
            <span class="time">{{ formatTime(q.createdAt) }}</span>
          </div>
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
import { listQuestions } from '@/api/question';
import type { Question } from '@/types';
import TagList from '@/components/common/TagList.vue';

const questions = ref<Question[]>([]);
const loading = ref(true);
const currentPage = ref(0);
const pageSize = 20;
const totalElements = ref(0);
const totalPages = ref(0);

async function fetchQuestions() {
  loading.value = true;
  try {
    const { data } = await listQuestions({
      page: currentPage.value,
      size: pageSize,
      sort: 'createdAt,desc',
    });
    questions.value = data.data.content;
    totalElements.value = data.data.totalElements;
    totalPages.value = data.data.totalPages;
  } catch (e) {
    console.error('Failed to fetch questions', e);
  } finally {
    loading.value = false;
  }
}

function handlePageChange(page: number) {
  currentPage.value = page - 1;
  fetchQuestions();
}

function formatTime(iso: string): string {
  const d = new Date(iso);
  const diff = Date.now() - d.getTime();
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`;
  return d.toLocaleDateString('zh-CN');
}

onMounted(fetchQuestions);
</script>

<style scoped>
.question-list-page {
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

.question-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-card {
  display: flex;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: box-shadow 0.2s, border-color 0.2s;
  background: #fff;
}

.question-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.question-stats {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 60px;
  text-align: center;
  margin-right: 16px;
  color: #909399;
  font-size: 16px;
}

.question-stats .stat small {
  font-size: 12px;
}

.question-stats .hasAnswers {
  color: #67c23a;
  font-weight: 600;
}

.question-main {
  flex: 1;
}

.question-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #303133;
  line-height: 1.4;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.question-meta .author {
  color: #409eff;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
