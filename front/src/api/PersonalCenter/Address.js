/**
 * 个人中心收货地址 API 模块
 * 提供地址新增、查询、删除、更新、设为默认等接口。
 */
import request from "../request";

/**
 * 新增收货地址。
 *
 * @param {Object} address - 地址数据对象
 * @returns {Promise<*>} 返回新增结果的响应数据
 */
export function ADDaddress(address) {
  return request.post("/address/add",address);
}

/**
 * 获取当前登录用户的收货地址列表。
 *
 * @returns {Promise<*>} 返回地址列表
 */
export function getAddressList() {
  return request.get("/address/getAddressesList");
}

/**
 * 根据 ID 删除收货地址。
 *
 * @param {string|number} id - 地址 ID
 * @returns {Promise<*>} 返回删除结果的响应数据
 */
export function deleteAddress(id) {
  let date = new FormData();
  date.append('id', id)
  return request.post("/address/DeleteAddress",date);
}

/**
 * 更新收货地址信息。
 *
 * @param {Object} address - 地址数据对象
 * @returns {Promise<*>} 返回更新结果的响应数据
 */
export function updateAddress(address) {
  return request.post("/address/updateAddress",address);
}

/**
 * 将指定地址设为默认地址。
 *
 * @param {string|number} id - 地址 ID
 * @returns {Promise<*>} 返回设置结果的响应数据
 */
export function setDefaultAddress(id) {
  let date = new FormData();
  date.append('id', id)
  return request.post("/address/setDefaultAddress",date);
}

/**
 * 根据 ID 查询单条收货地址详情。
 *
 * @param {string|number} id - 地址 ID
 * @returns {Promise<*>} 返回地址详情
 */
export function getAddressById(id) {
  return request.get("/address/getAddressById?addressId="+id);
}
