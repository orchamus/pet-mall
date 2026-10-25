/**
 * AI 对话工具模块
 * 封装与 DeepSeek 后端的对话请求
 */
import request from '@/api/request.js';

/**
 * 与 DeepSeek 进行对话
 * @param {Array<{role: string, content: string}>} messages - 对话消息列表
 * @returns {Promise<any>} AI 返回的对话内容
 */
export async function chatWithDeepSeek(messages) {
	const res = await request.post('/ai/chat', { messages });
	return res.data;
}
