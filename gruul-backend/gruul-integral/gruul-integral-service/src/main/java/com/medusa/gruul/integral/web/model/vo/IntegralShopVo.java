package com.medusa.gruul.integral.web.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 积分商城 vo 基础信息
 * @Author: xiaoq
 * @Date: 2023/11/25 10:33
 */
@Data
@Schema(title = "IntegralShopVo对象", description = "积分商城信息")
public class IntegralShopVo {
    private Long id;

    @Schema(title = "类型 :0积分商城关闭 1.积分商城开启")
    private Boolean type;

    @Schema(title = "积分抵扣比例百分比")
    private BigDecimal ratio;

    @Schema(title = "顶底抵扣上限额度百分比")
    private BigDecimal ceiling;

    @Schema(title = "店铺ID")
    private String shopId;

}
