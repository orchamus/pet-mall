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
 * 媒体资源存储实体，统一管理上传的图片、视频等文件资源信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("media")
@ApiModel(value="Media对象", description="媒体资源存储表")
public class Media implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 媒体资源唯一标识。
     */
    @ApiModelProperty(value = "媒体资源ID")
    @TableId(value = "media_id", type = IdType.AUTO)
    private Integer mediaId;

    /**
     * 文件存储路径或访问 URL。
     */
    @ApiModelProperty(value = "文件存储路径/URL")
    private String url;

    /**
     * 上传时间。
     */
    @ApiModelProperty(value = "上传时间")
    private LocalDateTime uploadTime;


}
