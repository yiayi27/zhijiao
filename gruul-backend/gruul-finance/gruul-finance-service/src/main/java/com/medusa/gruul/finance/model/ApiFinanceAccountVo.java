package com.medusa.gruul.finance.model;

import cn.hutool.core.util.NumberUtil;
import com.medusa.gruul.finance.api.entity.FinanceAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type Api finance account vo.
 *
 * @description: 小程序端个人佣金详情
 * @author: alan
 * @date: 2023/11/5 21:29
 */
@Data
@Schema(title = "小程序端个人佣金详情")
public class ApiFinanceAccountVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 累计佣金
     */
    @Schema(title = "累计佣金")
    private BigDecimal brokerageTotal;

    /**
     * 可提现佣金
     */
    @Schema(title = "可提现佣金")
    private BigDecimal brokerage;

    /**
     * 已提现佣金
     */
    @Schema(title = "已提现佣金")
    private BigDecimal withdrawalBrokerage;

    /**
     * 待审核佣金
     */
    @Schema(title = "待审核佣金")
    private BigDecimal waitReviewedBrokerage;

    /**
     * 预计佣金（待结算佣金）
     */
    @Schema(title = "预计佣金（待结算佣金）")
    private BigDecimal brokerageFrozen;


    /**
     * To vo api finance account vo.
     *
     * @param account             the account
     * @param withdrawalBrokerage the withdrawal brokerage
     * @return the api finance account vo
     */
    public static ApiFinanceAccountVo toVo(FinanceAccount account, BigDecimal withdrawalBrokerage) {
        ApiFinanceAccountVo apiFinanceAccountVo = new ApiFinanceAccountVo();
        apiFinanceAccountVo.setBrokerageTotal(NumberUtil.sub(account.getBrokerageTotal(), account.getBrokerage()));
        apiFinanceAccountVo.setBrokerage(account.getBrokerage());
        apiFinanceAccountVo.setBrokerageFrozen(account.getBrokerageFrozen());
        apiFinanceAccountVo.setWithdrawalBrokerage(withdrawalBrokerage);
        return apiFinanceAccountVo;
    }
}
