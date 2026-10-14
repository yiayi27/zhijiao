package com.medusa.gruul.integral.api.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 折扣信息
 * @Author: xiaoq
 * @Date: 2023/12/5 10:22
 */
@Data
public class DiscountInfoDto {
    @Schema(title = "积分抵扣比例百分比")
    @TableField("ratio")
    private BigDecimal ratio;

    @Schema(title = "顶底抵扣上限额度百分比")
    @TableField("ceiling")
    private BigDecimal ceiling;

}
