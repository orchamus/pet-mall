package com.example.back.controller;

import com.example.back.entity.Location;
import com.example.back.utils.Result;

import com.example.back.service.impl.LocationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 店铺位置模块 Controller。
 *
 * <p>提供店铺位置地址列表查询能力，基础路径为 /shopLocation。</p>
 *

 */
@RestController
@RequestMapping("/shopLocation")
public class LocationController {
    @Autowired
    private LocationServiceImpl LocationService;

    /**
     * 获取店铺地址列表。
     *
     * @return 地址集合
     */
    @GetMapping("/getAddress")
    public Result<List<Location>> getAddress(){
        Result<List<Location>> result = new Result<>();
        List<Location> LocationList=  LocationService.getAddressList();
        if (LocationList == null) {
            result.fail("查询失败");
        }
        result.setData(LocationList);
        return result;
    }
}
