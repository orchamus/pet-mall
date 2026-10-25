/**
 * 购物车 API 模块
 * 提供购物车列表查询、添加商品及删除商品等接口
 */
import request from './request.js';

/**
 * 获取当前用户的购物车列表
 * @returns {Promise<any>} 购物车列表数据
 */
export function getCartList() {
	return request.get('/cart/getCartList');
}

/**
 * 添加商品到购物车
 * @param {Object} product - 商品对象
 * @param {number} quantity - 购买数量
 * @returns {Promise<any>} 添加结果
 */
export function addToCart(product, quantity) {
	return request.post('/cart/addTocart?quantity=' + quantity, product);
}

/**
 * 删除购物车商品
 * @param {Array<number|string>} ids - 待删除的购物车记录 ID 列表
 * @returns {Promise<any>} 删除结果
 */
export function deleteCart(ids) {
	return request.post('/cart/deleteCart', ids);
}
