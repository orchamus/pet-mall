package com.example.back.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.entity.ProductReviews;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品评论服务接口。
 *
 * <p>定义商品评价相关的核心业务操作，包括评价提交、重复评价校验以及分页查询商品评价。</p>
 */
public interface IProductReviewsService extends IService<ProductReviews> {

    /**
     * 提交商品评价。
     *
     * <p>校验用户登录状态、评价内容长度、订单状态及商品匹配性，校验通过后保存评价记录。</p>
     *
     * @param orderId   订单ID
     * @param productId 商品ID
     * @param token     用户登录 token
     * @param content   评价内容
     * @return 评价是否成功提交
     * @throws com.example.back.config.ExceptionConfig.BusinessException 当用户未登录、内容不符合要求、订单状态不允许评价或已评价时抛出
     */
      boolean addReviews(String orderId, Integer productId, String token, String content);

    /**
     * 判断指定订单是否已评价。
     *
     * @param orderId 订单ID
     * @return 已评价返回 true，否则返回 false
     */
      boolean hasReviewed(String orderId);

    /**
     * 分页查询指定商品的评价列表。
     *
     * @param productId 商品ID
     * @param pageNum   当前页码
     * @param pageSize  每页大小
     * @return 商品评价分页结果
     */
      Page<ProductReviews> getProductReviews(Integer productId, Integer pageNum, Integer pageSize);
}
