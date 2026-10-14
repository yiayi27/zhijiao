package com.medusa.gruul.goods.mq;

import cn.hutool.core.date.DateUtil;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.goods.api.constant.ShoppingCartMyQueneName;
import com.medusa.gruul.goods.api.entity.ShoppingCart;
import com.medusa.gruul.goods.api.model.dto.manager.SupplierDto;
import com.medusa.gruul.platform.api.constant.ExchangeConstant;
import com.medusa.gruul.platform.api.constant.QueueNameConstant;
import com.medusa.gruul.platform.api.model.dto.SubscribeMsgSendDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * <p>
 * mq发送
 * </p>
 *
 * @author lcysike
 * @since 2023-11-19
 */
@Slf4j
@Component
public class Sender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 购物车变更信息发送微信订阅消息
     *
     * @param shoppingCartMessage 购物车消息
     */
    public void sendShoppingCartMessage(ShoppingCartMessage shoppingCartMessage) {
        ShoppingCart shoppingCart = new ShoppingCart();
        log.info(shoppingCart.toString());
        log.info("send message:" + shoppingCartMessage.toString());
        amqpTemplate.convertAndSend(ShoppingCartMyQueneName.GENERATE_SHOPPING_CART, shoppingCartMessage);
    }
}
