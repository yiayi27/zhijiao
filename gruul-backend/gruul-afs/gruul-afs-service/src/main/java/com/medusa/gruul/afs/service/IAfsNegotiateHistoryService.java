package com.medusa.gruul.afs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.afs.api.entity.AfsNegotiateHistory;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.afs.api.entity.AfsOrderItem;
import com.medusa.gruul.afs.model.BaseApplyDto;
import com.medusa.gruul.afs.model.NegotiateHistoryDto;

import java.util.List;

/**
 * <p>
 * 协商历史 服务类
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
public interface IAfsNegotiateHistoryService extends IService<AfsNegotiateHistory> {

    /**
     * 初始化协商历史
     *
     * @param afsOrderItemList 售后订单商品信息
     * @param dto 售后申请信息
     * @param name 发起人名称
     */
    void init(List<AfsOrderItem> afsOrderItemList, BaseApplyDto dto, String name);

    /**
     * 用户主动撤销
     *
     * @param afsOrder 售后订单信息
     * @param isSystem 是否系统操作
     */
    void userCancel(AfsOrder afsOrder, boolean isSystem);

    /**
     * 买家已退货
     *
     * @param afsOrder 售后订单信息
     * @param isLogistics 是否物流发货
     */
    void userReturn(AfsOrder afsOrder, boolean isLogistics);

    /**
     * 签收补货单
     *
     * @param afsOrder 售后订单信息
     */
    void pointExchangeSuccess(AfsOrder afsOrder);

    /**
     * 卖家拒绝
     *
     * @param afsOrder 售后订单信息
     */
    void sellerRefuse(AfsOrder afsOrder);

    /**
     * 卖家同意申请
     *
     * @param afsOrder 售后订单
     * @param isSystem 是否系统操作
     */
    void sellerApprove(AfsOrder afsOrder, Boolean isSystem);

    /**
     * 卖家发货
     *
     * @param afsOrder 售后订单
     */
    void sellerShipped(AfsOrder afsOrder);

    /**
     * 卖家同意申请等待退货
     *
     * @param afsOrder 售后订单信息
     * @param isSystem 是否系统操作
     * @param address 退货地址
     * @param isLogistics 是否物流发货
     */
    void sellerApproveNeedReturn(AfsOrder afsOrder, Boolean isSystem, String address, Boolean isLogistics);

    /**
     * 商家同意退款申请
     *
     * @param afsOrder 售后订单信息
     * @param isSystem 是否系统操作
     */
    void refund(AfsOrder afsOrder, boolean isSystem);

    /**
     * 获取订单的售后协商历史
     *
     * @param dto 协商信息
     * @return List<AfsNegotiateHistory>
     */
    List<AfsNegotiateHistory> negotiateHistoryList(NegotiateHistoryDto dto);

    /**
     * 买家签收退货单
     *
     * @param afsOrder 售后订单信息
     * @param message 签收信息
     */
    void sellerCustomMessage(AfsOrder afsOrder,String message);
}
