import request from './request';
import type { ApiResponse, Tag, Question } from '@/types';

export function listTags(params: { page?: number; size?: number }) {
  return request.get<ApiResponse<{ content: Tag[]; totalElements: number; totalPages: number }>>(
    '/tags', { params }
  );
}

export function getTagQuestions(tagId: number, params: { page?: number; size?: number }) {
  return request.get<ApiResponse<{ content: Question[]; totalElements: number; totalPages: number }>>(
    `/tags/${tagId}/questions`, { params }
  );
}
