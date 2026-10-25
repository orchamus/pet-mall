/**
 * 订单 API 模块
 * 提供订单列表查询、下单、取消、确认收货、退款申请及退款信息查询等接口
 */
import request from './request.js';

/**
 * 获取当前用户的订单列表
 * @param {number} [pageNum=1] - 当前页码
 * @param {number} [pageSize=10] - 每页条数
 * @returns {Promise<any>} 订单分页数据
 */
export function getOrderList(pageNum = 1, pageSize = 10) {
	return request.get('/orders/getOrderByToken', { pageNum, pageSize });
}

/**
 * 从购物车创建订单
 * @param {Array<Object>} cartItems - 选中的购物车商品项
 * @param {number|string} addressId - 收货地址 ID
 * @returns {Promise<any>} 创建结果
 */
export function createOrderByCart(cartItems, addressId) {
	return request.post('/orders/createOrderByCart?addressId=' + addressId, cartItems);
}

/**
 * 立即购买创建订单
 * @param {number|string} productId - 商品 ID
 * @param {number} quantity - 购买数量
 * @param {number|string} addressId - 收货地址 ID
 * @returns {Promise<any>} 创建结果
 */
export function createOrderByNow(productId, quantity, addressId) {
	return request.post('/orders/createOrderBynow?productId=' + productId + '&quantity=' + quantity + '&addressId=' + addressId);
}

/**
 * 取消订单
 * @param {number|string} orderId - 订单 ID
 * @returns {Promise<any>} 取消结果
 */
export function cancelOrder(orderId) {
	return request.post('/orders/cancelOrder?orderId=' + orderId);
}

/**
 * 确认收货，将订单状态推进到下一步
 * @param {number|string} orderId - 订单 ID
 * @returns {Promise<any>} 操作结果
 */
export function confirmReceipt(orderId) {
	return request.post('/orders/changeOrderStatusToNext?orderId=' + orderId);
}

/**
 * 申请订单退款
 * @param {number|string} orderId - 订单 ID
 * @param {string} reason - 退款原因
 * @returns {Promise<any>} 申请结果
 */
export function requestRefund(orderId, reason) {
	return request.post('/orders/requestRefund?orderId=' + orderId + '&reason=' + encodeURIComponent(reason));
}

/**
 * 查询订单退款信息
 * @param {number|string} orderId - 订单 ID
 * @returns {Promise<any>} 退款详情数据
 */
export function getOrderRefund(orderId) {
	return request.get('/order-refunds/getOrderRefundByOrderId', { orderId });
}
