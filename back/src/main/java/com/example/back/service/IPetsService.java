package com.example.back.service;

import com.example.back.entity.Pets;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 宠物扩展属性服务接口。
 *
 * <p>定义宠物类商品扩展属性的查询操作。</p>
 */
public interface IPetsService extends IService<Pets> {

   /**
    * 根据商品ID查询宠物扩展属性。
    *
    * @param id 商品ID
    * @return 宠物扩展属性，不存在则返回 null
    */
   public Pets getPetsByProductId(Integer id);
}
