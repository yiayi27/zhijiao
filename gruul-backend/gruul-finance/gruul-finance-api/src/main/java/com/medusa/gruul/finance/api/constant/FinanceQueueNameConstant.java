package com.medusa.gruul.finance.api.constant;

/**
 * The interface Finance queue name constant.
 *
 * @description: OrderQueueNameConstant.java
 * @author: alan
 * @date: 2023/10/21 22:10
 */
public interface FinanceQueueNameConstant {


    /**
     * 订单已支付
     */
    String DATA_INIT = "gruul.finance.data.init";
    /**
     * 订单已支付
     */
    String ORDER_PAYED = "gruul.finance.order_payed";
    /**
     * 订单关闭
     */
    String ORDER_CLOSE = "gruul.finance.order_close";
    /**
     * 订单退款
     */
    String ORDER_RETURN = "gruul.finance.order_return";
    /**
     * 订单已签收
     */
    String ORDER_RECEIPT = "gruul.finance.order_receipt";
    /**
     * 完成订单
     */
    String ORDER_COMPLETED = "gruul.finance.order_completed";


}
