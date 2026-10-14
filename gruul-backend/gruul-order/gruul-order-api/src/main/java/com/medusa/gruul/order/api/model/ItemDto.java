package com.medusa.gruul.order.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


/**
 * 商品规格数量Map
 *
 * @author alan
 * @date 2023/10/4 14:36
 */
@Data
@Schema(title = "商品规格数量Map", description = "商品规格数量Map")
public class ItemDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Schema(title = "商品SKU ID")
    private Long skuId;

    @NotNull
    @Schema(title = "商品数量")
    private Integer number;

    /**
     * 所属活动id
     */
    @Schema(title = "所属活动id")
    private Long activityId;

    /**
     * 所属活动商品id
     */
    @Schema(title = "所属活动商品id")
    private Long activityProductId;


}
