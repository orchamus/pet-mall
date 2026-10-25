package com.example.back.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 订单主实体，记录用户购买商品的订单核心信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("orders")
@ApiModel(value="Orders对象", description="订单主表")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单唯一标识，使用 UUID 生成。
     */
    @ApiModelProperty(value = "订单号(UUID)")
    @TableId(value = "order_id", type = IdType.ASSIGN_UUID)
    private String orderId;

    /**
     * 下单用户 ID。
     */
    @ApiModelProperty(value = "下单用户ID")
    private Integer userId;

    /**
     * 商品 ID。
     */
    @ApiModelProperty(value = "商品id")
    private Integer productId;

    /**
     * 商品数量。
     */
    @ApiModelProperty(value = "商品数量")
    private Integer quantity;

    /**
     * 收货地址 ID。
     */
    @ApiModelProperty(value = "收货地址ID")
    private Integer addressId;

    /**
     * 商品单价。
     */
    @ApiModelProperty(value = "货物单价")
    private Double price;

    /**
     * 商店 ID。
     */
    @ApiModelProperty(value = "商店ID")
    private Integer shopId;

    /**
     * 订单状态：1-待发货，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款。
     */
    @ApiModelProperty(value = "订单状态(1-待发货,2-已发货,3-已完成,4-已取消,5-退款中,6-已退款)")
    private Integer status;

    /**
     * 退款前的订单状态，用于退款恢复场景。
     */
    @ApiModelProperty(value = "退款前的订单状态")
    private Integer preRefundStatus;

    /**
     * 支付时间。
     */
    @ApiModelProperty(value = "支付时间")
    private LocalDateTime paymentTime;

    /**
     * 发货时间。
     */
    @ApiModelProperty(value = "发货时间")
    private LocalDateTime deliveryTime;

    /**
     * 确认收货时间。
     */
    @ApiModelProperty(value = "确认收货时间")
    private LocalDateTime receiveTime;

    /**
     * 订单关闭时间。
     */
    @ApiModelProperty(value = "订单关闭时间")
    private LocalDateTime closeTime;

    /**
     * 下单时间。
     */
    @ApiModelProperty(value = "下单时间")
    private LocalDateTime createdAt;

    /**
     * 最后修改时间。
     */
    @ApiModelProperty(value = "最后修改时间")
    private LocalDateTime updatedAt;

    /**
     * 关联商品信息，非数据库持久化字段。
     */
    @TableField(exist = false)
    private Products product;
}
