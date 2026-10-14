package com.medusa.gruul.integral.api.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 积分订单
 * @author xiaoq
 */
@Data
@Accessors(chain = true)
public class IntegralOrderDTO {
    private Long orderId;
}
