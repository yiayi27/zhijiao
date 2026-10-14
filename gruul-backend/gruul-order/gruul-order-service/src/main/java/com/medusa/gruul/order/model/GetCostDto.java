package com.medusa.gruul.order.model;

import com.medusa.gruul.order.api.model.ItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Get cost dto.
 */
@Data
public class GetCostDto {
    @Schema(title = "提货点id")
    private String pointId;
    @Schema(title = "总金额")
    private BigDecimal amount;
    @Schema(title = "经度,120.21937542")
    private double longitude;
    @Schema(title = "纬度 30.25924446")
    private double latitude;
    @Schema(title = "收货地址ID")
    private Long addressId;
    @NotNull
    @Schema(title = "配送类型：0-->用户自提（需要经纬度、提货点ID、总金额）;1->物流模式（需要商品规格数量Map、收货地址中post_code）;2->送货上门（需要经纬度、提货点ID、总金额）")
    private Integer type;
    @Schema(title = "商品规格数量Map")
    private List<ItemDto> items;
    @Schema(title = "区域编码")
    private String region;


    /**
     * Gets item sku ids.
     *
     * @return the item sku ids
     */
    public List<Long> getItemSkuIds() {
        return new ArrayList<>(items.stream().map(ItemDto::getSkuId).collect(Collectors.toSet()));
    }
}
