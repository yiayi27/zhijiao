package com.medusa.gruul.integral.mq;

import cn.hutool.core.util.ObjectUtil;
import com.medusa.gruul.integral.api.constant.IntegralQueueNameConstant;
import com.medusa.gruul.integral.api.model.IntegralOrderDTO;
import com.medusa.gruul.integral.service.IIntegralDetailService;
import com.medusa.gruul.integral.service.IIntegralOrderService;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.payment.api.model.dto.WxPayNotifyResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 积分队列监听
 *
 * @author xiaoq
 * Date : 2023/12/19 11:16
 */
@Slf4j
@Component
public class IntegralListener {


    @Autowired
    private IIntegralDetailService integralDetailService;

    @Autowired
    private IIntegralOrderService integralOrderService;

    //===============================订阅======================================//

    /**
     * 订单完成创建积分明细及通知用户加积分
     *
     * @param orderVo 订单详情
     */
    @RabbitListener(queues = IntegralQueueNameConstant.ORDER_PAY_OK, ackMode = "AUTO")
    public void receiveSendPayedOrderMessage(OrderVo orderVo) {
        log.info("receive message:" + orderVo.toString());
        if (ObjectUtil.isNull(orderVo)) {
            //手动确认
            return;
        }
        try {
            integralDetailService.orderFinish(orderVo);
        } catch (Exception ex) {
            log.error("用户增加积分消费失败", ex);
        }
    }

    /**
     * 积分订单支付成功
     *
     * @param wxPayNotifyResultDto 支付成功回调参数
     */
    @RabbitListener(queues = IntegralQueueNameConstant.INTEGRAL_PAY, ackMode = "AUTO")
    public void integralPayOkMessage(WxPayNotifyResultDto wxPayNotifyResultDto) {
        try {
            integralOrderService.integralOrderDone(Long.valueOf(wxPayNotifyResultDto.getOutTradeNo()));
        } catch (Exception ex) {
            log.error("积分订单支付成功回调失败", ex);
        }
    }

    /**
     * 积分订单超时未支付
     */
    @RabbitListener(queues = IntegralQueueNameConstant.INTEGRAL_PAY_FAIL, ackMode = "AUTO")
    public void integralPayFail(IntegralOrderDTO integralOrder) {
        if (integralOrder == null || integralOrder.getOrderId() == null) {
            return;
        }
        try {
            integralOrderService.delIntegralOrder(integralOrder.getOrderId());
        } catch (Exception e) {
            log.error("err:", e);
        }
    }
}
