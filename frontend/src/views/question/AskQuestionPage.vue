<template>
  <div class="ask-question-page">
    <h2>提出问题</h2>

    <el-form :model="form" label-position="top" class="question-form">
      <el-form-item label="标题">
        <el-input
          v-model="form.title"
          placeholder="简明扼要地描述你的问题"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="详细描述（支持 Markdown）">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="12"
          placeholder="详细描述你的问题，可以使用 Markdown 格式..."
        />
      </el-form-item>

      <el-form-item label="标签（逗号分隔）">
        <el-input
          v-model="tagsInput"
          placeholder="例如：java, spring-boot, mysql"
        />
        <div class="tag-hint">最多 5 个标签，用逗号分隔</div>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          发布问题
        </el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { createQuestion } from '@/api/question';
import { ElMessage } from 'element-plus';

const router = useRouter();

const form = ref({
  title: '',
  content: '',
});
const tagsInput = ref('');
const submitting = ref(false);

async function handleSubmit() {
  if (!form.value.title.trim()) {
    ElMessage.warning('请输入问题标题');
    return;
  }
  if (!form.value.content.trim()) {
    ElMessage.warning('请输入问题描述');
    return;
  }

  submitting.value = true;
  try {
    const tags = tagsInput.value
      .split(/[，,]/)  // 支持中英文逗号
      .map(t => t.trim())
      .filter(t => t.length > 0)
      .slice(0, 5);

    const { data } = await createQuestion({
      title: form.value.title,
      content: form.value.content,
      tags,
    });
    ElMessage.success('问题发布成功');
    router.push(`/questions/${data.data.id}`);
  } catch (e) {
    ElMessage.error('发布问题失败');
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.ask-question-page {
  max-width: 960px;
  margin: 24px auto;
  padding: 0 16px;
}

.ask-question-page h2 {
  font-size: 22px;
  color: #303133;
  margin: 0 0 24px;
}

.question-form {
  background: #fff;
  padding: 24px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.tag-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
