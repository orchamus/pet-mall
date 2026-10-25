/**
 * 购物车相关 API 模块
 * 提供购物车商品添加、列表查询、批量删除等接口。
 */
import request from "../request";

/**
 * 将商品加入购物车。
 *
 * @param {Object} product - 商品对象
 * @param {number} quantity - 加入数量
 * @returns {Promise<*>} 返回添加结果的响应数据
 */
export function addCart(product, quantity) {
    return request.post('/cart/addTocart?quantity=' + quantity,product)
}

/**
 * 获取当前登录用户的购物车列表。
 *
 * @returns {Promise<*>} 返回购物车列表
 */
export function getCartList() {
    return request.get('/cart/getCartList')
}

/**
 * 批量删除购物车商品项。
 *
 * @param {Array<string|number>} ids - 待删除的购物车项 ID 数组
 * @returns {Promise<*>} 返回删除结果的响应数据
 */
export function deleteCartItems(ids) {
    return request.post('/cart/deleteCart', ids)
}
