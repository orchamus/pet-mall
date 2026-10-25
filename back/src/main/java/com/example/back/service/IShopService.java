package com.example.back.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商店服务接口。
 *
 * <p>定义店铺入驻、信息维护、Logo 上传以及分页查询店铺列表等核心业务操作。</p>
 */
public interface IShopService extends IService<Shop> {

    /**
     * 新增店铺。
     *
     * @param shop  店铺实体
     * @param token 当前用户登录 token
     * @return 店铺是否新增成功
     */
     public boolean addShop(Shop shop, String token);

    /**
     * 根据 token 获取当前用户关联的店铺信息。
     *
     * @param token 当前用户登录 token
     * @return 店铺实体，若不存在可能返回 null
     */
     public Shop getShopByToken(String token);

    /**
     * 更新店铺信息。
     *
     * @param shop 待更新的店铺实体
     * @return 更新是否成功
     */
     public boolean updateShop(Shop shop);

    /**
     * 上传店铺 Logo 图片。
     *
     * @param file  图片文件
     * @param token 当前用户登录 token
     * @return 上传后的图片访问 URL
     */
     public String uploadShopImg(MultipartFile file,String token);

    /**
     * 分页查询店铺列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param shopName 店铺名称模糊查询关键字，可为空
     * @return 店铺分页结果
     */
     public Page<Shop> getAllShopListByPage(Integer pageNum, Integer pageSize, String shopName);
}
