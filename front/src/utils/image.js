/**
 * 图片处理工具模块
 * 负责根据商品对象解析并拼接商品主图 URL，提供默认图片兜底。
 */

// 从环境变量读取 API 基础地址，用于拼接本地图片相对路径
const baseUrl = import.meta.env.VITE_API_BASE_URL;

/**
 * 获取商品主图 URL。
 * 优先使用商品 media 列表中的第一张图片；若不存在或无效则返回默认图。
 *
 * @param {Object} product - 商品对象
 * @param {Array<Object>} [product.productMedias] - 商品媒体列表
 * @param {Object} [product.productMedias[].media] - 媒体对象
 * @param {string} [product.productMedias[].media.url] - 媒体 URL
 * @param {string} [product.productMedias[].mediaUrl] - 备用媒体 URL
 * @returns {string} 商品主图完整 URL
 */
export function getProductImage(product) {
  if (!product || !Array.isArray(product.productMedias) || !product.productMedias.length) {
    return baseUrl + '/media/productMedia/default.jpg';
  }
  const first = product.productMedias[0];
  const mediaUrl = first && (first.media?.url || first.mediaUrl);
  if (!mediaUrl) return baseUrl + '/media/productMedia/default.jpg';
  return mediaUrl.startsWith('http') ? mediaUrl : baseUrl + (mediaUrl.startsWith('/') ? mediaUrl : '/' + mediaUrl);
}
