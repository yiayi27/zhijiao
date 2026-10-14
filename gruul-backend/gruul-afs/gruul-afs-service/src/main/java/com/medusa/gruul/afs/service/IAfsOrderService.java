package com.medusa.gruul.afs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.afs.api.model.AfsSimpleVo;
import com.medusa.gruul.afs.model.*;
import com.medusa.gruul.common.core.util.PageUtils;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.order.api.entity.OrderSetting;
import com.medusa.gruul.order.api.model.OrderVo;

import java.util.List;

/**
 * <p>
 * 售后工单 服务类
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
public interface IAfsOrderService extends IService<AfsOrder> {

    /**
     * 用户申请售后
     *
     * @param dto 申请售后信息
     * @return the result
     */
    AfsOrder userApply(UserApplyDto dto);

    /**
     * 根据订单ID和商品ID查询进行中的订单
     *
     * @param orderId 订单id
     * @param productSkuId skuId
     * @return com.medusa.gruul.afs.api.entity.AfsOrder
     */
    AfsOrder selectProgressByOrderIdAndProductSkuId(Long orderId, Long productSkuId);

    /**
     * 获取当前订单的售后次数
     *
     * @param orderId 订单id
     * @return Integer
     */
    Integer getUserApplyNumber(Long orderId);

    /**
     * 用户取消售后
     *
     * @param afsId 售后订单id
     * @param isSystem 是否系统取消
     */
    void userCancel(Long afsId, boolean isSystem);

    /**
     * 用户确认已退货
     *
     * @param afsId           售后订单id
     * @param deliveryCode    快递公司编码
     * @param deliveryCompany 快递公司名称
     * @param deliverySn      快递单号
     * @param phone           收件人电话
     * @param reason          原因
     */
    void userReturn(Long afsId, String deliveryCode, String deliveryCompany, String deliverySn, String phone,
                    String reason);

    /**
     * 保存售后单
     *
     * @param dto 售后信息
     * @param secureUser 用户信息
     * @param orderVo 订单信息
     * @param userAfterSaleNum 用户售后次数
     * @param orderSetting 订单设置信息
     * @param receiptBillId 签收单id
     * @return com.medusa.gruul.afs.api.entity.AfsOrder
     */
    AfsOrder saveAfsOrder(BaseApplyDto dto, SecureUser secureUser, OrderVo orderVo, Integer userAfterSaleNum,
                          OrderSetting orderSetting, Long receiptBillId);

    /**
     * 检查订单状态
     *
     * @param orderVo 订单信息
     * @param dto 售后信息
     * @param orderSetting 订单设置信息
     * @param userAfterSaleNum 用户售后次数
     */
    void checkOrderStatus(OrderVo orderVo, BaseApplyDto dto, OrderSetting orderSetting, Integer userAfterSaleNum);

    /**
     * 根据售后单ID获取售后详情
     *
     * @param afsId 售后工单id
     * @return ApiAfsOrderVo
     */
    AfsOrderVo getAfsOrderInfo(Long afsId);

    /**
     * 根据签收单查询售后
     *
     * @param receiptBillId
     * @return java.util.List<com.medusa.gruul.afs.api.model.AfsSimpleVo>
     * @author alan
     * @date 2024/3/17 22:30
     */
    List<AfsSimpleVo> getAfsOrderByReceiptBillId(Long receiptBillId);

    /**
     * 订单签收
     *
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:30
     */
    void orderReceipt(OrderVo orderVo);

    /**
     * 更新补货单的相关售后状态
     *
     * @param orderIds
     * @return void
     * @author alan
     * @date 2024/3/17 22:31
     */
    void updateReplenish(List<Long> orderIds);

    /**
     * 订单发货
     *
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:31
     */
    void orderShipped(OrderVo orderVo);

    /**
     * 用户查询售后订单信息
     *
     * @param dto 售后订单信息
     * @return PageUtils<ApiAfsOrderVo>
     */
    PageUtils<ApiAfsOrderVo> searchOrder(SearchOrderDto dto);

    /**
     * 订单完成
     *
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:32
     */
    void orderCompleted(OrderVo orderVo);

    /**
     * 获取退货地址
     *
     * @param orderId
     * @return com.medusa.gruul.afs.model.ReturnAddressVo
     * @author alan
     * @date 2024/3/17 22:32
     */
    ReturnAddressVo getReturnAddress(Long orderId);

    /**
     * 获取订单是否过期
     *
     * @param orderId
     * @return java.lang.Boolean
     * @author alan
     * @date 2024/3/17 22:33
     */
    Boolean getAfsExpire(Long orderId);

    /**
     * 发货单签收逻辑
     *
     * @param orderVo
     * @return void
     * @author alan
     * @date 2024/3/17 22:07
     */
    void deliverReceipt(OrderVo orderVo);
}
