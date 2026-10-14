package com.medusa.gruul.finance.model;


import com.medusa.gruul.common.core.param.AbstractQueryParam;
import com.medusa.gruul.finance.api.enums.TradeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type Search trade detail dto.
 *
 * @description: SearchWithdrawalRecordDto.java
 * @author: alan
 * @date: 2024/2/22 20:53
 */
@Data
@Schema(title = "PC端查看对账单")
public class SearchTradeDetailDto extends AbstractQueryParam {


    @Schema(title = "开始日期：格式 2024-02-22 19:20:00")
    private String startTime;


    @Schema(title = "结束日期：格式 2024-02-22 19:20:00")
    private String endTime;


    @Schema(title = "资金流向:-1->全部;0->收入;1->支出", example = "0")
    private Integer flowType;

    /**
     * 交易类型
     */
    @Schema(title = "交易类型")
    private TradeTypeEnum type;

}
