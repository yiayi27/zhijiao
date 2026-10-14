package com.medusa.gruul.datasets.mq;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.medusa.gruul.datasets.api.constant.DataSetsQueueNameConstant;
import com.medusa.gruul.datasets.api.model.DataSetsAddMessage;
import com.medusa.gruul.datasets.service.IDcShopActiveAnalyseService;
import com.medusa.gruul.datasets.service.IDcShopRankingService;
import com.medusa.gruul.order.api.model.OrderVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @Description: 数据中心队列监听
 * @Author: xiaoq
 * @Date: 2024/05/06 14:40
 */
@Slf4j
@Component
public class DataSetsListener {

    @Autowired
    private IDcShopRankingService dcShopRankingService;

    @Autowired
    private IDcShopActiveAnalyseService dcShopActiveAnalyseService;
    //===============================订阅======================================//

    /**
     * 监听订单状态 并插入数据  状态 :订单支付成功
     *
     * @param orderVo
     * @param properties
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = DataSetsQueueNameConstant.ORDER_PAY_OK)
    public void receiveSendPayedOrderMessage(OrderVo orderVo, MessageProperties properties, Channel channel)
            throws IOException {
        log.info("receive message:" + orderVo.toString());
        if (ObjectUtil.isNull(orderVo)) {
            //手动确认
            channel.basicAck(properties.getDeliveryTag(), true);
            return;
        }
        dcShopRankingService.orderPayFinish(orderVo);
        //手动确认
        channel.basicAck(properties.getDeliveryTag(), true);
    }


    @RabbitListener(queues = DataSetsQueueNameConstant.NEW_USER_VISIT)
    public void uvAdd(DataSetsAddMessage dataSetsAddMessage, MessageProperties properties, Channel channel) throws IOException {
        log.info("收到新用户访问消息:当前时间{},消息内容{}", DateUtil.now(), dataSetsAddMessage.toString());
        try {
            String shopId = dataSetsAddMessage.getShopId();
            dcShopActiveAnalyseService.saveCallerInfo(shopId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        channel.basicAck(properties.getDeliveryTag(), false);
    }

}
