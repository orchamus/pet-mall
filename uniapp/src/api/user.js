/**
 * 用户相关 API 模块
 * 提供登录、注册、邮箱验证码、用户信息管理及密码修改等接口
 */
import request from './request.js';

/**
 * 账号密码登录
 * @param {Object} loginData - 登录参数，如账号和密码
 * @returns {Promise<any>} 登录结果，通常包含 token
 */
export function login(loginData) {
	return request.post('/user/login', loginData);
}

/**
 * 用户注册
 * @param {Object} registerData - 注册用户信息
 * @param {string} code - 邮箱验证码
 * @returns {Promise<any>} 注册结果
 */
export function register(registerData, code) {
	const formData = new FormData();
	formData.append('user', JSON.stringify(registerData));
	formData.append('code', code);
	return request.post('/user/register', formData, { 'Content-Type': 'multipart/form-data' });
}

/**
 * 发送注册用邮箱验证码
 * @param {string} email - 邮箱地址
 * @returns {Promise<any>} 发送结果
 */
export function sendEmailCodeForRegister(email) {
	const formData = new FormData();
	formData.append('email', email);
	return request.post('/user/sendEmailCodeForRegister', formData, { 'Content-Type': 'multipart/form-data' });
}

/**
 * 发送登录用邮箱验证码
 * @param {string} email - 邮箱地址
 * @returns {Promise<any>} 发送结果
 */
export function sendEmailCodeForLogin(email) {
	const formData = new FormData();
	formData.append('email', email);
	return request.post('/user/sendEmailCodeForLogin', formData, { 'Content-Type': 'multipart/form-data' });
}

/**
 * 邮箱验证码登录
 * @param {string} email - 邮箱地址
 * @param {string} code - 邮箱验证码
 * @returns {Promise<any>} 登录结果
 */
export function loginByEmail(email, code) {
	const formData = new FormData();
	formData.append('email', email);
	formData.append('code', code);
	return request.post('/user/loginByEmail', formData, { 'Content-Type': 'multipart/form-data' });
}

/**
 * 获取当前登录用户信息
 * @returns {Promise<any>} 用户信息数据
 */
export function getUserInfo() {
	return request.get('/user/getUserInfo');
}

/**
 * 用户登出
 * @returns {Promise<any>} 登出结果
 */
export function logout() {
	return request.get('/user/logout');
}

/**
 * 更新用户信息
 * @param {Object} userInfo - 待更新的用户信息
 * @returns {Promise<any>} 更新结果
 */
export function updateUserInfo(userInfo) {
	return request.post('/user/updateUserInfo', userInfo);
}

/**
 * 修改用户密码
 * @param {string} oldPassword - 原密码
 * @param {string} newPassword - 新密码
 * @returns {Promise<any>} 修改结果
 */
export function updateUserPassword(oldPassword, newPassword) {
	const formData = new FormData();
	formData.append('oldPassword', oldPassword);
	formData.append('newPassword', newPassword);
	return request.post('/user/updateUserPassword', formData, { 'Content-Type': 'multipart/form-data' });
}

/**
 * 分页查询全部用户（管理后台场景）
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @param {string} userName - 用户名关键字
 * @returns {Promise<any>} 用户分页数据
 */
export function getAllUser(pageNum, pageSize, userName) {
	return request.get('/user/getAllUser', { pageNum, pageSize, userName });
}
