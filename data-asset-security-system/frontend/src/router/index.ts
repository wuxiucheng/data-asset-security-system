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
        path: 'datasource',
        name: 'DataSource',
        component: () => import('@/views/datasource/index.vue'),
        meta: { title: '数据源配置', requiresAuth: true },
      },
      {
        path: 'asset-discovery',
        name: 'AssetDiscovery',
        component: () => import('@/views/asset-discovery/index.vue'),
        meta: { title: '发现资产', requiresAuth: true },
      },
      {
        path: 'asset-field',
        name: 'AssetField',
        component: () => import('@/views/asset-field/index.vue'),
        meta: { title: '字段管理', requiresAuth: true },
      },
      {
        path: 'batch-refresh',
        name: 'BatchRefresh',
        component: () => import('@/views/batch-refresh/index.vue'),
        meta: { title: '批量刷新', requiresAuth: true },
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
      {
        path: 'report-asset-list',
        name: 'ReportAssetList',
        component: () => import('@/views/report-asset-list/index.vue'),
        meta: { title: '资产清单报告', requiresAuth: true },
      },
      {
        path: 'report-classification-stats',
        name: 'ReportClassificationStats',
        component: () => import('@/views/report-classification-stats/index.vue'),
        meta: { title: '分类分级统计', requiresAuth: true },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', requiresAuth: true },
      },
      {
        path: 'password',
        name: 'Password',
        component: () => import('@/views/password/index.vue'),
        meta: { title: '修改密码', requiresAuth: true },
      },
      {
        path: 'mfa',
        name: 'Mfa',
        component: () => import('@/views/mfa/index.vue'),
        meta: { title: '多因素认证', requiresAuth: true },
      },
      {
        path: 'session',
        name: 'Session',
        component: () => import('@/views/session/index.vue'),
        meta: { title: '会话管理', requiresAuth: true },
      },
      {
        path: 'audit-log',
        name: 'AuditLog',
        component: () => import('@/views/audit-log/index.vue'),
        meta: { title: '审计日志', requiresAuth: true },
      },
      {
        path: 'sensitive-rule',
        name: 'SensitiveRule',
        component: () => import('@/views/sensitive-rule/index.vue'),
        meta: { title: '敏感识别规则', requiresAuth: true },
      },
      {
        path: 'mask-strategy',
        name: 'MaskStrategy',
        component: () => import('@/views/mask-strategy/index.vue'),
        meta: { title: '脱敏策略', requiresAuth: true },
      },
      {
        path: 'quality-rule',
        name: 'QualityRule',
        component: () => import('@/views/quality-rule/index.vue'),
        meta: { title: '质量规则', requiresAuth: true },
      },
      {
        path: 'quality-task',
        name: 'QualityTask',
        component: () => import('@/views/quality-task/index.vue'),
        meta: { title: '质量任务', requiresAuth: true },
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
