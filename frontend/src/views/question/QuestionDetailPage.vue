<template>
  <div class="question-detail-page" v-if="question">
    <div class="question-header">
      <h1>{{ question.title }}</h1>
      <div class="question-meta">
        <span class="author">{{ question.author?.username }}</span>
        <span class="time">{{ formatTime(question.createdAt) }}</span>
        <span class="views">👁 {{ question.viewCount }} 浏览</span>
        <el-tag v-if="question.status === 'RESOLVED'" type="success" size="small">已解决</el-tag>
      </div>
      <TagList :tags="question.tags" />
    </div>

    <div class="question-body">
      <MarkdownRenderer :content="question.contentHtml || ''" />
    </div>

    <div class="answers-section">
      <h2>{{ question.answerCount }} 个回答</h2>
      <div v-if="answersLoading">加载中...</div>
      <div v-else-if="answers.length === 0" class="no-answers">
        还没有回答，快来抢沙发！
      </div>
      <AnswerItem v-else v-for="a in answers" :key="a.id" :answer="a" />
    </div>

    <div class="answer-form" v-if="auth.isAuthenticated">
      <h3>写下你的回答</h3>
      <el-input
        v-model="answerContent"
        type="textarea"
        :rows="6"
        placeholder="支持 Markdown 语法..."
      />
      <el-button
        type="primary"
        style="margin-top: 12px"
        @click="submitAnswer"
        :loading="submitting"
        :disabled="!answerContent.trim()"
      >
        提交回答
      </el-button>
    </div>
    <div v-else class="login-hint">
      <router-link to="/login">登录</router-link> 后参与回答
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getQuestion, listAnswers, createAnswer } from '@/api/question';
import type { Question, Answer } from '@/types';
import { useAuthStore } from '@/stores/auth';
import MarkdownRenderer from '@/components/common/MarkdownRenderer.vue';
import AnswerItem from '@/components/common/AnswerItem.vue';
import TagList from '@/components/common/TagList.vue';
import { ElMessage } from 'element-plus';

const route = useRoute();
const auth = useAuthStore();

const question = ref<Question | null>(null);
const answers = ref<Answer[]>([]);
const answersLoading = ref(false);
const answerContent = ref('');
const submitting = ref(false);

async function fetchQuestion() {
  try {
    const id = Number(route.params.id);
    const { data } = await getQuestion(id);
    question.value = data.data;
  } catch (e) {
    ElMessage.error('获取问题失败');
  }
}

async function fetchAnswers() {
  answersLoading.value = true;
  try {
    const id = Number(route.params.id);
    const { data } = await listAnswers(id);
    answers.value = data.data;
  } catch (e) {
    console.error('Failed to fetch answers', e);
  } finally {
    answersLoading.value = false;
  }
}

async function submitAnswer() {
  if (!answerContent.value.trim()) return;
  submitting.value = true;
  try {
    const id = Number(route.params.id);
    await createAnswer(id, { content: answerContent.value });
    answerContent.value = '';
    ElMessage.success('回答成功');
    await fetchAnswers();
    if (question.value) question.value.answerCount++;
  } catch (e) {
    ElMessage.error('提交回答失败');
  } finally {
    submitting.value = false;
  }
}

function formatTime(iso: string): string {
  return new Date(iso).toLocaleString('zh-CN');
}

onMounted(async () => {
  await Promise.all([fetchQuestion(), fetchAnswers()]);
});
</script>

<style scoped>
.question-detail-page {
  max-width: 960px;
  margin: 24px auto;
  padding: 0 16px;
}

.question-header {
  margin-bottom: 24px;
}

.question-header h1 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 12px;
  line-height: 1.4;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
}

.question-meta .author {
  color: #409eff;
  font-weight: 500;
}

.question-body {
  padding: 24px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 24px;
}

.answers-section {
  margin-bottom: 32px;
}

.answers-section h2 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 16px;
}

.no-answers {
  color: #909399;
  padding: 20px;
  text-align: center;
  background: #f5f7fa;
  border-radius: 6px;
}

.answer-form {
  padding: 24px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.answer-form h3 {
  margin: 0 0 12px;
  font-size: 16px;
  color: #303133;
}

.login-hint {
  text-align: center;
  padding: 24px;
  color: #909399;
  background: #f5f7fa;
  border-radius: 6px;
}

.login-hint a {
  color: #409eff;
}
</style>
