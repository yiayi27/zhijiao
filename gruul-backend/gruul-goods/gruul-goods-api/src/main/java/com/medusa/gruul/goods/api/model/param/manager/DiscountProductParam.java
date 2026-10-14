package com.medusa.gruul.goods.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author lcysike
 * @Description: 商品
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "DiscountProductParam对象", description = "商品查询参数")
public class DiscountProductParam extends AbstractQueryParam {

    @Schema(title = "货号")
    private Long productSn;

    @Schema(title = "销售专区")
    private Long saleMode;

    @Schema(title = "展示分类id")
    private Long showCategoryId;

    @Schema(title = "商品名称")
    private String name;

    @Schema(title = "最小价格")
    private BigDecimal minPrice;

    @Schema(title = "最大价格")
    private BigDecimal maxPrice;

    @Schema(title = "发货单id")
    private String deliverId;

    @Schema(title = "接龙状态(0--未参加，1--已参加)")
    private Integer dominoState;

    @Schema(title = "匹配的商品list")
    private List<Long> productIds;

}