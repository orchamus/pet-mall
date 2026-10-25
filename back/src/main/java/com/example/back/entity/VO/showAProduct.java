package com.example.back.entity.VO;

import com.example.back.entity.Products;
import com.example.back.entity.Shop;
import lombok.Data;

/**
 * 商品及所属商店视图对象，用于返回单个商品详情与所属商店信息。
 */
@Data
public class showAProduct {

    /**
     * 商品信息。
     */
    private Products product;

    /**
     * 所属商店信息。
     */
    private Shop shop;
}
