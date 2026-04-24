import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

const devProxyTarget = process.env.VITE_DEV_PROXY_TARGET || 'http://192.168.222.172:8081/BigSlick'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/backend': {
        target: devProxyTarget,
        changeOrigin: true, // Adjusts the origin of the host header to the target
      },
    },
  },
})
