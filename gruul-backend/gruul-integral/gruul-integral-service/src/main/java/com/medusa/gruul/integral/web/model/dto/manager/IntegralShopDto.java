package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.integral.api.entity.IntegralShop;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 积分商城dto
 * @Author: xiaoq
 * @Date: 2023/11/22 14:40
 */
@Data
@Schema(title = "新增或修改积分商城的DTO")
public class IntegralShopDto {

    @Schema(title = "类型 :0积分商城关闭 1.积分商城开启")
    private Boolean type;

    @Schema(title = "积分抵扣比例百分比")
    private BigDecimal ratio;

    @Schema(title = "顶底抵扣上限额度百分比")
    private BigDecimal ceiling;

    @Schema(title = "店铺ID")
    private String shopId;


    public IntegralShop coverIntegralShop() {
        IntegralShop integralShop = new IntegralShop();
        BeanUtil.copyProperties(this, integralShop);
        return integralShop;
    }
}
