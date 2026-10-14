package com.medusa.gruul.integral.web.model.param.manager;


import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description: 积分商品订单查询
 * @Author: xiaoq
 * @Date: 2023/1/7 15:24
 */
@Data
@Schema(title = "积分优惠券订单查询")
public class IntegralCouponOrderParam extends AbstractQueryParam {
    @Schema(title = "用户微信名")
    private String wxuserName;


}
