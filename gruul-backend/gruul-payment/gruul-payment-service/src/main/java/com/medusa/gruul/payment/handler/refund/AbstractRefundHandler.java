package com.medusa.gruul.payment.handler.refund;

import cn.hutool.json.JSONUtil;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.bean.RefundResult;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.handler.Handler;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDto;
import com.medusa.gruul.payment.service.IPaymentRefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 张治保
 * date 2022/1/20
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRefundHandler implements Handler<Void> {

    private final IPaymentRefundService paymentRefundService;
    private final PayServiceManager payServiceManager;

    @Override
    public Void handle(Object... params) {
        if (this.hasErrorParam(params, RefundRequestDto.class,String.class,String.class)) {
            throw new ServiceException("参数错误");
        }
        RefundRequestDto refundRequest = this.cast(params[0], RefundRequestDto.class);
        String detailsId = this.cast(params[1], String.class);
        String notifyParam = this.cast(params[2], String.class);
        /*
         * 生成退款订单
         */
        log.info("------退款金额:{}分",refundRequest.getTotalFee());
        RefundOrder refundOrder = this.refundOrder(refundRequest.getTotalFee(), notifyParam);
        /*
         * 申请退款并获取退款结果
         */
        RefundResult result = this.refundResult(detailsId,refundOrder);
        String resultStr = Jackson.writeValueAsString(result);
        log.info("申请退款接口同步返回结果:{}", resultStr);
        if(!refundSuccess(result)){
            throw new ServiceException(result.getResultMsg());
        }
        /*
         * 退款记录
         */
        PaymentRefund refund = new PaymentRefund();
        refund.setOrderId(refundOrder.getRefundNo());
        refund.setAsynRequest(JSONUtil.toJsonStr(refundOrder));
        refund.setAsynResult(resultStr);
        refund.setRouteKey(refundRequest.getRouteKey());
        paymentRefundService.save(refund);
        log.info("::::申请退款接口执行完毕::OutTradeNo:{}::RefundNo:{}",refundOrder.getOutTradeNo(),refundOrder.getRefundNo());
        return null;
    }

    /**
     * 获取退款订单
     * @param refundFee 退款金额
     * @param notifyParam 付款成功回调参数
     * @return 退款订单
     */
    protected abstract RefundOrder refundOrder(Integer refundFee,String notifyParam);

    /**
     * 判断退款结果是否失败
     * @param refundResult 退款结果
     * @return 退款结果
     */
    protected abstract Boolean refundSuccess(RefundResult refundResult);

    /**
     * 获取退款结果
     * @param refundOrder 退款订单
     */
    private RefundResult refundResult(String detailsId,RefundOrder refundOrder){
        /*
         * 请求退款接口
         */
        RefundResult result;
        try {
            result = payServiceManager.refund(detailsId, refundOrder);
        } catch (Exception e) {
            log.error("退款异常", e);
            throw new ServiceException("退款失败," + e.getMessage());
        }
        return result;
    }
}
