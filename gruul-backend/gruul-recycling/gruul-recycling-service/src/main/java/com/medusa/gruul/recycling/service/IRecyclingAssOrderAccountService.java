package com.medusa.gruul.recycling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.ResultPageList;
import com.medusa.gruul.recycling.api.entity.RecyclingAssOrder;
import com.medusa.gruul.recycling.api.entity.RecyclingAssOrderAccount;
import com.medusa.gruul.recycling.model.vo.RecyclingAssOrderAccountStatisticalVo;
import com.medusa.gruul.recycling.model.vo.RecyclingAssOrderListVo;

import java.math.BigDecimal;

/**
 * <p>
 * 基金所得订单用户关联表 服务类
 * </p>
 *
 * @author whh
 * @since 2024-05-05
 */
public interface IRecyclingAssOrderAccountService extends IService<RecyclingAssOrderAccount> {


    /**
     * 获取店铺用户基金详情
     *
     * @param page     页数
     * @param size     条数
     * @param nickName 昵称
     * @param sortType 排序方式  1-基金值升序  2-基金值降序   3-按最近交易时间升序  4-按最近交易时间降序
     * @return com.medusa.gruul.recycling.model.vo.RecyclingOrderListVo
     */
    ResultPageList<RecyclingAssOrderListVo> recyclingAssOrderAccountList(Integer page, Integer size, String nickName, Integer sortType);

    /**
     * 用户数据新增
     *
     * @param recyclingAssOrder com.medusa.gruul.recycling.api.entity.RecyclingAssOrder
     */
    void addRecyclingAssOrder(RecyclingAssOrder recyclingAssOrder);

    /**
     * 基金详情统计
     *
     * @return com.medusa.gruul.recycling.model.vo.RecyclingAssOrderAccountStatisticalVo
     */
    RecyclingAssOrderAccountStatisticalVo recyclingAssOrderAccountStatistical();


    /**
     * 删除基金退款单
     *
     * @param recyclingAssOrder com.medusa.gruul.recycling.api.entity.RecyclingAssOrder
     * @param multiply          退款倍数
     * @param refundAmount      退款金额
     */
    void removeRecyclingAssOrder(RecyclingAssOrder recyclingAssOrder,BigDecimal multiply,BigDecimal refundAmount);
}
