import request from './request';
import type { ApiResponse, Notification } from '@/types';

export function listNotifications(params: { page?: number; size?: number }) {
  return request.get<ApiResponse<{ content: Notification[]; totalElements: number; totalPages: number }>>(
    '/notifications', { params }
  );
}

export function listUnread(params: { page?: number; size?: number }) {
  return request.get<ApiResponse<{ content: Notification[]; totalElements: number; totalPages: number }>>(
    '/notifications/unread', { params }
  );
}

export function unreadCount() {
  return request.get<ApiResponse<number>>('/notifications/unread/count');
}

export function markAsRead(notificationId: number) {
  return request.put<ApiResponse<null>>(`/notifications/${notificationId}/read`);
}

export function markAllAsRead() {
  return request.put<ApiResponse<null>>('/notifications/read');
}
