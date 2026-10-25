package com.example.back.service;

import com.example.back.entity.ProductMedia;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品媒体关联服务接口。
 *
 * <p>定义商品与媒体资源关联关系的查询、删除以及封面图查询等操作。</p>
 */
public interface IProductMediaService extends IService<ProductMedia> {

    /**
     * 根据商品ID查询全部媒体关联记录。
     *
     * @param productId 商品ID
     * @return 媒体关联列表
     */
     public List<ProductMedia> getListByProductId(Integer productId);

    /**
     * 根据媒体ID删除关联记录及对应的物理文件。
     *
     * @param mediaId 媒体ID
     * @param url     媒体文件访问 URL
     * @return 删除是否成功
     */
     public boolean deleteByMediaId(Integer mediaId,String url);

    /**
     * 根据商品ID查询封面图关联记录。
     *
     * @param productId 商品ID
     * @return 封面图媒体关联列表
     */
     public List<ProductMedia> findCovetByProductId(Integer productId);

}
