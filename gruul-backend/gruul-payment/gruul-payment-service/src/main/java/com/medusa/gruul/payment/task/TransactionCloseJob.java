package com.medusa.gruul.payment.task;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.medusa.gruul.common.core.util.LocalDateTimeUtils;
import com.medusa.gruul.payment.api.constant.StatusConstant;
import com.medusa.gruul.payment.api.entity.Payment;
import com.medusa.gruul.payment.service.IPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 关闭超时订单
 *
 * @author whh
 */
@Component
@Slf4j
public class TransactionCloseJob {

    @Autowired
    private IPaymentService paymentService;


    @Scheduled(cron = "0 0/1 * * * ?")
    public void execute() {
        log.debug("执行定时任务");
        List<Payment> list = paymentService.list(
                new QueryWrapper<Payment>().lt("timeout_express", LocalDateTimeUtils.convertDateToLdt(new Date()))
                        .eq("trade_status", StatusConstant.TradeStatus.WAIT_BUYER_PAY));
        if (CollectionUtil.isNotEmpty(list)) {
            log.debug("更新已过订单有效期的支付记录状态为交易关闭!");
            List<Long> idList = list.stream().map(Payment::getId).toList();
            Payment payment = new Payment();
            payment.setTradeStatus(StatusConstant.TradeStatus.TRADE_CLOSED);
            boolean flag = paymentService.update(payment, new QueryWrapper<Payment>().in("id", idList));
            log.debug("需更新的ids: ".concat(idList.toString()));
            log.debug("执行状态: ".concat(Boolean.toString(flag)));
        }
    }
}
