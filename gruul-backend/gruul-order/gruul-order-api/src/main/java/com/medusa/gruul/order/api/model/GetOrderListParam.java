package com.medusa.gruul.order.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * 查询订单列表参数
 * </p>
 *
 * @author alan
 * @since 2024/1/18
 */
@Data
@NoArgsConstructor
public class GetOrderListParam {
    private String shopId;
    private List<Long> orderIds;

    public GetOrderListParam(String shopId, List<Long> orderIds) {
        this.shopId = shopId;
        this.orderIds = orderIds;
    }
}
