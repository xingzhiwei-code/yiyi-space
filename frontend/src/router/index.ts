import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/LoginPage.vue'),
      meta: { guest: true },
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/auth/RegisterPage.vue'),
      meta: { guest: true },
    },
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/question/QuestionListPage.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/ask',
      name: 'AskQuestion',
      component: () => import('@/views/question/AskQuestionPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/questions/:id',
      name: 'QuestionDetail',
      component: () => import('@/views/question/QuestionDetailPage.vue'),
      meta: { requiresAuth: false },
    },
  ],
});

// 路由守卫
router.beforeEach((to) => {
  const auth = useAuthStore();

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'Login' };
  }

  if (to.meta.guest && auth.isAuthenticated) {
    return { name: 'Home' };
  }
});

export default router;
