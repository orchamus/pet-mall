package com.example.back.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.Result.ResponseResult;
import com.example.back.entity.Products;
import com.example.back.entity.VO.showAProduct;
import com.example.back.service.IProductsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品模块 Controller。
 *
 * <p>提供商品发布、编辑、删除、状态切换、图片临时上传、列表分页查询及商品详情查询能力，基础路径为 /products。</p>
 */
@RestController
@RequestMapping("/products")
public class ProductsController {
    @Resource
    private IProductsService productsService;

    /**
     * 临时上传文件（商品图片等）。
     *
     * @param file 待上传的文件
     * @return 包含文件名与访问 URL 的 Map
     */
    @PostMapping("/uploadFileTemporary")
    public ResponseResult<Map<String, String>> uploadFileTemporary(@RequestParam("file") MultipartFile file) {
        // 获取原始文件名
        String name = file.getOriginalFilename();
        String url =  productsService.uploadFileTemporary(file);
        Map<String, String> result = new HashMap<>();
        if(url != null){
            result.put("name", name);
            result.put("url", url);
            // 手动封装成功响应对象
            ResponseResult<Map<String, String>> responseResult = new ResponseResult<>();
            responseResult.setCode(200);
            responseResult.setData(result);
            responseResult.setMessage("上传成功");
            return responseResult;
        }
        return ResponseResult.fail("上传失败", null);
    }

    /**
     * 新增商品。
     *
     * @param products 商品对象
     * @param token    登录凭证（请求头 satoken）
     * @return 添加是否成功
     */
    @PostMapping("/addProduct")
    public ResponseResult<Boolean> addProduct(@RequestBody Products products,@RequestHeader("satoken") String token) {
        if(productsService.addProduct(products,token)){
            return ResponseResult.success("添加成功",true);
        }else{
            return ResponseResult.fail("添加失败",false);
        }
    }

    /**
     * 查询当前登录用户发布的商品列表。
     *
     * @param token 登录凭证（请求头 satoken）
     * @return 商品集合
     */
    @GetMapping("/getProductsByToken")
    public ResponseResult<List<Products>> getProductsByToken(@RequestHeader("satoken") String token){
       List<Products> products = productsService.getProductListByToken(token);
       return ResponseResult.success("获取成功",products);
    }

    /**
     * 更新商品信息。
     *
     * @param products 商品对象
     * @param token    登录凭证（请求头 satoken）
     * @return 更新是否成功
     */
    @PostMapping("/updateProduct")
    public ResponseResult<Boolean> updateProduct(@RequestBody Products products,@RequestHeader("satoken") String token) {
        if(productsService.updateProduct(products,token)){
            return ResponseResult.success("更新成功",true);
        }else{
            return ResponseResult.fail("更新失败",false);
        }
    }

    /**
     * 删除商品。
     *
     * @param productId 商品 ID
     * @return 删除是否成功
     */
    @PostMapping("/deleteProduct")
    public ResponseResult<Boolean> deleteProduct(@RequestParam("productId") Integer productId) {
        if(productsService.deleteProductById(productId)){
            return ResponseResult.success("删除成功",true);
        }else{
            return ResponseResult.fail("删除失败",false);
        }
    }

    /**
     * 切换商品状态（上架/下架）。
     *
     * @param productId 商品 ID
     * @return 状态切换是否成功
     */
    @PostMapping("/changStatus")
    public ResponseResult<Boolean> changStatus(@RequestParam("productId") Integer productId) {
        if(productsService.changStatus(productId)){
            return ResponseResult.success("状态更改成功",true);
        }else{
            return ResponseResult.fail("状态更改失败",false);
        }
    }

    /**
     * 分页查询全部商品列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param category 商品分类（可选）
     * @return 商品分页数据
     */
    @GetMapping("/getAllProductList")
    public ResponseResult<Page<Products>> getAllProductListPage(Integer pageNum, Integer pageSize,String category) {
        Page<Products> page = productsService.getAllProductListByPage(pageNum, pageSize,category);
        if(page != null){
            return ResponseResult.success("获取成功",page);
        }else{
            return ResponseResult.fail("获取失败",null);
        }
    }

    /**
     * 查询单个商品详情。
     *
     * @param productId 商品 ID
     * @return 商品详情对象
     */
    @GetMapping("/getAProductById")
    public ResponseResult<showAProduct> getAProductById(Integer productId) {
        showAProduct products = productsService.getAProductById(productId);
        if(products != null){
            return ResponseResult.success("获取成功",products);
        }else{
            return ResponseResult.fail("获取失败");
        }
    }

    /**
     * 根据关键字分页搜索商品。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param keyword  搜索关键字
     * @return 商品分页数据
     */
    @GetMapping("/getProductBySearch")
    public ResponseResult<Page<Products>> getProductBySearch(Integer pageNum, Integer pageSize,@RequestParam("keyword") String keyword) {
        Page<Products> page = productsService.getProductListBySearch(pageNum, pageSize,keyword);
        if(page != null){
            return ResponseResult.success("获取成功",page);
        }else{
            return ResponseResult.fail("获取失败",null);
        }
    }
}
