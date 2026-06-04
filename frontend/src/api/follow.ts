import request from './request';
import type { ApiResponse, FollowData } from '@/types';

export function follow(userId: number) {
  return request.post<ApiResponse<null>>(`/follows/${userId}`);
}

export function unfollow(userId: number) {
  return request.delete<ApiResponse<null>>(`/follows/${userId}`);
}

export function checkFollow(userId: number) {
  return request.get<ApiResponse<boolean>>(`/follows/check/${userId}`);
}

export function getFollowers(userId: number, params: { page?: number; size?: number }) {
  return request.get<ApiResponse<{ content: FollowData[]; totalElements: number }>>(
    `/follows/${userId}/followers`, { params }
  );
}

export function getFollowing(userId: number, params: { page?: number; size?: number }) {
  return request.get<ApiResponse<{ content: FollowData[]; totalElements: number }>>(
    `/follows/${userId}/following`, { params }
  );
}

export function countFollowers(userId: number) {
  return request.get<ApiResponse<number>>(`/follows/${userId}/followers/count`);
}

export function countFollowing(userId: number) {
  return request.get<ApiResponse<number>>(`/follows/${userId}/following/count`);
}
