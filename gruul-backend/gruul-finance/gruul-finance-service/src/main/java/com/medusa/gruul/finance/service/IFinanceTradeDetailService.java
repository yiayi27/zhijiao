package com.medusa.gruul.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.common.core.util.PageUtils;
import com.medusa.gruul.finance.api.entity.FinanceTradeDetail;
import com.medusa.gruul.finance.model.SearchTradeDetailDto;
import com.medusa.gruul.finance.model.ShopOverviewVo;
import com.medusa.gruul.finance.model.TradeOverviewVo;
import com.medusa.gruul.order.api.model.OrderVo;
import com.medusa.gruul.order.api.model.TradeLineVo;
import com.medusa.gruul.order.api.model.TradeVo;

import java.util.List;

/**
 * <p>
 * 资金交易明细表 服务类
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
public interface IFinanceTradeDetailService extends IService<FinanceTradeDetail> {

    /**
     * 分页获取交易明细
     *
     * @param dto the dto
     * @return the trade detail page
     */
    PageUtils<FinanceTradeDetail> getTradeDetailPage(SearchTradeDetailDto dto);

    /**
     * 获取对账单
     *
     * @param dto the dto
     * @return the trade overview
     */
    TradeOverviewVo getTradeOverview(SearchTradeDetailDto dto);

    /**
     * 添加交易明细
     *
     * @param orderVo the order vo
     */
    void addTrade(OrderVo orderVo);

    /**
     * 添加订单退款记录
     *
     * @param orderVo
     * @return boolean
     */
    void addReturn(OrderVo orderVo);

    /**
     * 根据起止时间查询交易明细
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the trade vo by time
     */
    TradeVo getTradeVoByTime(String startDate, String endDate);

    /**
     * 根据起止时间分组查询交易明细
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the trade vo group by date
     */
    List<TradeLineVo> getTradeVoGroupByDate(String startTime, String endTime);

    /**
     * 获取商铺概览
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the overview
     */
    ShopOverviewVo getOverview(String startTime, String endTime);


    /**
     * 商品评价后增加返利数据信息 纳入账单支出
     * @param vo  订单详情
     *
     * @author 张治保
     */
    void addRebateDetail(OrderVo vo);

}
