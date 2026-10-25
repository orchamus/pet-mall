/**
 * HTTP 请求封装模块
 * 基于 axios 创建统一请求实例，配置请求/响应拦截器，并封装 get/post/put/delete 方法供业务调用。
 */
import axios from 'axios';
import { ElMessage } from 'element-plus';


// 创建 axios 实例
const service = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL = 'http://localhost:8090/', // 设置 API base URL（同时赋值给环境变量）
    timeout: 5000, // 设置请求超时时间为 5000 毫秒
});

// 请求拦截器：在请求发送前统一注入 token
service.interceptors.request.use(
    config => {
        // 从 sessionStorage 获取用户 token
        const token = sessionStorage.getItem('token');
        if (token) {
            // 存在 token 时写入 satoken 请求头
            config.headers['satoken'] = token;
        }else {
            // 不存在 token 时设置为空字符串，避免后端校验异常
            config.headers['satoken'] = "";
        }
        return config;
    },
    error => {
        // 对请求错误做些什么
        console.error('请求错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器：统一处理返回状态码与错误提示
service.interceptors.response.use(
    response => {
        // 对响应数据做点什么
        const res = response.data;
        // 同时检查 code 与 statusCode，任一不为 200 均视为失败
        if (res.code !== 200 && res.statusCode !== '200') {
            // 如果状态码不是200，表示请求失败
            ElMessage.error(res.message || '请求失败');
            return Promise.reject(res.message || '请求失败');
        } else {
            // 如果状态码是200，表示请求成功
            return res;
        }
    },
    error => {
        // 对响应错误做点什么
        console.error('响应错误:', error);
        ElMessage.error(error.message || '请求失败');
        return Promise.reject(error);
    }
);

/**
 * 统一请求对象
 * 对 axios 的 get/post/put/delete 进行二次封装，保持与业务层调用风格一致。
 */
export default {
    /**
     * 发送 GET 请求。
     *
     * @param {string} url - 请求地址
     * @param {Object} [params] - URL 查询参数
     * @returns {Promise<*>} axios 响应 Promise
     */
    get(url, params,) {
        return service.get(url, { params });
    },
    /**
     * 发送 POST 请求。
     *
     * @param {string} url - 请求地址
     * @param {*} data - 请求体数据
     * @returns {Promise<*>} axios 响应 Promise
     */
    post(url, data) {
        return service.post(url, data);
    },
    /**
     * 发送 PUT 请求。
     *
     * @param {string} url - 请求地址
     * @param {*} data - 请求体数据
     * @returns {Promise<*>} axios 响应 Promise
     */
    put(url, data) {
        return service.put(url, data);
    },
    /**
     * 发送 DELETE 请求。
     *
     * @param {string} url - 请求地址
     * @param {Object} [params] - URL 查询参数
     * @returns {Promise<*>} axios 响应 Promise
     */
    delete(url, params) {
        return service.delete(url, { params });
    },
};
