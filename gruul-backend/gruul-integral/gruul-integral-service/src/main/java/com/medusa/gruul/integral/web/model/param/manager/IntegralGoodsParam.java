package com.medusa.gruul.integral.web.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: xiaoq
 * @Date: 2023/11/19 18:01
 */
@Data
@Schema(title = "IntegralGoodsParam对象", description = "积分商品查询参数")
public class IntegralGoodsParam extends AbstractQueryParam {
    private Long id;

    @Schema(title = "货号")
    private String productSn;

    @Schema(title = "积分商品名")
    private String integralGoodName;

    @Schema(title = "状态(默认上架，0--下架（仓库中），1--上架，2--已售完)")
    private Integer status;
}
