package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.back.entity.Pets;
import com.example.back.mapper.PetsMapper;
import com.example.back.service.IPetsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 宠物扩展属性服务实现类。
 *
 * <p>实现 {@link IPetsService} 接口，提供宠物类商品扩展属性的查询能力。</p>
 */
@Service
public class PetsServiceImpl extends ServiceImpl<PetsMapper, Pets> implements IPetsService {

    /**
     * 根据商品ID查询宠物扩展属性。
     *
     * @param id 商品ID
     * @return 宠物扩展属性，不存在则返回 null
     */
    @Override
    public Pets getPetsByProductId(Integer id) {
        return this.getOne(new QueryWrapper<Pets>().eq("product_id", id));
    }
}
