<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>数据资产安全管理系统</h1>
        <p>Data Asset Security Management System</p>
      </div>

      <!-- 第一步：输入用户名密码 -->
      <el-form
        v-if="loginStep === 1"
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 第二步：MFA验证 -->
      <div v-if="loginStep === 2" class="mfa-step">
        <el-alert
          title="需要多因素认证"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        >
          <p>请输入Google Authenticator中的6位验证码</p>
        </el-alert>

        <el-form
          ref="mfaFormRef"
          :model="mfaForm"
          :rules="mfaRules"
          class="login-form"
          @keyup.enter="handleMfaVerify"
        >
          <el-form-item prop="code">
            <el-input
              v-model="mfaForm.code"
              placeholder="请输入6位验证码"
              size="large"
              maxlength="6"
              show-word-limit
              :prefix-icon="Key"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="loading"
              @click="handleMfaVerify"
            >
              验证
            </el-button>
          </el-form-item>

          <el-form-item>
            <el-button
              type="info"
              size="large"
              class="login-button"
              @click="useBackupCode"
            >
              使用备用码
            </el-button>
          </el-form-item>
        </el-form>

        <el-button
          type="text"
          @click="loginStep = 1"
          style="margin-top: 10px"
        >
          返回登录
        </el-button>
      </div>

      <!-- 第三步：使用备用码 -->
      <div v-if="loginStep === 3" class="backup-code-step">
        <el-alert
          title="使用备用码登录"
          type="warning"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        >
          <p>请输入其中一个备用码（每个备用码只能使用一次）</p>
        </el-alert>

        <el-form
          ref="backupCodeFormRef"
          :model="backupCodeForm"
          :rules="backupCodeRules"
          class="login-form"
          @keyup.enter="handleBackupCodeVerify"
        >
          <el-form-item prop="code">
            <el-input
              v-model="backupCodeForm.code"
              placeholder="请输入8位备用码"
              size="large"
              maxlength="8"
              show-word-limit
              :prefix-icon="Key"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="loading"
              @click="handleBackupCodeVerify"
            >
              验证
            </el-button>
          </el-form-item>
        </el-form>

        <el-button
          type="text"
          @click="loginStep = 2"
          style="margin-top: 10px"
        >
          返回验证码输入
        </el-button>
      </div>

      <div class="login-footer">
        <p>默认账号: admin / admin123</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Key } from '@element-plus/icons-vue'
import { authApi, type LoginRequest, type LoginResponse } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()

const loginFormRef = ref<FormInstance>()
const mfaFormRef = ref<FormInstance>()
const backupCodeFormRef = ref<FormInstance>()
const loading = ref(false)
const loginStep = ref(1)

const loginForm = reactive<LoginRequest>({
  username: '',
  password: '',
  rememberMe: false
})

const mfaForm = reactive({
  code: ''
})

const backupCodeForm = reactive({
  code: ''
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
}

const mfaRules: FormRules = {
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码必须是6位数字', trigger: 'blur' }
  ]
}

const backupCodeRules: FormRules = {
  code: [
    { required: true, message: '请输入备用码', trigger: 'blur' },
    { pattern: /^\d{8}$/, message: '备用码必须是8位数字', trigger: 'blur' }
  ]
}

// 第一步：用户名密码登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const { data } = await authApi.login(loginForm)

      // 检查是否需要MFA验证
      if (data.userInfo && data.userInfo.mfaEnabled) {
        // 需要MFA验证，进入第二步
        loginStep.value = 2
        ElMessage.info('请输入MFA验证码')
      } else {
        // 不需要MFA验证，直接登录成功
        handleLoginSuccess(data)
      }
    } catch (error: any) {
      ElMessage.error(error.message || '登录失败')
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}

// 第二步：MFA验证
const handleMfaVerify = async () => {
  if (!mfaFormRef.value) return

  await mfaFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      // 重新登录，这次带上MFA验证码
      const loginData: LoginRequest = {
        ...loginForm,
        mfaCode: mfaForm.code
      }

      const { data } = await authApi.login(loginData)
      handleLoginSuccess(data)
    } catch (error: any) {
      ElMessage.error(error.message || 'MFA验证失败')
      console.error('MFA验证失败:', error)
    } finally {
      loading.value = false
    }
  })
}

// 使用备用码
const useBackupCode = () => {
  loginStep.value = 3
}

// 第三步：备用码验证
const handleBackupCodeVerify = async () => {
  if (!backupCodeFormRef.value) return

  await backupCodeFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      // 重新登录，这次带上备用码
      const loginData: LoginRequest = {
        ...loginForm,
        mfaCode: backupCodeForm.code
      }

      const { data } = await authApi.login(loginData)
      handleLoginSuccess(data)
    } catch (error: any) {
      ElMessage.error(error.message || '备用码验证失败')
      console.error('备用码验证失败:', error)
    } finally {
      loading.value = false
    }
  })
}

// 登录成功处理
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
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 14px;
  color: #999;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
}

.login-footer p {
  font-size: 12px;
  color: #999;
}

.mfa-step,
.backup-code-step {
  padding: 20px 0;
}
</style>
