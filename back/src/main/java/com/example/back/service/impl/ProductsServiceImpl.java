package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.entity.*;
import com.example.back.entity.VO.showAProduct;
import com.example.back.entity.VO.showShopAndProducts;
import com.example.back.mapper.ProductsMapper;
import com.example.back.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.back.config.ExceptionConfig.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.UUID;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 商品服务实现类。
 *
 * <p>实现 {@link IProductsService} 接口，提供商品发布、编辑、上下架、删除、图片上传、分页查询及店铺商品聚合查询等功能。
 * 商品类别 category=0 表示宠物，category=1 表示用品；商品图片先上传至临时目录，商品保存时转移至正式目录。</p>
 */
@Service
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products> implements IProductsService {
    // 商品名称正则表达式(2-50个字符，支持中英文、数字和常见标点)
    private static final Pattern PRODUCT_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-,.()]{2,50}$");
    // 允许的图片扩展名
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};
    // 最大文件大小(5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Resource
    private IShopService shopService;
    @Resource
    private IProductMediaService productMediaService;
    @Resource
    private IMediaService mediaService;
    @Resource
    private IPetsService petsService;
    @Resource
    private ISuppliesService suppliesService;

    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 根据 token 获取当前店铺的商品列表。
     *
     * @param token 当前用户登录 token
     * @return 商品列表
     * @throws BusinessException 当 token 无效或店铺信息无效时抛出
     */
    @Override
    public List<Products> getProductListByToken(String token) {
        // 验证token
        if (StringUtils.isBlank(token)) {
            throw new BusinessException("Token不能为空");
        }

        // 获取店铺信息
        Shop shop = shopService.getShopByToken(token);
        if (shop == null || shop.getShopId() == null) {
            throw new BusinessException("店铺信息无效");
        }

        List<Products> productsList = this.list(new QueryWrapper<Products>().eq("shop_id", shop.getShopId()));
        if (productsList != null && !productsList.isEmpty()) {
            // 为每个商品填充扩展属性与媒体信息
            for (Products product : productsList) {
                enrichProductDetails(product);
            }
        }
        return productsList;
    }

    /**
     * 新增商品。
     *
     * <p>业务规则：</p>
     * <ol>
     *   <li>校验商品基本信息与媒体信息；</li>
     *   <li>通过 token 获取店铺并设置商品归属；</li>
     *   <li>保存商品后处理媒体文件（临时文件转正）并保存扩展属性。</li>
     * </ol>
     *
     * @param products 商品实体
     * @param token    当前用户登录 token
     * @return 商品是否新增成功
     * @throws BusinessException 当商品信息不合法、店铺无效或保存失败时抛出
     */
    @Override
    public boolean addProduct(Products products, String token) {
        // 验证token
        if (StringUtils.isBlank(token)) {
            throw new BusinessException("Token不能为空");
        }

        // 验证商品基本信息
        validateProduct(products);

        // 验证商品媒体
        if (products.getProductMedias() == null || products.getProductMedias().isEmpty()) {
            throw new BusinessException("商品至少需要一张图片");
        }

        // 获取店铺信息
        Shop shop = shopService.getShopByToken(token);
        if (shop == null || shop.getShopId() == null) {
            throw new BusinessException("店铺信息无效");
        }

        try {
            // 默认商品状态为下架/待审核（status=0）
            products.setStatus("0");
            products.setShopId(shop.getShopId());

            // 保存商品基本信息
            if (!this.save(products)) {
                throw new BusinessException("商品保存失败");
            }

            // 处理商品媒体：临时文件转正并保存关联
            for (ProductMedia productMedia : products.getProductMedias()) {
                processProductMedia(productMedia, products.getProductId());
            }

            // 根据商品类别保存扩展属性
            saveProductDetails(products);

            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("添加商品失败: " + e.getMessage());
        }
    }

    /**
     * 临时上传商品图片文件。
     *
     * <p>校验文件格式与大小后，将文件保存到 uploadPath/temporary 目录，返回临时访问 URL。</p>
     *
     * @param file 图片文件
     * @return 临时文件访问 URL
     * @throws BusinessException 当文件为空、格式不支持或上传失败时抛出
     */
    @Override
    public String uploadFileTemporary(MultipartFile file) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("文件格式不支持");
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
            throw new BusinessException("只支持jpg, jpeg, png, gif格式的图片");
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("图片大小不能超过5MB");
        }

        // 生成 UUID 文件名并保存到临时目录
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String filePath = uploadPath + File.separator + "temporary" + File.separator + uuid + "." + fileType;
        File destFile = new File(filePath);

        try {
            // 确保目录存在
            destFile.getParentFile().mkdirs();
            file.transferTo(destFile);
            return "/media/temporary/" + uuid + "." + fileType;
        } catch (Exception e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 确认并转移临时文件到正式商品媒体目录。
     *
     * @param url 临时文件 URL
     * @return 文件转移是否成功
     * @throws BusinessException 当 URL 为空、临时文件不存在或转移失败时抛出
     */
    @Override
    public boolean confirmFiles(String url) {
        if (StringUtils.isBlank(url)) {
            throw new BusinessException("文件URL不能为空");
        }

        // 从 URL 中提取文件名
        String fileName = url.replace("/media/temporary/", "");
        String filePath = uploadPath + File.separator + "temporary" + File.separator + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            throw new BusinessException("临时文件不存在");
        }

        String newFilePath = uploadPath + File.separator + "productMedia" + File.separator + fileName;
        File newFile = new File(newFilePath);

        try {
            // 确保目标目录存在
            newFile.getParentFile().mkdirs();
            return file.renameTo(newFile);
        } catch (Exception e) {
            throw new BusinessException("文件转移失败: " + e.getMessage());
        }
    }

    /**
     * 更新商品信息。
     *
     * <p>仅允许更新属于当前店铺的商品；新增媒体文件时仅处理尚未保存的媒体。</p>
     *
     * @param product 待更新的商品实体
     * @param token   当前用户登录 token
     * @return 更新是否成功
     * @throws BusinessException 当商品信息不合法、无权修改或保存失败时抛出
     */
    @Override
    public boolean updateProduct(Products product, String token) {
        // 验证token
        if (StringUtils.isBlank(token)) {
            throw new BusinessException("Token不能为空");
        }

        // 验证商品ID
        if (product == null || product.getProductId() == null) {
            throw new BusinessException("商品ID不能为空");
        }

        // 验证商品基本信息
        validateProduct(product);

        // 获取店铺信息
        Shop shop = shopService.getShopByToken(token);
        if (shop == null || shop.getShopId() == null) {
            throw new BusinessException("店铺信息无效");
        }

        // 验证商品是否属于该店铺
        Products existingProduct = this.getById(product.getProductId());
        if (existingProduct == null || !existingProduct.getShopId().equals(shop.getShopId())) {
            throw new BusinessException("无权修改此商品");
        }

        try {
            product.setShopId(shop.getShopId());

            // 更新商品基本信息
            if (!this.updateById(product)) {
                throw new BusinessException("商品更新失败");
            }

            // 处理新增的商品媒体，已存在的媒体不重复处理
            if (product.getProductMedias() != null) {
                for (ProductMedia productMedia : product.getProductMedias()) {
                    if (productMediaService.getOne(new QueryWrapper<ProductMedia>()
                            .eq("media_id", productMedia.getMediaId())) == null) {
                        processProductMedia(productMedia, product.getProductId());
                    }
                }
            }

            // 根据商品类别更新扩展属性
            updateProductDetails(product);

            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("更新商品失败: " + e.getMessage());
        }
    }

    /**
     * 根据商品ID删除商品。
     *
     * <p>级联删除商品媒体文件、媒体关联记录及商品扩展属性。</p>
     *
     * @param productId 商品ID
     * @return 删除是否成功
     * @throws BusinessException 当商品ID无效、商品不存在或删除失败时抛出
     */
    @Override
    public boolean deleteProductById(Integer productId) {
        // 验证商品ID
        if (productId == null || productId <= 0) {
            throw new BusinessException("商品ID无效");
        }

        Products product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        try {
            // 删除商品媒体文件及关联记录
            List<ProductMedia> productMediaList = productMediaService.getListByProductId(productId);
            if (productMediaList != null && !productMediaList.isEmpty()) {
                for (ProductMedia productMedia : productMediaList) {
                    productMediaService.deleteByMediaId(productMedia.getMediaId(), productMedia.getMedia().getUrl());
                }
            }

            // 根据商品类别删除扩展属性
            if ("0".equals(product.getCategory())) {
                petsService.remove(new QueryWrapper<Pets>().eq("product_id", productId));
            } else if ("1".equals(product.getCategory())) {
                suppliesService.remove(new QueryWrapper<Supplies>().eq("product_id", productId));
            }

            // 删除商品媒体关联
            productMediaService.remove(new QueryWrapper<ProductMedia>().eq("product_id", productId));

            // 删除商品基本信息
            return this.removeById(productId);
        } catch (Exception e) {
            throw new BusinessException("删除商品失败: " + e.getMessage());
        }
    }

    /**
     * 切换商品上下架状态。
     *
     * <p>status=0 与 status=1 之间切换；其他状态视为异常。</p>
     *
     * @param productId 商品ID
     * @return 状态切换是否成功
     * @throws BusinessException 当商品ID无效、商品不存在或状态异常时抛出
     */
    @Override
    public boolean changStatus(Integer productId) {
        // 验证商品ID
        if (productId == null || productId <= 0) {
            throw new BusinessException("商品ID无效");
        }

        Products product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 在 0 与 1 之间切换状态
        if ("0".equals(product.getStatus())) {
            product.setStatus("1");
        } else if ("1".equals(product.getStatus())) {
            product.setStatus("0");
        } else {
            throw new BusinessException("商品状态异常");
        }

        return this.updateById(product);
    }

    /**
     * 分页查询商品列表。
     *
     * <p>默认仅查询已上架（status=0）的商品；若传入 category 则按类别过滤。</p>
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param category 商品类别，可为空
     * @return 商品分页结果
     * @throws BusinessException 当类别参数无效时抛出
     */
    @Override
    public Page<Products> getAllProductListByPage(Integer pageNum, Integer pageSize, String category) {
        // 验证分页参数
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<Products> page = new Page<>(pageNum, pageSize);
        // 仅查询已上架商品
        QueryWrapper<Products> queryWrapper = new QueryWrapper<Products>().eq("status", "0");

        if (StringUtils.isNotBlank(category)) {
            if (!"0".equals(category) && !"1".equals(category)) {
                throw new BusinessException("商品类别参数无效");
            }
            queryWrapper.eq("category", category);
        }

        page = this.page(page, queryWrapper);
        return getProductsPageMedia(page);
    }

    /**
     * 根据商品ID查询商品详情（含店铺信息）。
     *
     * @param productId 商品ID
     * @return 商品详情视图
     * @throws BusinessException 当商品ID无效、商品不存在或店铺信息不存在时抛出
     */
    @Override
    public showAProduct getAProductById(Integer productId) {
        // 验证商品ID
        if (productId == null || productId <= 0) {
            throw new BusinessException("商品ID无效");
        }

        Products product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 填充扩展属性与媒体信息
        enrichProductDetails(product);

        showAProduct showAProduct = new showAProduct();
        showAProduct.setProduct(product);

        // 获取店铺信息
        Shop shop = shopService.getById(product.getShopId());
        if (shop == null) {
            throw new BusinessException("店铺信息不存在");
        }
        showAProduct.setShop(shop);

        return showAProduct;
    }

    /**
     * 根据关键字分页搜索商品。
     *
     * <p>默认仅查询已上架（status=0）的商品；关键字同时匹配商品名称与描述。</p>
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param keyword  搜索关键字，可为空
     * @return 商品分页结果
     */
    @Override
    public Page<Products> getProductListBySearch(Integer pageNum, Integer pageSize, String keyword) {
        // 验证分页参数
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Page<Products> page = new Page<>(pageNum, pageSize);
        // 仅查询已上架商品
        QueryWrapper<Products> queryWrapper = new QueryWrapper<Products>().eq("status", "0");

        // 如果有关键词，添加搜索条件
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like("name", keyword)
                    .or()
                    .like("description", keyword));
        }

        page = this.page(page, queryWrapper);
        return getProductsPageMedia(page);
    }

    /**
     * 根据店铺ID查询店铺及其商品分页列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param shopId   店铺ID
     * @return 店铺与商品分页视图
     * @throws BusinessException 当店铺ID无效、分页参数无效或店铺不存在时抛出
     */
    @Override
    public showShopAndProducts getShopAndProductsByShopId(Integer pageNum, Integer pageSize, Integer shopId) {
        // 验证店铺ID
        if (shopId == null || shopId <= 0) {
            throw new BusinessException("店铺ID无效");
        }

        // 验证分页参数
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        // 获取店铺信息
        Shop shop = shopService.getById(shopId);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }

        Page<Products> page = new Page<>(pageNum, pageSize);
        // 查询店铺下已上架的商品
        page = this.page(page, new QueryWrapper<Products>()
                .eq("shop_id", shopId)
                .eq("status", "0"));

        page = this.getProductsPageMedia(page);

        showShopAndProducts result = new showShopAndProducts();
        result.setShop(shop);
        result.setProducts(page);

        return result;
    }

    /**
     * 辅助方法：验证商品基本信息。
     *
     * @param product 商品实体
     * @throws BusinessException 当商品信息不合法时抛出
     */
    // 辅助方法：验证商品基本信息
    private void validateProduct(Products product) {
        if (product == null) {
            throw new BusinessException("商品信息不能为空");
        }
        if (StringUtils.isBlank(product.getName())) {
            throw new BusinessException("商品名称不能为空");
        }
        if (!PRODUCT_NAME_PATTERN.matcher(product.getName()).matches()) {
            throw new BusinessException("商品名称必须是2-50个字符，支持中英文、数字和常见标点");
        }
        if (StringUtils.isBlank(product.getDescription())) {
            throw new BusinessException("商品描述不能为空");
        }
        if (product.getDescription().length() > 1000) {
            throw new BusinessException("商品描述不能超过1000个字符");
        }
        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            throw new BusinessException("商品库存不能为负数");
        }
        if (StringUtils.isBlank(product.getCategory())) {
            throw new BusinessException("商品类别不能为空");
        }
        if (!"0".equals(product.getCategory()) && !"1".equals(product.getCategory())) {
            throw new BusinessException("商品类别参数无效");
        }
    }

    /**
     * 辅助方法：处理商品媒体。
     *
     * <p>将临时目录中的文件转移到正式目录，保存 media 记录并建立 product_media 关联。</p>
     *
     * @param productMedia 商品媒体关联实体
     * @param productId    商品ID
     * @throws BusinessException 当媒体信息不完整、文件转移失败或保存失败时抛出
     */
    // 辅助方法：处理商品媒体
    private void processProductMedia(ProductMedia productMedia, Integer productId) {
        if (productMedia == null || productMedia.getMedia() == null) {
            throw new BusinessException("商品媒体信息不完整");
        }
        if (StringUtils.isBlank(productMedia.getMedia().getUrl())) {
            throw new BusinessException("媒体URL不能为空");
        }

        // 确认并转移临时文件
        if (!confirmFiles(productMedia.getMedia().getUrl())) {
            throw new BusinessException("文件确认失败");
        }

        String fileName = productMedia.getMedia().getUrl().replace("/media/temporary/", "");
        productMedia.getMedia().setUrl("/media/productMedia/" + fileName);

        if (!mediaService.save(productMedia.getMedia())) {
            throw new BusinessException("媒体保存失败");
        }

        productMedia.setProductId(productId);
        productMedia.setMediaId(productMedia.getMedia().getMediaId());

        if (!productMediaService.save(productMedia)) {
            throw new BusinessException("商品媒体关联保存失败");
        }
    }

    /**
     * 辅助方法：保存商品扩展属性。
     *
     * @param product 商品实体
     * @throws BusinessException 当扩展属性缺失或保存失败时抛出
     */
    // 辅助方法：保存商品详细信息
    private void saveProductDetails(Products product) {
        if ("0".equals(product.getCategory())) {
            if (product.getPets() == null) {
                throw new BusinessException("宠物详细信息不能为空");
            }
            product.getPets().setProductId(product.getProductId());
            petsService.save(product.getPets());
        } else if ("1".equals(product.getCategory())) {
            if (product.getSupplies() == null) {
                throw new BusinessException("用品详细信息不能为空");
            }
            product.getSupplies().setProductId(product.getProductId());
            suppliesService.save(product.getSupplies());
        }
    }

    /**
     * 辅助方法：更新商品扩展属性。
     *
     * @param product 商品实体
     * @throws BusinessException 当扩展属性缺失或更新失败时抛出
     */
    // 辅助方法：更新商品详细信息
    private void updateProductDetails(Products product) {
        if ("0".equals(product.getCategory())) {
            if (product.getPets() == null) {
                throw new BusinessException("宠物详细信息不能为空");
            }
            product.getPets().setProductId(product.getProductId());
            petsService.updateById(product.getPets());
        } else if ("1".equals(product.getCategory())) {
            if (product.getSupplies() == null) {
                throw new BusinessException("用品详细信息不能为空");
            }
            product.getSupplies().setProductId(product.getProductId());
            suppliesService.updateById(product.getSupplies());
        }
    }

    /**
     * 辅助方法：丰富商品详情。
     *
     * <p>为商品填充宠物/用品扩展属性及全部媒体信息。</p>
     *
     * @param product 商品实体
     */
    // 辅助方法：丰富商品详情
    private void enrichProductDetails(Products product) {
        if ("0".equals(product.getCategory())) {
            product.setPets(petsService.getPetsByProductId(product.getProductId()));
        } else if ("1".equals(product.getCategory())) {
            product.setSupplies(suppliesService.getSuppliesByproductId(product.getProductId()));
        }

        List<ProductMedia> productMedias = productMediaService.getListByProductId(product.getProductId());
        if (productMedias != null) {
            for (ProductMedia productMedia : productMedias) {
                Media media = mediaService.getById(productMedia.getMediaId());
                productMedia.setMedia(media);
            }
        }
        product.setProductMedias(productMedias);
    }

    /**
     * 辅助方法：获取带媒体信息的商品分页。
     *
     * <p>为分页结果中的每个商品填充扩展属性与封面图（media_type=0）。</p>
     *
     * @param page 商品分页对象
     * @return 填充后的商品分页对象
     */
    // 辅助方法：获取带媒体信息的商品分页
    private Page<Products> getProductsPageMedia(Page<Products> page) {
        for (Products product : page.getRecords()) {
            if ("0".equals(product.getCategory())) {
                product.setPets(petsService.getPetsByProductId(product.getProductId()));
            } else if ("1".equals(product.getCategory())) {
                product.setSupplies(suppliesService.getSuppliesByproductId(product.getProductId()));
            }

            // 仅填充封面图（media_type=0）
            List<ProductMedia> productMedias = new ArrayList<>();
            ProductMedia productMedia = productMediaService.getOne(new QueryWrapper<ProductMedia>()
                    .eq("product_id", product.getProductId())
                    .eq("media_type", "0"));

            if (productMedia != null) {
                Media media = mediaService.getById(productMedia.getMediaId());
                productMedia.setMedia(media);
                productMedias.add(productMedia);
            }
            product.setProductMedias(productMedias);
        }
        return page;
    }
}
