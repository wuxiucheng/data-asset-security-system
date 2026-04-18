import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type UserInfo } from '@/api'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const raw = localStorage.getItem('userInfo')

  const userInfo = ref<UserInfo | null>(null)

  try {
    userInfo.value = raw && raw !== 'undefined'
      ? JSON.parse(raw)
      : null
  } catch (e) {
    console.warn('userInfo parse error:', e)
    userInfo.value = null
  }

  // 计算属性
  const isLogin = computed(() => !!token.value)
  const userRoles = computed(() => userInfo.value?.roles || [])

  // 方法
  const login = async (username: string, password: string) => {
    const res = await authApi.login({ username, password })
    token.value = res.token
    userInfo.value = res.userInfo

    // 保存到localStorage
    localStorage.setItem('token', res.token)
    localStorage.setItem('userInfo', JSON.stringify(res.userInfo))

    return res
  }

  const logout = async () => {
    try {
      await authApi.logout()
    } finally {
      token.value = ''
      userInfo.value = null

      // 清除localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }

  const getUserInfo = async () => {
    const res = await authApi.getUserInfo()
    userInfo.value = res
    localStorage.setItem('userInfo', JSON.stringify(res))
    return res
  }

  const hasRole = (role: string) => {
    return userRoles.value.includes(role)
  }

  const hasAnyRole = (roles: string[]) => {
    return roles.some(role => userRoles.value.includes(role))
  }

  return {
    token,
    userInfo,
    isLogin,
    userRoles,
    login,
    logout,
    getUserInfo,
    hasRole,
    hasAnyRole,
  }
})
