<template>
  <el-button
    :type="favorited ? 'warning' : 'default'"
    :plain="!favorited"
    size="small"
    @click="handleToggle"
  >
    <span class="icon">{{ favorited ? '⭐' : '☆' }}</span>
    <span>{{ count }}</span>
  </el-button>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { toggleFavorite, checkFavorite } from '@/api/interaction';
import type { InteractionTargetType } from '@/types';
import { ElMessage } from 'element-plus';

const props = defineProps<{
  targetType: InteractionTargetType;
  targetId: number;
  initialCount: number;
}>();

const auth = useAuthStore();

const favorited = ref(false);
const count = ref(props.initialCount);

async function handleToggle() {
  if (!auth.isAuthenticated) {
    ElMessage.warning('请先登录');
    return;
  }
  try {
    const { data } = await toggleFavorite(props.targetType, props.targetId);
    favorited.value = data.data.liked;
    count.value = data.data.count;
  } catch (e) {
    console.error('Toggle favorite failed', e);
  }
}

async function loadStatus() {
  if (!auth.isAuthenticated) return;
  try {
    const { data } = await checkFavorite(props.targetType, props.targetId);
    favorited.value = data.data;
  } catch (e) {
    // ignore
  }
}

loadStatus();
</script>

<style scoped>
.icon {
  margin-right: 4px;
}
</style>
