package com.example.back.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户购物车实体，记录用户暂存的商品选购信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cart")
@ApiModel(value="Cart对象", description="用户购物车表")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物车项唯一标识。
     */
    @ApiModelProperty(value = "购物车项唯一标识")
    @TableId(value = "cart_id", type = IdType.AUTO)
    private Integer cartId;

    /**
     * 关联用户 ID。
     */
    @ApiModelProperty(value = "关联用户ID")
    private Integer userId;

    /**
     * 关联商品 ID。
     */
    @ApiModelProperty(value = "关联商品ID")
    private Integer productId;


    /**
     * 添加时间。
     */
    @ApiModelProperty(value = "添加时间")
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
