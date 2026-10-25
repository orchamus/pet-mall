/**
 * 宠物视频 API 模块
 * 提供视频列表及视频详情查询接口
 */
import request from './request.js';

/**
 * 获取宠物视频列表
 * @returns {Promise<any>} 视频列表数据
 */
export function getVideoList() {
	return request.get('/petVideo/list');
}

/**
 * 获取视频详情
 * @param {number|string} videoId - 视频 ID
 * @returns {Promise<any>} 视频详情数据
 */
export function getVideoDetail(videoId) {
	return request.get('/petVideo/detail', { videoId });
}
