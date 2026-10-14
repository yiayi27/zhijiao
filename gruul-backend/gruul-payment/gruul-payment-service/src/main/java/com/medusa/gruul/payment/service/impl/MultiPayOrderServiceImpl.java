package com.medusa.gruul.payment.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.common.bean.PayMessage;
import com.egzosn.pay.common.bean.TransactionType;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.common.system.model.Platform;
import com.medusa.gruul.payment.api.constant.StatusConstant;
import com.medusa.gruul.payment.api.entity.Payment;
import com.medusa.gruul.payment.api.entity.PaymentRecord;
import com.medusa.gruul.payment.api.entity.PaymentWechat;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.enums.PaymentQueueEnum;
import com.medusa.gruul.payment.api.model.dto.WxPayNotifyResultDto;
import com.medusa.gruul.payment.api.model.pay.MorePayParam;
import com.medusa.gruul.payment.api.model.pay.PayParam;
import com.medusa.gruul.payment.common.helper.PayHelper;
import com.medusa.gruul.payment.common.model.OrderPaymentRecord;
import com.medusa.gruul.payment.common.model.PayRequestOrder;
import com.medusa.gruul.payment.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author 张治保
 * date 2024/12/17
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MultiPayOrderServiceImpl implements MultiPayOrderService {

    private final AmqpTemplate amqpTemplate;
    private final IPaymentService paymentService;
    private final IPaymentWechatService paymentWechatService;
    private final IPaymentRecordService paymentRecordService;
    private final Snowflake outTradeNoSnowflake;
    private final MultiMerchantService multiMerchantService;

    /**
     * 支付订单预处理 pretreatment
     */
    @Override
    public PayRequestOrder pretreatment(PayParam payParam) {
        MorePayParam morePayParam = this.toMoreParamOrder(payParam);
        PayType payType = payParam.getPayType();
        String detailsId = multiMerchantService.detailsId(ISystem.shopId(), payType);
        /*
         * 创建支付订单
         */
        PayRequestOrder payOrder = this.createOrder(detailsId, morePayParam);
        /*
         * 初始化支付记录
         */
        this.init(payOrder);
        return payOrder;
    }

    @Override
    public PayRequestOrder createOrder(String detailsId, MorePayParam payParam) {
        /*
         *获取交易类型
         */
        Platform platform = payParam.getPlatform();
        TransactionType transactionType = PayHelper.wayType(platform, payParam.getPayType());
        /*
         * 渲染支付订单数据
         */
        PayRequestOrder payOrder = new PayRequestOrder();
        payOrder.setOrderId(payParam.getOrderId());
        payOrder.setPayType(payParam.getPayType());
        payOrder.setTradeType(PayHelper.tradeType(platform));
        payOrder.setRouteKey(payParam.getRouteKey());
        payOrder.setOutTradeNo(outTradeNoSnowflake.nextIdStr());
        payOrder.setDetailsId(detailsId);
        payOrder.setWayTrade(transactionType.getType());
        payOrder.setTransactionType(transactionType);
        payOrder.setSubject(payParam.getSubject());
        payOrder.setBody(payParam.getBody());
        payOrder.setPrice(payParam.getPrice());
        payOrder.setCurType(PayHelper.curType(payParam.getFeeType()));
        payOrder.setExpirationTime(new Date(System.currentTimeMillis() + (payParam.getSeconds() + 20) * 1000));
        payOrder.setWapName(payParam.getSubject());
        payOrder.setAddition(payParam.getAttach());
        payOrder.setSpbillCreateIp(payParam.getIp());
        payOrder.setAuthCode(payParam.getAuthCode());
        payOrder.setWapName(payParam.getWapName());
        payOrder.setWapUrl(payParam.getWapUrl());
        return payOrder;
    }


    @Override
    public void init(PayRequestOrder payOrder) {
        Payment payment = this.newPayment(payOrder);
        this.newPaymentWechat(payment.getId(), payOrder);
        this.newPaymentRecord(payment.getId(), payOrder);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void success(PayMessage payMessage) {
        log.info("进入支付成功处理-{}", payMessage);
        String outTradeNo = payMessage.getOutTradeNo();
        Payment payment = paymentService.lambdaQuery().eq(Payment::getTransactionId, outTradeNo).one();
        if (payment == null) {
            log.info("未查询到payment记录:outTradeNo:{}", outTradeNo);
            throw new ServiceException("未查询到支付记录");
        }
        if (payment.getBusinessNotifyStatus().equals(StatusConstant.BusinessNotifyStatus.PROCESSED)
                &&
                payment.getThirdPartyNotifyStatus().equals(StatusConstant.ThirdpartyNotifyStatus.PROCESSED)) {
            return;
        }
        if (StrUtil.isBlank(payment.getRouteKey())) {
            throw new ServiceException("路由Key不能为空");
        }
        /*
         * 设置回调参数
         */
        PaymentRecord record = paymentRecordService.lambdaQuery().eq(PaymentRecord::getPaymentId, payment.getId()).one();
        record.setNotifyParam(Jackson.writeValueAsString(payMessage));
        paymentRecordService.updateById(record);

        /*
         * 设置交易成功
         */
        payment.setThirdPartyNotifyNumber(payment.getThirdPartyNotifyNumber() + 1);
        payment.setThirdPartyNotifyStatus(StatusConstant.ThirdpartyNotifyStatus.PROCESSED);
        payment.setTradeStatus(StatusConstant.TradeStatus.TRADE_SUCCESS);
        payment.setBusinessNotifyStatus(StatusConstant.BusinessNotifyStatus.PROCESSED);
        paymentService.updateById(payment);

        log.info("发送mq消息.....队列:{}", payment.getRouteKey());
        /*
         * 支付成功回调
         */
        amqpTemplate.convertAndSend(
                PaymentQueueEnum.QUEUE_PAYMENT_NOTIFY_SUCCESS.getExchange(),
                payment.getRouteKey(),
                new WxPayNotifyResultDto(
                        payment.getOrderId(),
                        payment.getBusinessParams(),
                        payment.getShopId(),
                        payment.getTransactionId()
                )
        );

    }

    @Override
    public OrderPaymentRecord paymentRecord(String outTradeNo) {
        Payment payment = paymentService.lambdaQuery()
                .eq(Payment::getTransactionId, outTradeNo)
                .one();
        if (payment == null) {
            throw new ServiceException("支付记录不存在");
        }
        /*
         *查询支付记录
         */
        PaymentRecord record = paymentRecordService.lambdaQuery().eq(PaymentRecord::getPaymentId, payment.getId()).one();
        String sendParam;
        if (record == null || StrUtil.isBlank(sendParam = record.getSendParam())) {
            throw new ServiceException("支付记录不存在或支付记录请求数据不存在");
        }
        OrderPaymentRecord paymentRecord = Jackson.readValue(sendParam, OrderPaymentRecord.class);
        paymentRecord.setNotifyParam(record.getNotifyParam());
        log.info("查询支付记录:{}", paymentRecord);
        return paymentRecord;
    }


    /**
     * 创建payment
     */
    private Payment newPayment(PayRequestOrder payOrder) {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment()
                .setOrderId(payOrder.getOrderId())
                .setTransactionId(payOrder.getOutTradeNo())
                .setPayChannel(PayHelper.payChannel(payOrder.getPayType()))
                .setFeeType(payOrder.getCurType().getType())
                .setTotalFee(payOrder.getPrice())
                .setBody(payOrder.getBody())
                .setBusinessParams(payOrder.getAddition())
                .setTerminalIp(payOrder.getSpbillCreateIp())
                .setBusinessNotifyUrl(payOrder.getNotifyUrl())
                .setRouteKey(payOrder.getRouteKey())
                .setThirdPartyNotifyStatus(StatusConstant.ThirdpartyNotifyStatus.UNTREATED)
                .setThirdPartyNotifyNumber(0)
                .setBusinessNotifyStatus(StatusConstant.BusinessNotifyStatus.UNTREATED)
                .setTradeStatus(StatusConstant.TradeStatus.WAIT_BUYER_PAY);
        payment.setCreateTime(now);
        payment.setShopId(payOrder.getShopId());
        payment.setDeleted(false);
        payment.setTimeoutExpress(LocalDateTimeUtil.of(payOrder.getExpirationTime()).minusSeconds(20));
        paymentService.save(payment);

        return payment;
    }

    /**
     * 创建wechat
     */
    private void newPaymentWechat(Long paymentId, PayRequestOrder payOrder) {
        //记录渠道信息数据
        PaymentWechat paymentWechat = new PaymentWechat();
        paymentWechat.setTradeType(payOrder.getTradeType());
        paymentWechat.setPaymentId(paymentId);
        paymentWechat.setOutTradeNo(payOrder.getOutTradeNo());
        paymentWechat.setSubject(payOrder.getSubject());
        paymentWechat.setShopId(payOrder.getShopId());
        paymentWechat.setOpenId(payOrder.getOrderId());
        paymentWechatService.save(paymentWechat);
    }

    /**
     * 生成支付记录
     */
    private void newPaymentRecord(Long paymentId, PayRequestOrder payOrder) {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentId(paymentId);
        paymentRecord.setSendParam(Jackson.writeValueAsString(payOrder));
        paymentRecord.setRequestParams(Jackson.writeValueAsString(payOrder));
        paymentRecordService.save(paymentRecord);
    }

    /**
     * 转换成更多参数的订单支付对象
     */
    private MorePayParam toMoreParamOrder(PayParam payParam) {
        if (payParam instanceof MorePayParam) {
            return (MorePayParam) payParam;
        }
        MorePayParam morePayParam = new MorePayParam();

        morePayParam.setOrderId(payParam.getOrderId())
                .setPayType(payParam.getPayType())
                .setSubject(payParam.getSubject())
                .setBody(payParam.getBody())
                .setRouteKey(payParam.getRouteKey())
                .setFeeType(payParam.getFeeType())
                .setPrice(payParam.getPrice())
                .setSeconds(payParam.getSeconds())
                .setAttach(payParam.getAttach());

        morePayParam.setIp(ISystem.ip())
                .setPlatform(ISystem.platform())
                .setOpenId(ISecurity.userMust().getOpenid())
                .setAuthCode(null)
                .setWapName(null)
                .setWapUrl(null);

        return morePayParam;
    }
}
