package com.example.back.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.Result.ResponseResult;
import com.example.back.entity.DTO.orderRequestBycart;
import com.example.back.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 订单服务接口。
 *
 * <p>定义订单创建、查询、状态流转、退款申请与审批等核心业务操作，覆盖立即购买、购物车下单及商家订单管理场景。</p>
 */
public interface IOrdersService extends IService<Orders> {

    /**
     * 立即购买创建订单。
     *
     * @param token     当前用户登录 token
     * @param productId 商品ID
     * @param addressId 收货地址ID
     * @param quantity  购买数量
     * @return 订单是否创建成功
     */
    public boolean createOrderBynow(String token,Integer productId, Integer addressId,Integer quantity);

    /**
     * 根据 token 分页查询当前用户的订单列表。
     *
     * @param token    当前用户登录 token
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @return 订单分页结果
     */
    public Page<Orders> getOrdersByToken(String token,Integer pageNum, Integer pageSize);

    /**
     * 从购物车创建订单。
     *
     * @param token               当前用户登录 token
     * @param orderRequestBycart  购物车结算项列表
     * @param addressId           收货地址ID
     * @return 订单创建结果
     */
    public ResponseResult<Boolean> createOrderByCart(String token, List<orderRequestBycart> orderRequestBycart, Integer addressId);

    /**
     * 商家根据 token 分页查询所属店铺的订单列表。
     *
     * @param token    当前用户登录 token
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @return 订单分页结果
     */
    public Page<Orders> getOrdersByTokenForOrderManagement(String token, Integer pageNum, Integer pageSize);

    /**
     * 多条件查询订单列表（支持商家与用户视角）。
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
    public Page<Orders> searchOrdersForOrder(String token,
                                                       Integer pageNum,
                                                       Integer pageSize,
                                                       Integer orderStatus,
                                                       String orderId,
                                                       String productName,
                                                       List<String> orderDate);

    /**
     * 将订单状态推进到下一节点（发货 -> 待评价 / 待收货 -> 待评价）。
     *
     * @param orderId 订单ID
     * @return 状态更新是否成功
     */
    public boolean changOrderStatusToNext(String orderId);

    /**
     * 提交退款申请。
     *
     * @param orderId 订单ID
     * @param reason  退款原因
     * @param token   当前用户登录 token
     * @return 退款申请是否提交成功
     */
    public boolean requestRefund(String orderId, String reason, String token);

    /**
     * 审批退款申请。
     *
     * @param orderId       订单ID
     * @param OrderRefundId 退款记录ID
     * @param isAgree       是否同意退款
     * @return 审批是否成功
     */
    public boolean reviewRefund(String orderId, String OrderRefundId,boolean isAgree);

    /**
     * 根据 token 查询当前用户的全部订单。
     *
     * @param token 当前用户登录 token
     * @return 订单列表
     */
    public List<Orders> getAllOrdersByToken(String token);
}
