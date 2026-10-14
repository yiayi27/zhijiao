package com.medusa.gruul.payment.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.payment.api.constant.ReturnCodeConstant;
import com.medusa.gruul.payment.api.constant.StatusConstant;
import com.medusa.gruul.payment.api.entity.Payment;
import com.medusa.gruul.payment.api.entity.PaymentRecord;
import com.medusa.gruul.payment.api.entity.PaymentWechat;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.enums.WxPayEnum;
import com.medusa.gruul.payment.api.model.dto.PayRequestDto;
import com.medusa.gruul.payment.api.model.dto.PayResultDto;
import com.medusa.gruul.payment.api.model.dto.WxPayResultDto;
import com.medusa.gruul.payment.common.model.PayRequestOrder;
import com.medusa.gruul.payment.conf.PayProperty;
import com.medusa.gruul.payment.service.IPaymentRecordService;
import com.medusa.gruul.payment.service.IPaymentService;
import com.medusa.gruul.payment.service.IPaymentWechatService;
import com.medusa.gruul.payment.service.MultiMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.github.binarywang.wxpay.v3.util.PemUtils.loadPrivateKey;

/**
 * @author whh
 */
@Component
public class WxPayMiniHandler extends AbstractHandler {

    /**
     * 超时关闭订单
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1000,
            Thread.ofVirtual().name("transactionCloseTime-schedule-pool", 0).factory()
    );
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private IPaymentRecordService paymentRecordService;
    @Autowired
    private IPaymentWechatService paymentWechatService;
    @Autowired
    private PayProperty payProperty;
    @Autowired
    private MultiMerchantService multiMerchantService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultDto handle(PayRequestDto payRequestDto) throws ServiceException {
        WxPayEnum wxPayType = WxPayEnum.getPayByTradeType(payRequestDto.getTradeType());
        return switch (wxPayType) {
            case JSAPI_MINI, JSAPI_MP -> wxJsApiPay(payRequestDto);
            case H5 -> wxH5Pay(payRequestDto);
            default -> throw new ServiceException("TradeType未匹配对应wxPay支付.");
        };
    }


    /**
     * wx pay h5
     *
     * @return PayResultDto
     */
    public PayResultDto wxH5Pay(PayRequestDto payRequestDto) {
        PayResultDto test = this.test(payRequestDto);
        //校验指定模式下的数据是否为空
        if (test != null) {
            return test;
        }

        //记录调用信息
        Payment payment = this.generatePayment(payRequestDto, payProperty.getWorkerId(), payProperty.getDatacenterId());
        paymentService.save(payment);

        //记录渠道信息数据
        PaymentWechat paymentWechat = new PaymentWechat();
        paymentWechat.setTradeType(WxPayEnum.H5.getType());
        paymentWechat.setPaymentId(payment.getId());
        paymentWechat.setOutTradeNo(payRequestDto.getOutTradeNo());
        paymentWechat.setSubject(payRequestDto.getSubject());
        paymentWechat.setShopId(payRequestDto.getShopId());
        paymentWechat.setOpenId(payRequestDto.getOpenId());
        paymentWechatService.save(paymentWechat);

        //记录业务数据
        PaymentRecord paymentRecord = this.generateRecod(payRequestDto);
        paymentRecord.setPaymentId(payment.getId());


        //获取支付配置,并封装数据调用支付接口
        WxPayService wxPayService = multiMerchantService.getWxPayService(ISystem.shopId());

        WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request();

        wxPayUnifiedOrderV3Request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(BaseWxPayRequest.yuanToFen(payment.getTotalFee().toString())));
        wxPayUnifiedOrderV3Request.setDescription(payment.getBody());
        wxPayUnifiedOrderV3Request.setOutTradeNo(payment.getTransactionId());
//        wxPayUnifiedOrderV3Request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(payRequestDto.getOpenId()));
        wxPayUnifiedOrderV3Request.setSceneInfo(new WxPayUnifiedOrderV3Request.SceneInfo().setPayerClientIp(payment.getTerminalIp())
                .setH5Info(new WxPayUnifiedOrderV3Request.H5Info().setType("Wap")));
        wxPayUnifiedOrderV3Request.setNotifyUrl(payRequestDto.getNotifyUrl());
        wxPayUnifiedOrderV3Request.setAttach(payment.getBusinessParams());

        test = new PayResultDto();
        WxPayResultDto wxPayResultDto = new WxPayResultDto();
        try {
            Object orderV3 = wxPayService.createOrderV3(TradeTypeEnum.H5, wxPayUnifiedOrderV3Request);
            paymentRecordService.save(paymentRecord);
            wxPayResultDto.setMWebUrl((String) orderV3);
            wxPayResultDto.setResultCode("SUCCESS");
            wxPayResultDto.setTradeType(WxPayConstants.TradeType.MWEB);
            wxPayResultDto.setTransactionId(payment.getTransactionId());
            test.setWxResult(wxPayResultDto);
            transactionCloseTime(payment);
            return test;
        } catch (WxPayException e) {
            test.setReturnCode(e.getReturnCode());
            test.setReturnMsg(e.getCustomErrorMsg());
            wxPayResultDto.setResultCode(e.getResultCode());
            wxPayResultDto.setErrCode(e.getErrCode());
            wxPayResultDto.setErrCodeDes(e.getErrCodeDes());
            test.setWxResult(wxPayResultDto);
            return test;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }


