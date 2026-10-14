package com.medusa.gruul.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.finance.api.entity.FinanceTradeDetail;
import com.medusa.gruul.order.api.model.TradeLineVo;
import com.medusa.gruul.order.api.model.TradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 资金交易明细表 Mapper 接口
 * </p>
 *
 * @author alan
 * @since 2024-02-22
 */
public interface FinanceTradeDetailMapper extends BaseMapper<FinanceTradeDetail> {

    /**
     * Gets trade vo by time.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the trade vo by time
     */
    TradeVo getTradeVoByTime(@Param(value = "startTime") String startTime, @Param(value = "endTime") String endTime);

    /**
     * Gets trade vo group by date.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the trade vo group by date
     */
    List<TradeLineVo> getTradeVoGroupByDate(@Param(value = "startTime") String startTime,
                                            @Param(value = "endTime") String endTime);
}
