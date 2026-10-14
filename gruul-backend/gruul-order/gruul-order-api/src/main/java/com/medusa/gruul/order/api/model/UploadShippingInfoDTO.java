package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2025/4/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UploadShippingInfoDTO implements Serializable {

    /**
     * 订单信息
     */
    private OrderVo order;

    /**
     * 发货信息
     */
    private OrderDeliveryDto orderDelivery;
}
