package com.medusa.gruul.integral.api.constant;

import lombok.Getter;

/**
 * @description: 消息队列枚举配置
 * @author: xiaoq
 * @date: 2023/12/19 14:16
 */
@Getter
public enum IntegralQueueEnum {

	/**
	 * 积分增减
	 */
	QUEUE_INTEGRAL_DEDUCTION(IntegralConstant.EXCHANGE_NAME, IntegralQueueNameConstant.DEDUCTION_INTEGRAL,
			"gruul.account.integral.queue.change"),


	//==========================支付模块===================================================================

	/**
	 * 订单支付成功回调
	 */
	INTEGRAL_QUEUE_ORDER_PAYMENT_NOTIFY(IntegralConstant.PAYMENT_EXCHANGE, IntegralQueueNameConstant.PAYMENT_NOTIFY,
			"gruul.integralOrder.payment.notify"),
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

	IntegralQueueEnum(String exchange, String name, String routeKey) {
		this.exchange = exchange;
		this.name = name;
		this.routeKey = routeKey;
	}


}
