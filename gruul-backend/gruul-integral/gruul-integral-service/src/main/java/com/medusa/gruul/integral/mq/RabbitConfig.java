package com.medusa.gruul.integral.mq;

import com.medusa.gruul.common.core.config.Jackson;
import com.medusa.gruul.integral.api.constant.IntegralConstant;
import com.medusa.gruul.integral.api.constant.IntegralQueueEnum;
import com.medusa.gruul.integral.api.constant.IntegralQueueNameConstant;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.constant.OrderQueueEnum;
import com.medusa.gruul.payment.api.constant.PaymentExchangeConstant;
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

import java.util.HashMap;
import java.util.Map;

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
     * 用户服务积分交换机
     */
    @Bean
    DirectExchange accountDirect() {
        return ExchangeBuilder
                .directExchange(IntegralConstant.EXCHANGE_NAME)
                .durable(true)
                .build();
    }


    /**
     * 积分服务队列
     */
    @Bean
    public Queue integralQueue() {
        return new Queue(IntegralQueueEnum.QUEUE_INTEGRAL_DEDUCTION.getName(), true);
    }


    /**
     * 绑定积分 route key
     */
    @Bean
    Binding integralBinding(DirectExchange accountDirect, Queue integralQueue) {
        return BindingBuilder.
                bind(integralQueue).
                to(accountDirect).
                with(IntegralQueueEnum.QUEUE_INTEGRAL_DEDUCTION.getRouteKey());
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

    /**
     * 支付回调服务交换机
     */
    @Bean
    DirectExchange paymentDirect() {
        return ExchangeBuilder
                .directExchange(PaymentExchangeConstant.PAYMENT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    CustomExchange orderDelayDirect() {
        Map<String, Object> args = new HashMap<>(1);
        args.put("x-delayed-type", "direct");
        return new CustomExchange(OrderConstant.DELAY_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    /**
     * 订单 支付完成
     */
    @Bean
    public Queue orderPayedQueue() {
        return new Queue(IntegralQueueNameConstant.ORDER_PAY_OK, true);
    }

    /**
     * 将订单支付队列绑定到交换机
     */
    @Bean
    Binding orderPayedBinding(DirectExchange orderDirect, Queue orderPayedQueue) {
        return BindingBuilder
                .bind(orderPayedQueue)
                .to(orderDirect)
                .with(OrderQueueEnum.QUEUE_ORDER_PAYED.getRouteKey());
    }


    /**
     * 积分支付服务交换机
     */
    @Bean
    DirectExchange integralDirect() {
        return ExchangeBuilder.directExchange(IntegralConstant.PAYMENT_EXCHANGE).durable(true)
                .build();
    }


    /**
     * 订单 支付完成
     */
    @Bean
    public Queue integralOrderPayQueue() {
        return new Queue(IntegralQueueNameConstant.INTEGRAL_PAY, true);
    }

    /**
     * 订单支付超时取消
     */
    @Bean
    public Queue integralOrderPayFail() {
        return new Queue(IntegralQueueNameConstant.INTEGRAL_PAY_FAIL, true);
    }

    /**
     * 将订单支付队列绑定到交换机
     */
    @Bean
    Binding integralOrderPayBinding(DirectExchange paymentDirect, Queue integralOrderPayQueue) {
        return BindingBuilder
                .bind(integralOrderPayQueue)
                .to(paymentDirect)
                .with(IntegralQueueNameConstant.INTEGRAL_PAY);
    }

    /**
     * 订单支付超时绑定到交换机
     */
    @Bean
    public Binding integralOrderPayFileBinding(CustomExchange orderDelayDirect, Queue integralOrderPayFail) {
        return BindingBuilder
                .bind(integralOrderPayFail)
                .to(orderDelayDirect)
                .with(IntegralQueueNameConstant.INTEGRAL_PAY_FAIL).noargs();
    }

}
