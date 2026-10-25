package com.example.back.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.example.back.utils.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商店位置信息实体，用于描述店铺的地址与基本标识信息。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("shop")
public class Location extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 店铺详细地址。
     */
    private String  address;

    /**
     * 商店 ID。
     */
    private Integer shopId;

    /**
     * 商店名称。
     */
    private String shopName;

}
