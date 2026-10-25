package com.example.back.entity;

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
 * 订单退款实体，记录用户针对订单发起的退款申请及审批状态。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_refunds")
@ApiModel(value="OrderRefunds对象", description="订单退款表")
public class OrderRefunds implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 退款单唯一标识，使用 UUID 生成。
     */
    @ApiModelProperty(value = "退单ID(UUID)")
    @TableId(value = "refund_id", type = IdType.ASSIGN_UUID)
    private String refundId;

    /**
     * 关联订单 ID。
     */
    @ApiModelProperty(value = "关联的订单ID")
    private String orderId;

    /**
     * 申请退款的用户 ID。
     */
    @ApiModelProperty(value = "申请退款的用户ID")
    private Integer userId;

    /**
     * 退款原因。
     */
    @ApiModelProperty(value = "退款原因")
    private String refundReason;

    /**
     * 退款状态：1-申请中，2-审核通过，3-已拒绝。
     */
    @ApiModelProperty(value = "退款状态(1-申请中,2-审核通过,3-已拒绝)")
    private Integer refundStatus;

    /**
     * 申请时间。
     */
    @ApiModelProperty(value = "申请时间")
    private LocalDateTime applyTime;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedAt;


}
