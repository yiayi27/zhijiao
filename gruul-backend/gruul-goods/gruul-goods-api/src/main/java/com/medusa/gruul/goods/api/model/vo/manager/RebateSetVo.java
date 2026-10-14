package com.medusa.gruul.goods.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


/**
 * <p>
 * 获取默认返利信息Vo
 * </p>
 *
 * @author lcysike
 * @since 2024-08-10
 */
@Data
@Schema(title = "RebateSetVo对象", description = "返利设置查询返回信息")
public class RebateSetVo {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 商品返利比例
     */
    @Schema(title = "商品返利比例")
    private BigDecimal rebateRatio;

    /**
     * 余额使用条件(0--普通用户，1--会员),多个以逗号分割
     */
    @Schema(title = "余额使用条件(0--普通用户，1--会员),多个以逗号分割")
    private String balanceUseCondition;

    /**
     * 返利条件(0--普通用户，1--会员),多个以逗号分割
     */
    @Schema(title = "返利条件(0--普通用户，1--会员),多个以逗号分割")
    private String rebateCondition;
}