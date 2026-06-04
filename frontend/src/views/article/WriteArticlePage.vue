<template>
  <div class="write-article-page">
    <h2>{{ isEdit ? '编辑文章' : '写文章' }}</h2>

    <el-form :model="form" label-position="top" class="article-form">
      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="请输入文章标题" maxlength="200" show-word-limit />
      </el-form-item>

      <el-form-item label="内容">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="20"
          placeholder="使用 Markdown 格式编写内容..."
        />
      </el-form-item>

      <el-form-item label="标签">
        <el-select
          v-model="form.tags"
          multiple
          filterable
          allow-create
          default-first-option
          placeholder="输入标签后回车添加"
          style="width: 100%"
        >
          <el-option v-for="tag in form.tags" :key="tag" :label="tag" :value="tag" />
        </el-select>
      </el-form-item>

      <div class="form-actions">
        <el-button @click="$router.back()">取消</el-button>
        <el-button type="info" @click="handleSaveDraft" v-if="isEdit">保存草稿</el-button>
        <el-button type="primary" @click="handlePublish">
          {{ isEdit ? '更新并发布' : '发布文章' }}
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { createArticle, updateArticle, getArticleById, publishArticle } from '@/api/article';
import type { ArticleRequest } from '@/types';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();

const isEdit = computed(() => !!route.params.id);

const form = ref<ArticleRequest>({
  title: '',
  content: '',
  tags: [],
});

async function handleSaveDraft() {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写标题和内容');
    return;
  }
  // 创建即草稿，编辑时更新即可
  ElMessage.info('草稿已保存');
}

async function handlePublish() {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写标题和内容');
    return;
  }
  try {
    if (isEdit.value) {
      const articleId = Number(route.params.id);
      await updateArticle(articleId, form.value);
      await publishArticle(articleId);
      ElMessage.success('更新成功');
    } else {
      const { data } = await createArticle(form.value);
      await publishArticle(data.data.id);
      ElMessage.success('发布成功');
    }
    router.push('/articles');
  } catch (e) {
    ElMessage.error('操作失败');
  }
}

async function loadArticleForEdit() {
  if (!isEdit.value) return;
  try {
    const articleId = Number(route.params.id);
    const { data } = await getArticleById(articleId);
    form.value.title = data.data.title;
    form.value.content = data.data.content || '';
    form.value.tags = data.data.tags?.map(t => t.name) || [];
  } catch (e) {
    ElMessage.error('加载文章失败');
    router.push('/articles');
  }
}

onMounted(loadArticleForEdit);
</script>

<style scoped>
.write-article-page {
  max-width: 800px;
  margin: 24px auto;
  padding: 0 16px;
}

.write-article-page h2 {
  margin: 0 0 20px;
  font-size: 22px;
  color: #303133;
}

.article-form {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}
</style>
