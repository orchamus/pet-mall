package com.example.back.service;

import com.example.back.entity.OrderRefunds;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单退款服务接口。
 *
 * <p>定义订单退款记录的查询操作，支撑退款申请与审批流程。</p>
 */
public interface IOrderRefundsService extends IService<OrderRefunds> {

        /**
         * 根据订单ID查询退款记录。
         *
         * @param orderId 订单ID
         * @return 退款记录，不存在则返回 null
         */
        public OrderRefunds getRefundOrderByOrderId(String orderId);
}
