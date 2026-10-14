package com.medusa.gruul.datasets.mq;


import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.datasets.api.constant.DataSetsConstant;
import com.medusa.gruul.datasets.api.constant.DataSetsQueueEnum;
import com.medusa.gruul.datasets.api.constant.DataSetsQueueNameConstant;
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


//==================================数据服务=================================================

    /**
     * 数据服务 消息消费队列绑定的交换机
     *
     * @return
     */
    @Bean
    DirectExchange dataSetsDirect() {
        return ExchangeBuilder.
                directExchange(DataSetsConstant.EXCHANGE_NAME).
                durable(true).
                build();
    }

    /**
     * 新用户访问队列
     *
     * @return
     */
    @Bean
    public Queue dataSetsNewUserVisitQueue() {
        return new Queue(DataSetsQueueNameConstant.NEW_USER_VISIT, true);
    }

    /**
     * 绑定RouteKey 将新用户访问队列绑定到交换机
     *
     * @return
     */
    @Bean
    Binding newUserVisitBinding(DirectExchange dataSetsDirect, Queue dataSetsNewUserVisitQueue) {
        return BindingBuilder.bind(dataSetsNewUserVisitQueue).to(dataSetsDirect)
                .with(DataSetsQueueEnum.QUEUE_DATASETS_NEW_USER_VISIT.getRouteKey());
    }

//======================================订单服务==============================================
//======================================订单服务==============================================

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

    /**
     * 订单支付成功 队列 (数据服务)
     */
    @Bean
    public Queue orderPayCompleteQueue() {
        return new Queue(DataSetsQueueNameConstant.ORDER_PAY_OK, true);
    }

    /**
     * 将订单完成队列绑定到交换机
     */
    @Bean
    Binding orderAccomplishBinding(DirectExchange orderDirect, Queue orderPayCompleteQueue) {
        return BindingBuilder
                .bind(orderPayCompleteQueue)
                .to(orderDirect)
                .with(OrderQueueEnum.QUEUE_ORDER_PAYED.getRouteKey());
    }


}
