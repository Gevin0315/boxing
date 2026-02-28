import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import { visualizer } from 'rollup-plugin-visualizer'

export default defineConfig({
  plugins: [
    vue(),
    // 可视化打包分析
    visualizer({
      open: false,
      gzipSize: 10,
      brotliSize: 10
    })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    // 代码分割优化
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
          'vendor': ['axios']
        }
      }
    },
    // 压缩优化
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        pure_funcs: ['console.log', 'console.info']
      }
    },
    // 打包优化
    chunkSizeWarningLimit: 1500,
    // 源码映射优化（生产环境使用 CDN）
    ...(process.env.NODE_ENV === 'production' ? {
      build: {
        rollupOptions: {
          external: ['vue', 'element-plus', 'axios']
        }
      }
    } : {}),
    // 开发环境 source map
    ...(process.env.NODE_ENV !== 'production' ? {
      build: {
        sourcemap: true
      }
    } : {})
  },
  css: {
    // CSS 代码分割
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler' // 使用更快的编译器
      }
    }
  }
})
