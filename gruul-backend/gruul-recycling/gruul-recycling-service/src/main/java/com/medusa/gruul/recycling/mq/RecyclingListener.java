package com.medusa.gruul.recycling.mq;

import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.recycling.constant.MqQueueNameConstant;
import com.medusa.gruul.recycling.service.IRecyclingAssOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author whh
 * @description 用户队列监听
 * @date: 2023/12/11
 */
@Slf4j
@Component
public class RecyclingListener {

    @Autowired
    private IRecyclingAssOrderService recyclingAssOrderService;


    @RabbitListener(queues = MqQueueNameConstant.ORDER_PAY_OK_QUEUE_CHANGE)
    public void orderPayOk(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        log.info(" orderPayOk  receive message:  orderId = {}", orderVo.getId());
        //手动确认ack
        log.info("order productTotalQuantity number = {}", orderVo.getProductTotalQuantity());
        log.info("order status message = {}", orderVo.getStatus());
        recyclingAssOrderService.orderPayOk(orderVo);
        channel.basicAck(properties.getDeliveryTag(), true);
    }


    @RabbitListener(queues = MqQueueNameConstant.ORDER_RETURN_QUEUE_CHANGE)
    public void refundNotify(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        log.info(" refundNotify  receive message:  orderId = {}", orderVo.getId());
        try {
            log.info("order productTotalQuantity number = {}", orderVo.getProductTotalQuantity());
            log.info("order status message = {}", orderVo.getStatus());
            recyclingAssOrderService.refundNotify(orderVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        channel.basicAck(properties.getDeliveryTag(), true);
    }
}
