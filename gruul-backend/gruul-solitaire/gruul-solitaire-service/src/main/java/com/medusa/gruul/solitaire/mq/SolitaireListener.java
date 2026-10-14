package com.medusa.gruul.solitaire.mq;

import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.solitaire.api.constant.SolitaireConstant;
import com.medusa.gruul.solitaire.service.api.IApiSolitaireActivityService;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lcysike
 * @Description: 订阅订单消息demo
 * @since 2024-07-07
 */
@Slf4j
@Component
public class SolitaireListener {

    @Resource
    private IApiSolitaireActivityService apiSolitaireActivityService;

    /**
     * 使用方法：
     * 1.创建自己队列 assemble.order.payed  模块名.业务名.动作
     * 2.通过路由键 gruul.order.payed 到交换机 gruul.order.direct 绑定自己创建的队列
     * 3.收到消息之后手动确认，不然消息会重复发送
     * <p>
     * QUEUE_ORDER_SHIPPED(OrderConstant.EXCHANGE_NAME, OrderQueueNameConstant.ORDER_SHIPPED, "gruul.order.shipped"),
     *
     * @param orderVo
     * @param properties
     * @param channel
     */
    @RabbitListener(queues = SolitaireConstant.ORDER_PAY_SUCCEED)
    public void payed(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        log.info("payed receive message:" + orderVo.toString());
        //存储接龙活动订单与商品信息
        apiSolitaireActivityService.saveCurrentActivityRecord(orderVo);
        //手动确认
        channel.basicAck(properties.getDeliveryTag(), true);
    }


    //    @RabbitListener(queues = OrderQueueNameConstant.ORDER_COMPLETED)
    //    public void completed(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
    //        log.info("completed receive message:" + orderVo.toString());
    //        //手动确认
    //        channel.basicAck(properties.getDeliveryTag(), true);
    //    }

}
