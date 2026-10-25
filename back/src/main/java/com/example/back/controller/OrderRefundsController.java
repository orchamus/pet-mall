package com.example.back.controller;


import com.example.back.Result.ResponseResult;
import com.example.back.entity.OrderRefunds;
import com.example.back.service.IOrderRefundsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * 订单退款模块 Controller。
 *
 * <p>提供订单退款记录的查询能力，基础路径为 /order-refunds。</p>
 *

 */
@RestController
@RequestMapping("/order-refunds")
public class OrderRefundsController {
     @Resource
     private IOrderRefundsService orderRefundsService;

     /**
      * 根据订单编号查询退款记录。
      *
      * @param orderId 订单编号
      * @return 退款记录对象
      */
     @GetMapping("/getOrderRefundByOrderId")
     public ResponseResult<OrderRefunds> getOrderRefundByOrderId(String orderId){
         OrderRefunds orderRefunds = orderRefundsService.getRefundOrderByOrderId(orderId);
         return ResponseResult.success(orderRefunds);
     }
}
