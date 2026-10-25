import {createRouter, createWebHistory} from 'vue-router'
import Layout from '../layout/index.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: Layout,
      redirect: '/user',
      children: [
        {
          path: 'user',
          name: 'User',
          component: () => import('../views/user/index.vue'),
          meta: { title: '用户管理' },
        },
        {
          path: 'line',
          name: 'Line',
          component: () => import('../views/line/index.vue'),
          meta: { title: '线路管理' },
        },
      ],
    },
  ],
})

export default router