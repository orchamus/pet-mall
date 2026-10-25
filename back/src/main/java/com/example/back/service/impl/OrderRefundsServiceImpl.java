package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.back.entity.OrderRefunds;
import com.example.back.mapper.OrderRefundsMapper;
import com.example.back.service.IOrderRefundsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 订单退款服务实现类。
 *
 * <p>实现 {@link IOrderRefundsService} 接口，提供按订单ID查询退款记录的能力，支撑退款申请与审批流程。</p>
 */
@Service
public class OrderRefundsServiceImpl extends ServiceImpl<OrderRefundsMapper, OrderRefunds> implements IOrderRefundsService {

    /**
     * 根据订单ID查询退款记录。
     *
     * @param orderId 订单ID
     * @return 退款记录，不存在则返回 null
     */
    @Override
    public OrderRefunds getRefundOrderByOrderId(String orderId) {
        return this.getOne(new QueryWrapper<OrderRefunds>().eq("order_id", orderId));
    }
}
