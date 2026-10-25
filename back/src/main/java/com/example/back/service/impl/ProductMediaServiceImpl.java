package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.back.entity.ProductMedia;
import com.example.back.mapper.ProductMediaMapper;
import com.example.back.service.IMediaService;
import com.example.back.service.IProductMediaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品媒体关联服务实现类。
 *
 * <p>实现 {@link IProductMediaService} 接口，提供商品媒体关联查询、物理文件删除及封面图查询能力。
 * 删除媒体时会同步删除磁盘物理文件、商品媒体关联记录及 media 表记录。</p>
 */
@Service
public class ProductMediaServiceImpl extends ServiceImpl<ProductMediaMapper, ProductMedia> implements IProductMediaService {
    @Resource
    private IMediaService mediaService;
    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 根据商品ID查询全部媒体关联记录。
     *
     * <p>查询结果为空时返回 null；不为空时为每条记录填充关联的 Media 实体。</p>
     *
     * @param productId 商品ID
     * @return 媒体关联列表，无记录时返回 null
     */
    @Override
    public List<ProductMedia> getListByProductId(Integer productId) {
        List<ProductMedia> productMedia = this.list(new QueryWrapper<ProductMedia>().eq("product_id", productId));
        if (productMedia.size() == 0) {
            return null;
        }

        // 填充每条媒体关联对应的 Media 实体
        for (ProductMedia pm : productMedia) {
            pm.setMedia(mediaService.getById(pm.getMediaId()));
        }

        return productMedia;
    }

    /**
     * 根据媒体ID删除关联记录及对应的物理文件。
     *
     * <p>先根据 URL 拼接物理路径并删除文件，文件删除成功后再删除 product_media 与 media 表记录。</p>
     *
     * @param mediaId 媒体ID
     * @param url     媒体文件访问 URL
     * @return 删除是否成功
     */
    @Override
    public boolean deleteByMediaId(Integer mediaId, String url) {
        // 从 URL 中提取文件名并定位物理路径
        String fileName = url.replace("/media/productMedia/", "");
        String filePath = uploadPath + File.separator + "productMedia" + File.separator + fileName;
        File destFile = new File(filePath);
        if (!destFile.exists()) {
            System.out.println("文件不存在: " + filePath);
            return false;
        }
        // 尝试删除物理文件
        try {
            if (destFile.delete()) {
                System.out.println("文件删除成功: " + filePath);
                // 物理文件删除成功后，再删除关联记录与媒体表记录
                return true && this.remove(new QueryWrapper<ProductMedia>().eq("media_id", mediaId)) && mediaService.removeById(mediaId);
            } else {
                System.out.println("文件删除失败: " + filePath);
                return false;
            }
        } catch (SecurityException e) {
            System.out.println("删除文件时发生安全异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 根据商品ID查询封面图关联记录。
     *
     * <p>media_type=0 表示封面图，查询结果填充 Media 实体后返回单条记录的列表。</p>
     *
     * @param productId 商品ID
     * @return 封面图媒体关联列表
     */
    @Override
    public List<ProductMedia> findCovetByProductId(Integer productId) {
        List<ProductMedia> productMediaList = new ArrayList<>();
        // media_type=0 表示封面图
        ProductMedia productMedia = this.getOne(new QueryWrapper<ProductMedia>().eq("product_id", productId).eq("media_type", "0"));
        productMedia.setMedia(mediaService.getById(productMedia.getMediaId()));
        productMediaList.add(productMedia);
        return productMediaList;
    }
}
