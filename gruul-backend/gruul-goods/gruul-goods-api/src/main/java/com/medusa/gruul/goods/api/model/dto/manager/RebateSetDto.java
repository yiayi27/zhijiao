package com.medusa.gruul.goods.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.goods.api.entity.RebateSet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 店铺返利信息设置
 * </p>
 *
 * @author lcysike
 * @since 2024-05-21
 */
@Data
@Schema(title = "新增或修改店铺返利信息DTO")
public class RebateSetDto implements Serializable {

    private Long id;

    @Schema(title = "商品返利比例")
    private BigDecimal rebateRatio;

    @Schema(title = "余额使用条件(0--普通用户，1--会员),多个以逗号分割")
    private String balanceUseCondition;

    @Schema(title = "返利条件(0--普通用户，1--会员),多个以逗号分割")
    private String rebateCondition;

    public RebateSet coverRebateSet() {
        RebateSet rebateSet = new RebateSet();
        BeanUtil.copyProperties(this, rebateSet);
        return rebateSet;
    }
}