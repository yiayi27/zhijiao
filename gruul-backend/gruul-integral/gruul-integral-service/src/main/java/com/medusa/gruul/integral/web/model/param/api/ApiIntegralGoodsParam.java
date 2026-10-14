package com.medusa.gruul.integral.web.model.param.api;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 积分商品分页查询
 * @Author: xiaoq
 * @Date: 2023/11/29 12:57
 */
@Data
public class ApiIntegralGoodsParam extends AbstractQueryParam {
    @Schema(title = "店铺ID")
    private String shopId;
}
