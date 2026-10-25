/**
 * 商品管理相关 API 模块
 * 提供商品增删改查、媒体管理、商品评价等接口。
 */
import request from "../request";

/**
 * 临时上传商品相关文件。
 *
 * @param {File} file - 待上传的文件
 * @returns {Promise<*>} 返回上传后的文件信息
 */
export function uploadFileTemporary(file) {
    let data = new FormData();
    data.append('file', file);
    return request.post('/products/uploadFileTemporary',data)
}

/**
 * 新增商品。
 *
 * @param {Object} product - 商品数据对象
 * @returns {Promise<*>} 返回新增结果的响应数据
 */
export function addProduct(product) {
    return request.post('/products/addProduct',product)
}

/**
 * 获取当前登录用户（商家）的商品列表。
 *
 * @returns {Promise<*>} 返回商品列表
 */
export function getProductByToken() {
    return request.get('/products/getProductsByToken')
}

/**
 * 删除商品媒体资源。
 *
 * @param {Object} media - 商品媒体对象
 * @returns {Promise<*>} 返回删除结果的响应数据
 */
export function deletedMedia(media) {
    return request.post('/product-media/deletedMedia',media)
}

/**
 * 更新商品信息。
 *
 * @param {Object} product - 商品数据对象
 * @returns {Promise<*>} 返回更新结果的响应数据
 */
export function updateProduct(product) {
    return request.post('/products/updateProduct',product)
}

/**
 * 根据商品 ID 删除商品。
 *
 * @param {string|number} productId - 商品 ID
 * @returns {Promise<*>} 返回删除结果的响应数据
 */
export function deleteProduct(productId) {
    let data = new FormData();
    data.append('productId', productId);
    return request.post('/products/deleteProduct',data)
}

/**
 * 切换商品状态（如上下架）。
 *
 * @param {string|number} id - 商品 ID
 * @returns {Promise<*>} 返回状态切换后的响应数据
 */
export function changeProductStatus(id) {
    let data = new FormData();
    data.append('productId', id);
    return request.post('/products/changStatus',data)
}

/**
 * 分页查询全部商品列表，可按分类筛选。
 *
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @param {string} [category] - 商品分类，为空时查询全部分类
 * @returns {Promise<*>} 返回分页商品列表
 */
export function getAllProductList(pageNum,pageSize,category) {
    if(category === ''){
       return request.get('/products/getAllProductList?pageNum='+pageNum+'&pageSize='+pageSize)
    }else{
       return request.get('/products/getAllProductList?pageNum='+pageNum+'&pageSize='+pageSize+'&category='+category)
    }
}

/**
 * 根据商品 ID 查询商品详情。
 *
 * @param {string|number} productId - 商品 ID
 * @returns {Promise<*>} 返回商品详情
 */
export function getAProductById(productId) {
    return request.get('/products/getAProductById?productId='+productId)
}

/**
 * 根据关键词搜索商品。
 *
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @param {string} keyword - 搜索关键词
 * @returns {Promise<*>} 返回搜索结果
 */
export function getProducBykeyword(pageNum,pageSize,keyword) {
    return request.get('/products/getProductBySearch?pageNum='+pageNum+'&pageSize='+pageSize,{keyword:keyword})
}

/**
 * 为指定商品添加评价。
 *
 * @param {string|number} orderId - 订单 ID
 * @param {string} content - 评价内容
 * @param {string|number} productId - 商品 ID
 * @returns {Promise<*>} 返回评价结果的响应数据
 */
export function addReviews(orderId, content, productId) {
    let formData = new FormData();
    formData.append('orderId', orderId);
    formData.append('content', content);
    formData.append('productId', productId);
    return request.post('/product-reviews/add', formData)
}

/**
 * 查询指定订单是否已评价。
 *
 * @param {string|number} orderId - 订单 ID
 * @returns {Promise<*>} 返回评价状态
 */
export function hasReviewed(orderId) {
    return request.get('/product-reviews/has-reviewed?orderId=' + orderId)
}

/**
 * 分页查询指定商品的评价列表。
 *
 * @param {string|number} productId - 商品 ID
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @returns {Promise<*>} 返回评价列表
 */
export function getProductReviews(productId,pageNum,pageSize) {
    return request.get('/product-reviews/get-reviews?pageNum='+pageNum+'&pageSize='+pageSize +'&productId='+productId)
}

/**
 * 分页查询指定店铺及其商品列表。
 *
 * @param {number} pageNum - 当前页码
 * @param {number} pageSize - 每页条数
 * @param {string|number} shopId - 店铺 ID
 * @returns {Promise<*>} 返回店铺及商品数据
 */
export function getShopAndProducts(pageNum,pageSize,shopId) {
    return request.get('/shop/getShopAndProducts?pageNum='+pageNum+'&pageSize='+pageSize+'&shopId='+shopId)
}
