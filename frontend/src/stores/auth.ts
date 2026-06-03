import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login as apiLogin, getMe as apiGetMe, register as apiRegister } from '@/api/auth';
import type { User, AuthResponse } from '@/types';

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null);
  const accessToken = ref<string | null>(localStorage.getItem('accessToken'));
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'));

  const isAuthenticated = computed(() => !!accessToken.value);

  async function doLogin(username: string, password: string) {
    const { data } = await apiLogin({ username, password });
    const auth = data.data as AuthResponse;

    accessToken.value = auth.accessToken;
    refreshToken.value = auth.refreshToken;
    localStorage.setItem('accessToken', auth.accessToken);
    localStorage.setItem('refreshToken', auth.refreshToken);

    await fetchMe();
  }

  async function doRegister(username: string, email: string, password: string) {
    await apiRegister({ username, email, password });
    await doLogin(username, password);
  }

  async function fetchMe() {
    try {
      const { data } = await apiGetMe();
      user.value = data.data as User;
    } catch {
      logout();
    }
  }

  function logout() {
    user.value = null;
    accessToken.value = null;
    refreshToken.value = null;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  return {
    user,
    accessToken,
    refreshToken,
    isAuthenticated,
    doLogin,
    doRegister,
    fetchMe,
    logout,
  };
});
