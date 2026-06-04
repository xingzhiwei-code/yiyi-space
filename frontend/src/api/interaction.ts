import request from './request';
import type { ApiResponse, InteractionResponse, InteractionTargetType } from '@/types';

export function toggleLike(targetType: InteractionTargetType, targetId: number) {
  return request.post<ApiResponse<InteractionResponse>>('/interactions/like', null, {
    params: { targetType, targetId }
  });
}

export function toggleFavorite(targetType: InteractionTargetType, targetId: number) {
  return request.post<ApiResponse<InteractionResponse>>('/interactions/favorite', null, {
    params: { targetType, targetId }
  });
}

export function checkLike(targetType: InteractionTargetType, targetId: number) {
  return request.get<ApiResponse<boolean>>('/interactions/like/check', {
    params: { targetType, targetId }
  });
}

export function checkFavorite(targetType: InteractionTargetType, targetId: number) {
  return request.get<ApiResponse<boolean>>('/interactions/favorite/check', {
    params: { targetType, targetId }
  });
}

export function getMyFavorites(params: { page?: number; size?: number }) {
  return request.get<ApiResponse<any>>('/interactions/me/favorites', { params });
}
