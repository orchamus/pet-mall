package com.example.back.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商品基础信息实体，对应系统中出售的商品主数据。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("products")
@ApiModel(value="Products对象", description="商品基础信息表")
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品唯一标识符。
     */
    @ApiModelProperty(value = "商品唯一标识符")
    @TableId(value = "product_id", type = IdType.AUTO)
    private Integer productId;

    /**
     * 关联商店 ID。
     */
    @ApiModelProperty(value = "关联商店ID")
    private Integer shopId;

    /**
     * 商品名称。
     */
    @ApiModelProperty(value = "商品名称")
    private String name;

    /**
     * 商品详细描述。
     */
    @ApiModelProperty(value = "详细描述")
    private String description;

    /**
     * 销售价格。
     */
    @ApiModelProperty(value = "销售价格")
    private Double price;

    /**
     * 库存数量。
     */
    @ApiModelProperty(value = "库存数量")
    private Integer stockQuantity;

    /**
     * 商品类别：0-宠物活体，1-周边用品。
     */
    @ApiModelProperty(value = "商品类别：宠物活体(0)/周边用品(1)")
    private String category;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 最后更新时间。
     */
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime updatedAt;

    /**
     * 商品状态：0-上架，2-下架。
     */
    @ApiModelProperty(value = "状态标志（0代表上架 2代表下架）")
    private String status;

    /**
     * 商品媒体资源列表，非数据库持久化字段。
     */
    @TableField(exist = false)
    private List<ProductMedia> productMedias;

    /**
     * 关联宠物扩展信息，非数据库持久化字段。
     */
    @TableField(exist = false)
    private Pets pets;

    /**
     * 关联周边用品扩展信息，非数据库持久化字段。
     */
    @TableField(exist = false)
    private Supplies supplies;
}
