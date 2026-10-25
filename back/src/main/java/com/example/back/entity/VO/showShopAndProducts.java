package com.example.back.entity.VO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.entity.Products;
import com.example.back.entity.Shop;
import lombok.Data;


/**
 * 商店及其商品分页视图对象，用于返回商店详情与关联商品分页数据。
 */
@Data
public class showShopAndProducts {

    /**
     * 商店信息。
     */
    private Shop shop;

    /**
     * 商品分页列表。
     */
    private Page<Products> products;
}
