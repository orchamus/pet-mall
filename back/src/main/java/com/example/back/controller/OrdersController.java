package com.example.back.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.Result.ResponseResult;
import com.example.back.config.ExceptionConfig.BusinessException;
import com.example.back.entity.DTO.orderRequestBycart;
import com.example.back.entity.Orders;
import com.example.back.service.IOrdersService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单模块 Controller。
 *
 * <p>提供立即下单、购物车下单、订单查询、订单状态流转、退款申请及退款审核能力，基础路径为 /orders。</p>
 *

 */
@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Resource
    private IOrdersService ordersService;

    /**
     * 立即购买创建订单。
     *
     * @param token     登录凭证（请求头 satoken）
     * @param productId 商品 ID
     * @param addressId 收货地址 ID
     * @param quantity  购买数量
     * @return 下单是否成功
     */
    @PostMapping("/createOrderBynow")
     public ResponseResult<Boolean> createOrderBynow(@RequestHeader(value = "satoken") String token,
                                            @RequestParam("productId") Integer productId,
                                            @RequestParam("addressId") Integer addressId,
                                            @RequestParam("quantity") Integer quantity){
         // 校验 token 是否为空，未登录则返回提示
         if("".equals(token)){
             return ResponseResult.fail("请先登录",false);
         }
         try {
             if(ordersService.createOrderBynow(token, productId, addressId, quantity)){
                 return ResponseResult.success("下单成功",true);
             }else{
                 return ResponseResult.fail("下单失败",false);
             }
         } catch (BusinessException e) {
             // 捕获业务异常，返回友好错误信息
             return ResponseResult.fail(e.getMessage(), false);
         } catch (Exception e) {
             // 捕获其他异常，返回通用错误信息
             return ResponseResult.fail("下单失败，请稍后重试", false);
         }
     }

     /**
      * 从购物车创建订单。
      *
      * @param token    登录凭证（请求头 satoken）
      * @param cartList 购物车结算项列表
      * @param addressId 收货地址 ID
      * @return 下单结果
      */
     @PostMapping("/createOrderByCart")
     public ResponseResult<Boolean> createOrderByCart( @RequestHeader(value = "satoken") String token,
                                              @RequestBody List<orderRequestBycart> cartList,
                                              Integer addressId){
         // 校验 token 是否为空
         if("".equals(token)){
             return ResponseResult.fail("请先登录",false);
         }
         try {
             return ordersService.createOrderByCart(token, cartList, addressId);
         } catch (BusinessException e) {
             return ResponseResult.fail(e.getMessage(), false);
         } catch (Exception e) {
             return ResponseResult.fail("下单失败，请稍后重试", false);
         }
     }

     /**
      * 分页查询当前登录用户的订单列表。
      *
      * @param token    登录凭证（请求头 satoken）
      * @param pageNum  当前页码
      * @param pageSize 每页大小
      * @return 订单分页数据
      */
     @GetMapping("/getOrderByToken")
    public ResponseResult<Page<Orders>> getOrderByToken(@RequestHeader(value = "satoken") String token,Integer pageNum,Integer pageSize){
        Page<Orders> orders = ordersService.getOrdersByToken(token,pageNum,pageSize);
        return ResponseResult.success("获取成功",orders);
     }

    /**
     * 查询当前登录用户的全部订单。
     *
     * @param token 登录凭证（请求头 satoken）
     * @return 订单集合
     */
    @GetMapping("/getAllOrdersByToken")
    public ResponseResult<List<Orders>> getAllOrdersByToken(@RequestHeader(value = "satoken") String token) {
        List<Orders> orders = ordersService.getAllOrdersByToken(token);
        return ResponseResult.success("获取成功", orders);
    }

     /**
      * 分页查询当前登录用户的订单（订单管理视角）。
      *
      * @param token    登录凭证（请求头 satoken）
      * @param pageNum  当前页码
      * @param pageSize 每页大小
      * @return 订单分页数据
      */
     @GetMapping("/getOrdersByTokenForOrderManagement")
    public ResponseResult<Page<Orders>> getOrdersByTokenForOrderManagement(@RequestHeader(value = "satoken") String token,Integer pageNum,Integer pageSize){
        Page<Orders> orders = ordersService.getOrdersByTokenForOrderManagement(token,pageNum,pageSize);
        return ResponseResult.success("获取成功",orders);
    }

    /**
     * 按条件搜索订单。
     *
     * @param token       登录凭证（请求头 satoken）
     * @param orderStatus 订单状态（可选）
     * @param orderId     订单编号
     * @param productName 商品名称
     * @param orderDate   下单日期范围
     * @param pageNum     当前页码
     * @param pageSize    每页大小
     * @return 订单分页数据
     */
    @PostMapping("/searchOrdersForOrder")
    public ResponseResult<Page<Orders>> searchOrders(@RequestHeader(value = "satoken") String token,
                                       @RequestParam(value = "orderStatus",required = false) Integer orderStatus,
                                       @RequestParam("orderId") String orderId,
                                       @RequestParam("productName") String productName,
                                       @RequestParam("orderDate") List<String> orderDate,
                                        Integer pageNum,Integer pageSize){
        Page<Orders> orders = ordersService. searchOrdersForOrder(token,pageNum,pageSize,orderStatus,orderId,productName,orderDate);
        return ResponseResult.success("搜索成功",orders);
    }

    /**
     * 将订单状态推进到下一步。
     *
     * @param orderId 订单编号
     * @return 状态变更是否成功
     */
    @PostMapping("/changeOrderStatusToNext")
    public ResponseResult<Boolean> changOrderStatusToNext(String orderId){
        if(ordersService.changOrderStatusToNext(orderId))
            return ResponseResult.success("修改成功",true);
        else
            return ResponseResult.fail("修改失败",false);
    }

    /**
     * 申请订单退款。
     *
     * @param orderId 订单编号
     * @param reason  退款原因
     * @param token   登录凭证（请求头 satoken）
     * @return 申请是否成功
     */
    @PostMapping("/requestRefund")
    public ResponseResult<Boolean> requestRefund(@RequestParam("orderId") String orderId,
                                        @RequestParam("reason") String reason,
                                        @RequestHeader(value = "satoken") String token){
        if(ordersService.requestRefund(orderId,reason,token))
            return ResponseResult.success("申请退款成功",true);
        else
            return ResponseResult.fail("申请退款失败",false);
    }

    /**
     * 取消订单。
     *
     * <p>将订单状态设置为已取消，并记录关闭时间。</p>
     *
     * @param orderId 订单编号
     * @return 取消是否成功
     */
    @PostMapping("/cancelOrder")
    public ResponseResult<Boolean> cancelOrder(String orderId){
        Orders orders = ordersService.getById(orderId);
        // 设置订单状态为已取消（4）
        orders.setStatus(4);
        // 记录订单关闭时间
        orders.setCloseTime(LocalDateTime.now());
        if(ordersService.updateById(orders))
            return ResponseResult.success("取消订单成功",true);
        else
            return ResponseResult.fail("取消订单失败",false);
    }

    /**
     * 审核订单退款申请。
     *
     * @param orderId       订单编号
     * @param OrderRefundId 退款记录 ID
     * @param isAgree       是否同意退款
     * @return 审核是否成功
     */
    @PostMapping("/reviewRefund")
    public ResponseResult<Boolean> reviewRefund(@RequestParam("orderId") String orderId,
                                       @RequestParam("OrderRefundId")String OrderRefundId,
                                       @RequestParam("isAgree")boolean isAgree){
        if(ordersService.reviewRefund(orderId,OrderRefundId,isAgree)){
            return ResponseResult.success("审核成功",true);
        }else{
            return ResponseResult.fail("审核失败",false);
        }
    }
}
