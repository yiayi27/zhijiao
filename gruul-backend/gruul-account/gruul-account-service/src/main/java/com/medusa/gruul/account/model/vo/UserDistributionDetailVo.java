package com.medusa.gruul.account.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author lcysike
 * @description
 * @data: 2024/11/10
 */
@Data
@Schema(title = "用户分销明细信息")
public class UserDistributionDetailVo {

    @Schema(title = "用户名称")
    private String nikeName;

    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "注册时间")
    private LocalDateTime createTime;

    @Schema(title = "单个客户分销佣金")
    private BigDecimal singleUserAmount;

    @Schema(title = "单个分销客户订单数量")
    private Integer userOrderCount;

}
