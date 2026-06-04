<template>
  <el-button
    :type="liked ? 'danger' : 'default'"
    :plain="!liked"
    size="small"
    @click="handleToggle"
  >
    <span class="icon">{{ liked ? '❤️' : '🤍' }}</span>
    <span>{{ count }}</span>
  </el-button>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { toggleLike, checkLike } from '@/api/interaction';
import type { InteractionTargetType } from '@/types';
import { ElMessage } from 'element-plus';

const props = defineProps<{
  targetType: InteractionTargetType;
  targetId: number;
  initialCount: number;
}>();

const auth = useAuthStore();

const liked = ref(false);
const count = ref(props.initialCount);

async function handleToggle() {
  if (!auth.isAuthenticated) {
    ElMessage.warning('请先登录');
    return;
  }
  try {
    const { data } = await toggleLike(props.targetType, props.targetId);
    liked.value = data.data.liked;
    count.value = data.data.count;
  } catch (e) {
    console.error('Toggle like failed', e);
  }
}

// 加载初始状态
async function loadStatus() {
  if (!auth.isAuthenticated) return;
  try {
    const { data } = await checkLike(props.targetType, props.targetId);
    liked.value = data.data;
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