    /**
     * wx pay jsApi
     *
     * @return PayResultDto
     */
    public PayResultDto wxJsApiPay(PayRequestDto payRequestDto) {
        PayResultDto test = this.test(payRequestDto);
        //校验指定模式下的数据是否为空
        if (test != null) {
            return test;
        }

        //记录调用信息
        Payment payment = this.generatePayment(payRequestDto, payProperty.getWorkerId(), payProperty.getDatacenterId());
        paymentService.save(payment);
        //记录渠道信息数据
        PaymentWechat paymentWechat = new PaymentWechat();
        paymentWechat.setTradeType(payRequestDto.getTradeType());
        paymentWechat.setPaymentId(payment.getId());
        paymentWechat.setOutTradeNo(payRequestDto.getOutTradeNo());
        paymentWechat.setSubject(payRequestDto.getSubject());
        paymentWechat.setShopId(payRequestDto.getShopId());
        paymentWechat.setOpenId(payRequestDto.getOpenId());
        paymentWechatService.save(paymentWechat);

        //记录业务数据
        PaymentRecord paymentRecord = this.generateRecod(payRequestDto);
        paymentRecord.setPaymentId(payment.getId());


        //获取支付配置,并封装数据调用支付接口
        WxPayService wxPayService = multiMerchantService.getWxPayService(ISystem.shopId());
        WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request();
        wxPayUnifiedOrderV3Request.setDescription(payment.getBody());
        wxPayUnifiedOrderV3Request.setOutTradeNo(payment.getOrderId());
        wxPayUnifiedOrderV3Request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(BaseWxPayRequest.yuanToFen(payment.getTotalFee().toString())));
        wxPayUnifiedOrderV3Request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(payRequestDto.getOpenId()));
        wxPayUnifiedOrderV3Request.setSceneInfo(new WxPayUnifiedOrderV3Request.SceneInfo().setPayerClientIp(payment.getTerminalIp()));
        wxPayUnifiedOrderV3Request.setNotifyUrl(payRequestDto.getNotifyUrl());
        wxPayUnifiedOrderV3Request.setAttach(payment.getBusinessParams());

        test = new PayResultDto();
        WxPayResultDto wxPayResultDto = new WxPayResultDto();
        try {
            PayRequestOrder payOrder = new PayRequestOrder();
            payOrder.setDetailsId(wxPayService.getConfig().getServiceId());
            payOrder.setPayType(PayType.WECHAT);
            paymentRecord.setSendParam(Jackson.writeValueAsString(payOrder));
            //清空serviceId
            wxPayService.getConfig().setServiceId(null);
            WxPayUnifiedOrderV3Result.JsapiResult jsapiResult  = wxPayService.createOrderV3(TradeTypeEnum.JSAPI,wxPayUnifiedOrderV3Request);

            wxPayResultDto.setAppId(jsapiResult.getAppId());
            // 格式：秒级时间戳
            wxPayResultDto.setTimeStamp(jsapiResult.getTimeStamp());
            wxPayResultDto.setNonceStr(jsapiResult.getNonceStr());
            wxPayResultDto.setPackageValue(jsapiResult.getPackageValue());
            wxPayResultDto.setPaySign(jsapiResult.getPaySign());
            wxPayResultDto.setResultCode("SUCCESS");
            wxPayResultDto.setTradeType(WxPayConstants.TradeType.MWEB);
            wxPayResultDto.setTransactionId(payment.getTransactionId());
            wxPayResultDto.setTotalFee(BaseWxPayRequest.yuanToFen(payment.getTotalFee().toString()));
            wxPayResultDto.setSignType("RSA2");
            test.setWxResult(wxPayResultDto);
            transactionCloseTime(payment);
            return test;
        } catch (WxPayException e) {
            test.setReturnCode(e.getReturnCode());
            test.setReturnMsg(e.getReturnMsg());
            wxPayResultDto.setResultCode(e.getResultCode());
            wxPayResultDto.setErrCode(e.getErrCode());
            wxPayResultDto.setErrCodeDes(e.getErrCodeDes());
            test.setWxResult(wxPayResultDto);
            return test;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private PrivateKey loadPrivateKey(String keyPath) throws IOException {
        String content = Files.readString(Paths.get(keyPath));
        content = content
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(content)));
        } catch (Exception e) {
            throw new RuntimeException("私钥加载失败", e);
        }
    }


    /**
     * 超时关闭交易
     *
     * @param payment 记录
     */
    public void transactionCloseTime(Payment payment) {
        try {
            long delay = LocalDateTimeUtils.getMilliByTime(payment.getTimeoutExpress()) - System.currentTimeMillis();
            EXECUTOR_SERVICE.schedule(() -> {
                Payment p = paymentService.getById(payment.getId());
                if (p.getTradeStatus().equals(StatusConstant.TradeStatus.WAIT_BUYER_PAY)) {
                    p.setTradeStatus(StatusConstant.TradeStatus.TRADE_CLOSED);
                    paymentService.updateById(p);
                }
            }, delay, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }


    @Override
    public PaymentRecord generateRecod(PayRequestDto payRequestDto) {
        return super.generateRecod(payRequestDto);
    }

    @Override
    public Payment generatePayment(PayRequestDto payRequestDto, long workerId, long datacenterId) {
        return super.generatePayment(payRequestDto, workerId, datacenterId);
    }

    @Override
    public PayResultDto test(PayRequestDto payRequestDto) {
        PayResultDto resultDto = new PayResultDto();
        resultDto.setReturnCode(ReturnCodeConstant.FAIL);
        //trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。
        if (StrUtil.isEmpty(payRequestDto.getOpenId()) && !payRequestDto.getTradeType().equals(CommonConstants.NUMBER_SIX)) {
            resultDto.setReturnMsg("JSAPI模式下,用户openId不能为空");
            return resultDto;
        }
        return null;
    }

}
