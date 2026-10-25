/**
 * 店铺 API 模块
 * 提供店铺信息查询、新增、更新及店铺商品列表查询等接口
 */
import request from './request.js';

/**
 * 获取当前店铺信息
 * @returns {Promise<any>} 店铺信息数据
 */
export function getShop() {
	return request.get('/shop/getShop');
}

/**
 * 新增店铺
 * @param {Object} data - 店铺信息对象
 * @returns {Promise<any>} 接口返回结果
 */
export function addShop(data) {
	return request.post('/shop/addShop', data);
}

/**
 * 更新店铺信息
 * @param {Object} data - 店铺信息对象
 * @returns {Promise<any>} 接口返回结果
 */
export function updateShop(data) {
	return request.post('/shop/updateShop', data);
}

/**
 * 分页查询店铺列表
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @param {string} shopName - 店铺名称关键字
 * @returns {Promise<any>} 店铺分页数据
 */
export function getAllShop(pageNum, pageSize, shopName) {
	return request.get('/shop/getAllShop', { pageNum, pageSize, shopName });
}

/**
 * 查询店铺及其商品列表
 * @param {number} pageNum - 商品当前页码
 * @param {number} pageSize - 商品每页条数
 * @param {number|string} shopId - 店铺 ID
 * @returns {Promise<any>} 店铺及商品分页数据
 */
export function getShopAndProducts(pageNum, pageSize, shopId) {
	return request.get('/shop/getShopAndProducts', { pageNum, pageSize, shopId });
}
