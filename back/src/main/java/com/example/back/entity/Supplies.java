package com.example.back.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 周边商品扩展属性实体，记录宠物周边用品（食品、玩具等）的详细规格信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("supplies")
@ApiModel(value="Supplies对象", description="周边商品扩展属性表")
public class Supplies implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 周边商品记录唯一标识。
     */
    @ApiModelProperty(value = "周边商品记录ID")
    @TableId(value = "supply_id", type = IdType.AUTO)
    private Integer supplyId;

    /**
     * 关联商品 ID。
     */
    @ApiModelProperty(value = "关联商品ID")
    private Integer productId;

    /**
     * 周边类型，例如食品、玩具、窝垫等。
     */
    @ApiModelProperty(value = "周边类型(食品/玩具/窝垫)")
    private String supplyType;

    /**
     * 材质说明。
     */
    @ApiModelProperty(value = "材质说明")
    private String material;

    /**
     * 尺寸规格，例如 S/M/L 或具体规格。
     */
    @ApiModelProperty(value = "尺寸(S/M/L或具体规格)")
    private String size;

    /**
     * 商品重量，单位千克。
     */
    @ApiModelProperty(value = "重量(千克)")
    private BigDecimal weightKg;

    /**
     * 适用宠物类型，例如狗、猫、通用等。
     */
    @ApiModelProperty(value = "适用宠物类型(狗/猫/通用等)")
    private String targetSpecies;


}
