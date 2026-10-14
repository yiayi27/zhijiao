package com.medusa.gruul.account.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author whh
 * @description
 * @date: 2024/5/11
 */
@Data
public class AreaAccountInfoDto {

    @Schema(title = "用户店铺id")
    private String shopUserId;

    @Schema(title = "用户名称")
    private String nikeName;

    @Schema(title = "头像url")
    private String avatarUrl;

    @Schema(title = "首次登陆小程序时间")
    private LocalDateTime firstLoginTime;

    @Schema(title = "消费次数")
    private Integer consumeNum;

    @Schema(title = "交易总额")
    private BigDecimal consumeTotleMoney;

    @Schema(title = "最后交易时间")
    private LocalDateTime lastDealTime;


}
