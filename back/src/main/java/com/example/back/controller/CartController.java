package com.example.back.controller;


import com.example.back.Result.ResponseResult;
import com.example.back.entity.Cart;
import com.example.back.entity.Products;
import com.example.back.service.ICartService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车模块 Controller。
 *
 * <p>提供购物车商品添加、购物车列表查询及批量删除能力，基础路径为 /cart。</p>
 *

 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    private ICartService cartService;

    /**
     * 添加商品到购物车。
     *
     * @param token    登录凭证（请求头 satoken）
     * @param products 商品对象
     * @param quantity 购买数量
     * @return 添加是否成功
     */
    @PostMapping("/addTocart")
    public ResponseResult<Boolean> addTocart(@RequestHeader("satoken") String token, @RequestBody Products products, Integer quantity){
        return cartService.addCart(token,products,quantity);
    }

    /**
     * 查询当前登录用户的购物车列表。
     *
     * @param token 登录凭证（请求头 satoken）
     * @return 购物车条目集合
     */
    @GetMapping("/getCartList")
    public ResponseResult<List<Cart>> getCart(@RequestHeader("satoken") String token){
        List<Cart> cartList;
        try {
            cartList = cartService.getCartListByToken(token);
        } catch (Exception e) {
            // 异常时打印堆栈并返回失败信息
            e.printStackTrace();
            return ResponseResult.fail("获取购物车失败");
        }
        return ResponseResult.success("获取购物车成功",cartList);
    }

    /**
     * 批量删除购物车条目。
     *
     * @param ids 购物车条目 ID 集合
     * @return 删除是否成功
     */
    @PostMapping("/deleteCart")
    public ResponseResult<Boolean> deleteCart(@RequestBody List<Integer> ids){
        if(cartService.deleteCart(ids)){
            return ResponseResult.success("删除成功",true);
        }else{
            return ResponseResult.fail("删除失败",false);
        }
    }
}
