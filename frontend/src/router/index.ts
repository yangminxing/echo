import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import FormDesigner from '../views/designer/FormDesigner.vue'
import FormEditor from '../views/designer/FormEditor.vue'
import FlowDesigner from '../views/designer/FlowDesigner.vue'
import FixedFormDev from '../views/developer/FixedFormDev.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    children: [
      {
        path: 'form-designer',
        name: 'FormDesigner',
        component: FormDesigner
      },
      {
        path: 'form-editor/:id?',
        name: 'FormEditor',
        component: FormEditor
      },
      {
        path: 'flow-designer',
        name: 'FlowDesigner',
        component: FlowDesigner
      },
      {
        path: 'fixed-form-dev',
        name: 'FixedFormDev',
        component: FixedFormDev
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router