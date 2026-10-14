package com.medusa.gruul.datasets.api.constant;

import lombok.Getter;

/**
 * @Description: 数据服务队列 enum配置
 * @Author: xiaoq
 * @Date: 2024/5/7 15:38
 */
@Getter
public enum DataSetsQueueEnum {

	/**
	 * 新用户访问
	 */
	QUEUE_DATASETS_NEW_USER_VISIT(DataSetsConstant.EXCHANGE_NAME, DataSetsQueueNameConstant.NEW_USER_VISIT,
			"gruul.datasets.user.visit");


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

	DataSetsQueueEnum(String exchange, String name, String routeKey) {
		this.exchange = exchange;
		this.name = name;
		this.routeKey = routeKey;
	}
}
