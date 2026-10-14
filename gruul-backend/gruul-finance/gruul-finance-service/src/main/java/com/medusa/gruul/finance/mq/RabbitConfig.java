package com.medusa.gruul.finance.mq;

import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.finance.api.constant.FinanceConstant;
import com.medusa.gruul.finance.api.constant.FinanceQueueEnum;
import com.medusa.gruul.finance.api.constant.FinanceQueueNameConstant;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.constant.OrderQueueEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

/**
 * @Description: RabbitConfig.java
 * @Author: alan
 * @Date: 2023/10/6 14:01
 */
@Slf4j
@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());
        return messageHandlerMethodFactory;
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter(Jackson.mapper);
    }

    @Bean
    public AmqpTemplate amqpTemplate() {
        // 使用jackson 消息转换器
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setEncoding("UTF-8");
        // 消息发送失败返回到队列中，yml需要配置 publisher-returns: true
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(returned -> {
            String correlationId = returned.getMessage().getMessageProperties().getCorrelationId();
            log.info("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, returned.getReplyCode(), returned.getReplyText(), returned.getExchange(),
                    returned.getRoutingKey());
        });
        // 消息确认，yml需要配置 publisher-confirms: true
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送到exchange成功,id: {}", correlationData.getId());
            } else {
                log.info("消息发送到exchange失败,原因: {}", cause);
            }
        });
        return rabbitTemplate;
    }

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange accountDirect() {
        return ExchangeBuilder
                .directExchange(FinanceConstant.ACCOUNT_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange financeDirect() {
        return ExchangeBuilder
                .directExchange(FinanceConstant.EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange orderDirect() {
        return ExchangeBuilder
                .directExchange(OrderConstant.EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue orderPayed() {
        return new Queue(FinanceQueueNameConstant.ORDER_PAYED, true);
    }

    @Bean
    public Queue orderClose() {
        return new Queue(FinanceQueueNameConstant.ORDER_CLOSE, true);
    }


    @Bean
    public Queue orderReceipt() {
        return new Queue(FinanceQueueNameConstant.ORDER_RECEIPT, true);
    }

    @Bean
    public Queue orderReturn() {
        return new Queue(FinanceQueueNameConstant.ORDER_RETURN, true);
    }

    @Bean
    public Queue orderCompleted() {
        return new Queue(FinanceQueueNameConstant.ORDER_COMPLETED, true);
    }

    @Bean
    public Queue orderDataInitQueue() {
        return new Queue(FinanceQueueNameConstant.DATA_INIT, true);
    }

    /**
     * 将订单支付队列绑定到交换机
     */
    @Bean
    Binding orderPayedBinding(DirectExchange orderDirect, Queue orderPayed) {
        return BindingBuilder
                .bind(orderPayed)
                .to(orderDirect)
                .with(OrderQueueEnum.QUEUE_ORDER_PAYED.getRouteKey());
    }

    /**
     * 将订单退款队列绑定到交换机
     */
    @Bean
    Binding orderReturnBinding(DirectExchange orderDirect, Queue orderReturn) {
        return BindingBuilder
                .bind(orderReturn)
                .to(orderDirect)
                .with(OrderQueueEnum.QUEUE_ORDER_RETURN.getRouteKey());
    }

    /**
     * 将订单关闭队列绑定到交换机
     */
    @Bean
    Binding orderCloseBinding(DirectExchange orderDirect, Queue orderClose) {
        return BindingBuilder
                .bind(orderClose)
                .to(orderDirect)
                .with(OrderQueueEnum.QUEUE_ORDER_CLOSE.getRouteKey());
    }

    /**
     * 将订单签收队列绑定到交换机
     */
    @Bean
    Binding orderReceiptBinding(DirectExchange orderDirect, Queue orderReceipt) {
        return BindingBuilder
                .bind(orderReceipt)
                .to(orderDirect)
                .with(OrderQueueEnum.QUEUE_ORDER_RECEIPT.getRouteKey());
    }

    /**
     * 将订单完成队列绑定到交换机
     */
    @Bean
    Binding orderCompletedBinding(DirectExchange orderDirect, Queue orderCompleted) {
        return BindingBuilder
                .bind(orderCompleted)
                .to(orderDirect)
                .with(OrderQueueEnum.QUEUE_ORDER_COMPLETED.getRouteKey());
    }

    /**
     * 将k数据初始化绑定到控制台模块
     */
    @Bean
    Binding orderDataInitBinding(DirectExchange accountDirect, Queue orderDataInitQueue) {
        return BindingBuilder
                .bind(orderDataInitQueue)
                .to(accountDirect)
                .with(FinanceQueueEnum.QUEUE_DATA_INIT.getRouteKey());
    }

}
