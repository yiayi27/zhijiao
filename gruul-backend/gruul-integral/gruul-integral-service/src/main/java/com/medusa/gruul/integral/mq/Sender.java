package com.medusa.gruul.integral.mq;

import com.medusa.gruul.account.api.model.IntegraChangelDto;
import com.medusa.gruul.integral.api.constant.IntegralQueueEnum;
import com.medusa.gruul.integral.api.constant.IntegralQueueNameConstant;
import com.medusa.gruul.integral.api.model.IntegralOrderDTO;
import com.medusa.gruul.order.api.constant.OrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: xiaoq
 * @Date: 2023/12/19 11:36
 */

@Slf4j
@Component
public class Sender {

    @Autowired
    private AmqpTemplate amqpTemplate;


    /**
     * 积分增减操作
     *
     * @param integraChangelDto
     */
    public void sendIntegralDeductionMessage(IntegraChangelDto integraChangelDto) {
        log.info("receive message:" + integraChangelDto.toString());
        convertAndSend(integraChangelDto, IntegralQueueEnum.QUEUE_INTEGRAL_DEDUCTION);
    }

    private void convertAndSend(Object message, IntegralQueueEnum queue) {
        amqpTemplate.convertAndSend(queue.getExchange(), queue.getRouteKey(), message);
    }


    public void delayIntegralOrder(IntegralOrderDTO integralOrder, long seconds) {
        amqpTemplate.convertAndSend(
                OrderConstant.DELAY_EXCHANGE_NAME,
                IntegralQueueNameConstant.INTEGRAL_PAY_FAIL,
                integralOrder,
                (message) -> {
                    message.getMessageProperties().setHeader(MessageProperties.X_DELAY, seconds * 1000);
                    return message;
                }
        );
    }


}
