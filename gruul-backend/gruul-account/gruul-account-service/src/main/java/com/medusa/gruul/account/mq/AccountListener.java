package com.medusa.gruul.account.mq;

import cn.hutool.core.util.ObjectUtil;
import com.medusa.gruul.account.api.constant.AccountQueueNameConstant;
import com.medusa.gruul.account.api.model.IntegraChangelDto;
import com.medusa.gruul.account.api.model.MemberBalanceUpdateDto;
import com.medusa.gruul.account.service.*;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.payment.api.model.dto.WxPayNotifyResultDto;
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
public class AccountListener {

    @Autowired
    private IMiniAccountExtendsService miniAccountExtendsService;

    @Autowired
    private IApiMiniAccountCollectService miniAccountCollectService;

    @Autowired
    private IMemberCardInfoService memberCardInfoService;

    @Autowired
    private IMemberBalanceRecordService memberBalanceRecordService;


    /**
     * 用户积分增减对象
     *
     * @param integraChangelDto 积分操作对象
     * @param properties        org.springframework.amqp.core.MessageProperties
     * @param channel           com.rabbitmq.client.Channel
     */
    @RabbitListener(queues = AccountQueueNameConstant.ACCOUNT_INTEGRAL_QUEUE_CHANGE)
    public void integralChangeReceive(IntegraChangelDto integraChangelDto, MessageProperties properties, Channel channel) throws IOException {
        log.info("receive message:" + integraChangelDto.toString());
        try {
            miniAccountExtendsService.integralValueChange(integraChangelDto);
        } catch (Exception e) {
            log.info("积分队列操作异常捕获,防止无限抛出异常");
        }
        channel.basicAck(properties.getDeliveryTag(), false);

    }


    /**
     * 订单支付成功->修改用户最后交易时间
     *
     * @param orderVo    com.medusa.gruul.order.api.model.OrderVo
     * @param properties org.springframework.amqp.core.MessageProperties
     * @param channel    com.rabbitmq.client.Channel
     * @throws IOException
     */
    @RabbitListener(queues = AccountQueueNameConstant.ACCOUNT_ORDER_PAY_OK_QUEUE_CHANGE)
    public void orderPayOk(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        log.info("orderPayOk receive message:" + orderVo.toString());
        if (ObjectUtil.isNull(orderVo)) {
            //手动确认
            channel.basicAck(properties.getDeliveryTag(), true);
            return;
        }
        miniAccountExtendsService.modifyLastDealTime(orderVo.getUserId(), orderVo.getPayTime());
        //手动确认
        channel.basicAck(properties.getDeliveryTag(), true);
    }


    /**
     * 订单完成通知;指定用户增加消费次数和消费金额
     *
     * @param orderVo    com.medusa.gruul.order.api.model.OrderVo
     * @param properties org.springframework.amqp.core.MessageProperties
     * @param channel    com.rabbitmq.client.Channel
     * @throws IOException
     */
    @RabbitListener(queues = AccountQueueNameConstant.ACCOUNT_ORDER_COMPLETE_OK_QUEUE_CHANGE)
    public void orderCompleted(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        log.info("completed receive message:" + orderVo.toString());
        if (ObjectUtil.isNull(orderVo)) {
            //手动确认
            channel.basicAck(properties.getDeliveryTag(), true);
            return;
        }
        miniAccountExtendsService.orderCompleted(orderVo);
        //手动确认
        channel.basicAck(properties.getDeliveryTag(), true);
    }

    /**
     * 更新用户收藏 sql表数据
     *
     * @param collectMessage
     * @param properties
     * @param channel
     */
    @RabbitListener(queues = AccountQueueNameConstant.ACCOUNT_COLLECT)
    public void userCollect(CollectMessage collectMessage, MessageProperties properties, Channel channel) throws IOException {
        log.info(" receive message:" + collectMessage.toString());
        //更新或插入 用户收藏数据
        miniAccountCollectService.updateAccountCollect(collectMessage);
        channel.basicAck(properties.getDeliveryTag(), false);
    }


    /**
     * 生成默认账号
     *
     * @param jsonData json字符串
     * @deprecated 确定是否仍在使用
     */
    @RabbitListener(queues = AccountQueueNameConstant.ACCOUNT_DEFAULT, ackMode = "AUTO")
    public void accountDefault(String jsonData) {
        log.info(" receive message:" + jsonData);
//        miniAccountService.generateAccountDefault(jsonData);
    }


    /**
     * 会员付费开卡支付成功回调
     *
     * @Description 回调函数，支付成功后绑定会员
     * @Author zhaokw
     * @Date 13:11 2024\6\6 0006
     **/
    @RabbitListener(queues = AccountQueueNameConstant.MEMBER_PAY_OK_QUEUE_CHANGE, ackMode = "AUTO")
    public void memberCompleted(WxPayNotifyResultDto wxPayNotifyResultDto) {
        log.info(" receive message:" + wxPayNotifyResultDto.toString());
        try {
            memberCardInfoService.memberCompleted(wxPayNotifyResultDto);
        } catch (Exception e) {
            log.info("会员信息更新失败,防止无限抛出异常");
        }
    }

    /**
     * 会员取消扣款
     *
     * @param
     */
    @RabbitListener(queues = AccountQueueNameConstant.MEMBER_DEFAULT, ackMode = "AUTO")
    public void cancelMemberBalanceUpdate(MemberBalanceUpdateDto memberBalanceUpdateDto) {
        log.info(" receive message:" + memberBalanceUpdateDto.toString());
        memberBalanceRecordService.memberBalanceModify(memberBalanceUpdateDto);
    }

    /**
     * 订单交易成功，增加用户返利金额
     *
     * @param orderVo    订单数据
     * @param properties
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = AccountQueueNameConstant.MEMBER_REBATE_PAY_OK_QUEUE_CHANGE)
    public void memberRebatePayedListener(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        log.info("memberRebatePayed receive message:" + orderVo.toString());
        memberBalanceRecordService.memberRebatePayed(orderVo);
        //手动确认
        channel.basicAck(properties.getDeliveryTag(), true);
    }

    /**
     * 订单交易成功，结算对应分销员分销金额
     *
     * @param orderVo    订单数据
     * @param properties
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = AccountQueueNameConstant.MEMBER_DISTRIBUTION_OK_QUEUE_CHANGE)
    public void memberDistributionCompletedListener(OrderVo orderVo, MessageProperties properties, Channel channel) throws IOException {
        log.info("memberDistributionCompleted receive message:" + orderVo.toString());
        memberBalanceRecordService.userDistributionCompleted(orderVo);
        //手动确认
        channel.basicAck(properties.getDeliveryTag(), true);
    }


    /**
     * 生成初始化sql
     *
     * @param shopId 店铺id
     */
    @RabbitListener(queues = AccountQueueNameConstant.MEMBER_INIT_QUEUE_CHANGE, ackMode = "AUTO")
    public void memberInit(String shopId,MessageProperties properties, Channel channel) throws IOException {
        log.info(" receive message:" + shopId);
        memberCardInfoService.memberInit(shopId);
        //手动确认
        channel.basicAck(properties.getDeliveryTag(), true);
    }
}
