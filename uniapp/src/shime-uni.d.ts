/**
 * TypeScript 类型声明文件
 * 扩展 Vue 组件选项类型，注入 UniApp 应用与页面生命周期 hooks
 */
export {};

declare module "vue" {
  /** 合并 App 实例与 Page 实例的生命周期类型 */
  type Hooks = App.AppInstance & Page.PageInstance;
  interface ComponentCustomOptions extends Hooks {}
}
