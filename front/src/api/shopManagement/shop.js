/**
 * 店铺管理相关 API 模块
 * 提供店铺信息查询、更新、新增、Logo 上传以及全量店铺查询等接口。
 */
import request from "../request";

/**
 * 获取当前登录用户的店铺信息。
 *
 * @returns {Promise<*>} 返回店铺详情
 */
export function getShop() {
    return request.get("/shop/getShop");
}

/**
 * 更新店铺信息。
 *
 * @param {Object} data - 店铺数据对象
 * @returns {Promise<*>} 返回更新结果的响应数据
 */
export function updateShop(data) {
    return request.post("/shop/updateShop", data);
}

/**
 * 新增店铺。
 *
 * @param {Object} data - 店铺数据对象
 * @returns {Promise<*>} 返回新增结果的响应数据
 */
export function addShop(data) {
    return request.post("/shop/addShop", data);
}

/**
 * 上传店铺 Logo。
 *
 * @param {File} file - Logo 图片文件
 * @returns {Promise<*>} 返回上传结果的响应数据
 */
export function uploadShopLogo(file) {
    let form = new FormData();
    form.append("logo", file);
    return request.post("/shop/uploadShopLogo", form);
}

/**
 * 获取全部店铺列表。
 *
 * @returns {Promise<*>} 返回店铺列表
 */
export function getAllShop() {
    return request.get("/shop/getAllShop");
}
