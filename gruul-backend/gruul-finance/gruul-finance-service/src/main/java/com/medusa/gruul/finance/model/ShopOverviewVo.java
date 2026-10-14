package com.medusa.gruul.finance.model;

import com.medusa.gruul.order.api.model.TradeLineVo;
import com.medusa.gruul.order.api.model.TradeVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * The type Shop overview vo.
 *
 * @description: 店铺实时概况
 * @author: alan
 * @date: 2023/11/5 21:29
 */
@Data
@Schema(title = "店铺实时概况")
public class ShopOverviewVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 更新时间
     */
    @Schema(title = "更新时间")
    private String updateTime;
    /**
     * 昨日交易详情
     */
    @Schema(title = "昨日交易详情，默认查询返回，按照时间范围查询不返回")
    private TradeVo yesterdayTrade;
    /**
     * 交易详情
     */
    @Schema(title = "交易详情")
    private TradeVo trade;
    /**
     * 最近交易详情（默认显示本周）
     */
    @Schema(title = "最近交易详情（默认显示本周）")
    private List<TradeLineVo> tradeLineVos;
}
