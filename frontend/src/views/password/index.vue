<template>
  <div class="password-container">
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>

      <el-form :model="passwordForm" :rules="rules" ref="passwordFormRef" label-width="120px" class="password-form">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            placeholder="请输入当前密码"
            show-password
            @keyup.enter="handleSubmit"
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码"
            show-password
            @keyup.enter="handleSubmit"
          />
          <div class="password-strength">
            <div class="strength-bar">
              <div 
                class="strength-item" 
                :class="{ active: passwordStrength >= 1 }"
              ></div>
              <div 
                class="strength-item" 
                :class="{ active: passwordStrength >= 2 }"
              ></div>
              <div 
                class="strength-item" 
                :class="{ active: passwordStrength >= 3 }"
              ></div>
            </div>
            <span class="strength-text">{{ strengthText }}</span>
          </div>
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码"
            show-password
            @keyup.enter="handleSubmit"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            <el-icon><Check /></el-icon> 确认修改
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-alert
        title="密码安全提示"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <ul class="security-tips">
            <li>密码长度至少为6个字符，建议8-20个字符</li>
            <li>建议包含大小写字母、数字和特殊字符</li>
            <li>不要使用生日、手机号等个人信息作为密码</li>
            <li>建议定期更换密码，提高账户安全性</li>
            <li>不要在多个网站使用相同的密码</li>
          </ul>
        </template>
      </el-alert>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Check, RefreshLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 表单引用
const passwordFormRef = ref<FormInstance>()

// 表单数据
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 提交加载状态
const submitLoading = ref(false)

// 密码强度计算
const passwordStrength = computed(() => {
  const password = passwordForm.newPassword
  let strength = 0
  
  if (password.length >= 6) strength++
  if (password.length >= 8) strength++
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^A-Za-z0-9]/.test(password)) strength++
  
  return Math.min(strength, 3)
})

// 密码强度文本
const strengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return '请输入密码'
  if (strength === 1) return '密码强度：弱'
  if (strength === 2) return '密码强度：中'
  return '密码强度：强'
})

// 自定义验证规则
const validateNewPassword = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度至少为6个字符'))
  } else if (value.length > 20) {
    callback(new Error('密码长度不能超过20个字符'))
  } else if (value === passwordForm.oldPassword) {
    callback(new Error('新密码不能与当前密码相同'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 提交修改密码
const handleSubmit = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    
    await ElMessageBox.confirm(
      '修改密码后，您需要重新登录。确定要继续吗？',
      '确认修改',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    submitLoading.value = true
    
    // 调用API修改密码
    await userStore.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    ElMessage.success('密码修改成功，即将重新登录...')
    
    // 延迟1秒后退出登录
    setTimeout(() => {
      userStore.logout()
      window.location.href = '/login'
    }, 1000)
    
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      ElMessage.error('密码修改失败，请检查当前密码是否正确')
    }
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const handleReset = () => {
  if (!passwordFormRef.value) return
  passwordFormRef.value.resetFields()
  ElMessage.info('表单已重置')
}
</script>

<style scoped>
.password-container {
  padding: 20px;
}

.password-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.password-form {
  margin-bottom: 30px;
}

.password-strength {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.strength-bar {
  display: flex;
  gap: 4px;
}

.strength-item {
  width: 60px;
  height: 6px;
  background-color: #e0e0e0;
  border-radius: 3px;
  transition: all 0.3s;
}

.strength-item.active:nth-child(1) {
  background-color: #f56c6c;
}

.strength-item.active:nth-child(2) {
  background-color: #e6a23c;
}

.strength-item.active:nth-child(3) {
  background-color: #67c23a;
}

.strength-text {
  font-size: 12px;
  color: #909399;
}

.security-tips {
  margin: 0;
  padding-left: 20px;
  list-style-type: disc;
}

.security-tips li {
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
}
</style>
