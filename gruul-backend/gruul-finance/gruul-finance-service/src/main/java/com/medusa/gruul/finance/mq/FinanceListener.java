package com.medusa.gruul.finance.mq;


import com.medusa.gruul.common.system.ISystem;
import com.medusa.gruul.finance.api.constant.FinanceQueueNameConstant;
import com.medusa.gruul.finance.service.IFinanceTradeDetailService;
import com.medusa.gruul.order.api.model.OrderVo;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @Description: 订阅订单消息demo
 * @Author: alan
 * @Date: 2023/10/6 13:49
 */
@Slf4j
@Component
public class FinanceListener {

    @Resource
    private IFinanceTradeDetailService financeTradeDetailService;


    /**
     * 使用方法：
     * 1.创建自己队列 assemble.order.payed  模块名.业务名.动作
     * 2.通过路由键 gruul.order.payed 到交换机 gruul.order.direct 绑定自己创建的队列
     * 3.收到消息之后手动确认，不然消息会重复发送
     *
     * @param orderVo
     * @param properties
     * @param channel
     * @return void
     * @author alan
     * @date 2023/12/1 11:07
     */
    @RabbitListener(queues = FinanceQueueNameConstant.ORDER_PAYED)
    public void payed(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        log.info("payed receive message:" + orderVo.toString());
        //插入交易流水记录
        ISystem.switchTenant(
                orderVo.getShopId(),
                //插入交易记录和商品交易明细
                () -> financeTradeDetailService.addTrade(orderVo)
        );

        //手动确认
        channel.basicAck(properties.getDeliveryTag(), true);

    }

    /**
     * 将商品的返利纳入账单的支出
     *
     * @param orderVo    商品
     * @param properties properties
     * @param channel    channel;
     */
    @RabbitListener(queues = FinanceQueueNameConstant.ORDER_COMPLETED)
    public void orderCompleted(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        financeTradeDetailService.addRebateDetail(orderVo);
        channel.basicAck(properties.getDeliveryTag(), true);
    }

    /**
     * 订单退款消息，订单退款记录
     *
     * @param orderVo
     * @param properties
     * @param channel
     * @return void
     * @author alan
     * @date 2024/3/17 21:57
     */
    @RabbitListener(queues = FinanceQueueNameConstant.ORDER_RETURN)
    public void orderReturn(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        log.info("return  message:" + orderVo.toString());
        //插入交易流水记录
        ISystem.switchTenant(
                orderVo.getShopId(),
                //插入交易记录和商品交易明细
                () -> financeTradeDetailService.addReturn(orderVo)
        );
        //手动确认
        channel.basicAck(properties.getDeliveryTag(), false);
    }

}

