package com.example.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.Result.ResponseResult;
import com.example.back.config.ExceptionConfig.BusinessException;
import com.example.back.entity.*;
import com.example.back.entity.DTO.orderRequestBycart;
import com.example.back.mapper.OrdersMapper;
import com.example.back.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现类。
 *
 * <p>实现 {@link IOrdersService} 接口，覆盖立即购买、购物车下单、订单查询、订单状态流转、退款申请与审批等核心业务。
 * 下单操作使用 {@link Transactional} 保证库存扣减与订单创建的一致性；退款流程通过记录原订单状态实现审批后的状态回退。</p>
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {
    @Resource
    private IUserService userService;
    @Resource
    private IProductsService productsService;
    @Resource
    private IProductMediaService productMediaService;
    @Resource
    private ICartService  cartService;
    @Resource
    private IShopService shopService;
    @Resource
    private IOrderRefundsService orderRefundsService;

    /**
     * 立即购买创建订单。
     *
     * <p>事务边界：整个方法在事务中执行。先校验当前用户为普通用户（userType=0），再校验库存充足，
     * 最后保存订单并扣减商品库存。任意异常触发事务回滚。</p>
     *
     * @param token     当前用户登录 token
     * @param productId 商品ID
     * @param addressId 收货地址ID
     * @param quantity  购买数量
     * @return 订单是否创建成功
     * @throws BusinessException 当库存不足等业务规则不满足时抛出
     * @throws RuntimeException  当事务执行异常时抛出，事务将回滚
     */
    @Transactional
    @Override
    public boolean createOrderBynow(String token, Integer productId, Integer addressId,Integer quantity) {
        User user = (User)userService.getUserByToken(token).getData();
        Products products = productsService.getById(productId);
        // 仅普通用户允许下单
        if(!"0".equals(user.getUserType())){
            return false;
        }
        try{
            // 校验库存是否满足购买数量
            if (products.getStockQuantity() < quantity) {
                throw new BusinessException("商品 [" + products.getName() + "] 库存不足，当前库存: " +
                        products.getStockQuantity() + "，需求数量: " + quantity +"请刷新页面获取最新数据");
            }
            // 组装订单信息
            Orders orders = new Orders();
            orders.setUserId(user.getId());
            orders.setProductId(productId);
            orders.setAddressId(addressId);
            orders.setStatus(1);
            orders.setQuantity(quantity);
            orders.setPrice(productsService.getById(productId).getPrice());
            orders.setPaymentTime(LocalDateTime.now());
            // 扣减库存
            products.setStockQuantity(products.getStockQuantity()-quantity);
            orders.setShopId(products.getShopId());
            this.save(orders);
            productsService.updateById(products);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException("下单失败，事务已回滚", e);
        }
        return true;
    }

    /**
     * 根据 token 分页查询当前用户的订单列表。
     *
     * @param token    当前用户登录 token
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @return 订单分页结果
     */
    @Override
    public Page<Orders> getOrdersByToken(String token, Integer pageNum, Integer pageSize) {
        User user = (User)userService.getUserByToken(token).getData();
        Page<Orders> page = new Page<>(pageNum, pageSize);
        // 查询当前用户的订单
        page = this.page(page, new QueryWrapper<Orders>().eq("user_id", user.getId()));
        // 为每条订单填充商品及封面图信息
        for (Orders order : page.getRecords()) {
            Products products = productsService.getById(order.getProductId());
            products.setProductMedias(productMediaService.findCovetByProductId(order.getProductId()));
            order.setProduct(products);
        }
        return page;
    }

    /**
     * 从购物车创建订单。
     *
     * <p>事务边界：整个方法在事务中执行。遍历购物车结算项，依次校验库存、创建订单、扣减库存并移除购物车记录。
     * 任意步骤失败触发事务回滚，保证数据一致性。</p>
     *
     * @param token               当前用户登录 token
     * @param orderRequestBycart  购物车结算项列表
     * @param addressId           收货地址ID
     * @return 订单创建结果
     * @throws BusinessException 当库存不足等业务规则不满足时抛出
     * @throws RuntimeException  当事务执行异常时抛出，事务将回滚
     */
    @Transactional
    @Override
    public ResponseResult<Boolean> createOrderByCart(String token,
                                            List<orderRequestBycart> orderRequestBycart,
                                            Integer addressId) {
        User user = (User)userService.getUserByToken(token).getData();
        // 仅普通用户允许购买
        if(!"0".equals(user.getUserType())){
            return ResponseResult.fail("你无权购买商品",false);
        }
        try{
            for (orderRequestBycart order : orderRequestBycart) {
                // 根据购物车ID获取商品信息
                Products products = productsService.getById(cartService.getById(order.getCartId()).getProductId());
                // 校验库存
                if (products.getStockQuantity() < order.getQuantity()) {
                    throw new BusinessException(String.format("商品 [%s] 库存不足，当前库存: %d，需求数量: %d，请刷新页面获取最新数据",
                            products.getName(),
                            products.getStockQuantity(),
                            order.getQuantity()));
                }
                // 组装订单并扣减库存
                Orders orders = new Orders();
                orders.setUserId(user.getId());
                orders.setProductId(products.getProductId());
                orders.setAddressId(addressId);
                orders.setStatus(1);
                orders.setQuantity(order.getQuantity());
                orders.setPrice(productsService.getById(products.getProductId()).getPrice());
                orders.setPaymentTime(LocalDateTime.now());
                orders.setShopId(products.getShopId());
                products.setStockQuantity(products.getStockQuantity()-order.getQuantity());
                this.save(orders);
                productsService.updateById(products);
                cartService.removeById(order.getCartId());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException("下单失败，事务已回滚", e);
        }
        return ResponseResult.success("下单成功",true);
    }

    /**
     * 商家根据 token 分页查询所属店铺的订单列表。
     *
     * @param token    当前用户登录 token
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @return 订单分页结果
     */
    @Override
    public Page<Orders> getOrdersByTokenForOrderManagement(String token,  Integer pageNum, Integer pageSize) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        // 通过 token 获取商家店铺
        Shop shop = shopService.getShopByToken(token);
        page = this.page(page, new QueryWrapper<Orders>().eq("shop_id", shop.getShopId()));
        // 为每条订单填充商品信息
        for (Orders order : page.getRecords()) {
            Products products = productsService.getById(order.getProductId());
            order.setProduct(products);
        }
        return page;
    }

    /**
     * 多条件查询订单列表（支持商家与用户视角）。
     *
     * <p>根据 token 解析当前身份：若用户无店铺则按用户ID过滤，否则按店铺ID过滤。
     * 支持按订单状态、订单ID、商品名称、下单时间范围组合查询。</p>
     *
     * @param token       当前用户登录 token
     * @param pageNum     当前页码
     * @param pageSize    每页大小
     * @param orderStatus 订单状态，可为空
     * @param orderId     订单ID模糊查询关键字，可为空
     * @param productName 商品名称模糊查询关键字，可为空
     * @param orderDate   下单时间范围，格式为 [开始日期, 结束日期]，可为空
     * @return 订单分页结果
     */
    @Override
    public Page<Orders> searchOrdersForOrder(String token,
                                                       Integer pageNum,
                                                       Integer pageSize,
                                                       Integer orderStatus,
                                                       String orderId,
                                                       String productName,
                                                       List<String> orderDate) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        // 根据 token 判断当前是商家还是普通用户
        Shop shop = shopService.getShopByToken(token);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        // 按订单状态过滤
        if(orderStatus!= null){
            queryWrapper.eq("status", orderStatus);
        }
        // 按订单ID模糊过滤
        if(!"".equals(orderId)){
            queryWrapper.like("order_id", orderId);
        }
        // 按商品名称模糊过滤，通过 exists 子查询关联 products 表
        if(!"".equals(productName)){
            queryWrapper.exists(
                    "SELECT 1 FROM products p WHERE p.product_id = orders.product_id AND p.name LIKE {0}",
                    "%" + productName + "%"
            );
        }
        // 按下单时间范围过滤
        if(orderDate!= null && orderDate.size() == 2){
            queryWrapper.between("payment_time", orderDate.get(0), orderDate.get(1));
        }

        // 区分用户视角与商家视角
        if(shop == null){  //如果是用户
            User user = (User)userService.getUserByToken(token).getData();
            queryWrapper.eq("user_id", user.getId());
        }else{//如果是商家
            queryWrapper.eq("shop_id", shop.getShopId());
        }

        page = this.page(page, queryWrapper);
        // 为结果填充商品及封面图
        for (Orders order : page.getRecords()) {
            Products products = productsService.getById(order.getProductId());
            products.setProductMedias(productMediaService.findCovetByProductId(order.getProductId()));
            order.setProduct(products);
        }
        return page;
    }

    /**
     * 将订单状态推进到下一节点。
     *
     * <p>状态流转规则：</p>
     * <ul>
     *   <li>status=1（待发货）-> status=2（待收货），记录发货时间；</li>
     *   <li>status=2（待收货）-> status=7（待评价），记录收货时间。</li>
     * </ul>
     *
     * @param orderId 订单ID
     * @return 状态更新是否成功
     */
    @Override
    public boolean changOrderStatusToNext(String orderId) {
        Orders orders = this.getById(orderId);
        if(orders.getStatus() == 1){
            orders.setDeliveryTime(LocalDateTime.now());
            orders.setStatus(2);
        }else if(orders.getStatus() == 2){
            orders.setReceiveTime(LocalDateTime.now());
            orders.setStatus(7);
        }
        return this.updateById(orders);
    }

    /**
     * 提交退款申请。
     *
     * <p>业务规则：若订单已存在退款记录则更新原记录；否则新建退款记录。同时保存订单当前状态到 preRefundStatus，
     * 并将订单状态置为退款中（status=5）。</p>
     *
     * @param orderId 订单ID
     * @param reason  退款原因
     * @param token   当前用户登录 token
     * @return 退款申请是否提交成功
     */
    @Override
    public boolean requestRefund(String orderId, String reason, String token) {
        User user = (User)userService.getUserByToken(token).getData();
        OrderRefunds orderRefunds = new OrderRefunds();
        // 查询是否已存在退款记录
        OrderRefunds orderRefunds1 = orderRefundsService.getOne(new QueryWrapper<OrderRefunds>().eq("order_id", orderId));
        if( orderRefunds1!= null) {
            // 更新已有退款记录
            orderRefunds1.setApplyTime(LocalDateTime.now());
            orderRefunds1.setRefundReason(reason);
            orderRefunds1.setRefundStatus(1);
            Orders orders = this.getById(orderId);
            orders.setPreRefundStatus(orders.getStatus());
            orders.setStatus(5);
            return this.updateById(orders) && orderRefundsService.updateById(orderRefunds1);
        }

        // 新建退款记录
        orderRefunds.setOrderId(orderId);
        orderRefunds.setRefundReason(reason);
        orderRefunds.setApplyTime(LocalDateTime.now());
        orderRefunds.setUserId(user.getId());
        orderRefunds.setRefundStatus(1);
        Orders orders = this.getById(orderId);
        orders.setPreRefundStatus(orders.getStatus());
        orders.setStatus(5);
        return this.updateById(orders) && orderRefundsService.save(orderRefunds);
    }

    /**
     * 审批退款申请。
     *
     * <p>业务规则：同意退款（isAgree=true）则将订单状态置为已退款（status=6）并记录关闭时间；
     * 拒绝退款则将订单状态恢复为申请退款前的原状态（preRefundStatus）。</p>
     *
     * @param orderId       订单ID
     * @param OrderRefundId 退款记录ID
     * @param isAgree       是否同意退款
     * @return 审批是否成功
     */
    @Override
    public boolean reviewRefund(String orderId, String OrderRefundId, boolean isAgree) {
        Orders orders = this.getById(orderId);
        OrderRefunds orderRefunds = orderRefundsService.getById(OrderRefundId);
        if(isAgree){
            orders.setStatus(6);
            orders.setCloseTime(LocalDateTime.now());
            orderRefunds.setRefundStatus(2);
        }else{
            // 拒绝退款，恢复订单原状态
            orders.setStatus(orders.getPreRefundStatus());
            orderRefunds.setRefundStatus(3);
        }
        return this.updateById(orders) && orderRefundsService.updateById(orderRefunds);
    }

    /**
     * 根据 token 查询当前用户的全部订单。
     *
     * @param token 当前用户登录 token
     * @return 订单列表
     * @throws BusinessException 当 token 无效或用户不存在时抛出
     */
    @Override
    public List<Orders> getAllOrdersByToken(String token) {
        // 1. 通过 token 获取用户信息
        User user = (User) userService.getUserByToken(token).getData();
        if (user == null) {
            throw new BusinessException("无效的token或用户不存在");
        }

        // 2. 查询该用户的所有订单
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId())
                .orderByDesc("created_at");

        List<Orders> ordersList = baseMapper.selectList(queryWrapper);

        // 3. 为每个订单设置关联的商品信息
        for (Orders order : ordersList) {
            Products product = productsService.getById(order.getProductId());
            if (product != null) {
                // 设置商品封面图
                product.setProductMedias(productMediaService.findCovetByProductId(order.getProductId()));
                order.setProduct(product);
            }
        }

        return ordersList;
    }
}
