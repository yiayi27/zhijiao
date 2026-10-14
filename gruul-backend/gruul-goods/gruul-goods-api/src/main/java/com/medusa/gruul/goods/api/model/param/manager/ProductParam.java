package com.medusa.gruul.goods.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


/**
 * @author lcysike
 * @Description: 商品
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ProductParam对象", description = "商品查询参数")
public class ProductParam extends AbstractQueryParam {

    @Schema(title = "货号")
    private String productSn;

    @Schema(title = "销售专区")
    @NotBlank(message = "请选择专区")
    private Long saleMode;

    @Schema(title = "展示分类")
    private Long showCategoryId;

    @Schema(title = "商品名称")
    private String name;

    @Schema(title = "状态(默认上架，0--下架，1--上架 )")
    private Integer status;

    @Schema(title = "供应商id")
    private String providerId;

    @Schema(title = "供应商")
    private String providerName;

    @Schema(title = "接龙状态(0--未参加，1--已参加)")
    private Integer dominoState;

    @Schema(title = "创建开始时间")
    private String createBeginTime;

    @Schema(title = "创建结束时间")
    private String createEndTime;

    @Schema(title = "最小价格")
    private BigDecimal minPrice;

    @Schema(title = "最大价格")
    private BigDecimal maxPrice;
}