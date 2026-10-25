/**
 * 应用入口文件
 * 负责创建 Vue SSR 应用实例并挂载 Pinia 状态管理
 */
import { createSSRApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";

/**
 * 创建 UniApp 应用实例
 * @returns {{ app: import('vue').App }} 包含 Vue 应用实例的对象
 */
export function createApp() {
	const app = createSSRApp(App);
	const pinia = createPinia();
	app.use(pinia);
	return {
		app,
	};
}
