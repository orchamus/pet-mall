/**
 * 店铺位置 API 模块
 * 提供店铺地理位置信息查询接口
 */
import request from './request.js';

/**
 * 获取店铺位置列表
 * @returns {Promise<any>} 店铺位置数据
 */
export function getShopLocations() {
	return request.get('/shopLocation/getAddress');
}
