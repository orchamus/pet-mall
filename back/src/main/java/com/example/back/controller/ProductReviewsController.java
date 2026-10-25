package com.example.back.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.Result.ResponseResult;
import com.example.back.config.ExceptionConfig.BusinessException;
import com.example.back.entity.ProductReviews;
import com.example.back.service.IProductReviewsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 商品评论模块 Controller。
 *
 * <p>提供商品评价提交、评价状态查询及评价列表分页查询能力，基础路径为 /product-reviews。</p>
 */
@RestController
@RequestMapping("/product-reviews")
public class ProductReviewsController {
     @Resource
     private IProductReviewsService productReviewsService;

     /**
      * 提交商品评价。
      *
      * <p>根据订单与商品信息提交用户评价，需要登录 token。</p>
      *
      * @param token   登录凭证（请求头 satoken）
      * @param orderId 订单编号
      * @param productId 商品 ID
      * @param content 评价内容
      * @return 评价是否成功的布尔结果
      */
     @PostMapping("/add")
     public ResponseResult<Boolean> addProductReviews(@RequestHeader("satoken") String token,
                                                      @RequestParam("orderId") String orderId,
                                                      @RequestParam("productId") Integer productId,
                                                      @RequestParam("content") String content) {
         try {
             boolean result = productReviewsService.addReviews(orderId, productId, token, content);
             if (result) {
                 return ResponseResult.success("评价成功", true);
             } else {
                 return ResponseResult.fail("评价失败", false);
             }
         } catch (BusinessException e) {
             // 捕获业务异常，返回具体错误提示
             return ResponseResult.fail(e.getMessage(), false);
         } catch (Exception e) {
             // 捕获系统异常，返回通用错误提示
             return ResponseResult.fail("评价失败，请稍后重试", false);
         }
     }

     /**
      * 查询订单是否已评价。
      *
      * @param orderId 订单编号
      * @return 是否已评价的布尔结果
      */
     @GetMapping("/has-reviewed")
     public ResponseResult<Boolean> hasReviewed(@RequestParam("orderId") String orderId) {
         return ResponseResult.success(productReviewsService.hasReviewed(orderId));
     }

     /**
      * 分页查询商品评价列表。
      *
      * @param pageNum  当前页码
      * @param pageSize 每页大小
      * @param productId 商品 ID
      * @return 商品评价分页数据
      */
     @GetMapping("/get-reviews")
     public ResponseResult<Page<ProductReviews>> getProductReviews(Integer pageNum, Integer pageSize, Integer productId) {
         Page<ProductReviews> page = productReviewsService.getProductReviews(productId, pageNum, pageSize);
         return ResponseResult.success(page);
     }
}
