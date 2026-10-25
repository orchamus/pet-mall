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
 * 商店信息实体，记录平台上入驻店铺的基本资料与营业状态。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("shop")
@ApiModel(value="Shop对象", description="商店信息表")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商店唯一标识。
     */
    @ApiModelProperty(value = "商店唯一标识")
    @TableId(value = "shop_id", type = IdType.AUTO)
    private Integer shopId;

    /**
     * 关联商家用户 ID。
     */
    @ApiModelProperty(value = "关联商家用户ID")
    private Integer userId;

    /**
     * 商店名称。
     */
    @ApiModelProperty(value = "商店名称")
    private String shopName;

    /**
     * 商店描述。
     */
    @ApiModelProperty(value = "商店描述")
    private String description;

    /**
     * 商店 logo URL。
     */
    @ApiModelProperty(value = "商店logo URL")
    private String logo;

    /**
     * 联系邮箱。
     */
    @ApiModelProperty(value = "联系邮箱")
    private String contactEmail;

    /**
     * 实体店地址。
     */
    @ApiModelProperty(value = "实体店地址")
    private String address;

    /**
     * 商店状态：0-关闭，1-营业中，2-暂停营业。
     */
    @ApiModelProperty(value = "状态(0-关闭,1-营业中,2-暂停营业)")
    private Integer status;

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
