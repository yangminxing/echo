<template>
  <div class="home-container">
    <header class="top-header">
      <div class="logo">Echo System</div>
      <nav class="top-nav">
        <div
          v-for="menu in topMenus"
          :key="menu.key"
          class="top-menu-item"
          :class="{ active: activeTopMenu === menu.key }"
          @click="handleTopMenuClick(menu)"
        >
          {{ menu.label }}
        </div>
      </nav>
      <div class="user-info">
        <span>{{ username }}</span>
        <button @click="handleLogout" class="logout-btn">退出</button>
      </div>
    </header>

    <div class="main-content">
      <aside class="left-sidebar">
        <div
          v-for="item in currentLeftMenu"
          :key="item.key"
          class="left-menu-item"
          :class="{ active: activeLeftMenu === item.key }"
          @click="handleLeftMenuClick(item)"
        >
          {{ item.label }}
        </div>
      </aside>

      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { logout } from '../api/login'

interface MenuItem {
  key: string
  label: string
  path?: string
}

interface TopMenu extends MenuItem {
  children?: MenuItem[]
}

const router = useRouter()
const route = useRoute()

const username = ref(localStorage.getItem('username') || 'User')

const topMenus: TopMenu[] = [
  {
    key: 'design',
    label: '在线设计',
    children: [
      { key: 'form-designer', label: '表单设计器', path: '/home/form-designer' },
      { key: 'flow-designer', label: '流程设计器', path: '/home/flow-designer' }
    ]
  },
  {
    key: 'develop',
    label: '在线开发',
    children: [
      { key: 'fixed-form-dev', label: '固定表单开发', path: '/home/fixed-form-dev' }
    ]
  }
]

const activeTopMenu = ref('design')
const activeLeftMenu = ref('form-designer')

const currentLeftMenu = computed(() => {
  const topMenu = topMenus.find(m => m.key === activeTopMenu.value)
  return topMenu?.children || []
})

const handleTopMenuClick = (menu: TopMenu) => {
  activeTopMenu.value = menu.key
  if (menu.children && menu.children.length > 0) {
    const firstChild = menu.children[0]
    activeLeftMenu.value = firstChild.key
    if (firstChild.path) {
      router.push(firstChild.path)
    }
  }
}

const handleLeftMenuClick = (item: MenuItem) => {
  activeLeftMenu.value = item.key
  if (item.path) {
    router.push(item.path)
  }
}

const handleLogout = async () => {
  try {
    await logout()
  } catch (error) {
    console.error('Logout error:', error)
  }
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/login')
}

const initRoute = () => {
  const path = route.path
  if (path.includes('form-designer')) {
    activeTopMenu.value = 'design'
    activeLeftMenu.value = 'form-designer'
  } else if (path.includes('flow-designer')) {
    activeTopMenu.value = 'design'
    activeLeftMenu.value = 'flow-designer'
  } else if (path.includes('fixed-form-dev')) {
    activeTopMenu.value = 'develop'
    activeLeftMenu.value = 'fixed-form-dev'
  }
}

initRoute()
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.top-header {
  height: 60px;
  background: #2c3e50;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.logo {
  font-size: 20px;
  font-weight: 600;
  margin-right: 40px;
}

.top-nav {
  display: flex;
  gap: 8px;
  flex: 1;
}

.top-menu-item {
  padding: 8px 20px;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.3s;
}

.top-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.top-menu-item.active {
  background: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logout-btn {
  padding: 6px 16px;
  background: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.logout-btn:hover {
  background: #e64444;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.left-sidebar {
  width: 200px;
  background: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  padding: 16px 0;
}

.left-menu-item {
  padding: 12px 24px;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.left-menu-item:hover {
  background: #ecf5ff;
  color: #409eff;
}

.left-menu-item.active {
  background: #ecf5ff;
  color: #409eff;
  border-left-color: #409eff;
}

.content-area {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background: #fafafa;
}
</style>