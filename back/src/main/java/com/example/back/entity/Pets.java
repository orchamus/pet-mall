package com.example.back.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 宠物类商品扩展属性实体，记录活体宠物商品的品种、出生日期等详细信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pets")
@ApiModel(value="Pets对象", description="宠物类商品扩展属性表")
public class Pets implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 宠物记录唯一标识。
     */
    @ApiModelProperty(value = "宠物记录ID")
    @TableId(value = "pet_id", type = IdType.AUTO)
    private Integer petId;

    /**
     * 关联商品 ID。
     */
    @ApiModelProperty(value = "关联商品ID")
    private Integer productId;

    /**
     * 物种，例如狗、猫、鸟等。
     */
    @ApiModelProperty(value = "物种(如狗/猫/鸟)")
    private String species;

    /**
     * 品种，例如金毛、布偶猫等。
     */
    @ApiModelProperty(value = "品种(如金毛/布偶猫)")
    private String breed;

    /**
     * 出生日期。
     */
    @ApiModelProperty(value = "出生日期")
    private LocalDate birthDate;

    /**
     * 性别。
     */
    @ApiModelProperty(value = "性别")
    private String gender;

    /**
     * 健康状况描述。
     */
    @ApiModelProperty(value = "健康状况描述")
    private String healthStatus;


}
