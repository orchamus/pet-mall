package com.example.back.mapper;

import com.example.back.entity.Products;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 商品基础信息表 Mapper 接口
 * </p>
 * <p>
 * 负责 Products 实体对应数据表的持久化操作，继承 MyBatis-Plus BaseMapper 提供通用 CRUD 能力。
 * </p>
 */
public interface ProductsMapper extends BaseMapper<Products> {

}
