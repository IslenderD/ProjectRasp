import { createMemoryHistory, createRouter } from 'vue-router'

import Login from '@/Login.vue'
import Homepage from '@/Homepage.vue'
import InGame from '@/InGame.vue'

const routes = [
    { path: '/', component: Login},
    { path: '/homepage/:name+', component: Homepage},
    { path: '/ingame/:name+', component: InGame}
]

const router = createRouter({
    history: createMemoryHistory(),
    routes,
})

export default router