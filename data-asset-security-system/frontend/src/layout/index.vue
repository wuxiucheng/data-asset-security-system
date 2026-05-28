<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="layout-aside">
      <div class="logo">
        <h2>数据资产安全管理</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="layout-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-sub-menu index="user">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户权限管理</span>
          </template>
          <el-menu-item index="/user">用户管理</el-menu-item>
          <el-menu-item index="/role">角色管理</el-menu-item>
          <el-menu-item index="/permission">权限管理</el-menu-item>
          <el-menu-item index="/mfa">
            <el-icon><Lock /></el-icon>
            <span>多因素认证</span>
          </el-menu-item>
          <el-menu-item index="/session">
            <el-icon><Monitor /></el-icon>
            <span>会话管理</span>
          </el-menu-item>
          <el-menu-item index="/audit-log">
            <el-icon><DocumentChecked /></el-icon>
            <span>审计日志</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="responsibility">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>责任体系管理</span>
          </template>
          <el-menu-item index="/department">部门管理</el-menu-item>
          <el-menu-item index="/owner">责任人管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="classification">
          <template #title>
            <el-icon><Files /></el-icon>
            <span>分类分级管理</span>
          </template>
          <el-menu-item index="/classification-standard">分类标准</el-menu-item>
          <el-menu-item index="/classification">数据分类</el-menu-item>
          <el-menu-item index="/grading-standard">分级标准</el-menu-item>
          <el-menu-item index="/grading">数据分级</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="asset">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>数据资产管理</span>
          </template>
          <el-menu-item index="/asset">资产列表</el-menu-item>
          <el-menu-item index="/datasource">数据源配置</el-menu-item>
          <el-menu-item index="/asset-discovery">发现资产</el-menu-item>
          <el-menu-item index="/asset-field">字段管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="governance">
          <template #title>
            <el-icon><Warning /></el-icon>
            <span>数据治理</span>
          </template>
          <el-menu-item index="/sensitive-rule">敏感识别规则</el-menu-item>
          <el-menu-item index="/mask-strategy">脱敏策略</el-menu-item>
          <el-menu-item index="/quality-rule">质量规则</el-menu-item>
          <el-menu-item index="/quality-task">质量探查任务</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="approval">
          <template #title>
            <el-icon><Checked /></el-icon>
            <span>审批流程</span>
          </template>
          <el-menu-item index="/approval-definition">流程定义</el-menu-item>
          <el-menu-item index="/approval-instance">流程实例</el-menu-item>
          <el-menu-item index="/approval-task">我的待办</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="statistics">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>统计分析</span>
          </template>
          <el-menu-item index="/statistics">资产统计</el-menu-item>
          <el-menu-item index="/trend">趋势分析</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="report">
          <template #title>
            <el-icon><DocumentCopy /></el-icon>
            <span>报告管理</span>
          </template>
          <el-menu-item index="/report-asset-list">资产清单报告</el-menu-item>
          <el-menu-item index="/report-classification-stats">分类分级统计</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="toggle-icon" @click="toggleSidebar">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRouteName">{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-icon><Avatar /></el-icon>
              <span>{{ userStore.userInfo?.realName || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  HomeFilled,
  User,
  OfficeBuilding,
  Files,
  Document,
  DocumentCopy,
  DataAnalysis,
  Fold,
  Expand,
  Avatar,
  ArrowDown,
  Lock,
  Monitor,
  DocumentChecked,
  Warning,
  Checked,
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const currentRouteName = computed(() => route.meta.title as string)

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      router.push('/password')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })

        await userStore.logout()
        ElMessage.success('退出登录成功')
        router.push('/login')
      } catch (error) {
        // 用户取消操作
      }
      break
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  border-bottom: 1px solid #1f2d3d;
}

.logo h2 {
  font-size: 18px;
  margin: 0;
}

.layout-menu {
  border-right: none;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toggle-icon {
  font-size: 20px;
  cursor: pointer;
  color: #666;
}

.toggle-icon:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f5f5;
}

.layout-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
