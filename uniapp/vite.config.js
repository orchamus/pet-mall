/**
 * Vite 构建配置文件
 * 配置 UniApp 插件、路径别名及开发服务器参数
 */
import { defineConfig } from 'vite'
import { resolve } from 'path'
import uni from '@dcloudio/vite-plugin-uni'
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    // 注册 UniApp Vite 插件，支持多端编译
    uni(),
  ],
  resolve: {
    alias: {
      // 配置 @ 指向 src 目录，便于模块引入
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    // 开发服务器端口
    port: 5241,
    // 监听所有网络接口，便于真机调试
    host: '0.0.0.0',
    // 端口被占用时自动尝试其他端口
    strictPort: false
  }
})
