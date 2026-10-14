package com.medusa.gruul.integral.api.constant;

/**
 * @Description: 积分模块队列
 * @Author: xiaoq
 * @Date: 2023/12/19 14:26
 */
public interface IntegralQueueNameConstant {

	/**
	 * 扣减积分
	 */
	String DEDUCTION_INTEGRAL = "gruul.account.integral.queue.change";


	/**
	 * 用户订单支付队列
	 */
    String ORDER_PAY_OK = "gruul.order.payed.accomplish";


	/**
	 * 支付回调
	 */
	String PAYMENT_NOTIFY = "gruul.order.payment.notify";

    /**
     * 积分订单支付
     */
    String INTEGRAL_PAY = "gruul.integral.payment.ok";

    /**
     * 积分支付失败
     */
    String INTEGRAL_PAY_FAIL = "gruul.integral.payment.fail";




    /**
	 * 完成订单 获取积分详情生成积分明细
	 */
	String ORDER_DETAIL = "gruul.order.completed";



    /**
     * 生成订单失败归还扣除积分
     */
	String CREATE_ORDER_FAIL = "gruul.order.create.fail";


//    /**
//     * 取消订单失败 扣除已归还的积分
//     */
//	String CancelOrderFail = "gruul.order.cancel.fail";




}
