/// <reference types='@dcloudio/types' />
/**
 * TypeScript 类型声明文件
 * 引入 Vue 类型并扩展 @vue/runtime-core 的组件自定义选项，以支持 UniApp 生命周期
 */
import 'vue'

declare module '@vue/runtime-core' {
  /** 合并 App 实例与 Page 实例的生命周期类型 */
  type Hooks = App.AppInstance & Page.PageInstance;

  interface ComponentCustomOptions extends Hooks {

  }
}
