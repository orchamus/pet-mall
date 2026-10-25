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
 * 商品评论实体，对应业务中用户对已购商品发表的评分与评价信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product_reviews")
@ApiModel(value="ProductReviews对象", description="商品评论表")
public class ProductReviews implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论唯一标识，使用 UUID 生成。
     */
    @ApiModelProperty(value = "评论ID(UUID)")
    @TableId(value = "review_id", type = IdType.ASSIGN_UUID)
    private String reviewId;

    /**
     * 关联商品 ID。
     */
    @ApiModelProperty(value = "关联商品ID")
    private Integer productId;

    /**
     * 关联订单 ID。
     */
    @ApiModelProperty(value = "关联订单ID")
    private String orderId;

    /**
     * 评论用户 ID。
     */
    @ApiModelProperty(value = "评论用户ID")
    private Integer userId;

    /**
     * 评分，取值范围为 1-5 星。
     */
    @ApiModelProperty(value = "评分(1-5星)")
    private Integer rating;

    /**
     * 评论内容。
     */
    @ApiModelProperty(value = "评论内容")
    private String content;

    /**
     * 评论状态：0-待审核，1-已发布，2-已删除。
     */
    @ApiModelProperty(value = "状态(0-待审核,1-已发布,2-已删除)")
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
     * 关联用户信息，非数据库持久化字段。
     */
    @TableField(exist = false)
    private User user;
}
