package com.medusa.gruul.payment.handler.refundNotify;

import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.handler.Handler;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.api.enums.PaymentQueueEnum;
import com.medusa.gruul.payment.api.model.dto.RefundNotifyResultDto;
import com.medusa.gruul.payment.common.constant.AliConst;
import com.medusa.gruul.payment.service.IPaymentRefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.Map;

/**
 * @author 张治保
 * date 2022/1/25
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRefundNotifyHandler implements Handler<Void> {

    private final IPaymentRefundService paymentRefundService;
    private final AmqpTemplate amqpTemplate;

    @Override
    public Void handle(Object... params) {
        if (hasErrorParam(params,PayMessage.class)){
            throw new ServiceException("参数错误");
        }
        PayMessage message = cast(params[0], PayMessage.class);
        String refundNo = refundNo(message.getPayMessage());
        log.info("::退款回调::RefundNo::{}",refundNo);
        if(StrUtil.isEmpty(refundNo)){
            throw new ServiceException("未正确获取退款单号");
        }
        PaymentRefund refund = paymentRefundService.lambdaQuery()
            .eq(PaymentRefund::getOrderId, refundNo)
            .one();
        if(refund == null){
            return null;
        }
        if (StrUtil.isNotEmpty(refund.getSynCallback())){
            return null;
        }
        String outTradeNo = message.getOutTradeNo();
        /*
         * 通知退款消息
         */
        amqpTemplate.convertAndSend(
            PaymentQueueEnum.QUEUE_PAYMENT_NOTIFY_SUCCESS.getExchange(),
            refund.getRouteKey(),
            refundNotifyResult(refund.getShopId(), outTradeNo,refundNo)
        );
        /*
         * 更新退款异步结果
         */
        refund.setSynCallback(Jackson.writeValueAsString(message));
        paymentRefundService.updateById(refund);
        log.info("::::退款回调接口执行完毕::OutTradeNo:{}::RefundNo:{}",outTradeNo,refundNo);
        return null;
    }

    /**
     * 获取MQ通知消息
     * @return mq通知消息
     */
    protected RefundNotifyResultDto refundNotifyResult(String shopId,String outTradeNo,String refundNo){
        RefundNotifyResultDto result = new RefundNotifyResultDto();
        result.setShopId(shopId);
        result.setOutTradeNo(outTradeNo);
        result.setRefundId(refundNo);
        return result;
    }

    /**
     * 获取退款单号
     * @param message 退款回调参数
     * @return 退款单号
     */
    protected abstract String refundNo(Map<String,Object> message);
}
