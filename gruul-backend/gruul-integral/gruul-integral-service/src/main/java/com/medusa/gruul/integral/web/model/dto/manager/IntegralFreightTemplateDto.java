package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.medusa.gruul.integral.api.entity.IntegralFreightTemplate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: xiaoq
 * @Date: 2023/11/20 9:10
 */
public class IntegralFreightTemplateDto {

    private Long id;

    @Schema(title = "模板名称")
    @TableField("name")
    private String name;

    @Schema(title = "积分商品表id")
    @TableField("integral_goods_id")
    private Long integralGoodsId;

    @Schema(title = "计费类型:0->按重量；1->按件数")
    @TableField("charge_type")
    private Integer chargeType;

    @Schema(title = "首重kg")
    @TableField("first_weight")
    private BigDecimal firstWeight;

    @Schema(title = "首费（元）")
    @TableField("first_fee")
    private BigDecimal firstFee;

    @Schema(title = "续重")
    @TableField("continue_weight")
    private BigDecimal continueWeight;

    @Schema(title = "续费")
    @TableField("continue_fee")
    private BigDecimal continueFee;

    @Schema(title = "目的地（省、市）")
    @TableField("dest")
    private String dest;

    public IntegralFreightTemplate coverIntegralFreightTemplate() {
        IntegralFreightTemplate integralFreightTemplate = new IntegralFreightTemplate();
        BeanUtil.copyProperties(this, integralFreightTemplate);
        return integralFreightTemplate;
    }
}
