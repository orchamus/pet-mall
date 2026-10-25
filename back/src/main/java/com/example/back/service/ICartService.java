package com.example.back.service;

import com.example.back.Result.ResponseResult;
import com.example.back.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.back.entity.Products;

import java.util.List;

/**
 * 购物车服务接口。
 *
 * <p>定义购物车商品的添加、查询与删除操作。</p>
 */
public interface ICartService extends IService<Cart> {

   /**
    * 将商品加入当前用户购物车。
    *
    * @param token    当前用户登录 token
    * @param product  待加入的商品实体
    * @param quantity 购买数量
    * @return 添加结果
    */
   public ResponseResult<Boolean> addCart(String token, Products product, int quantity);

   /**
    * 根据 token 查询当前用户的购物车列表。
    *
    * @param token 当前用户登录 token
    * @return 购物车列表
    */
   public List<Cart> getCartListByToken(String token);

   /**
    * 根据ID列表删除购物车记录。
    *
    * @param ids 购物车记录ID列表
    * @return 删除是否成功
    */
   public boolean deleteCart(List<Integer> ids);
}
