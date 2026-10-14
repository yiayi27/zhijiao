package com.medusa.gruul.integral.web.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 商品基础信息 供订单显示是使用
 * @Author: xiaoq
 * @Date: 2023/11/26 13:43
 */
@Data
@Schema(title = "商品基础信息")
public class ShoppingInfVo {
    @Schema(title = "积分商品表id")
    private Long id;

    @Schema(title = "积分商品名")
    private String integralGoodName;

    @Schema(title = "积分(售卖)")
    private BigDecimal integralPrice;

    @Schema(title = "商品主图")
    private String pic;

    @Schema(title = "货号")
    private String productSn;
}
