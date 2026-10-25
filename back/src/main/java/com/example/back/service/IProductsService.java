package com.example.back.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.entity.Products;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.back.entity.VO.showAProduct;
import com.example.back.entity.VO.showShopAndProducts;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商品服务接口。
 *
 * <p>定义商品发布、编辑、上下架、删除、图片上传、分页查询以及店铺商品聚合查询等核心业务操作。</p>
 */
public interface IProductsService extends IService<Products> {

    /**
     * 根据 token 获取当前店铺的商品列表。
     *
     * @param token 当前用户登录 token
     * @return 商品列表
     */
    public List<Products> getProductListByToken(String token);

    /**
     * 新增商品。
     *
     * @param products 商品实体
     * @param token    当前用户登录 token
     * @return 商品是否新增成功
     */
    public boolean addProduct(Products products,String token);

    /**
     * 临时上传商品图片文件。
     *
     * @param file 图片文件
     * @return 临时文件访问 URL
     */
    public String uploadFileTemporary(MultipartFile file);

    /**
     * 确认并转移临时文件到正式商品媒体目录。
     *
     * @param url 临时文件 URL
     * @return 文件转移是否成功
     */
    public boolean confirmFiles(String url);

    /**
     * 更新商品信息。
     *
     * @param product 待更新的商品实体
     * @param token   当前用户登录 token
     * @return 更新是否成功
     */
    public boolean updateProduct(Products product,String token);

    /**
     * 根据商品ID删除商品。
     *
     * @param productId 商品ID
     * @return 删除是否成功
     */
    public boolean deleteProductById(Integer productId);

    /**
     * 切换商品上下架状态。
     *
     * @param productId 商品ID
     * @return 状态切换是否成功
     */
    public boolean changStatus(Integer productId);

    /**
     * 分页查询商品列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param category 商品类别，可为空
     * @return 商品分页结果
     */
    public Page<Products> getAllProductListByPage(Integer pageNum, Integer pageSize,String category);

    /**
     * 根据商品ID查询商品详情（含店铺信息）。
     *
     * @param productId 商品ID
     * @return 商品详情视图
     */
    public showAProduct getAProductById(Integer productId);

    /**
     * 根据关键字分页搜索商品。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param keyword  搜索关键字，可为空
     * @return 商品分页结果
     */
    public Page<Products> getProductListBySearch(Integer pageNum, Integer pageSize,String keyword);

    /**
     * 根据店铺ID查询店铺及其商品分页列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param shopId   店铺ID
     * @return 店铺与商品分页视图
     */
    public showShopAndProducts getShopAndProductsByShopId(Integer pageNum, Integer pageSize, Integer shopId);
}
