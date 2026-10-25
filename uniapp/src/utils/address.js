/**
 * 地址数据转换工具模块
 * 负责后端地址对象与前端表单对象之间的双向转换，以及地址文本拼接
 */

/**
 * 将后端地址对象归一化为前端地址表单结构
 * @param {Object} addr - 后端地址对象
 * @returns {Object|null} 前端地址对象，输入为空时返回 null
 */
export function normalizeAddress(addr) {
  if (!addr) return null;
  const isDefault = addr.isDefault === 1 || addr.isDefault === '1';
  return {
    addressId: addr.addressId,
    receiverName: addr.consignee || '',
    phone: addr.phone || addr.email || '',
    province: addr.province || '',
    city: addr.city || '',
    district: addr.district || '',
    region: addr.region || '',
    detailAddress: addr.detail || '',
    postalCode: addr.postalCode || '',
    isDefault,
    isDefaultValue: isDefault ? '1' : '0',
    tag: addr.tag || ''
  };
}

/**
 * 批量归一化地址列表
 * @param {Array<Object>} list - 后端地址对象数组
 * @returns {Array<Object>} 前端地址对象数组
 */
export function normalizeAddressList(list) {
  return (list || []).map(normalizeAddress).filter(Boolean);
}

/**
 * 将前端地址表单转换为后端地址对象
 * @param {Object} form - 前端地址表单对象
 * @returns {Object} 后端地址对象
 */
export function denormalizeAddress(form) {
  const province = (form.province || '').trim();
  const city = (form.city || '').trim();
  const district = (form.district || '').trim();
  // 优先使用省市区拼接成 region；若省市区为空，则回退到已有的 region 字段
  const regionParts = [province, city, district].filter(Boolean);
  const region = regionParts.length ? regionParts.join('') : (form.region || '').trim();

  return {
    addressId: form.addressId || undefined,
    consignee: (form.receiverName || '').trim(),
    email: (form.phone || '').trim(),
    phone: (form.phone || '').trim(),
    province,
    city,
    district,
    region,
    detail: (form.detailAddress || '').trim(),
    postalCode: (form.postalCode || '').trim(),
    isDefault: form.isDefault ? 1 : 0,
    tag: (form.tag || '').trim()
  };
}

/**
 * 拼接完整地址文本
 * @param {Object} addr - 地址对象
 * @returns {string} 完整地址字符串
 */
export function formatFullAddress(addr) {
  if (!addr) return '';
  const region = addr.province || addr.city || addr.district
    ? `${addr.province || ''}${addr.city || ''}${addr.district || ''}`
    : (addr.region || '');
  return `${region}${addr.detailAddress || addr.detail || ''}`;
}

/**
 * 拼接省市区区域文本
 * @param {Object} addr - 地址对象
 * @returns {string} 区域文本
 */
export function formatRegion(addr) {
  if (!addr) return '';
  return addr.province || addr.city || addr.district
    ? `${addr.province || ''}${addr.city || ''}${addr.district || ''}`
    : (addr.region || '');
}
