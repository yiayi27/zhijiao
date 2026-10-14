package com.medusa.gruul.afs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.afs.api.entity.AfsOrderItem;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 申请的详情 服务类
 * </p>
 *
 * @author alan
 * @since 2024-08-21
 */
public interface IAfsOrderItemService extends IService<AfsOrderItem> {

    /**
     * 根据原始订单ID查询换货单的ID
     *
     * @param orderId 订单id
     * @return java.util.List<java.lang.Long>
     */
    List<Long> getExchangeOrder(Long orderId);

    /**
     * 获取用户退款次数 -- 针对商品
     * @param orderId 订单id
     * @return java.util.HashMap<java.lang.String, java.lang.Object>
     */
    HashMap<String, Object> userApply(Long orderId);
}
