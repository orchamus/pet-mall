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
 * 用户实体，记录平台注册用户的账号、身份及状态信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@ApiModel(value="User对象", description="用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识。
     */
    @ApiModelProperty(value = "用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名。
     */
    @ApiModelProperty(value = "用户名")
    private String userName;

    /**
     * 用户昵称。
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 用户类型：0-普通用户，1-商家用户，2-管理员。
     */
    @ApiModelProperty(value = "用户类型（0为普通用户，1为商家用户，2为管理员）")
    private String userType;

    /**
     * 性别。
     */
    @ApiModelProperty(value = "性别")
    private String sex;

    /**
     * 登录密码。
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 邮箱地址。
     */
    @ApiModelProperty(value = "邮箱地址")
    private String email;

    /**
     * 头像地址。
     */
    @ApiModelProperty(value = "头像地址")
    private String avatar;

    /**
     * 账号状态：0-正常，1-停用。
     */
    @ApiModelProperty(value = "账号状态（0正常 1停用）")
    private String status;

    /**
     * 删除标志：0-存在，2-删除。
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    /**
     * 验证码或临时编码，非数据库持久化字段。
     */
    @TableField(exist = false)
    private String code;
}
