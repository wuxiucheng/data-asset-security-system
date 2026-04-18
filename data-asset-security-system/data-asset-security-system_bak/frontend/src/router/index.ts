import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', requiresAuth: true },
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', requiresAuth: true },
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/role/index.vue'),
        meta: { title: '角色管理', requiresAuth: true },
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('@/views/permission/index.vue'),
        meta: { title: '权限管理', requiresAuth: true },
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/department/index.vue'),
        meta: { title: '部门管理', requiresAuth: true },
      },
      {
        path: 'owner',
        name: 'Owner',
        component: () => import('@/views/owner/index.vue'),
        meta: { title: '责任人管理', requiresAuth: true },
      },
      {
        path: 'classification-standard',
        name: 'ClassificationStandard',
        component: () => import('@/views/classification-standard/index.vue'),
        meta: { title: '分类标准', requiresAuth: true },
      },
      {
        path: 'classification',
        name: 'Classification',
        component: () => import('@/views/classification/index.vue'),
        meta: { title: '数据分类', requiresAuth: true },
      },
      {
        path: 'grading-standard',
        name: 'GradingStandard',
        component: () => import('@/views/grading-standard/index.vue'),
        meta: { title: '分级标准', requiresAuth: true },
      },
      {
        path: 'grading',
        name: 'Grading',
        component: () => import('@/views/grading/index.vue'),
        meta: { title: '数据分级', requiresAuth: true },
      },
      {
        path: 'asset',
        name: 'Asset',
        component: () => import('@/views/asset/index.vue'),
        meta: { title: '资产列表', requiresAuth: true },
      },
      {
        path: 'asset-field',
        name: 'AssetField',
        component: () => import('@/views/asset-field/index.vue'),
        meta: { title: '字段管理', requiresAuth: true },
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/index.vue'),
        meta: { title: '资产统计', requiresAuth: true },
      },
      {
        path: 'trend',
        name: 'Trend',
        component: () => import('@/views/trend/index.vue'),
        meta: { title: '趋势分析', requiresAuth: true },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLogin) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && userStore.isLogin) {
    // 已登录用户访问登录页，跳转到首页
    next('/dashboard')
  } else {
    next()
  }
})

export default router
