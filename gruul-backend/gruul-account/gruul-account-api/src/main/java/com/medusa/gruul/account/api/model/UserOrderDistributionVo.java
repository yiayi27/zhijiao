package com.medusa.gruul.account.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单用户对应分销人员相关信息
 *
 * @author lcysike
 * @description
 * @data: 2024/11/11
 */
@Data
public class UserOrderDistributionVo {

    /**
     * 一级分销员id
     */
    @Schema(title = "一级分销员id")
    private String firstUserId;

    /**
     * 一级分销员名称
     */
    @Schema(title = "用户名称")
    private String firstNikeName;

    /**
     * 一级分销员手机号码
     */
    @Schema(title = "手机号码")
    private String firstPhone;

    /**
     * 一级分销金额
     */
    @Schema(title = "一级分销金额")
    private BigDecimal firstDistributionAmount;

    /**
     * 二级分销员id
     */
    @Schema(title = "二级分销员id")
    private String secondUserId;

    /**
     * 二级分销员名称
     */
    @Schema(title = "用户名称")
    private String secondNikeName;

    /**
     * 二级分销员手机号码
     */
    @Schema(title = "手机号码")
    private String secondPhone;

    /**
     * 二级分销金额
     */
    @Schema(title = "二级分销金额")
    private BigDecimal secondDistributionAmount;

}
