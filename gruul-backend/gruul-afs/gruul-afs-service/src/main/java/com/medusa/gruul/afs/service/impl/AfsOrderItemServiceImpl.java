package com.medusa.gruul.afs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.afs.api.entity.AfsOrderItem;
import com.medusa.gruul.afs.mapper.AfsOrderItemMapper;
import com.medusa.gruul.afs.service.IAfsOrderItemService;
import com.medusa.gruul.afs.service.IAfsOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 提货点申请的详情 服务实现类
 * </p>
 *
 * @author alan
 * @since 2024-08-21
 */
@Service
public class AfsOrderItemServiceImpl extends ServiceImpl<AfsOrderItemMapper, AfsOrderItem> implements IAfsOrderItemService {

    @Resource
    private IAfsOrderService afsOrderService;

    /**
     * 根据原始订单ID查询换货单的ID
     *
     * @param orderId 订单id
     * @return java.util.List<java.lang.Long>
     */
    @Override
    public List<Long> getExchangeOrder(Long orderId) {
        return baseMapper.selectExchangeOrderIdsByOriginalOrderId(orderId);
    }

    /**
     * 获取用户退款次数 -- 针对商品
     * @param orderId 订单id
     * @return java.util.HashMap<java.lang.String, java.lang.Object>
     */
    @Override
    public HashMap<String, Object> userApply(Long orderId) {
        List<Map<String, String>> maps = baseMapper.userApplyItem(orderId);
        Boolean afsExpire = afsOrderService.getAfsExpire(orderId);
        HashMap<String, Object> res = new HashMap<>(2);
        res.put("number", maps);
        res.put("expire", afsExpire);
        return res;
    }
}
