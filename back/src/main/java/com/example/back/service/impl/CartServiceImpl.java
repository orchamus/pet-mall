package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.back.Result.ResponseResult;
import com.example.back.entity.Cart;
import com.example.back.entity.ProductMedia;
import com.example.back.entity.Products;
import com.example.back.entity.User;
import com.example.back.mapper.CartMapper;
import com.example.back.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.back.service.IProductMediaService;
import com.example.back.service.IProductsService;
import com.example.back.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现类。
 *
 * <p>实现 {@link ICartService} 接口，提供购物车商品添加、列表查询与批量删除功能。
 * 购物车记录通过 user_id + product_id 唯一索引保证同一商品不会重复加入。</p>
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {
    @Resource
    private IProductsService productsService;
    @Resource
    private IUserService userService;
    @Resource
    private IProductMediaService productMediaService;

    /**
     * 将商品加入当前用户购物车。
     *
     * <p>仅普通用户（userType=0）可操作购物车；同一商品重复加入会触发唯一索引冲突并返回友好提示。</p>
     *
     * @param token    当前用户登录 token
     * @param product  待加入的商品实体
     * @param quantity 购买数量（当前逻辑未写入购物车表，仅预留参数）
     * @return 添加结果
     */
    @Override
    public ResponseResult<Boolean> addCart(String token, Products product, int quantity) {
        if("".equals(token)){
            return ResponseResult.fail("用户未登录",false);
        }
        User user = (User)userService.getUserByToken(token).getData();
        // 仅普通用户可操作购物车
        if(!"0".equals(user.getUserType())){
            return ResponseResult.fail("您没有权限操作购物车",false);
        } else{
            Cart cart = new Cart();
            cart.setUserId(user.getId());
            cart.setProductId(product.getProductId());
            try {
                if (this.save(cart)) {
                    return ResponseResult.success("添加成功", true);
                }
            } catch (DuplicateKeyException e) {
                // 同一商品重复加入时触发唯一索引冲突
                return ResponseResult.fail("商品已在购物车中",false);
            }
        }
        return ResponseResult.fail("添加失败",false);
    }

    /**
     * 根据 token 查询当前用户的购物车列表。
     *
     * <p>为每条购物车记录填充商品信息及封面图。</p>
     *
     * @param token 当前用户登录 token
     * @return 购物车列表
     */
    @Override
    public List<Cart> getCartListByToken(String token) {
        User user = (User)userService.getUserByToken(token).getData();
        Integer userId = user.getId();
        List<Cart> cartList = this.list(new QueryWrapper<Cart>().eq("user_id",userId));
        // 填充商品及封面图信息
        for(Cart cart:cartList){
            Products product = productsService.getById(cart.getProductId());
            List <ProductMedia> productMedia = productMediaService.findCovetByProductId(product.getProductId());
            product.setProductMedias(productMedia);
            cart.setProduct(product);
        }
        return cartList;
    }

    /**
     * 根据ID列表删除购物车记录。
     *
     * @param ids 购物车记录ID列表
     * @return 删除是否成功，任一删除异常返回 false
     */
    @Override
    public boolean deleteCart(List<Integer> ids) {
        try {
            ids.forEach(this::removeById);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
