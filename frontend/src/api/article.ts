import request from './request';
import type { ApiResponse, Article, ArticleRequest } from '@/types';

export function listArticles(params: { page?: number; size?: number }) {
  return request.get<ApiResponse<{ content: Article[]; totalElements: number; totalPages: number }>>(
    '/articles', { params }
  );
}

export function getArticleBySlug(slug: string) {
  return request.get<ApiResponse<Article>>(`/articles/slug/${slug}`);
}

export function getArticleById(id: number) {
  return request.get<ApiResponse<Article>>(`/articles/${id}`);
}

export function createArticle(data: ArticleRequest) {
  return request.post<ApiResponse<Article>>('/articles', data);
}

export function updateArticle(id: number, data: ArticleRequest) {
  return request.put<ApiResponse<Article>>(`/articles/${id}`, data);
}

export function deleteArticle(id: number) {
  return request.delete<ApiResponse<null>>(`/articles/${id}`);
}

export function publishArticle(id: number) {
  return request.put<ApiResponse<Article>>(`/articles/${id}/publish`);
}
