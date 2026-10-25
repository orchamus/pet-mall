package com.example.back.service;

import com.example.back.entity.Address;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 收货地址服务接口。
 *
 * <p>定义用户收货地址的查询、新增、修改、删除及默认地址设置等操作。</p>
 */
public interface IAddressService extends IService<Address> {

      /**
       * 根据 token 获取当前用户的收货地址列表。
       *
       * @param token 当前用户登录 token
       * @return 收货地址列表
       */
      public List<Address> getAddress(String token);

      /**
       * 为当前用户新增收货地址。
       *
       * @param token   当前用户登录 token
       * @param address 待新增的地址实体
       * @return 新增是否成功
       */
      public boolean addAddress(String token, Address address);

      /**
       * 修改当前用户的收货地址。
       *
       * @param token   当前用户登录 token
       * @param address 待更新的地址实体
       * @return 修改是否成功
       */
      public boolean updateAddress(String token, Address address);

      /**
       * 根据地址ID删除收货地址。
       *
       * @param addressId 地址ID
       * @return 删除是否成功
       */
      public boolean deleteAddress(Integer addressId);

      /**
       * 设置当前用户的默认收货地址。
       *
       * @param token     当前用户登录 token
       * @param addressId 待设为默认的地址ID
       * @return 设置是否成功
       */
      public boolean setDefaultAddress(String token, Integer addressId);
}
