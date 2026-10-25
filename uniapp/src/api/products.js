/**
 * 商品 API 模块
 * 提供商品列表、商品详情、搜索、当前用户商品及加入购物车等相关接口
 */
import request from './request.js';

/**
 * 分页获取全部商品列表
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @param {string} [category] - 商品分类
 * @returns {Promise<any>} 商品分页数据
 */
export function getAllProductList(pageNum, pageSize, category) {
	const params = { pageNum, pageSize };
	// 仅在传入分类时添加到查询参数，避免空值影响查询
	if (category) params.category = category;
	return request.get('/products/getAllProductList', params);
}

/**
 * 根据 ID 获取商品详情
 * @param {number|string} productId - 商品 ID
 * @returns {Promise<any>} 商品详情数据
 */
export function getAProductById(productId) {
	return request.get('/products/getAProductById', { productId });
}

/**
 * 根据关键字搜索商品
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @param {string} keyword - 搜索关键字
 * @returns {Promise<any>} 商品搜索分页数据
 */
export function getProductBySearch(pageNum, pageSize, keyword) {
	return request.get('/products/getProductBySearch', { pageNum, pageSize, keyword });
}

/**
 * 获取当前登录用户关联的商品列表
 * @returns {Promise<any>} 商品列表数据
 */
export function getProductsByToken() {
	return request.get('/products/getProductsByToken');
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

/**
 * 将商品加入购物车
 * @param {Object} product - 商品对象
 * @param {number} quantity - 购买数量
 * @returns {Promise<any>} 接口返回结果
 */
export function addCart(product, quantity) {
	return request.post('/cart/addTocart?quantity=' + quantity, product);
}
