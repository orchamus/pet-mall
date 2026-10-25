package com.example.back.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 商品与媒体资源关联实体，维护商品展示所需的图片、视频等媒介关系。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product_media")
@ApiModel(value="ProductMedia对象", description="商品与媒体资源关联关系表")
public class ProductMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联商品 ID，作为关联表主键。
     */
    @ApiModelProperty(value = "关联商品ID")
    @TableId(value = "product_id", type = IdType.AUTO)
    private Integer productId;

    /**
     * 关联媒体资源 ID。
     */
    @ApiModelProperty(value = "关联媒体ID")
    private Integer mediaId;

    /**
     * 展示排序，数值越小越靠前。
     */
    @ApiModelProperty(value = "展示排序(数值越小越靠前)")
    private Integer sortOrder;

    /**
     * 媒体类型：0-商品封面，1-商品其他展示内容。
     */
    @ApiModelProperty(value = "0为商品封面，1为商品其他展示内容。")
    private String mediaType;

    /**
     * 文件名称。
     */
    @ApiModelProperty(value = "文件名称")
    private String name;

    /**
     * 关联媒体资源详情，非数据库持久化字段。
     */
    @TableField(exist = false)
    private Media media;
}
