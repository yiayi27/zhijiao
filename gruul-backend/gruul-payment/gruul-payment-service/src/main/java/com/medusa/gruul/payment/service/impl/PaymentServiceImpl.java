package com.medusa.gruul.payment.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceApacheHttpImpl;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.payment.api.constant.ReturnCodeConstant;
import com.medusa.gruul.payment.api.constant.StatusConstant;
import com.medusa.gruul.payment.api.entity.Payment;
import com.medusa.gruul.payment.api.entity.PaymentRecord;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.api.entity.PaymentWechat;
import com.medusa.gruul.payment.api.enums.PayChannelEnum;
import com.medusa.gruul.payment.api.enums.PaymentQueueEnum;
import com.medusa.gruul.payment.api.model.dto.PayStatusDto;
import com.medusa.gruul.payment.api.model.dto.RefundNotifyResultDto;
import com.medusa.gruul.payment.api.model.dto.WxPayNotifyResultDto;
import com.medusa.gruul.payment.mapper.PaymentMapper;
import com.medusa.gruul.payment.service.IPaymentRecordService;
import com.medusa.gruul.payment.service.IPaymentRefundService;
import com.medusa.gruul.payment.service.IPaymentService;
import com.medusa.gruul.payment.service.IPaymentWechatService;
import com.medusa.gruul.platform.api.feign.RemoteMiniInfoService;
import com.medusa.gruul.platform.api.model.dto.ShopConfigDto;
import com.medusa.gruul.platform.api.model.vo.PayInfoVo;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.Executors.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whh
 * @since 2023-11-04
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements IPaymentService {

    private static final ExecutorService THREAD_POOL_EXECUTOR = newVirtualThreadPerTaskExecutor();
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    @Autowired
    private RemoteMiniInfoService remoteMiniInfoService;
    @Autowired
    private IPaymentRecordService paymentRecordService;
    @Autowired
    private IPaymentRefundService paymentRefundService;
    @Autowired
    private IPaymentWechatService paymentWechatService;
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 统一回调入口
     *
     * @param xmlResult xml
     * @return "" || success
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notify(String xmlResult) {
        logger.info("支付回调------->".concat(xmlResult));
        //todo 目前只有微信的回调方式,之后需要在做扩展
        WxPayOrderNotifyResult payOrderNotifyResult = WxPayOrderNotifyResult.fromXML(xmlResult);
        String outTradeNo = payOrderNotifyResult.getOutTradeNo();
        Payment payment = this.baseMapper.selectOne(new QueryWrapper<Payment>().eq("transaction_id", outTradeNo));

        // 统一函数抽取
        return innerHandleCallback(payment, xmlResult);
    }


    /**
     * 函数抽取共用
     *
     * @param payment 支付信息
     * @param xmlResult xml报文
     * @return String
     */
    @Override
    public String innerHandleCallback(Payment payment, String xmlResult) {
        //回调都已处理直接返回
        if (payment.getBusinessNotifyStatus().equals(StatusConstant.BusinessNotifyStatus.PROCESSED)
                && payment.getThirdPartyNotifyStatus().equals(StatusConstant.ThirdpartyNotifyStatus.PROCESSED)) {
            return ReturnCodeConstant.SUCCESS;
        }
        //回调次数加1
        payment.setThirdPartyNotifyNumber(payment.getThirdPartyNotifyNumber() + 1);

        //队列回调只进行一次
        if (StrUtil.isNotEmpty(payment.getRouteKey())) {
            //支付流水订单交易状态设置为成功,并且已设置回调状态为已设置
            THREAD_POOL_EXECUTOR.execute(() -> {
                payment.setThirdPartyNotifyStatus(StatusConstant.ThirdpartyNotifyStatus.PROCESSED);
                payment.setTradeStatus(StatusConstant.TradeStatus.TRADE_SUCCESS);
                payment.setBusinessNotifyStatus(StatusConstant.BusinessNotifyStatus.PROCESSED);
                this.baseMapper.updateById(payment);
                amqpNotifySend(payment);
            });
            return ReturnCodeConstant.SUCCESS;
        } else if (StrUtil.isNotEmpty(payment.getBusinessNotifyUrl())) {
            //url回调多次
            //第三方回调已处理,业务回调未处理直接发送业务回调请求
            if (payment.getThirdPartyNotifyStatus().equals(StatusConstant.ThirdpartyNotifyStatus.PROCESSED) &&
                    payment.getBusinessNotifyStatus().equals(StatusConstant.BusinessNotifyStatus.UNTREATED)) {
                businessNotify(payment);
                return "";
            }
            //支付流水订单设置为成功
            payment.setThirdPartyNotifyStatus(StatusConstant.ThirdpartyNotifyStatus.PROCESSED);
            payment.setTradeStatus(StatusConstant.TradeStatus.TRADE_SUCCESS);
            this.baseMapper.updateById(payment);
            //设置回调参数
            PaymentRecord paymentRecord = paymentRecordService.getOne(new QueryWrapper<PaymentRecord>().eq("payment_id", payment.getId()));
            paymentRecord.setNotifyParam(JSONUtil.toJsonStr(xmlResult));
            paymentRecordService.updateById(paymentRecord);
            businessNotify(payment);
            return "";
        } else {
            return ReturnCodeConstant.FAIL;
        }
    }


    /**
     * 退款回调
     *
     * @param request 请求信息
     * @return string
     */
    @Override
    public String wxPayRefundNotify(HttpServletRequest request) throws Exception {
        log.warn("callback wxPayRefundNotify");
        String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());

        log.warn("微信回调数据 : " + xmlResult);
        WxPayRefundResult wxPayRefundResult = WxPayRefundResult.fromXML(xmlResult, WxPayRefundResult.class);

        String s = JSONUtil.toJsonStr(wxPayRefundResult);
        log.warn("微信回调数据解析 : " + s);
        // 封装 WxPayService
        WxPayService wxPayService = innerHandleShopConfigServer(wxPayRefundResult);
        WxPayRefundNotifyResult result = wxPayService.parseRefundNotifyResult(wxPayRefundResult.getXmlString());
        WxPayRefundNotifyResult.ReqInfo reqInfo = result.getReqInfo();

        PaymentRefund refund = paymentRefundService.getOne(new QueryWrapper<PaymentRefund>().eq("order_id", reqInfo.getOutTradeNo()));
        refund.setSynCallback(s);
        paymentRefundService.updateById(refund);
        // 封装 RefundNotifyResultDto
        RefundNotifyResultDto dto = innerHandleRefundNotifyResultDto(result, reqInfo);
        THREAD_POOL_EXECUTOR.execute(() -> {
            amqpTemplate.convertAndSend(PaymentQueueEnum.QUEUE_PAYMENT_NOTIFY_SUCCESS.getExchange(), refund.getRouteKey(), dto);
        });
        return ReturnCodeConstant.SUCCESS;
    }


    /**
     * 封装RefundNotifyResultDto
     *
     * @param result 退款结果
     * @param reqInfo 请求信息
     * @return RefundNotifyResultDto
     */
    private RefundNotifyResultDto innerHandleRefundNotifyResultDto(WxPayRefundNotifyResult result, WxPayRefundNotifyResult.ReqInfo reqInfo) {
        RefundNotifyResultDto dto = new RefundNotifyResultDto();
        BeanUtils.copyProperties(result, dto);
        BeanUtils.copyProperties(reqInfo, dto);
        return dto;
    }


    /**
     * 封装 WxPayService
     *
     * @param wxPayRefundResult 微信退款消息
     * @return WxPayService
     */
    private WxPayService innerHandleShopConfigServer(WxPayRefundResult wxPayRefundResult) {
        ShopConfigDto shopConfig = remoteMiniInfoService.getShopConfigAndAppId(wxPayRefundResult.getAppid());
        if (shopConfig == null) {
            throw new ServiceException("商户配置不存在");
        }
        PayInfoVo payInfo = shopConfig.getPayInfo();
        log.warn("shopConfig log = " + JSONUtil.toJsonStr(shopConfig));
        WxPayService wxPayService = new WxPayServiceApacheHttpImpl();
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(wxPayRefundResult.getAppid());
        wxPayConfig.setMchId(payInfo.getMchId());
        wxPayConfig.setMchKey(payInfo.getMchKey());
        wxPayConfig.setKeyPath(payInfo.getCertificatesPath());
        wxPayService.setConfig(wxPayConfig);
        return wxPayService;
    }


    /**
     * 队列回调通知
     *
     * @param payment 支付信息
     */
    private void amqpNotifySend(Payment payment) {
        PaymentWechat paymentWechat = paymentWechatService.getOne(new QueryWrapper<PaymentWechat>().eq("payment_id", payment.getId()));
        WxPayNotifyResultDto wxPayNotifyResultDto = new WxPayNotifyResultDto(paymentWechat.getOutTradeNo(), payment.getBusinessParams(),
                payment.getShopId(), payment.getTransactionId());
        amqpTemplate.convertAndSend(PaymentQueueEnum.QUEUE_PAYMENT_NOTIFY_SUCCESS.getExchange(), payment.getRouteKey(), wxPayNotifyResultDto);
    }

    /**
     * 获取指定订单交易状态
     *
     * @param outTradeNo    业务订单号
     * @param payChannel    支付渠道
     * @param shopId      店铺标识
     * @param transactionId
     * @return 状态
     */
    @Override
    public PayStatusDto getPayStatus(String outTradeNo, String payChannel, String shopId, String transactionId) {
        PayStatusDto payStatusDto = new PayStatusDto();
        Payment payment = this.baseMapper.selectOrderStatus(outTradeNo, payChannel, transactionId, shopId);
        if (payment == null) {
            payStatusDto.setTradeStatus(0);
            return payStatusDto;
        }
        payStatusDto.setTradeStatus(payment.getTradeStatus());
        return payStatusDto;
    }

    /**
     * 回调业务(异步)
     */
    private void businessNotify(Payment payment) {
        THREAD_POOL_EXECUTOR.execute(() -> {
            //回调微信渠道
            if (payment.getPayChannel().equals(PayChannelEnum.WX.getType())) {
                PaymentWechat paymentWechat = paymentWechatService.getOne(new QueryWrapper<PaymentWechat>().eq("payment_id", payment.getId()));
                WxPayNotifyResultDto notifyParam = new WxPayNotifyResultDto();
                notifyParam.setOutTradeNo(paymentWechat.getOutTradeNo());
                notifyParam.setBusinessParams(payment.getBusinessParams());
                notifyParam.setShopId(payment.getShopId());
                notifyParam.setTransactionId(payment.getTransactionId());
                String post = HttpUtil.post(payment.getBusinessNotifyUrl(), JSONUtil.toJsonStr(notifyParam));
                if (StrUtil.equalsAnyIgnoreCase(post, ReturnCodeConstant.SUCCESS)) {
                    payment.setBusinessNotifyStatus(StatusConstant.BusinessNotifyStatus.PROCESSED);
                    this.baseMapper.updateById(payment);
                }
            }
        });

    }
}
