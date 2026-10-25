package com.example.back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 萌宠视频实体，用于维护与商品关联的宠物宣传视频信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pet_video")
@ApiModel(value = "PetVideo对象", description = "萌宠视频表")
public class PetVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 视频唯一标识。
     */
    @ApiModelProperty(value = "视频ID")
    @TableId(value = "video_id", type = IdType.AUTO)
    private Integer videoId;

    /**
     * 视频标题。
     */
    @ApiModelProperty(value = "视频标题")
    private String title;

    /**
     * 视频描述。
     */
    @ApiModelProperty(value = "视频描述")
    private String description;

    /**
     * 封面图 URL。
     */
    @ApiModelProperty(value = "封面图URL")
    private String coverUrl;

    /**
     * 视频文件 URL。
     */
    @ApiModelProperty(value = "视频URL")
    private String videoUrl;

    /**
     * 关联商品 ID。
     */
    @ApiModelProperty(value = "关联商品ID")
    private Integer productId;

    /**
     * 展示排序，数值越小越靠前。
     */
    @ApiModelProperty(value = "排序")
    private Integer sortOrder;

    /**
     * 视频状态：1-启用，0-禁用。
     */
    @ApiModelProperty(value = "状态 1-启用 0-禁用")
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

    /**
     * 关联商品信息，非数据库持久化字段。
     */
    @TableField(exist = false)
    private Products product;

    /**
     * 关联商品名称，非数据库持久化字段。
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 关联商品价格，非数据库持久化字段。
     */
    @TableField(exist = false)
    private Double productPrice;
}
