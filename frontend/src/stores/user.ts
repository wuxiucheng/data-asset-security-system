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
    // API返回格式: { code: 0, data: { token, userInfo } }
    const data = res.data || res
    token.value = data.token
    userInfo.value = data.userInfo

    // 保存到localStorage
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(data.userInfo))

    return data
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
    // API返回格式: { code: 0, data: userInfo }
    const data = res.data || res
    userInfo.value = data
    localStorage.setItem('userInfo', JSON.stringify(data))
    return data
  }

  const hasRole = (role: string) => {
    return userRoles.value.includes(role)
  }

  const hasAnyRole = (roles: string[]) => {
    return roles.some(role => userRoles.value.includes(role))
  }

  const updateProfile = async (data: any) => {
    // 调用API更新个人信息
    const res = await authApi.updateProfile(data)
    // API返回格式: { code: 0, data: updatedUserInfo }
    const result = res.data || res
    
    // 更新本地用户信息
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...data }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
    
    return result
  }

  const changePassword = async (data: any) => {
    // 调用API修改密码
    const res = await authApi.changePassword(data)
    // API返回格式: { code: 0, message: "success" }
    return res.data || res
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
    updateProfile,
    changePassword,
  }
})
