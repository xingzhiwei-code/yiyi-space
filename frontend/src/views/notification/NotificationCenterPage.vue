<template>
  <div class="notification-page">
    <div class="page-header">
      <h2>通知中心</h2>
      <el-button size="small" @click="handleMarkAllRead" v-if="unreadCount > 0">
        全部标记已读
      </el-button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="notifications.length === 0" class="empty">
      <p>暂无通知</p>
    </div>

    <div v-else class="notification-list">
      <div
        v-for="n in notifications"
        :key="n.id"
        class="notification-item"
        :class="{ unread: !n.read }"
        @click="handleRead(n)"
      >
        <div class="notif-icon">{{ getIcon(n.type) }}</div>
        <div class="notif-content">
          <div class="notif-text">
            <span v-if="n.fromUser" class="from-user">{{ n.fromUser.username }}</span>
            {{ n.content }}
          </div>
          <div class="notif-time">{{ formatTime(n.createdAt) }}</div>
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
import { listNotifications, unreadCount as getUnreadCount, markAsRead, markAllAsRead } from '@/api/notification';
import type { Notification } from '@/types';

const notifications = ref<Notification[]>([]);
const loading = ref(true);
const currentPage = ref(0);
const pageSize = 20;
const totalElements = ref(0);
const totalPages = ref(0);
const unreadCount = ref(0);

async function fetchNotifications() {
  loading.value = true;
  try {
    const { data } = await listNotifications({
      page: currentPage.value,
      size: pageSize,
    });
    notifications.value = data.data.content;
    totalElements.value = data.data.totalElements;
    totalPages.value = data.data.totalPages;
  } catch (e) {
    console.error('Failed to fetch notifications', e);
  } finally {
    loading.value = false;
  }
}

async function fetchUnreadCount() {
  try {
    const { data } = await getUnreadCount();
    unreadCount.value = data.data;
  } catch (e) {
    // ignore
  }
}

async function handleRead(n: Notification) {
  if (!n.read) {
    try {
      await markAsRead(n.id);
      n.read = true;
      unreadCount.value = Math.max(0, unreadCount.value - 1);
    } catch (e) {
      // ignore
    }
  }
}

async function handleMarkAllRead() {
  try {
    await markAllAsRead();
    notifications.value.forEach(n => n.read = true);
    unreadCount.value = 0;
  } catch (e) {
    console.error('Failed to mark all read', e);
  }
}

function handlePageChange(page: number) {
  currentPage.value = page - 1;
  fetchNotifications();
}

function getIcon(type: string): string {
  const icons: Record<string, string> = {
    ANSWER: '💬',
    COMMENT: '📝',
    ACCEPT: '✅',
    LIKE: '❤️',
    FOLLOW: '👤',
  };
  return icons[type] || '🔔';
}

function formatTime(iso: string): string {
  const d = new Date(iso);
  const diff = Date.now() - d.getTime();
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`;
  return d.toLocaleDateString('zh-CN');
}

onMounted(() => {
  fetchNotifications();
  fetchUnreadCount();
});
</script>

<style scoped>
.notification-page {
  max-width: 800px;
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

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  background: #fff;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f5f7fa;
}

.notification-item.unread {
  background: #f0f5ff;
  border-color: #d4e4ff;
}

.notif-icon {
  font-size: 20px;
  flex-shrink: 0;
  margin-top: 2px;
}

.notif-content {
  flex: 1;
}

.notif-text {
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
}

.from-user {
  color: #409eff;
  font-weight: 500;
}

.notif-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
