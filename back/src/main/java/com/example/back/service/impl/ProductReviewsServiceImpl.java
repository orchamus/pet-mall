package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.config.ExceptionConfig.BusinessException;
import com.example.back.entity.Orders;
import com.example.back.entity.ProductReviews;
import com.example.back.entity.User;
import com.example.back.mapper.ProductReviewsMapper;
import com.example.back.service.IOrdersService;
import com.example.back.service.IProductReviewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.back.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 商品评论服务实现类。
 *
 * <p>实现 {@link IProductReviewsService} 接口，提供订单评价提交、重复评价校验、商品评价分页查询等功能。
 * 核心规则包括：仅允许普通用户（userType=0）对待评价状态（status=7）的订单提交一次评价，评价成功后订单状态流转为已完成（status=3）。</p>
 */
@Service
public class ProductReviewsServiceImpl extends ServiceImpl<ProductReviewsMapper, ProductReviews> implements IProductReviewsService {
    @Resource
    private IUserService userService;
    @Resource
    private IOrdersService ordersService;

    /**
     * 提交商品评价。
     *
     * <p>业务规则：</p>
     * <ul>
     *   <li>用户必须登录且为普通用户；</li>
     *   <li>评价内容长度需在 10-500 字符之间，并过滤 HTML 标签；</li>
     *   <li>订单必须存在、属于当前用户、处于待评价状态（status=7）且商品匹配；</li>
     *   <li>同一订单只能评价一次，评价成功后订单状态置为已完成（status=3）。</li>
     * </ul>
     *
     * @param orderId   订单ID
     * @param productId 商品ID
     * @param token     用户登录 token
     * @param content   评价内容
     * @return 评价是否成功提交
     * @throws BusinessException 当用户未登录、评价内容不符合要求、订单状态不允许评价或已评价时抛出
     */
    @Override
    public boolean addReviews(String orderId, Integer productId, String token, String content) {
        // 从 token 解析当前登录用户
        User user = (User) userService.getUserByToken(token).getData();
        if (user == null) {
            throw new BusinessException("请先登录");
        }

        // 校验评价内容非空且长度在 10-500 字符之间
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("评价内容不能为空");
        }
        String text = content.trim();
        if (text.length() < 10 || text.length() > 500) {
            throw new BusinessException("评价内容长度需在 10-500 字符之间");
        }
        // 过滤评价内容中的 HTML 标签，防止 XSS
        text = text.replaceAll("<[^>]+>", "");

        // 校验订单存在性、归属、状态及商品匹配性
        Orders order = ordersService.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(user.getId())) {
            throw new BusinessException("无权评价该订单");
        }
        if (order.getStatus() == null || order.getStatus() != 7) {
            throw new BusinessException("订单不在待评价状态，无法评价");
        }
        if (!order.getProductId().equals(productId)) {
            throw new BusinessException("订单商品不匹配");
        }

        // 校验同一订单是否已存在评价，防止重复提交
        long existed = this.count(new QueryWrapper<ProductReviews>().eq("order_id", orderId));
        if (existed > 0) {
            throw new BusinessException("该订单已评价，请勿重复提交");
        }

        // 组装并保存评价记录
        ProductReviews review = new ProductReviews();
        review.setOrderId(orderId);
        review.setProductId(productId);
        review.setUserId(user.getId());
        review.setContent(text);
        review.setStatus(1);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        boolean saved = this.save(review);

        // 评价成功后，将订单状态更新为已完成
        if (saved) {
            order.setStatus(3);
            ordersService.updateById(order);
        }
        return saved;
    }

    /**
     * 判断指定订单是否已评价。
     *
     * @param orderId 订单ID
     * @return 已评价返回 true，否则返回 false
     */
    @Override
    public boolean hasReviewed(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return false;
        }
        return this.count(new QueryWrapper<ProductReviews>().eq("order_id", orderId)) > 0;
    }

    /**
     * 分页查询指定商品的评价列表。
     *
     * <p>仅查询状态为启用（status=1）的评价，按创建时间倒序排列，并为每条评价填充用户信息。</p>
     *
     * @param productId 商品ID
     * @param pageNum   当前页码
     * @param pageSize  每页大小
     * @return 商品评价分页结果
     */
    @Override
    public Page<ProductReviews> getProductReviews(Integer productId, Integer pageNum, Integer pageSize) {
        Page<ProductReviews> page = new Page<>(pageNum, pageSize);
        // 组装查询条件：指定商品、启用状态、按创建时间倒序
        page = this.page(page, new QueryWrapper<ProductReviews>()
                .eq("product_id", productId)
                .eq("status", 1)
                .orderByDesc("created_at"));
        // 为每条评价关联用户信息
        for (ProductReviews review : page.getRecords()) {
            review.setUser(userService.getById(review.getUserId()));
        }
        return page;
    }
}
