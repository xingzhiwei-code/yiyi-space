import request from './request';
import type { ApiResponse, AuthResponse, User } from '@/types';

export function register(data: { username: string; email: string; password: string }) {
  return request.post<ApiResponse<User>>('/auth/register', data);
}

export function login(data: { username: string; password: string }) {
  return request.post<ApiResponse<AuthResponse>>('/auth/login', data);
}

export function refreshToken(refreshToken: string) {
  return request.post<ApiResponse<AuthResponse>>('/auth/refresh', { refreshToken });
}

export function getMe() {
  return request.get<ApiResponse<User>>('/users/me');
}
