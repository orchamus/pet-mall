/**
 * 全局请求封装模块
 * 基于 uni.request 实现统一的 API 请求、请求头组装、错误提示及 HTTP 方法快捷调用
 */

/** 后端服务基础地址 */
const BASE_URL = 'http://localhost:8090';
/** 本地存储中 token 的键名 */
const TOKEN_KEY = 'satoken';

/**
 * 发起 HTTP 请求
 * @param {Object} options - 请求配置对象
 * @param {string} options.url - 请求地址
 * @param {string} [options.method='GET'] - 请求方法
 * @param {Object} [options.data={}] - 请求数据
 * @param {Object} [options.header={}] - 自定义请求头
 * @returns {Promise<any>} 请求成功时 resolve 接口响应数据，失败时 reject 错误信息
 */
const request = (options) => {
	return new Promise((resolve, reject) => {
		// 从本地缓存读取 satoken，用于用户鉴权
		const token = uni.getStorageSync(TOKEN_KEY) || '';
		// 统一 URL 以 / 开头，方便与 BASE_URL 拼接
		const url = options.url.startsWith('/') ? options.url : '/' + options.url;
		let data = options.data || {};
		const header = {
			'Content-Type': 'application/json',
			'satoken': token,
			...(options.header || {})
		};
		// H5 下 FormData 转换为 application/x-www-form-urlencoded，兼容后端 @RequestParam 接收
		if (typeof window !== 'undefined' && data instanceof FormData && typeof URLSearchParams !== 'undefined') {
			const params = new URLSearchParams();
			data.forEach((value, key) => params.append(key, value));
			data = params.toString();
			header['Content-Type'] = 'application/x-www-form-urlencoded';
		}
		uni.request({
			url: BASE_URL + url,
			method: options.method || 'GET',
			data,
			header,
			success: (res) => {
				const data = res.data;
				const code = data && (data.code || data.statusCode);
				// 根据后端返回状态码判断请求是否成功
				if (code === 200 || code === '200') {
					resolve(data);
				} else {
					const msg = data && (data.message || data.msg) ? (data.message || data.msg) : '请求失败';
					uni.showToast({
						title: msg,
						icon: 'none'
					});
					reject(msg);
				}
			},
			fail: (err) => {
				uni.showToast({
					title: '网络异常',
					icon: 'none'
				});
				reject(err);
			}
		});
	});
};

/**
 * 发起 GET 请求
 * @param {string} url - 请求地址
 * @param {Object} [params={}] - URL 查询参数
 * @param {Object} [header={}] - 自定义请求头
 * @returns {Promise<any>} 接口响应数据
 */
request.get = (url, params = {}, header = {}) => {
	return request({ url, method: 'GET', data: params, header });
};

/**
 * 发起 POST 请求
 * @param {string} url - 请求地址
 * @param {Object} [data={}] - 请求体数据
 * @param {Object} [header={}] - 自定义请求头
 * @returns {Promise<any>} 接口响应数据
 */
request.post = (url, data = {}, header = {}) => {
	return request({ url, method: 'POST', data, header });
};

/**
 * 发起 PUT 请求
 * @param {string} url - 请求地址
 * @param {Object} [data={}] - 请求体数据
 * @param {Object} [header={}] - 自定义请求头
 * @returns {Promise<any>} 接口响应数据
 */
request.put = (url, data = {}, header = {}) => {
	return request({ url, method: 'PUT', data, header });
};

/**
 * 发起 DELETE 请求
 * @param {string} url - 请求地址
 * @param {Object} [params={}] - URL 查询参数
 * @param {Object} [header={}] - 自定义请求头
 * @returns {Promise<any>} 接口响应数据
 */
request.delete = (url, params = {}, header = {}) => {
	return request({ url, method: 'DELETE', data: params, header });
};

export default request;
