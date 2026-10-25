package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.back.entity.Supplies;
import com.example.back.mapper.SuppliesMapper;
import com.example.back.service.ISuppliesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用品扩展属性服务实现类。
 *
 * <p>实现 {@link ISuppliesService} 接口，提供周边用品类商品扩展属性的查询能力。</p>
 */
@Service
public class SuppliesServiceImpl extends ServiceImpl<SuppliesMapper, Supplies> implements ISuppliesService {

    /**
     * 根据商品ID查询用品扩展属性。
     *
     * @param id 商品ID
     * @return 用品扩展属性，不存在则返回 null
     */
    @Override
    public Supplies getSuppliesByproductId(Integer id) {
        return this.getOne(new QueryWrapper<Supplies>().eq("product_id", id));
    }

}
