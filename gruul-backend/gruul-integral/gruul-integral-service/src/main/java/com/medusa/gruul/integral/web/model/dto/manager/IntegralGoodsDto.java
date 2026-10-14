package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.integral.api.entity.IntegralGoods;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:积分商品信息
 * @Author: xiaoq
 * @Date: 2023/11/19 13:48
 */
@Data
@Schema(title = "新增或修改积分商品的DTO")
public class IntegralGoodsDto {
    private Long id;

    @Schema(title = "积分商城表id")
    private Long tIntegralShopId;

    @NotNull
    @Schema(title = "积分商品名")
    private String integralGoodName;

    @Schema(title = "指导价")
    private BigDecimal guidePrice;

    @NotNull
    @Schema(title = "库存")
    private Integer inventory;

    @NotNull
    @Schema(title = "积分(售卖)")
    private BigDecimal integralPrice;

    @Schema(title = "属性模板id")
    private Long attributeId;

    @Schema(title = "属性模板名称")
    private String attributeName;

    @NotNull
    @Schema(title = "商品主图")
    private String pic;

    @Schema(title = "画册图片，连产品图片限制为6张，以逗号分割")
    private String albumPics;

    @Schema(title = "销量(初始销量)")
    private Integer sales;

    @Schema(title = "运费模板id")
    private Long freightTemplateId;

    @Schema(title = "状态(默认上架，0--下架（仓库中），1--上架，2--已售完)")
    private Integer status;

    @Schema(title = "货号")
    private String productSn;

    @Schema(title = "积分商品详情")
    private String detail;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "商品重量,默认为克")
    private BigDecimal weight;

    @Schema(title = "积分商品属性信息")
    private List<IntegralGoodsAttibuteDto> integralGoodsAttibuteDtos;

    @Schema(title = "积分商品运费模板信息")
    private List<IntegralFreightTemplateDto> integralFreightTemplateDtos;

    public IntegralGoods coverIntegralGoods() {
        IntegralGoods integralGoods = new IntegralGoods();
        BeanUtil.copyProperties(this, integralGoods);
        return integralGoods;
    }
}
