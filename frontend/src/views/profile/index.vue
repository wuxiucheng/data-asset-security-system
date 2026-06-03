<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="8">
          <div class="avatar-section">
            <el-avatar :size="120" :src="userInfo?.avatar" class="avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <h3 class="username">{{ userInfo?.realName || userInfo?.username }}</h3>
            <p class="user-role">{{ getRoleText(userInfo?.roles) }}</p>
            <el-button type="primary" @click="editAvatar" class="edit-avatar-btn">
              <el-icon><Camera /></el-icon> 修改头像
            </el-button>
          </div>
        </el-col>

        <el-col :span="16">
          <el-form :model="profileForm" :rules="rules" ref="profileFormRef" label-width="100px">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="profileForm.userId" disabled />
            </el-form-item>
            
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>

            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>

            <el-form-item label="部门" prop="department">
              <el-input v-model="profileForm.department" disabled />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleSave" :loading="saveLoading">
                <el-icon><Check /></el-icon> 保存修改
              </el-button>
              <el-button @click="handleReset">
                <el-icon><RefreshLeft /></el-icon> 重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-card>

    <!-- 修改头像对话框 -->
    <el-dialog v-model="avatarDialogVisible" title="修改头像" width="400px">
      <el-upload
        class="avatar-uploader"
        action="#"
        :show-file-list="false"
        :before-upload="beforeAvatarUpload"
        :http-request="uploadAvatar"
      >
        <img v-if="avatarUrl" :src="avatarUrl" class="avatar-preview" />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <div class="upload-tip">
        <p>支持 jpg、png 格式，文件大小不超过 2MB</p>
      </div>
      <template #footer>
        <el-button @click="avatarDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAvatar" :loading="uploadLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import { User, Camera, Check, RefreshLeft, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 用户信息
const userInfo = ref<any>(null)

// 表单引用
const profileFormRef = ref<FormInstance>()

// 表单数据
const profileForm = reactive({
  userId: '',
  username: '',
  realName: '',
  email: '',
  phone: '',
  department: ''
})

// 表单验证规则
const rules: FormRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 加载状态
const saveLoading = ref(false)

// 头像相关
const avatarDialogVisible = ref(false)
const avatarUrl = ref('')
const uploadLoading = ref(false)
const tempAvatarFile = ref<File | null>(null)

// 获取角色文本
const getRoleText = (roles: string[]) => {
  if (!roles || roles.length === 0) return '暂无角色'
  const roleMap: Record<string, string> = {
    'SYSTEM_ADMIN': '系统管理员',
    'DATA_ADMIN': '数据管理员',
    'DATA_USER': '数据用户',
    'DATA_VIEWER': '数据查看者'
  }
  return roles.map(role => roleMap[role] || role).join('、')
}

// 加载用户信息
const loadUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo)
    
    // 填充表单数据
    profileForm.userId = userInfo.value.userId
    profileForm.username = userInfo.value.username
    profileForm.realName = userInfo.value.realName
    profileForm.email = userInfo.value.email
    profileForm.phone = userInfo.value.phone || ''
    profileForm.department = userInfo.value.departmentName || '未设置'
    
    // 设置头像
    if (userInfo.value.avatar) {
      avatarUrl.value = userInfo.value.avatar
    }
  }
}

// 保存修改
const handleSave = async () => {
  if (!profileFormRef.value) return
  
  try {
    await profileFormRef.value.validate()
    saveLoading.value = true
    
    // 调用API更新用户信息
    await userStore.updateProfile({
      realName: profileForm.realName,
      email: profileForm.email,
      phone: profileForm.phone
    })
    
    // 更新本地存储的用户信息
    const updatedUserInfo = {
      ...userInfo.value,
      realName: profileForm.realName,
      email: profileForm.email,
      phone: profileForm.phone
    }
    localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
    userInfo.value = updatedUserInfo
    
    ElMessage.success('个人信息修改成功')
  } catch (error) {
    if (error !== false) {
      ElMessage.error('保存失败，请重试')
    }
  } finally {
    saveLoading.value = false
  }
}

// 重置表单
const handleReset = () => {
  loadUserInfo()
  ElMessage.info('已重置为原始信息')
}

// 修改头像
const editAvatar = () => {
  avatarDialogVisible.value = true
  avatarUrl.value = userInfo.value?.avatar || ''
}

// 头像上传前验证
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isJPGOrPNG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png'
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('头像只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  
  tempAvatarFile.value = rawFile
  
  // 预览图片
  const reader = new FileReader()
  reader.readAsDataURL(rawFile)
  reader.onload = (e) => {
    avatarUrl.value = e.target?.result as string
  }
  
  return false // 阻止自动上传
}

// 上传头像
const uploadAvatar = async () => {
  // 这里实现实际的头像上传逻辑
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ url: avatarUrl.value })
    }, 1000)
  })
}

// 确认修改头像
const confirmAvatar = async () => {
  if (!tempAvatarFile.value) {
    ElMessage.warning('请选择头像文件')
    return
  }
  
  try {
    uploadLoading.value = true
    
    // 模拟上传头像
    await uploadAvatar()
    
    // 更新用户信息
    const updatedUserInfo = {
      ...userInfo.value,
      avatar: avatarUrl.value
    }
    localStorage.setItem('userInfo', JSON.stringify(updatedUserInfo))
    userInfo.value = updatedUserInfo
    
    ElMessage.success('头像修改成功')
    avatarDialogVisible.value = false
  } catch (error) {
    ElMessage.error('头像上传失败，请重试')
  } finally {
    uploadLoading.value = false
  }
}

// 初始化
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.avatar-section {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.avatar {
  margin-bottom: 20px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  background-color: rgba(255, 255, 255, 0.2);
}

.username {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 10px 0;
}

.user-role {
  font-size: 14px;
  opacity: 0.9;
  margin: 0 0 20px 0;
}

.edit-avatar-btn {
  background-color: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: white;
}

.edit-avatar-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.4);
}

.avatar-uploader {
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 148px;
  height: 148px;
  text-align: center;
  line-height: 148px;
}

.avatar-preview {
  width: 148px;
  height: 148px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  text-align: center;
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}

.upload-tip p {
  margin: 0;
}
</style>
