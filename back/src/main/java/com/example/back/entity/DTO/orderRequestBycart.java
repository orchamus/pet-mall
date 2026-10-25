package com.example.back.entity.DTO;

import lombok.Data;

/**
 * 购物车结算请求数据传输对象，用于接收基于购物车提交订单时的请求参数。
 */
@Data
public class orderRequestBycart {

    /**
     * 购物车项 ID，标识待结算的购物车记录。
     */
    private Integer cartId;

    /**
     * 购买数量。
     */
    private Integer quantity;
}
