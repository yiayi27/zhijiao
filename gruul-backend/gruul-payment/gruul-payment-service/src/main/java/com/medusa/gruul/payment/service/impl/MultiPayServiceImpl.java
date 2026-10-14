package com.medusa.gruul.payment.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.common.bean.NoticeRequest;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.handler.Handler;
import com.medusa.gruul.common.core.util.SpringUtils;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.constant.OrderQueueEnum;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import com.medusa.gruul.order.api.feign.RemoteOrderService;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.payment.api.entity.Payment;
import com.medusa.gruul.payment.api.enums.FeeTypeEnum;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.model.pay.OrderPayParam;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.common.annotation.PlatformHandler;
import com.medusa.gruul.payment.common.context.DetailsIdContextHolder;
import com.medusa.gruul.payment.conf.PayProperty;
import com.medusa.gruul.payment.service.IPaymentService;
import com.medusa.gruul.payment.service.MultiPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * 多端支付服务
 *
 * @author 张治保
 * date 2024/12/17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiPayServiceImpl implements MultiPayService {

    private final PayProperty payProperty;
    private final IPaymentService paymentService;
    private final PayServiceManager payServiceManager;
    private final RemoteOrderService remoteOrderService;

    /**
     * 订单支付跳转toPay
     *
     * @param params 订单支付参数
     * @return 拉起需要的数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object toPay(OrderPayParam params) {
        /*
         *获取并检查订单
         */
        OrderVo order = checkAndGetOrder(params.getOrderId());
        /*
         * 渲染支付参数
         */
        PayParam payParam = new PayParam()
                .setPayType(params.getPayType())
                .setSubject("MALL-商城购物")
                .setBody(StrUtil.format("共计{}个商品", order.getProductTotalQuantity()))
                .setOrderId(String.valueOf(order.getId()))
                .setRouteKey(OrderQueueEnum.QUEUE_ORDER_PAYMENT_NOTIFY.getRouteKey())
                .setPrice(order.getPayAmount())
                .setFeeType(FeeTypeEnum.CNY)
                .setSeconds(
                        OrderTypeEnum.SEC_KILL == order.getType() ? TimeUnit.MINUTES.toSeconds(30L) : TimeUnit.HOURS.toSeconds(6L)
                )
                .setAttach(
                        Jackson.writeValueAsString(
                                new HashMap<String, Object>(8) {
                                    {
                                        put("orderId", order.getId());
                                        put("number", order.getProductTotalQuantity());
                                        put("price", order.getPayAmount());
                                        put("totalRebate", order.getRebateAmount());
                                    }
                                }
                        )
                );
        /*
         * 获取支付订单信息
         */
        PayResult payResult = this.toPay(payParam);
        /*
         * 订单设置业务流水号
         */
        remoteOrderService.setOrderTransactionId(params.getOrderId(), payResult.getOutTradeNo()).successData();
        return payResult;
    }

    /**
     * 支付
     *
     * @param payParam 支付参数
     */
    @Override
    public PayResult toPay(PayParam payParam) {
        Handler<PayResult> handler = SpringUtils.getBean(PlatformHandler.class, ISystem.platform());
        if (handler == null) {
            throw new ServiceException("当前平台不支持支付");
        }
        return handler.handle(payParam);
    }

    /**
     * notify 支付回调
     *
     * @param detailsId 支付配置详情id
     * @param request   回调数据
     * @return 是否成功
     */
    @Override
    public String notify(String detailsId, NoticeRequest request) {
        DetailsIdContextHolder.set(detailsId);
        log.error("回调数据 {}",request.toString());
        String payBack = payServiceManager.payBack(detailsId, request);
        DetailsIdContextHolder.remove();
        return payBack;
    }

    /**
     * 渲染前端页面地址
     *
     * @param payType    支付方式
     * @param outTradeNo 外部订单号
     * @return 需要跳转的页面url
     */
    @Override
    public String viewJump(PayType payType, String outTradeNo) {
        Payment payment = paymentService.lambdaQuery()
                .select(Payment::getOrderId)
                .eq(Payment::getTransactionId, outTradeNo)
                .one();
        return StrUtil.format(
                payProperty.getViewTemplate(),
                payType.name(),
                outTradeNo,
                payment == null ? "" : String.valueOf(payment.getOrderId())
        );
    }


    private OrderVo checkAndGetOrder(Long orderId) {
        OrderVo order = remoteOrderService.orderInfo(orderId);
        if (order == null) {
            throw new ServiceException("没有查询到需要支付的订单");
        }
        if (!OrderStatusEnum.canPay(order.getStatus())) {
            throw new ServiceException("当前状态不能支付此订单");
        }
        if (NumberUtil.isLess(order.getPayAmount(), OrderConstant.MIN_PAY_FEE)) {
            throw new ServiceException("支付金额有误");
        }
        log.info("====当前订单可以进行支付====");
        return order;
    }


}
