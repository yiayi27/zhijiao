package com.medusa.gruul.order.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 生成订单消息体
 *
 * @author alan
 * @date 2023/10/6 14:08
 */
@Data
public class CreateOrderMessage implements Serializable {
    /**
     * 訂單id
     */
    private Long orderId;

    /**
     * 訂單信息
     */
    private CreateOrderDto orderVo;

    /**
     * 創建訂單的用戶信息
     */
    private OrderUser user;


}
