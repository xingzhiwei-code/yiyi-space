import request from './request';
import type {
  ApiResponse,
  Comment,
  CommentRequest,
  CommentTargetType,
} from '@/types';

export function listComments(targetType: CommentTargetType, targetId: number, params: { page?: number; size?: number }) {
  return request.get<ApiResponse<{ content: Comment[]; totalElements: number; totalPages: number }>>(
    '/comments', { params: { targetType, targetId, ...params } }
  );
}

export function createComment(targetType: CommentTargetType, targetId: number, data: CommentRequest) {
  return request.post<ApiResponse<Comment>>('/comments', data, {
    params: { targetType, targetId },
  });
}

export function deleteComment(id: number) {
  return request.delete<ApiResponse<null>>(`/comments/${id}`);
}
