/**
 * 收货地址 API 模块
 * 提供地址列表查询、单条查询、新增、更新、删除及设置默认地址等接口
 */
import request from './request.js';

/**
 * 获取当前用户的收货地址列表
 * @returns {Promise<any>} 地址列表数据
 */
export function getAddressList() {
	return request.get('/address/getAddressesList');
}

/**
 * 根据 ID 获取单个地址详情
 * @param {number|string} id - 地址 ID
 * @returns {Promise<any>} 地址详情数据
 */
export function getAddressById(id) {
	return request.get('/address/getAddressById?addressId=' + id);
}

/**
 * 新增收货地址
 * @param {Object} address - 地址对象
 * @returns {Promise<any>} 接口返回结果
 */
export function addAddress(address) {
	return request.post('/address/add', address);
}

/**
 * 更新收货地址
 * @param {Object} address - 地址对象
 * @returns {Promise<any>} 接口返回结果
 */
export function updateAddress(address) {
	return request.post('/address/updateAddress', address);
}

/**
 * 删除收货地址
 * @param {number|string} id - 地址 ID
 * @returns {Promise<any>} 接口返回结果
 */
export function deleteAddress(id) {
	return request.post('/address/DeleteAddress?id=' + id);
}

/**
 * 设置默认收货地址
 * @param {number|string} id - 地址 ID
 * @returns {Promise<any>} 接口返回结果
 */
export function setDefaultAddress(id) {
	return request.post('/address/setDefaultAddress?id=' + id);
}
