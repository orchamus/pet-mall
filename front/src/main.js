/**
 * 应用入口文件
 * 负责创建 Vue 应用实例、注册全局插件、挂载应用到 DOM。
 */
import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router/index.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import VueVideoPlayer from 'vue-video-player'

// 创建 Vue 应用实例
const app = createApp(App)

// 注册 Vue Router，提供前端路由能力
app.use(router)

// 注册 Element Plus UI 组件库
app.use(ElementPlus)

// 全局注册 Element Plus 图标组件，方便模板中直接使用
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
// 注册 VueVideoPlayer 视频播放器插件
app.use(VueVideoPlayer)
// 将应用挂载到 id 为 app 的 DOM 节点上
app.mount('#app')
