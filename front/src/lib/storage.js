/**
 * 本地存储封装模块
 * 对 localStorage 进行简单封装，统一 key 大小写格式并支持对象自动序列化。
 */

/**
 * 将数据存储到 localStorage。
 *
 * @param {string} name - 存储键名（会自动转为大写）
 * @param {*} content - 待存储的数据，非字符串会自动调用 JSON.stringify 序列化
 * @returns {void}
 */
export const setStore = (name, content) => {
  if (!name) return
  if (typeof content !== 'string') {
    content = JSON.stringify(content)
  }
  window.localStorage.setItem(`${name.toUpperCase()}`, content)
}

/**
 * 从 localStorage 读取数据。
 *
 * @param {string} name - 存储键名（会自动转为大写）
 * @returns {string|null} 存储的原始字符串值；键名为空时返回 undefined
 */
export const getStore = name => {
  if (!name) return
  return window.localStorage.getItem(`${name.toUpperCase()}`)
}

/**
 * 从 localStorage 删除指定键。
 *
 * @param {string} name - 待删除的键名（会自动转为大写）
 * @returns {void}
 */
export const removeStore = name => {
  if (!name) return
  window.localStorage.removeItem(`${name.toUpperCase()}`)
}
