package com.medusa.gruul.finance.api.constant;

import lombok.Getter;

/**
 * The enum Finance queue enum.
 *
 * @description: 消息队列枚举配置
 * @author: alan
 * @date: 2023/11/30 9:04
 */
@Getter
public enum FinanceQueueEnum {
    /**
     * 订单已支付
     */
    QUEUE_ORDER_PAYED(FinanceConstant.EXCHANGE_NAME, FinanceQueueNameConstant.ORDER_PAYED, ""),
    /**
     * 订单已签收
     */
    QUEUE_ORDER_RECEIPT(FinanceConstant.EXCHANGE_NAME, FinanceQueueNameConstant.ORDER_RECEIPT, ""),
    /**
     * 完成订单
     */
    QUEUE_ORDER_COMPLETED(FinanceConstant.EXCHANGE_NAME, FinanceQueueNameConstant.ORDER_COMPLETED, ""),
    //================================控制台模块==========================================================================
    /**
     * 初始化订单设置
     */
    QUEUE_DATA_INIT(FinanceConstant.ACCOUNT_EXCHANGE, FinanceQueueNameConstant.DATA_INIT, "gruul.finance.data.init"),


    ;
    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    FinanceQueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }

}
