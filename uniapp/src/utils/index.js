/**
 * 通用工具函数模块
 * 提供价格格式化、防抖节流、页面导航、轻提示及日期图片处理等常用能力
 */

/**
 * 将价格格式化为保留两位小数的字符串
 * @param {number|string} price - 原始价格
 * @returns {string} 格式化后的价格文本，无效值返回 '0.00'
 */
export function formatPrice(price) {
	const num = Number(price);
	return isNaN(num) ? '0.00' : num.toFixed(2);
}

/**
 * 防抖函数
 * 在指定延迟内多次触发只执行最后一次，适用于输入框搜索、按钮高频点击等场景
 * @param {Function} fn - 待执行的函数
 * @param {number} [delay=300] - 延迟时间，单位毫秒
 * @returns {Function} 防抖包装后的函数
 */
export function debounce(fn, delay = 300) {
	let timer = null;
	return function (...args) {
		// 清除上一次的定时器，重新计时
		if (timer) clearTimeout(timer);
		timer = setTimeout(() => {
			fn.apply(this, args);
		}, delay);
	};
}

/**
 * 节流函数
 * 在指定时间间隔内最多执行一次，适用于滚动加载、窗口 resize 等高频事件
 * @param {Function} fn - 待执行的函数
 * @param {number} [interval=300] - 执行间隔，单位毫秒
 * @returns {Function} 节流包装后的函数
 */
export function throttle(fn, interval = 300) {
	let last = 0;
	return function (...args) {
		const now = Date.now();
		// 超过时间间隔才允许执行
		if (now - last >= interval) {
			last = now;
			fn.apply(this, args);
		}
	};
}

/**
 * 保留当前页面并跳转到指定页面
 * @param {string} url - 目标页面路径
 */
export function navigateTo(url) {
	uni.navigateTo({ url });
}

/**
 * 关闭当前页面并跳转到指定页面
 * @param {string} url - 目标页面路径
 */
export function redirectTo(url) {
	uni.redirectTo({ url });
}

/**
 * 跳转到 tabBar 页面
 * @param {string} url - tabBar 页面路径
 */
export function switchTab(url) {
	uni.switchTab({ url });
}

/**
 * 关闭当前页面，返回上一页面或多级页面
 * @param {number} [delta=1] - 返回的页面层数
 */
export function navigateBack(delta = 1) {
	uni.navigateBack({ delta });
}

/**
 * 显示轻提示
 * @param {string} title - 提示文本
 * @param {string} [icon='none'] - 提示图标
 */
export function showToast(title, icon = 'none') {
	uni.showToast({ title, icon });
}

/**
 * 将日期字符串格式化为 yyyy-MM-dd HH:mm
 * @param {string} dateStr - 原始日期字符串
 * @returns {string} 格式化后的日期文本
 */
export function formatDate(dateStr) {
	if (!dateStr) return '';
	const date = new Date(dateStr);
	if (isNaN(date.getTime())) return dateStr;
	const y = date.getFullYear();
	const m = String(date.getMonth() + 1).padStart(2, '0');
	const d = String(date.getDate()).padStart(2, '0');
	const h = String(date.getHours()).padStart(2, '0');
	const min = String(date.getMinutes()).padStart(2, '0');
	return `${y}-${m}-${d} ${h}:${min}`;
}

/**
 * 将相对图片地址补全为绝对地址
 * @param {string} url - 原始图片地址
 * @returns {string} 补全后的图片地址
 */
export function getImageUrl(url) {
	if (!url) return '';
	if (url.startsWith('http')) return url;
	const base = 'http://localhost:8090';
	return url.startsWith('/') ? base + url : base + '/' + url;
}

/**
 * 获取商品主图地址
 * 优先使用商品媒体列表中的第一张图片，缺失时返回默认 logo
 * @param {Object} product - 商品对象
 * @returns {string} 商品图片地址
 */
export function getProductImage(product) {
	if (!product || !Array.isArray(product.productMedias) || !product.productMedias.length) {
		return '/static/logo.png';
	}
	const first = product.productMedias[0];
	const mediaUrl = first && (first.media?.url || first.mediaUrl);
	return getImageUrl(mediaUrl) || '/static/logo.png';
}
