package com.medusa.gruul.goods.api.model.param.api;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 小程序商品查询参数
 *
 * @author kyl
 * @since 2023-10-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ApiProductParam对象", description = "小程序商品查询参数")
public class ApiProductParam extends AbstractQueryParam {
    private static final long serialVersionUID = 1L;

    @Schema(title = "商品名称")
    private String name;

    @Schema(title = "商品展示分类id")
    private Long showCategoryId;

    @Schema(title = "商品展示分类名称")
    private String showCategoryName;

    @Schema(title = "商品销售专区")
    private Long saleMode;


    @Schema(title = "创建开始时间")
    private String createBeginTime;

    @Schema(title = "创建结束时间")
    private String createEndTime;

    @Schema(title = "排序类型")
    private Integer type;

    @Schema(title = "排序规则(升序-asc 降序-desc)")
    private String sort;
}