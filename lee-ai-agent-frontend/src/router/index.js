import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: 'AI 旅行规划大师', transition: 'fade' }
  },
  {
    path: '/ai-trip',
    name: 'AiTrip',
    component: () => import('../views/AiTripChat.vue'),
    meta: { title: 'AI Trip 旅行规划', transition: 'slide' }
  },
  {
    path: '/manus',
    name: 'Manus',
    component: () => import('../views/ManusChat.vue'),
    meta: { title: 'AI 超级智能体', transition: 'slide' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  document.title = to.meta.title || 'AI 旅行规划大师'
})

export default router
