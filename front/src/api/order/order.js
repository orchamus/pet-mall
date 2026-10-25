/**
 * 订单相关 API 模块
 * 提供订单创建、查询、状态流转、退款等业务接口。
 */
import request from "../request";

/**
 * 立即购买：根据商品 ID、数量、地址 ID 创建订单。
 *
 * @param {string|number} productId - 商品 ID
 * @param {string|number} quantity - 购买数量
 * @param {string|number} addresId - 收货地址 ID（参数名为 addresId，与接口保持一致）
 * @returns {Promise<*>} 返回创建订单的响应数据
 */
export function createOrderBynow(productId, quantity,addresId) {
    let formData = new FormData();
    formData.append("productId", productId);
    formData.append("quantity", quantity);
    formData.append("addressId", addresId);
    return request.post("/orders/createOrderBynow", formData);
}

/**
 * 分页查询当前登录用户的订单列表。
 *
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @returns {Promise<*>} 返回分页订单列表
 */
export function getOrderByToken(pageNum, pageSize) {
    return request.get("/orders/getOrderByToken?pageNum=" + pageNum + "&pageSize=" + pageSize);
}

/**
 * 查询当前登录用户的全部订单（不分页）。
 *
 * @returns {Promise<*>} 返回全部订单列表
 */
export function getAllOrdersByToken() {
    return request.get("/orders/getAllOrdersByToken");
}

/**
 * 从购物车结算：根据购物车项与地址 ID 创建订单。
 *
 * @param {Array<Object>} cartItems - 购物车商品项列表
 * @param {string|number} addressId - 收货地址 ID
 * @returns {Promise<*>} 返回创建订单的响应数据
 */
export function createOrderBycart(cartItems,addressId) {
    return request.post("/orders/createOrderByCart?addressId=" + addressId,cartItems);
}

/**
 * 分页查询当前登录用户可用于订单管理的订单列表。
 *
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @returns {Promise<*>} 返回分页订单列表
 */
export function getOrdersByTokenForOrderManagement(pageNum, pageSize) {
    return request.get("/orders/getOrdersByTokenForOrderManagement?pageNum=" + pageNum + "&pageSize=" + pageSize);
}

/**
 * 商家端订单搜索。
 *
 * @param {Object} query - 搜索条件
 * @param {string} [query.productName] - 商品名称
 * @param {string} [query.status] - 订单状态
 * @param {string} [query.dateRange] - 下单日期范围
 * @param {string} [query.orderId] - 订单 ID
 * @param {number} query.pageNum - 当前页码
 * @param {number} query.pageSize - 每页条数
 * @returns {Promise<*>} 返回搜索结果的响应数据
 */
export function searchOrdersForOrder(query) {
    let formData = new FormData();
    formData.append("productName", query.productName);
    formData.append("orderStatus", query.status);
    formData.append("orderDate", query.dateRange);
    formData.append("orderId", query.orderId);
    return request.post("/orders/searchOrdersForOrder?pageNum=" + query.pageNum + "&pageSize=" + query.pageSize, formData);
}

/**
 * 用户端订单搜索。
 *
 * @param {Object} query - 搜索条件
 * @param {string} [query.productName] - 商品名称
 * @param {string} [query.status] - 订单状态
 * @param {string} [query.dateRange] - 下单日期范围
 * @param {string} [query.orderId] - 订单 ID
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @returns {Promise<*>} 返回搜索结果的响应数据
 */
export function searchOrdersForOrderCustomer(query,pageNum, pageSize) {
    let formData = new FormData();
    formData.append("productName", query.productName);
    formData.append("orderStatus", query.status);
    formData.append("orderDate", query.dateRange);
    formData.append("orderId", query.orderId);
    return request.post("/orders/searchOrdersForOrder?pageNum=" + pageNum + "&pageSize=" + pageSize, formData);
}

/**
 * 将订单状态推进到下一节点。
 *
 * @param {string|number} orderId - 订单 ID
 * @returns {Promise<*>} 返回状态更新后的响应数据
 */
export function changOrderStatusToNext(orderId) {
    return request.post("/orders/changeOrderStatusToNext?orderId=" + orderId);
}

/**
 * 申请订单退款。
 *
 * @param {string|number} orderId - 订单 ID
 * @param {string} reason - 退款原因
 * @returns {Promise<*>} 返回退款申请的响应数据
 */
export function requestRefund(orderId, reason) {
    let formData = new FormData();
    formData.append("reason", reason);
    formData.append("orderId", orderId);
    return request.post("/orders/requestRefund", formData);
}

/**
 * 取消订单。
 *
 * @param {string|number} orderId - 订单 ID
 * @returns {Promise<*>} 返回取消订单的响应数据
 */
export function cancelOrder(orderId) {
    return request.post("/orders/cancelOrder?orderId=" + orderId);
}

/**
 * 根据订单 ID 查询退款详情。
 *
 * @param {string|number} orderId - 订单 ID
 * @returns {Promise<*>} 返回退款详情
 */
export function getOrderRefundByOrderId(orderId) {
    return request.get("/order-refunds/getOrderRefundByOrderId?orderId=" + orderId);
}

/**
 * 审核订单退款。
 *
 * @param {string|number} OrderRefundId - 退款记录 ID
 * @param {string|number} orderId - 订单 ID
 * @param {string|number} isAgree - 是否同意退款（通常传 0/1 或布尔字符串）
 * @returns {Promise<*>} 返回审核结果的响应数据
 */
export function reviewRefund(OrderRefundId,orderId,isAgree) {
    let formData = new FormData();
    formData.append("orderId", orderId);
    formData.append("OrderRefundId", OrderRefundId);
    formData.append("isAgree", isAgree);
    return request.post("/orders/reviewRefund", formData);
}
