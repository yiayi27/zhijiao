package com.medusa.gruul.payment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.common.bean.PayMessage;
import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.common.core.exception.ServiceException;
import com.medusa.gruul.common.core.handler.Handler;
import com.medusa.gruul.common.core.util.SpringUtils;
import com.medusa.gruul.payment.api.entity.PaymentRefund;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.api.enums.PaymentQueueEnum;
import com.medusa.gruul.payment.api.model.dto.RefundNotifyResultDto;
import com.medusa.gruul.payment.api.model.dto.RefundRequestDto;
import com.medusa.gruul.payment.common.annotation.RefundHandler;
import com.medusa.gruul.payment.common.annotation.RefundNotifyHandler;
import com.medusa.gruul.payment.common.model.OrderPaymentRecord;
import com.medusa.gruul.payment.service.IPaymentRefundService;
import com.medusa.gruul.payment.service.MultiPayOrderService;
import com.medusa.gruul.payment.service.MultiPayRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2022/1/25
 */
@Service
@RequiredArgsConstructor
public class MultiPayRefundServiceImpl implements MultiPayRefundService {

    private final MultiPayOrderService multiPayOrderService;


    @Override
    public void refund(RefundRequestDto refundRequest) {
        OrderPaymentRecord record = multiPayOrderService.paymentRecord(refundRequest.getOrderId());
        String notifyParam;
        if (StrUtil.isBlank(notifyParam = record.getNotifyParam())) {
            throw new ServiceException("缺失支付记录或回调记录,无法退款");
        }
        /*
         * 获取处理器
         */
        Handler<Void> handler = SpringUtils.getBean(RefundHandler.class,record.getPayType());
        if (handler == null){
            throw new ServiceException("系统异常,缺失处理器");
        }
        handler.handle(refundRequest,record.getDetailsId(),notifyParam);
    }

    @Override
    public void refundNotify(PayType payType, PayMessage message) {
        Handler<Void> handler = SpringUtils.getBean(RefundNotifyHandler.class,payType);
        if (handler == null){
            throw new ServiceException("系统异常,缺失处理器");
        }
        handler.handle(message);
    }
}
