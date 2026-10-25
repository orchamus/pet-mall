package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.back.config.ExceptionConfig.BusinessException;
import com.example.back.entity.Address;
import com.example.back.entity.User;
import com.example.back.mapper.AddressMapper;
import com.example.back.service.IAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 收货地址服务实现类。
 *
 * <p>实现 {@link IAddressService} 接口，提供收货地址的查询、新增、修改、删除及默认地址设置功能。
 * 默认地址逻辑保证同一用户下最多只有一条默认地址。</p>
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

    @Resource
    private UserServiceImpl userService;

    /**
     * 根据 token 获取当前用户的收货地址列表。
     *
     * @param token 当前用户登录 token
     * @return 收货地址列表
     * @throws BusinessException 当 token 无效、用户信息无效或用户无权限时抛出
     */
    @Override
    public List<Address> getAddress(String token) {
        // 验证token
        if (!StringUtils.hasLength(token)) {
            throw new BusinessException("Token不能为空");
        }

        // 获取并验证用户信息
        User user = (User) userService.getUserByToken(token).getData();
        if (user == null || user.getId() == null) {
            throw new BusinessException("用户信息无效或未登录");
        }
        // 仅普通用户可操作收货地址
        if (!"0".equals(user.getUserType())) {
            throw new BusinessException("你没有权限这么做");
        }

        return this.list(new QueryWrapper<Address>().eq("user_id", user.getId()));
    }

    /**
     * 为当前用户新增收货地址。
     *
     * <p>若地址设置为默认，则先取消该用户下其他默认地址，保证默认地址唯一性。</p>
     *
     * @param token   当前用户登录 token
     * @param address 待新增的地址实体
     * @return 新增是否成功
     * @throws BusinessException 当 token 无效或用户信息无效时抛出
     */
    @Override
    public boolean addAddress(String token, Address address) {
        // 验证token
        if (!StringUtils.hasLength(token)) {
            throw new BusinessException("Token不能为空");
        }

        // 获取并验证用户信息
        User user = (User) userService.getUserByToken(token).getData();
        if (user == null || user.getId() == null) {
            throw new BusinessException("用户信息无效或未登录");
        }

        address.setUserId(user.getId());
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            // 用户选择设为默认，先取消该用户下其他默认地址
            QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", user.getId())
                    .eq("is_default", 1);
            Address currentDefaultAddress = this.getOne(queryWrapper);
            if (currentDefaultAddress != null) {
                currentDefaultAddress.setIsDefault(0);
                this.updateById(currentDefaultAddress);
            }
        } else {
            address.setIsDefault(0);
        }
        return this.save(address);
    }

    /**
     * 修改当前用户的收货地址。
     *
     * @param token   当前用户登录 token
     * @param address 待更新的地址实体
     * @return 修改是否成功
     * @throws BusinessException 当 token 无效、地址ID为空或地址不属于当前用户时抛出
     */
    @Override
    public boolean updateAddress(String token, Address address) {
        // 验证token
        if (!StringUtils.hasLength(token)) {
            throw new BusinessException("Token不能为空");
        }

        // 获取并验证用户信息
        User user = (User) userService.getUserByToken(token).getData();
        if (user == null || user.getId() == null) {
            throw new BusinessException("用户信息无效或未登录");
        }

        // 验证地址ID
        if (address == null || address.getAddressId() == null) {
            throw new BusinessException("地址ID不能为空");
        }

        // 验证地址是否属于当前用户
        Address existingAddress = this.getById(address.getAddressId());
        if (existingAddress == null || !existingAddress.getUserId().equals(user.getId())) {
            throw new BusinessException("无权修改此地址");
        }

        address.setUserId(user.getId());
        return this.updateById(address);
    }

    /**
     * 根据地址ID删除收货地址。
     *
     * @param addressId 地址ID
     * @return 删除是否成功
     * @throws BusinessException 当地址ID无效时抛出
     */
    @Override
    public boolean deleteAddress(Integer addressId) {
        // 验证地址ID
        if (addressId == null || addressId <= 0) {
            throw new BusinessException("地址ID无效");
        }

        return this.removeById(addressId);
    }

    /**
     * 设置当前用户的默认收货地址。
     *
     * <p>先将该用户下已有的默认地址取消默认状态，再将指定地址设为默认，确保同一用户默认地址唯一。</p>
     *
     * @param token     当前用户登录 token
     * @param addressId 待设为默认的地址ID
     * @return 设置是否成功
     * @throws BusinessException 当 token 无效、地址ID无效、地址不存在或无权操作时抛出
     */
    @Override
    public boolean setDefaultAddress(String token, Integer addressId) {
        // 验证token
        if (!StringUtils.hasLength(token)) {
            throw new BusinessException("Token不能为空");
        }

        // 验证地址ID
        if (addressId == null || addressId <= 0) {
            throw new BusinessException("地址ID无效");
        }

        // 获取并验证用户信息
        User user = (User) userService.getUserByToken(token).getData();
        if (user == null || user.getId() == null) {
            throw new BusinessException("用户信息无效或未登录");
        }

        // 验证要设置的地址是否存在且属于当前用户
        Address address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(user.getId())) {
            throw new BusinessException("地址不存在或无权操作");
        }

        // 查找当前默认地址
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId())
                .eq("is_default", 1);

        Address currentDefaultAddress = this.getOne(queryWrapper);

        // 更新地址状态
        address.setIsDefault(1);

        if (currentDefaultAddress != null) {
            // 如果已有默认地址，先取消默认状态
            currentDefaultAddress.setIsDefault(0);
            return this.updateById(currentDefaultAddress) && this.updateById(address);
        } else {
            // 如果没有默认地址，直接设置
            return this.updateById(address);
        }
    }


}
