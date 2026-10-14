package com.medusa.gruul.order.api.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 生成订单失败消息体
 *
 * @author alan
 * @date 2023/10/6 14:08
 */
@Data
public class OrderFailMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 使用的积分 积分可能为空，注意判断
     */
    private BigDecimal integration;

    /**
     * 优惠券 优惠券可能为空，注意判断
     */
    private Long couponId;

    /**
     * 商铺id
     */
    private String shopId;

    /**
     * 用户id
     */
    private String userId;

    public OrderFailMessage integralFail(Long couponId, String userId, String shopId) {
        OrderFailMessage failMessage=new OrderFailMessage();
        failMessage.setCouponId(couponId);
        failMessage.setUserId(userId);
        failMessage.setShopId(shopId);
        return failMessage;
    }

    public OrderFailMessage stockFail(Long couponId, BigDecimal integration, String userId, String shopId) {
        OrderFailMessage failMessage=new OrderFailMessage();
        failMessage.setCouponId(couponId);
        failMessage.setUserId(userId);
        failMessage.setShopId(shopId);
        failMessage.setIntegration(integration);
        return failMessage;
    }
}
