import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: () => import('../views/Dashboard.vue') },
  { path: '/aftersale', component: () => import('../views/AfterSaleOrder.vue') },
  { path: '/compensation', component: () => import('../views/CompensationRecord.vue') },
  { path: '/rules', component: () => import('../views/CompensationRule.vue') },
  { path: '/users', component: () => import('../views/UserManagement.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
