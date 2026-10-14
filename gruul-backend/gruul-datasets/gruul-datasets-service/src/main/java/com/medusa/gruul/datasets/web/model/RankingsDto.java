package com.medusa.gruul.datasets.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 排行榜dto
 * @Author: xiaoq
 * @Date: 2024/5/11 17:06
 */
@Data
public class RankingsDto {

    @Schema(title = "id值 商品 供货商 ")
    private Long id;

    @Schema(title = "交易量")
    private Integer tradingVolume;

    @Schema(title = "商铺交易额")
    private BigDecimal tradeVolume;

    @Schema(title = "商品单价 多规格时取最低价")
    private BigDecimal price;

    @Schema(title = "图片地址")
    private String url;

    @Schema(title = "昵称")
    private String nick;

    @Schema(title = "地域")
    private String region;

    @Schema(title = "供应商手机号")
    private String mobile;
}
