package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.integral.api.entity.IntegralGoodsAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Description:积分商品属性
 * @Author: xiaoq
 * @Date: 2023/11/20 9:00
 */
@Data
@Schema(title = "新增或者修改积分商品属性DTO")
public class IntegralGoodsAttibuteDto {

    private Long id;

    @Schema(title = "积分商品id")
    private Long integralGoodsId;

    @NotBlank
    @Schema(title = "属性名称")
    private String name;

    @Schema(title = "属性值")
    private String value;

    public IntegralGoodsAttribute coverIntegralGoodsAttribute() {
        IntegralGoodsAttribute integralGoodsAttribute = new IntegralGoodsAttribute();
        BeanUtil.copyProperties(this, integralGoodsAttribute);
        return integralGoodsAttribute;
    }
}
