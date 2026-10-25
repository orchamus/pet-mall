/**
 * 省市区三级联动工具模块
 * 基于 element-china-area-data 构建省市区数据，提供索引转换、文本解析及联动范围更新能力
 */
import { regionData } from 'element-china-area-data';

// 从原始数据中提取省级名称列表
const provinces = regionData.map((item) => item.label);

// 从原始数据中提取每个省对应的市级名称列表
const cities = regionData.map((item) =>
  (item.children || []).map((child) => child.label)
);

// 从原始数据中提取每个省-市对应的区级名称列表
const districts = regionData.map((item) =>
  (item.children || []).map((child) =>
    (child.children || []).map((c) => c.label)
  )
);

/**
 * 获取默认的三级联动数据范围
 * 默认返回第一个省、该市下第一个市、该区下第一个区
 * @returns {Array<string[]>} 包含省、市、区名称数组的数组
 */
export function getRegionRange() {
  return [provinces, cities[0] || [], districts[0][0] || []];
}

/**
 * 根据索引转换为省市区文本
 * @param {number[]} indexes - 三级索引数组 [provinceIndex, cityIndex, districtIndex]
 * @returns {{ province: string, city: string, district: string }} 省市区文本对象
 */
export function getRegionText(indexes) {
  const [pIndex, cIndex, dIndex] = indexes;
  const province = provinces[pIndex];
  const cityList = cities[pIndex] || [];
  const city = cityList[cIndex];
  const districtList = (districts[pIndex] || [])[cIndex] || [];
  const district = districtList[dIndex];
  return { province: province || '', city: city || '', district: district || '' };
}

/**
 * 根据选中的省、市索引更新三级联动范围
 * @param {number} pIndex - 省级索引
 * @param {number} cIndex - 市级索引
 * @returns {Array<string[]>} 更新后的省、市、区名称数组
 */
export function updateRegionRange(pIndex, cIndex) {
  return [
    provinces,
    cities[pIndex] || [],
    ((districts[pIndex] || [])[cIndex] || [])
  ];
}

/**
 * 根据省市区文本反查三级索引
 * 若找不到对应项，则回退到默认索引 0
 * @param {string} province - 省级名称
 * @param {string} city - 市级名称
 * @param {string} district - 区级名称
 * @returns {number[]} 三级索引数组 [provinceIndex, cityIndex, districtIndex]
 */
export function findRegionIndexes(province, city, district) {
  const pIndex = provinces.findIndex((p) => p === province);
  if (pIndex === -1) return [0, 0, 0];
  const cIndex = (cities[pIndex] || []).findIndex((c) => c === city);
  if (cIndex === -1) return [pIndex, 0, 0];
  const dIndex = ((districts[pIndex] || [])[cIndex] || []).findIndex((d) => d === district);
  return [pIndex, cIndex, dIndex === -1 ? 0 : dIndex];
}
