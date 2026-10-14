package com.medusa.gruul.integral.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @Description: 积分订单枚举状态
 * @Author: xiaoq
 * @Date: 2023/11/28 9:03
 */
@Getter
public enum IntegralOrderStatusEnum {

	/**
	 * 积分商品订单 不包含未支付 未评价等状态
	 */
	ALL(-1, "所有订单"),

	WAIT_FOR_SEND(0, "待发货"),

	SHIPPED(1, "已发货"),

	COMPLETE(2, "订单已完成"),

	UNPAID(99, "未支付的订单");

	@EnumValue
	/**
	 * 值
	 */ private final int status;

	/**
	 * 描述
	 */
	private final String desc;

	IntegralOrderStatusEnum(int status, String desc) {
		this.status = status;
		this.desc = desc;
	}

}
