package com.example.back.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.Result.ResponseResult;
import com.example.back.entity.Shop;
import com.example.back.service.IShopService;
import com.example.back.entity.VO.showShopAndProducts;
import com.example.back.service.IProductsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 店铺模块 Controller。
 *
 * <p>提供店铺信息维护、店铺 Logo 上传、店铺商品列表及全量店铺查询能力，基础路径为 /shop。</p>
 */
@RestController
@RequestMapping("/shop")
public class ShopController {
    @Resource
    private IShopService shopService;
    @Resource
    private IProductsService productsService;

    /**
     * 获取当前登录用户对应的店铺信息。
     *
     * @param token 登录凭证（请求头 satoken）
     * @return 店铺对象，未创建店铺时返回失败提示
     */
    @GetMapping("/getShop")
    public ResponseResult<Shop> getShop(@RequestHeader("satoken") String token){
        Shop shop = shopService.getShopByToken(token);
        if(shop != null){
            return ResponseResult.success("获取成功",shop);
        }else{
            return ResponseResult.fail("你还没创建店铺");
        }
    }


    /**
     * 更新店铺信息。
     *
     * @param shop 店铺对象
     * @return 更新是否成功
     */
    @PostMapping("/updateShop")
    public ResponseResult<Boolean> updateShop(@RequestBody Shop shop){
        boolean flag = shopService.updateShop(shop);
        if(flag){
            return ResponseResult.success("更新成功",true);
        }else{
            return ResponseResult.fail("更新失败",false);
        }
    }

    /**
     * 新增店铺。
     *
     * @param shop  店铺对象
     * @param token 登录凭证（请求头 satoken）
     * @return 添加是否成功
     */
    @PostMapping("/addShop")
    public ResponseResult<Boolean> addShop(@RequestBody Shop shop,@RequestHeader("satoken") String token){
        boolean flag = shopService.addShop(shop,token);
        if(flag){
            return ResponseResult.success("添加成功",true);
        }else{
            return ResponseResult.fail("添加失败",false);
        }
    }

    /**
     * 上传店铺 Logo。
     *
     * @param logo  Logo 图片文件
     * @param token 登录凭证（请求头 satoken）
     * @return 上传后的图片 URL
     */
    @PostMapping("/uploadShopLogo")
    public ResponseResult<String> uploadShopLogo( @RequestParam("logo") MultipartFile logo,@RequestHeader("satoken") String token){
        String url = shopService.uploadShopImg(logo,token);
        if(url != null){
            return ResponseResult.success("上传成功",url);
        }else{
            return ResponseResult.fail("上传失败");
        }
    }

    /**
     * 分页查询指定店铺及其商品信息。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param shopId   店铺 ID
     * @return 店铺及商品聚合信息
     */
    @GetMapping("/getShopAndProducts")
    public ResponseResult<showShopAndProducts> getShopAndProducts(Integer pageNum, Integer pageSize,Integer shopId){
        try{
            showShopAndProducts showShowAndProducts = productsService.getShopAndProductsByShopId(pageNum, pageSize, shopId);
            return ResponseResult.success("获取成功",showShowAndProducts);
        }catch (Exception e){
            // 异常时打印堆栈并返回失败结果
            e.printStackTrace();
            return ResponseResult.fail("获取失败",null);
        }
    }


    /**
     * 分页查询全部店铺列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param shopName 店铺名称关键字（可选）
     * @return 店铺分页数据
     */
    @GetMapping("/getAllShop")
    public ResponseResult<Page<Shop>> getAllShop(Integer pageNum, Integer pageSize,String shopName){
        Page<Shop> page = shopService.getAllShopListByPage(pageNum, pageSize,shopName);
        if(page != null){
            return ResponseResult.success("获取成功",page);
        }else{
            return ResponseResult.fail("获取失败",null);
        }
    }
}
