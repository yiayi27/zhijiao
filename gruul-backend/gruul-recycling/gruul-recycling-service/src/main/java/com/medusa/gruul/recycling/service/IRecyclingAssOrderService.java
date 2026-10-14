package com.medusa.gruul.recycling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.ResultPageList;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.recycling.api.entity.RecyclingAssOrder;
import com.medusa.gruul.recycling.model.vo.MeRecyclingAssOrderListVo;

/**
 * <p>
 * 基金所得订单关联表 服务类
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
public interface IRecyclingAssOrderService extends IService<RecyclingAssOrder> {

    /**
     * 用户下单成功，基金订单生成
     *
     * @param orderVo 订单对象
     */
    void orderPayOk(OrderVo orderVo);

    /**
     * 获取用户基金订单
     *
     * @param page 页数
     * @param size 条数
     * @return com.medusa.gruul.recycling.model.vo.MeRecyclingAssOrderList
     */
    ResultPageList<MeRecyclingAssOrderListVo> recyclingAssOrderAccountList(Integer page, Integer size);

    /**
     * 订单退款通知
     *
     * @param orderVo com.medusa.gruul.order.api.model.OrderVo
     */
    void refundNotify(OrderVo orderVo);
}
