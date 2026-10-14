package com.medusa.gruul.shops.api.enums;

import com.medusa.gruul.shops.api.constant.ExchangeConstant;
import com.medusa.gruul.shops.api.constant.QueueNameConstant;
import lombok.Getter;

/**
 * @author whh
 */
@Getter
public enum QueueEnum {

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

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
