/**
 * 支付会话与支付模拟工具模块
 * 用于在本地缓存中保存支付会话、地址缓存，并提供支付模拟及倒计时格式化能力
 */

/** 本地缓存中支付会话的键名 */
const PAYMENT_SESSION_KEY = 'pet_payment_session';
/** 本地缓存中地址缓存的键名 */
const ADDRESS_CACHE_KEY = 'pet_address_cache';

/**
 * 保存支付会话到本地缓存
 * @param {Object} data - 支付会话数据
 * @returns {boolean} 保存是否成功
 */
export function savePaymentSession(data) {
  try {
    const payload = {
      ...data,
      // 记录会话创建时间，便于后续校验有效期
      createdAt: Date.now()
    };
    uni.setStorageSync(PAYMENT_SESSION_KEY, payload);
    return true;
  } catch (e) {
    console.error('savePaymentSession failed', e);
    return false;
  }
}

/**
 * 从本地缓存读取支付会话
 * @returns {Object|null} 支付会话数据，不存在或读取失败时返回 null
 */
export function getPaymentSession() {
  try {
    return uni.getStorageSync(PAYMENT_SESSION_KEY) || null;
  } catch (e) {
    console.error('getPaymentSession failed', e);
    return null;
  }
}

/**
 * 清除本地缓存中的支付会话
 * @returns {boolean} 清除是否成功
 */
export function clearPaymentSession() {
  try {
    uni.removeStorageSync(PAYMENT_SESSION_KEY);
    return true;
  } catch (e) {
    console.error('clearPaymentSession failed', e);
    return false;
  }
}

/**
 * 保存地址缓存到本地缓存
 * @param {Array<Object>} list - 地址列表
 * @returns {boolean} 保存是否成功
 */
export function saveAddressCache(list) {
  try {
    uni.setStorageSync(ADDRESS_CACHE_KEY, {
      list,
      // 记录缓存更新时间，便于判断是否需要刷新
      updatedAt: Date.now()
    });
    return true;
  } catch (e) {
    console.error('saveAddressCache failed', e);
    return false;
  }
}

/**
 * 从本地缓存读取地址缓存
 * @returns {Object|null} 地址缓存数据，不存在或读取失败时返回 null
 */
export function getAddressCache() {
  try {
    return uni.getStorageSync(ADDRESS_CACHE_KEY) || null;
  } catch (e) {
    console.error('getAddressCache failed', e);
    return null;
  }
}

/**
 * 清除本地缓存中的地址缓存
 * @returns {boolean} 清除是否成功
 */
export function clearAddressCache() {
  try {
    uni.removeStorageSync(ADDRESS_CACHE_KEY);
    return true;
  } catch (e) {
    console.error('clearAddressCache failed', e);
    return false;
  }
}

/**
 * 更新支付会话的状态字段
 * @param {string} status - 新的支付状态
 * @returns {boolean} 更新是否成功
 */
export function updatePaymentSessionStatus(status) {
  try {
    const session = getPaymentSession();
    // 没有支付会话时无需更新
    if (!session) return false;
    uni.setStorageSync(PAYMENT_SESSION_KEY, {
      ...session,
      status,
      updatedAt: Date.now()
    });
    return true;
  } catch (e) {
    console.error('updatePaymentSessionStatus failed', e);
    return false;
  }
}

/**
 * 获取当前支付会话的状态
 * @returns {string} 支付状态，无会话时默认返回 'pending'
 */
export function getPaymentSessionStatus() {
  try {
    const session = getPaymentSession();
    return session?.status || 'pending';
  } catch (e) {
    console.error('getPaymentSessionStatus failed', e);
    return 'pending';
  }
}

/**
 * 模拟支付流程
 * 通过随机结果模拟真实支付的耗时与成功/失败状态，常用于本地调试
 * @returns {Promise<{ success: boolean, message: string }>} 支付模拟结果
 */
export function simulatePayment() {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 95% 概率模拟支付成功
      const success = Math.random() > 0.05;
      resolve({ success, message: success ? '支付成功' : '支付失败，请重试' });
    }, 1500);
  });
}

/**
 * 将秒数格式化为 mm:ss 倒计时文本
 * @param {number} seconds - 剩余秒数
 * @returns {string} 格式化后的倒计时字符串
 */
export function formatCountdown(seconds) {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
}
