/**
 * 商品评价 API 模块
 * 提供评价提交、评价列表查询及评价状态校验相关接口
 */
import request from './request.js';

/**
 * 添加商品评价
 * @param {number|string} orderId - 订单 ID
 * @param {number|string} productId - 商品 ID
 * @param {string} content - 评价内容
 * @returns {Promise<any>} 接口返回结果
 */
export function addReview(orderId, productId, content) {
	const encodedContent = encodeURIComponent(content);
	return request.post('/product-reviews/add?orderId=' + orderId + '&productId=' + productId + '&content=' + encodedContent);
}

/**
 * 查询商品评价列表
 * @param {number|string} productId - 商品 ID
 * @param {number} [pageNum=1] - 当前页码
 * @param {number} [pageSize=10] - 每页条数
 * @returns {Promise<any>} 接口返回的评价分页数据
 */
export function getProductReviews(productId, pageNum = 1, pageSize = 10) {
	return request.get('/product-reviews/get-reviews', { productId, pageNum, pageSize });
}

/**
 * 判断订单是否已评价
 * @param {number|string} orderId - 订单 ID
 * @returns {Promise<any>} 接口返回的评价状态结果
 */
export function hasReviewed(orderId) {
	return request.get('/product-reviews/has-reviewed', { orderId });
}
