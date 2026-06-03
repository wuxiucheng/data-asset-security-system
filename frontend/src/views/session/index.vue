<template>
  <div class="session-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>会话管理</h2>
          <el-button type="primary" @click="loadSessions" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <!-- 当前会话信息 -->
      <div class="current-session-section">
        <h3>当前会话</h3>
        <el-descriptions v-if="currentSession" :column="2" border>
          <el-descriptions-item label="会话ID">
            {{ currentSession.sessionId }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ currentSession.username }}
          </el-descriptions-item>
          <el-descriptions-item label="登录时间">
            {{ formatDateTime(currentSession.loginTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="最后访问时间">
            {{ formatDateTime(currentSession.lastAccessTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="过期时间">
            {{ formatDateTime(currentSession.expireTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="登录IP">
            {{ currentSession.loginIp }}
          </el-descriptions-item>
          <el-descriptions-item label="设备信息" :span="2">
            {{ currentSession.deviceInfo }}
          </el-descriptions-item>
          <el-descriptions-item label="状态" :span="2">
            <el-tag :type="getStatusType(currentSession.status)">
              {{ getStatusText(currentSession.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div v-else class="no-session">
          <el-empty description="暂无当前会话信息" />
        </div>
      </div>

      <!-- 活跃会话列表 -->
      <div class="active-sessions-section">
        <div class="section-header">
          <h3>活跃会话列表</h3>
          <el-button type="danger" @click="forceLogoutAll" :loading="loading">
            <el-icon><SwitchButton /></el-icon>
            强制下线所有会话
          </el-button>
        </div>

        <el-table
          :data="sessions"
          v-loading="loading"
          stripe
          style="width: 100%"
        >
          <el-table-column prop="sessionId" label="会话ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column label="登录时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.loginTime) }}
            </template>
          </el-table-column>
          <el-table-column label="最后访问时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.lastAccessTime) }}
            </template>
          </el-table-column>
          <el-table-column label="过期时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.expireTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="loginIp" label="登录IP" width="130" />
          <el-table-column prop="deviceInfo" label="设备信息" min-width="200" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button
                type="danger"
                size="small"
                @click="forceLogout(row.sessionId)"
                :loading="row.loading"
              >
                强制下线
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="!loading && sessions.length === 0" description="暂无活跃会话" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, SwitchButton } from '@element-plus/icons-vue'
import { sessionApi, type SessionInfo } from '@/api'

const currentSession = ref<SessionInfo | null>(null)
const sessions = ref<SessionInfo[]>([])
const loading = ref(false)

// 加载会话信息
const loadSessions = async () => {
  loading.value = true
  try {
    // 并行加载当前会话和活跃会话列表
    const [currentRes, activeRes] = await Promise.all([
      sessionApi.getCurrentSession(),
      sessionApi.getMyActiveSessions()
    ])

    currentSession.value = currentRes.data
    sessions.value = activeRes.data.map((session: SessionInfo) => ({
      ...session,
      loading: false
    }))
  } catch (error) {
    ElMessage.error('加载会话信息失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 强制下线指定会话
const forceLogout = async (sessionId: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要强制下线该会话吗？',
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 找到对应的会话并设置loading状态
    const session = sessions.value.find(s => s.sessionId === sessionId)
    if (session) {
      session.loading = true
    }

    await sessionApi.forceLogoutSession(sessionId)

    ElMessage.success('会话已强制下线')
    await loadSessions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('强制下线失败')
      console.error(error)
    }
  } finally {
    const session = sessions.value.find(s => s.sessionId === sessionId)
    if (session) {
      session.loading = false
    }
  }
}

// 强制下线所有会话
const forceLogoutAll = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要强制下线所有会话吗？这将包括当前会话，您需要重新登录。',
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await sessionApi.forceLogoutAllMySessions()

    ElMessage.success('所有会话已强制下线')

    // 延迟跳转到登录页
    setTimeout(() => {
      window.location.href = '/login'
    }, 1000)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('强制下线失败')
      console.error(error)
    }
  }
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取状态类型
const getStatusType = (status: string) => {
  const statusMap: Record<string, any> = {
    ACTIVE: 'success',
    INVALID: 'danger',
    EXPIRED: 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    ACTIVE: '活跃',
    INVALID: '失效',
    EXPIRED: '过期'
  }
  return statusMap[status] || status
}

onMounted(() => {
  loadSessions()
})
</script>

<style scoped>
.session-container {
  padding: 20px;
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

.current-session-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.current-session-section h3,
.active-sessions-section h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #606266;
}

.no-session {
  padding: 20px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #606266;
}
</style>