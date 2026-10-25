/**
 * 文本归一化与兜底工具模块
 * 用于处理商品、店铺、地址等字段的占位符判断与默认值填充
 */

/**
 * 判断文本是否为占位符（空值或仅由问号组成）
 * @param {*} value - 待判断的值
 * @returns {boolean} 是否为占位符
 */
export function isPlaceholder(value) {
	if (value === null || value === undefined || value === '') return true;
	const str = String(value).trim();
	return str === '' || /^[?？]+$/.test(str);
}

/**
 * 通用文本归一化：占位符返回兜底文案
 * @param {*} value - 待归一化的值
 * @param {string} [fallback='-'] - 占位符时的默认返回
 * @returns {string} 归一化后的文本
 */
export function normalizeText(value, fallback = '-') {
	return isPlaceholder(value) ? fallback : String(value).trim();
}

/**
 * 品种归一化：优先使用真实数据，缺失时尝试从商品名推断，否则返回兜底
 * @param {Object} product - 商品对象
 * @returns {string} 品种文本
 */
export function normalizeBreed(product) {
	if (!product) return '未知品种';
	const breed = product.pets?.breed;
	if (!isPlaceholder(breed)) return String(breed).trim();

	const name = String(product.name || '');
	const breedMap = {
		'金毛': '金毛寻回犬',
		'布偶': '布偶猫',
		'柯基': '柯基犬',
		'泰迪': '泰迪犬',
		'英短': '英国短毛猫',
		'柴犬': '柴犬',
		'蓝猫': '英国短毛猫',
		'橘猫': '中华田园猫',
		'波斯猫': '波斯猫',
		'暹罗': '暹罗猫',
		'缅因': '缅因猫',
		'美短': '美国短毛猫',
		'加菲': '加菲猫',
		'萨摩耶': '萨摩耶犬',
		'哈士奇': '哈士奇',
		'阿拉斯加': '阿拉斯加犬',
		'拉布拉多': '拉布拉多犬',
		'边牧': '边境牧羊犬',
		'博美': '博美犬',
		'比熊': '比熊犬',
		'银渐层': '英短银渐层',
		'金渐层': '英短金渐层',
		'龙猫': '龙猫',
		'仓鼠': '仓鼠',
		'兔子': '垂耳兔',
		'鹦鹉': '鹦鹉'
	};
	// 遍历品种关键词映射表，根据商品名称匹配对应品种
	for (const key in breedMap) {
		if (name.includes(key)) return breedMap[key];
	}
	return product.category === '0' || product.category === 0 ? '萌宠活体' : '未知品种';
}

/**
 * 健康状况兜底
 * @param {*} value - 健康状况原始值
 * @returns {string} 处理后的健康状况文本
 */
export function normalizeHealthStatus(value) {
	return normalizeText(value, '健康良好 · 已接种疫苗');
}

/**
 * 店铺名称兜底
 * @param {*} value - 店铺名称原始值
 * @returns {string} 处理后的店铺名称文本
 */
export function normalizeShopName(value) {
	return normalizeText(value, '萌宠乐园官方店');
}

/**
 * 店铺描述兜底
 * @param {*} value - 店铺描述原始值
 * @returns {string} 处理后的店铺描述文本
 */
export function normalizeShopDesc(value) {
	return normalizeText(value, '专业宠物用品与活体销售，品质保障，售后无忧。');
}

/**
 * 地址兜底
 * @param {*} value - 地址原始值
 * @returns {string} 处理后的地址文本
 */
export function normalizeAddress(value) {
	return normalizeText(value, '暂未填写地址');
}

/**
 * 用品属性兜底
 * @param {*} value - 用品属性原始值
 * @param {string} [fallback='暂无'] - 占位符时的默认返回
 * @returns {string} 处理后的用品属性文本
 */
export function normalizeSupplyValue(value, fallback = '暂无') {
	return normalizeText(value, fallback);
}
