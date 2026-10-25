package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.back.entity.Location;
import com.example.back.mapper.LocationMapper;
import com.example.back.service.ILocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 位置/店铺地址服务实现类。
 *
 * <p>实现 {@link ILocationService} 接口，提供店铺名称与地址的查询能力，主要用于地图选址场景。</p>
 */
@Slf4j
@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements ILocationService {

    /**
     * 获取店铺名称与地址列表。
     *
     * @return 店铺名称、地址及店铺ID列表；无数据时返回 null
     */
    //获取地址和店铺名
    public List<Location> getAddressList() {
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        // 仅查询店铺名、地址与店铺ID三个字段
        LambdaQueryWrapper<Location> select = queryWrapper.lambda().select(Location::getShopName, Location::getAddress,Location::getShopId);
        List<Location> list = this.list(select);
        log.info("查询成功:{}",list);
        if (list.size() == 0)
            return null;
        return list;
    }


}
