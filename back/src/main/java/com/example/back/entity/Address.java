package com.example.back.entity;

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
 * 用户收货地址实体，用于存储用户接收商品时使用的地址信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("address")
@ApiModel(value="Address对象", description="用户收货地址表")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址唯一标识。
     */
    @ApiModelProperty(value = "地址唯一标识")
    @TableId(value = "address_id", type = IdType.AUTO)
    private Integer addressId;

    /**
     * 关联用户 ID。
     */
    @ApiModelProperty(value = "关联用户ID")
    private Integer userId;

    /**
     * 收货人姓名。
     */
    @ApiModelProperty(value = "收货人姓名")
    private String consignee;

    /**
     * 收货人联系方式（邮箱）。
     */
    @ApiModelProperty(value = "收货人联系方式")
    private String email;

    /**
     * 详细地址。
     */
    @ApiModelProperty(value = "详细地址")
    private String detail;

    /**
     * 联系电话。
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;

    /**
     * 省。
     */
    @ApiModelProperty(value = "省")
    private String province;

    /**
     * 市。
     */
    @ApiModelProperty(value = "市")
    private String city;

    /**
     * 区。
     */
    @ApiModelProperty(value = "区")
    private String district;

    /**
     * 邮政编码。
     */
    @ApiModelProperty(value = "邮政编码")
    private String postalCode;

    /**
     * 是否默认地址：1-是，0-否。
     */
    @ApiModelProperty(value = "是否默认地址(1是,0否)")
    private Integer isDefault;

    /**
     * 地址标签，例如家、公司等。
     */
    @ApiModelProperty(value = "地址标签(如家、公司)")
    private String tag;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 最后修改时间。
     */
    @ApiModelProperty(value = "最后修改时间")
    private LocalDateTime updatedAt;

    /**
     * 省市区组合地址。
     */
    @ApiModelProperty(value = "省市区地址")
    private String region;

}
