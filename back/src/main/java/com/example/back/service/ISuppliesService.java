package com.example.back.service;

import com.example.back.entity.Supplies;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用品扩展属性服务接口。
 *
 * <p>定义周边用品类商品扩展属性的查询操作。</p>
 */
public interface ISuppliesService extends IService<Supplies> {

   /**
    * 根据商品ID查询用品扩展属性。
    *
    * @param id 商品ID
    * @return 用品扩展属性，不存在则返回 null
    */
   public Supplies getSuppliesByproductId(Integer id);
}
