# 登录不跳转问题修复

## 🔍 问题分析

### 现象
- 登录成功后不跳转到首页
- 前端不报错
- 用户停留在登录页面

### 根本原因

1. **Token键名不匹配**
   - userStore使用: `localStorage.getItem('token')`
   - 登录页面保存: `localStorage.setItem('accessToken', ...)`
   - 导致userStore.isLogin始终为false

2. **状态未同步**
   - 登录成功后只更新了localStorage
   - 没有更新userStore的响应式状态
   - 路由守卫检查userStore.isLogin时返回false

3. **路由守卫逻辑**
   ```typescript
   if (to.meta.requiresAuth && !userStore.isLogin) {
     next('/login')  // 因为isLogin为false，被重定向回登录页
   }
   ```

## ✅ 修复方案

### 1. 统一Token键名

修改登录成功处理函数，同时保存`token`和`accessToken`：

```typescript
localStorage.setItem('token', data.accessToken)
localStorage.setItem('accessToken', data.accessToken)
```

### 2. 更新userStore状态

登录成功后立即更新userStore：

```typescript
const userStore = useUserStore()
userStore.token = data.accessToken
userStore.userInfo = data.userInfo
```

### 3. 使用nextTick确保状态更新

```typescript
nextTick(() => {
  router.push('/dashboard')
})
```

## 📝 修改的文件

### frontend/src/views/login/index.vue

**修改内容**:

1. 添加导入：
```typescript
import { reactive, ref, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
```

2. 修改handleLoginSuccess函数：
```typescript
const handleLoginSuccess = (data: LoginResponse) => {
  // 保存Token和用户信息到localStorage
  localStorage.setItem('token', data.accessToken)
  localStorage.setItem('accessToken', data.accessToken)
  localStorage.setItem('refreshToken', data.refreshToken)
  localStorage.setItem('userInfo', JSON.stringify(data.userInfo))

  // 更新userStore
  const userStore = useUserStore()
  userStore.token = data.accessToken
  userStore.userInfo = data.userInfo

  ElMessage.success('登录成功')

  // 使用nextTick确保状态更新后再跳转
  nextTick(() => {
    router.push('/dashboard')
  })
}
```

## 🎯 修复效果

修复后的登录流程：

1. ✅ 用户输入用户名密码
2. ✅ 调用登录API成功
3. ✅ 保存token到localStorage（使用正确的键名）
4. ✅ 更新userStore的响应式状态
5. ✅ 显示"登录成功"提示
6. ✅ 路由跳转到/dashboard
7. ✅ 路由守卫检查userStore.isLogin为true
8. ✅ 成功进入首页

## 🧪 测试验证

### 测试步骤

1. 清除浏览器缓存和localStorage
2. 访问登录页面 http://localhost:5173/login
3. 输入用户名: admin
4. 输入密码: admin123
5. 点击登录按钮
6. 观察是否跳转到首页

### 预期结果

- ✅ 显示"登录成功"提示
- ✅ 自动跳转到首页（/dashboard）
- ✅ localStorage中保存了token和userInfo
- ✅ 页面显示用户信息

## 📊 相关代码流程

### 登录流程图

```
用户点击登录
    ↓
调用authApi.login()
    ↓
后端返回{ accessToken, refreshToken, userInfo }
    ↓
handleLoginSuccess()
    ↓
保存到localStorage
    ↓
更新userStore状态
    ↓
router.push('/dashboard')
    ↓
路由守卫检查userStore.isLogin
    ↓
isLogin为true，允许访问
    ↓
进入首页
```

### 状态管理流程

```
localStorage.setItem('token', ...)
    ↓
userStore.token = data.accessToken
    ↓
userStore.isLogin = computed(() => !!token.value)
    ↓
isLogin变为true
    ↓
路由守卫通过
```

## 💡 最佳实践

### 1. 统一状态管理

建议所有认证状态都通过userStore管理，不要直接操作localStorage：

```typescript
// 推荐
const userStore = useUserStore()
userStore.login(username, password)

// 不推荐
localStorage.setItem('token', token)
```

### 2. 响应式状态同步

确保Pinia store的状态与localStorage保持同步：

```typescript
// store定义时从localStorage初始化
const token = ref<string>(localStorage.getItem('token') || '')

// 更新时同步到localStorage
const setToken = (newToken: string) => {
  token.value = newToken
  localStorage.setItem('token', newToken)
}
```

### 3. 路由守卫优化

考虑使用更可靠的方式检查登录状态：

```typescript
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 检查token是否存在
  const hasToken = !!localStorage.getItem('token')

  if (to.meta.requiresAuth && !hasToken) {
    next('/login')
  } else {
    next()
  }
})
```

## 🔧 其他可能的问题

如果修复后仍然不跳转，检查：

1. **后端API返回格式**
   - 确认返回的是`accessToken`而不是`token`
   - 确认返回的数据结构正确

2. **CORS问题**
   - 检查浏览器控制台是否有CORS错误
   - 确认后端配置了正确的CORS策略

3. **路由配置**
   - 确认/dashboard路由存在
   - 确认路由组件可以正常加载

4. **浏览器控制台**
   - 打开开发者工具查看是否有错误
   - 检查Network标签查看API请求状态

---

**修复时间**: 2026-04-24
**问题**: 登录成功不跳转
**原因**: Token键名不匹配 + 状态未同步
**状态**: ✅ 已修复

🎯 登录跳转问题已修复！