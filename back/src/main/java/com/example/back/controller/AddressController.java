package com.example.back.controller;


import  com.example.back.Result.ResponseResult;
import com.example.back.config.ExceptionConfig.BusinessException;
import com.example.back.entity.Address;
import com.example.back.service.impl.AddressServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址模块 Controller。
 *
 * <p>提供用户收货地址的增删改查及默认地址设置能力，基础路径为 /address。</p>
 *

 */
@RestController
@RequestMapping("/address")
public class AddressController {
    @Resource
    private AddressServiceImpl addressService;

    /**
     * 新增收货地址。
     *
     * @param address 地址对象
     * @param token   登录凭证（请求头 satoken）
     * @return 添加是否成功
     */
    @PostMapping("/add")
    public ResponseResult<Boolean> add(@RequestBody Address address, @RequestHeader("satoken") String token) {
        System.out.println(address);
        if(addressService.addAddress(token,address)){
            return ResponseResult.success("添加成功",true);
        }else{
            return ResponseResult.fail("添加失败",false);
        }
    }

    /**
     * 查询当前登录用户的收货地址列表。
     *
     * @param token 登录凭证（请求头 satoken）
     * @return 地址集合
     */
    @GetMapping("/getAddressesList")
    public ResponseResult<List<Address>> getAddresses(@RequestHeader("satoken") String token) {
        try {
        return ResponseResult.success("获取成功",addressService.getAddress(token));
        }catch (BusinessException e) {
            return ResponseResult.fail(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.fail("获取失败");
        }
    }

    /**
     * 删除收货地址。
     *
     * @param addressId 地址 ID
     * @return 删除是否成功
     */
    @PostMapping("/DeleteAddress")
    public ResponseResult<Boolean> deleteAddress(@RequestParam("id") Integer addressId){
        if(addressService.deleteAddress(addressId)){
            return ResponseResult.success("删除成功",true);
        }else{
            return ResponseResult.fail("删除失败",false);
        }
    }

    /**
     * 更新收货地址。
     *
     * @param address 地址对象
     * @param token   登录凭证（请求头 satoken）
     * @return 更新是否成功
     */
    @PostMapping("/updateAddress")
    public ResponseResult<Boolean> updateAddress(@RequestBody Address address,@RequestHeader("satoken") String token){
       if(addressService.updateAddress(token,address)) {
           return ResponseResult.success("修改成功", true);
       }
        else {
           return ResponseResult.fail("修改失败", false);
       }
    }

    /**
     * 设置默认收货地址。
     *
     * @param addressId 地址 ID
     * @param token     登录凭证（请求头 satoken）
     * @return 设置是否成功
     */
    @PostMapping("/setDefaultAddress")
    public ResponseResult<Boolean> setDefaultAddress(@RequestParam("id") Integer addressId,@RequestHeader("satoken") String token){
        if(addressService.setDefaultAddress(token,addressId)){
            return ResponseResult.success("设置成功",true);
        }else{
            return ResponseResult.fail("设置失败",false);
        }
    }

    /**
     * 根据 ID 查询单个收货地址。
     *
     * @param addressId 地址 ID
     * @return 地址对象
     */
    @GetMapping("/getAddressById")
    public ResponseResult<Address> getOneAddress(Integer addressId){
        Address address = addressService.getById(addressId);
        return ResponseResult.success("获取成功",address);
    }
}
