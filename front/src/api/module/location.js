/**
 * 位置/地址相关 API 模块
 * 提供根据位置获取附近宠物店地址等接口。
 */
import request from "../request";

/**
 * 根据参数获取宠物店位置地址。
 *
 * @param {Object} params - 查询参数
 * @returns {Promise<*>} 返回包含地址列表的响应数据
 */
export function getLocation (params) {
  return request.get('/shopLocation/getAddress', params)
}
