package com.example.back.controller;


import com.example.back.Result.ResponseResult;
import com.example.back.entity.Media;
import com.example.back.service.IProductMediaService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * 商品媒体关联模块 Controller。
 *
 * <p>提供商品与媒体资源关联关系的管理能力，基础路径为 /product-media。</p>
 *

 */
@RestController
@RequestMapping("/product-media")
public class ProductMediaController {
    @Resource
    private IProductMediaService productMediaService;

    /**
     * 删除商品关联的媒体资源。
     *
     * @param media 媒体对象，需包含 mediaId 与 url
     * @return 删除是否成功
     */
    @PostMapping("/deletedMedia")
    public ResponseResult<Boolean> deletedMedia(@RequestBody Media media){
        if(productMediaService.deleteByMediaId(media.getMediaId(),media.getUrl())){
            return ResponseResult.success("删除成功",true);
        }else{
            return ResponseResult.success("删除失败",false);
        }
    }
}
