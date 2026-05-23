import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import SavedAccountPreview from '@/views/SavedAccountPreview.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView
    },
    {
      path: '/login',
      name: 'login-alt',
      component: LoginView
    },
    {
      path: '/preview/saved-account-item',
      name: 'saved-account-preview',
      component: SavedAccountPreview
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'catch-all',
      component: LoginView
    }
  ]
})

export default router
