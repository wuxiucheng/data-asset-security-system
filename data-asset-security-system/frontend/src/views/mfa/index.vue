<template>
  <div class="mfa-container">
    <el-card class="mfa-card">
      <template #header>
        <div class="card-header">
          <h2>多因素认证设置</h2>
          <el-tag :type="mfaEnabled ? 'success' : 'info'">
            {{ mfaEnabled ? '已启用' : '未启用' }}
          </el-tag>
        </div>
      </template>

      <!-- 未启用MFA时显示设置向导 -->
      <div v-if="!mfaEnabled && !setupStep" class="mfa-disabled">
        <el-result icon="warning" title="多因素认证未启用" sub-title="启用多因素认证可以提高账户安全性">
          <template #extra>
            <el-button type="primary" @click="startSetup">启用多因素认证</el-button>
          </template>
        </el-result>
      </div>

      <!-- MFA设置步骤 -->
      <div v-else-if="setupStep" class="mfa-setup">
        <el-steps :active="setupStep" finish-status="success" align-center>
          <el-step title="下载认证器" />
          <el-step title="扫描二维码" />
          <el-step title="输入验证码" />
          <el-step title="完成设置" />
        </el-steps>

        <!-- 步骤1：下载认证器 -->
        <div v-if="setupStep === 1" class="setup-content">
          <el-alert
            title="请先下载Google Authenticator应用"
            type="info"
            :closable="false"
            show-icon
          >
            <p>支持的应用：</p>
            <ul>
              <li>Android: Google Authenticator</li>
              <li>iOS: Google Authenticator</li>
              <li>其他支持TOTP的应用</li>
            </ul>
          </el-alert>
          <div class="setup-actions">
            <el-button @click="setupStep = 0">取消</el-button>
            <el-button type="primary" @click="setupStep = 2">下一步</el-button>
          </div>
        </div>

        <!-- 步骤2：扫描二维码 -->
        <div v-if="setupStep === 2" class="setup-content">
          <div v-loading="loading">
            <div v-if="mfaSetupInfo.qrCodeImage" class="qr-section">
              <p class="qr-tip">请使用Google Authenticator扫描下方二维码：</p>
              <img :src="mfaSetupInfo.qrCodeImage" alt="MFA二维码" class="qr-code" />
              <p class="secret-key">密钥：{{ mfaSetupInfo.secret }}</p>
            </div>
          </div>
          <div class="setup-actions">
            <el-button @click="setupStep = 1">上一步</el-button>
            <el-button type="primary" @click="setupStep = 3">下一步</el-button>
          </div>
        </div>

        <!-- 步骤3：输入验证码 -->
        <div v-if="setupStep === 3" class="setup-content">
          <el-form :model="verifyForm" :rules="verifyRules" ref="verifyFormRef" label-width="100px">
            <el-form-item label="验证码" prop="code">
              <el-input
                v-model="verifyForm.code"
                placeholder="请输入6位验证码"
                maxlength="6"
                show-word-limit
              />
            </el-form-item>
          </el-form>
          <div class="setup-actions">
            <el-button @click="setupStep = 2">上一步</el-button>
            <el-button type="primary" @click="verifyAndEnable" :loading="loading">验证并启用</el-button>
          </div>
        </div>

        <!-- 步骤4：完成设置 -->
        <div v-if="setupStep === 4" class="setup-content">
          <el-result icon="success" title="多因素认证启用成功" sub-title="您的账户现在受到多因素认证保护">
            <template #extra>
              <el-button type="primary" @click="showBackupCodes">查看备用码</el-button>
              <el-button @click="finishSetup">完成</el-button>
            </template>
          </el-result>
        </div>
      </div>

      <!-- 已启用MFA时显示管理界面 -->
      <div v-else-if="mfaEnabled" class="mfa-enabled">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="状态">
            <el-tag type="success">已启用</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="认证类型">
            {{ mfaConfig.mfaType }}
          </el-descriptions-item>
          <el-descriptions-item label="启用时间">
            {{ mfaConfig.enabledTime || '未知' }}
          </el-descriptions-item>
          <el-descriptions-item label="最后验证时间">
            {{ mfaConfig.lastVerifiedTime || '未知' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="mfa-actions">
          <el-button @click="showBackupCodesDialog">查看备用码</el-button>
          <el-button @click="generateNewBackupCodes" :loading="loading">生成新备用码</el-button>
          <el-button type="danger" @click="showDisableDialog">禁用MFA</el-button>
        </div>
      </div>
    </el-card>

    <!-- 备用码对话框 -->
    <el-dialog v-model="backupCodesVisible" title="备用码" width="500px">
      <el-alert
        title="请妥善保存备用码"
        type="warning"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      >
        <p>备用码用于在无法使用认证器时登录，每个备用码只能使用一次。</p>
      </el-alert>

      <div v-if="backupCodes.length > 0" class="backup-codes-list">
        <div v-for="(code, index) in backupCodes" :key="index" class="backup-code-item">
          {{ code }}
        </div>
      </div>

      <template #footer>
        <el-button @click="backupCodesVisible = false">关闭</el-button>
        <el-button type="primary" @click="copyBackupCodes">复制备用码</el-button>
      </template>
    </el-dialog>

    <!-- 禁用MFA对话框 -->
    <el-dialog v-model="disableDialogVisible" title="禁用多因素认证" width="400px">
      <el-alert
        title="安全警告"
        type="error"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      >
        <p>禁用多因素认证会降低账户安全性，请谨慎操作。</p>
      </el-alert>

      <el-form :model="disableForm" :rules="disableRules" ref="disableFormRef" label-width="80px">
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="disableForm.password"
            type="password"
            placeholder="请输入密码以确认"
            show-password
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="disableDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="disableMfa" :loading="loading">确认禁用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { mfaApi, type MfaSetupInfo } from '@/api'

const mfaEnabled = ref(false)
const mfaConfig = ref<MfaSetupInfo>({} as MfaSetupInfo)
const mfaSetupInfo = ref<MfaSetupInfo>({} as MfaSetupInfo)
const setupStep = ref(0)
const loading = ref(false)
const backupCodes = ref<string[]>([])
const backupCodesVisible = ref(false)
const disableDialogVisible = ref(false)

const verifyFormRef = ref<FormInstance>()
const disableFormRef = ref<FormInstance>()

const verifyForm = ref({
  code: ''
})

const disableForm = ref({
  password: ''
})

const verifyRules: FormRules = {
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码必须是6位数字', trigger: 'blur' }
  ]
}

const disableRules: FormRules = {
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 获取MFA状态
const checkMfaStatus = async () => {
  try {
    const res = await mfaApi.checkMfaStatus()
    mfaEnabled.value = res.data === true || res.data?.enabled === true

    if (mfaEnabled.value) {
      await getMfaConfig()
    }
  } catch (error) {
    console.error('获取MFA状态失败:', error)
  }
}

// 获取MFA配置
const getMfaConfig = async () => {
  try {
    const res = await mfaApi.getMfaConfig()
    mfaConfig.value = res.data
  } catch (error) {
    console.error('获取MFA配置失败:', error)
  }
}

// 开始设置
const startSetup = async () => {
  loading.value = true
  try {
    const res = await mfaApi.generateMfaSetup()
    mfaSetupInfo.value = res.data
    setupStep.value = 1
  } catch (error) {
    ElMessage.error('生成MFA设置信息失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 验证并启用
const verifyAndEnable = async () => {
  if (!verifyFormRef.value) return

  await verifyFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await mfaApi.enableMfa({
        mfaType: mfaSetupInfo.value.mfaType,
        secret: mfaSetupInfo.value.secret,
        verificationCode: verifyForm.value.code
      })

      setupStep.value = 4
      mfaEnabled.value = true
      ElMessage.success('多因素认证启用成功')

      // 获取备用码
      await getBackupCodes()
    } catch (error: any) {
      ElMessage.error(error.message || '验证失败，请检查验证码')
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

// 获取备用码
const getBackupCodes = async () => {
  loading.value = true
  try {
    const res = await mfaApi.generateBackupCodes()
    backupCodes.value = res.data
  } catch (error) {
    ElMessage.error('获取备用码失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 显示备用码
const showBackupCodes = () => {
  backupCodesVisible.value = true
}

// 显示备用码对话框
const showBackupCodesDialog = async () => {
  await getBackupCodes()
  backupCodesVisible.value = true
}

// 生成新备用码
const generateNewBackupCodes = async () => {
  try {
    await ElMessageBox.confirm(
      '生成新备用码后，旧备用码将失效，确定要继续吗？',
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await getBackupCodes()
    ElMessage.success('新备用码生成成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

// 复制备用码
const copyBackupCodes = () => {
  const text = backupCodes.value.join('\n')
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('备用码已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

// 显示禁用对话框
const showDisableDialog = () => {
  disableDialogVisible.value = true
}

// 禁用MFA
const disableMfa = async () => {
  if (!disableFormRef.value) return

  await disableFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await mfaApi.disableMfa(disableForm.value.password)

      mfaEnabled.value = false
      disableDialogVisible.value = false
      ElMessage.success('多因素认证已禁用')

      // 重置表单
      disableForm.value.password = ''
      setupStep.value = 0
    } catch (error: any) {
      ElMessage.error(error.message || '禁用失败，请检查密码')
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

// 完成设置
const finishSetup = () => {
  setupStep.value = 0
}

onMounted(() => {
  checkMfaStatus()
})
</script>

<style scoped>
.mfa-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.mfa-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.mfa-disabled {
  padding: 20px 0;
}

.mfa-setup {
  padding: 30px 0;
}

.setup-content {
  margin-top: 30px;
}

.qr-section {
  text-align: center;
  padding: 20px;
}

.qr-tip {
  font-size: 14px;
  color: #606266;
  margin-bottom: 20px;
}

.qr-code {
  width: 200px;
  height: 200px;
  margin: 20px auto;
  border: 1px solid #dcdfe6;
  padding: 10px;
  border-radius: 4px;
}

.secret-key {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.setup-actions {
  margin-top: 30px;
  text-align: center;
}

.setup-actions .el-button {
  margin: 0 10px;
}

.mfa-enabled {
  padding: 20px 0;
}

.mfa-actions {
  margin-top: 30px;
  text-align: center;
}

.mfa-actions .el-button {
  margin: 0 10px;
}

.backup-codes-list {
  max-height: 300px;
  overflow-y: auto;
}

.backup-code-item {
  padding: 10px;
  margin: 5px 0;
  background: #f5f7fa;
  border-radius: 4px;
  font-family: monospace;
  font-size: 14px;
  text-align: center;
  letter-spacing: 2px;
}
</style>