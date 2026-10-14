package com.medusa.gruul.integral.web.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description: 积分商品订单条件查询
 * @Author: xiaoq
 * @Date: 2023/11/23 14:18
 */
@Data
@Schema(title = "积分订单查询")
public class IntegralGoodsOrderParam extends AbstractQueryParam {

    @Schema(title = "积分物品订单类型 (0.待发货 1.已发货 2.已完成)")
    private Integer prizeOrderStatus;

    @Schema(title = "用户微信名")
    private String wxuserName;

    @NotNull
    @Schema(title = "订单类型（0为积分商品 1为积分优惠卷）")
    private Boolean orderType;

    @Schema(title = "积分订单id")
    private Long prizeOrderId;

    @Schema(title = "积分商品名称")
    private String integralGoodName;


}
