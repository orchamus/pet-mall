package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.entity.Shop;
import com.example.back.entity.User;
import com.example.back.mapper.ShopMapper;
import com.example.back.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.regex.Pattern;

/**
 * 商店服务实现类。
 *
 * <p>实现 {@link IShopService} 接口，提供店铺入驻、信息维护、Logo 上传及分页查询功能。
 * 店铺 Logo 按用户名命名并覆盖保存，限制文件大小为 5MB，仅允许常见图片格式。</p>
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    // 店名正则表达式(2-30个字符，支持中英文、数字和下划线)
    private static final Pattern SHOP_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,30}$");
    // 允许的图片扩展名
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};

    @Resource
    private UserServiceImpl userService;

    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 新增店铺。
     *
     * <p>校验店铺名称、地址、描述格式，并将店铺关联到当前登录用户。</p>
     *
     * @param shop  店铺实体
     * @param token 当前用户登录 token
     * @return 店铺是否新增成功
     * @throws IllegalArgumentException 当 token 无效、用户信息无效或店铺信息不合法时抛出
     */
    @Override
    public boolean addShop(Shop shop, String token) {
        // 验证token
        if (!StringUtils.hasLength(token)) {
            throw new IllegalArgumentException("Token不能为空");
        }

        // 验证用户
        User user = (User) userService.getUserByToken(token).getData();
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户信息无效或未登录");
        }

        // 验证店铺信息
        if (shop == null) {
            throw new IllegalArgumentException("店铺信息不能为空");
        }
        if (!StringUtils.hasLength(shop.getShopName())) {
            throw new IllegalArgumentException("店铺名称不能为空");
        }
        if (!SHOP_NAME_PATTERN.matcher(shop.getShopName()).matches()) {
            throw new IllegalArgumentException("店铺名称必须是2-30个字符，支持中英文、数字和下划线");
        }
        if (!StringUtils.hasLength(shop.getAddress())) {
            throw new IllegalArgumentException("店铺地址不能为空");
        }
        if (shop.getAddress().length() > 200) {
            throw new IllegalArgumentException("店铺地址不能超过200个字符");
        }
        if (!StringUtils.hasLength(shop.getDescription()) || shop.getDescription().length() > 500) {
            throw new IllegalArgumentException("店铺描述不能为空且不能超过500个字符");
        }

        shop.setUserId(user.getId());
        shop.setStatus(1);
        return this.save(shop);
    }

    /**
     * 根据 token 获取当前用户关联的店铺信息。
     *
     * @param token 当前用户登录 token
     * @return 店铺实体，不存在则返回 null
     * @throws IllegalArgumentException 当 token 无效或用户信息无效时抛出
     */
    @Override
    public Shop getShopByToken(String token) {
        // 验证token
        if (!StringUtils.hasLength(token)) {
            throw new IllegalArgumentException("Token不能为空");
        }

        User user = (User) userService.getUserByToken(token).getData();
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户信息无效或未登录");
        }

        return this.getOne(new QueryWrapper<Shop>().eq("user_id", user.getId()));
    }

    /**
     * 分页查询店铺列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param shopName 店铺名称模糊查询关键字，可为空
     * @return 店铺分页结果
     * @throws IllegalArgumentException 当店铺名称格式不正确时抛出
     */
    @Override
    public Page<Shop> getAllShopListByPage(Integer pageNum, Integer pageSize, String shopName) {
        // 验证分页参数
        int currentPage = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        Page<Shop> page = new Page<>(currentPage, size);
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasLength(shopName)) {
            if (!SHOP_NAME_PATTERN.matcher(shopName).matches()) {
                throw new IllegalArgumentException("店铺名称格式不正确");
            }
            queryWrapper.like("shop_name", shopName);
        }

        return baseMapper.selectPage(page, queryWrapper);
    }

    /**
     * 更新店铺信息。
     *
     * @param shop 待更新的店铺实体
     * @return 更新是否成功
     * @throws IllegalArgumentException 当店铺信息不合法时抛出
     */
    @Override
    public boolean updateShop(Shop shop) {
        // 验证店铺信息
        if (shop == null) {
            throw new IllegalArgumentException("店铺信息不能为空");
        }
        if (shop.getShopId() == null) {
            throw new IllegalArgumentException("店铺ID不能为空");
        }
        if (StringUtils.hasLength(shop.getShopName()) &&
                !SHOP_NAME_PATTERN.matcher(shop.getShopName()).matches()) {
            throw new IllegalArgumentException("店铺名称必须是2-30个字符，支持中英文、数字和下划线");
        }
        if (StringUtils.hasLength(shop.getAddress()) && shop.getAddress().length() > 200) {
            throw new IllegalArgumentException("店铺地址不能超过200个字符");
        }
        if (StringUtils.hasLength(shop.getDescription()) && shop.getDescription().length() > 500) {
            throw new IllegalArgumentException("店铺描述不能超过500个字符");
        }

        return this.updateById(shop);
    }

    /**
     * 上传店铺 Logo 图片。
     *
     * <p>校验图片格式与大小（最大 5MB），保存到 uploadPath/shopLogo 目录，文件以 用户名_shopLogo 命名。</p>
     *
     * @param file  图片文件
     * @param token 当前用户登录 token
     * @return 上传后的图片访问 URL
     * @throws IllegalArgumentException 当 token 无效、文件为空、格式不支持或大小超限时抛出
     * @throws RuntimeException         当文件写入失败时抛出
     */
    @Override
    public String uploadShopImg(MultipartFile file, String token) {
        // 验证token
        if (!StringUtils.hasLength(token)) {
            throw new IllegalArgumentException("Token不能为空");
        }

        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("文件格式不支持");
        }

        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        boolean allowedExtension = false;
        for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
            if (ext.equals(fileType)) {
                allowedExtension = true;
                break;
            }
        }
        if (!allowedExtension) {
            throw new IllegalArgumentException("只支持jpg, jpeg, png, gif格式的图片");
        }

        // 验证文件大小，限制为 5MB
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("图片大小不能超过5MB");
        }

        // 获取用户信息
        User user = (User) userService.getUserByToken(token).getData();
        if (user == null || !StringUtils.hasLength(user.getUserName())) {
            throw new IllegalArgumentException("用户信息无效");
        }

        String logoName = user.getUserName() + "_shopLogo";
        String filePath = uploadPath + File.separator + "shopLogo" + File.separator + logoName + "." + fileType;
        File destFile = new File(filePath);

        try {
            // 确保目录存在
            destFile.getParentFile().mkdirs();
            file.transferTo(destFile);
            return "/media/shopLogo/" + logoName + "." + fileType;
        } catch (Exception e) {
            throw new RuntimeException("店铺图片上传失败", e);
        }
    }
}
