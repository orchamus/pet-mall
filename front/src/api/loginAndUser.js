/**
 * 用户登录、注册及用户信息管理 API 模块
 * 提供注册、登录、邮箱验证码、用户信息查询与修改等接口。
 */
import request from "./request.js";

/**
 * 用户注册。
 *
 * @param {Object} registerData - 用户注册信息
 * @param {string} code - 邮箱验证码
 * @returns {Promise<*>} 返回注册结果的响应数据
 */
export function register(registerData,code) {
    let date = new FormData();
    date.append('user', JSON.stringify(registerData))
    date.append('code', code)
    return request.post('/user/register', date)
}

/**
 * 发送注册用邮箱验证码。
 *
 * @param {string} email - 用户邮箱
 * @returns {Promise<*>} 返回发送结果的响应数据
 */
export function senMsgByEmailForRegister(email) {
    let date = new FormData();
    date.append('email', email)
    return request.post('/user/sendEmailCodeForRegister', date)
}

/**
 * 发送登录用邮箱验证码。
 *
 * @param {string} email - 用户邮箱
 * @returns {Promise<*>} 返回发送结果的响应数据
 */
export function senMsgByEmailForLogin(email) {
    let date = new FormData();
    date.append('email', email)
    return request.post('/user/sendEmailCodeForLogin', date)
}

/**
 * 账号密码登录。
 *
 * @param {Object} loginData - 登录凭据（账号/密码）
 * @returns {Promise<*>} 返回登录结果的响应数据，通常包含 token
 */
export function loginAndUser(loginData) {
    return request.post('/user/login', loginData)
}

/**
 * 邮箱验证码登录。
 *
 * @param {string} email - 用户邮箱
 * @param {string} code - 邮箱验证码
 * @returns {Promise<*>} 返回登录结果的响应数据，通常包含 token
 */
export function loginByemail(email, code) {
    let date = new FormData();
    date.append('email', email)
    date.append('code', code)
    return request.post('/user/loginByEmail', date)
}

/**
 * 获取当前登录用户信息。
 *
 * @returns {Promise<*>} 返回用户详情
 */
export function getUserInfo() {
    return request.get('/user/getUserInfo');
}

/**
 * 用户退出登录。
 *
 * @returns {Promise<*>} 返回退出登录的响应数据
 */
export function userLogout() {
    return request.get('/user/logout');
}

/**
 * 更新当前登录用户信息。
 *
 * @param {Object} userInfo - 待更新的用户信息
 * @returns {Promise<*>} 返回更新结果的响应数据
 */
export function updateUserInfo(userInfo) {
    return request.post('/user/updateUserInfo', userInfo)
}

/**
 * 修改当前登录用户密码。
 *
 * @param {Object} passwordData - 密码数据
 * @param {string} passwordData.oldPassword - 原密码
 * @param {string} passwordData.newPassword - 新密码
 * @returns {Promise<*>} 返回修改结果的响应数据
 */
export function updateUserPassword(passwordData) {
    let date = new FormData();
    date.append('oldPassword', passwordData.oldPassword)
    date.append('newPassword', passwordData.newPassword)
    return request.post('/user/updateUserPassword', date)
}

/**
 * 更新当前登录用户头像。
 *
 * @param {File} file - 头像图片文件
 * @returns {Promise<*>} 返回上传结果的响应数据
 */
export function updateUserAvatar(file) {
    let date = new FormData();
    date.append('avatar', file)
    return request.post('/user/updateAvatar', date)
}

/**
 * 获取所有用户列表（管理员使用）。
 *
 * @returns {Promise<*>} 返回用户列表
 */
export function getAllUser() {
    return request.get("/user/getAllUser")
}
