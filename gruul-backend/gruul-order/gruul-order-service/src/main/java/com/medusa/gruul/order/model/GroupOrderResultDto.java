package com.medusa.gruul.order.model;

import com.medusa.gruul.order.api.model.ItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Group order result dto.
 * <p>
 * 查询订单需要的参数
 *
 * @author alan
 * @date 2023/11/5 21:59
 */
@Data
@Schema(title = "查询订单需要的参数")
public class GroupOrderResultDto {

    @NotNull
    @Schema(title = "之前返回的Order ID", example = "1")
    private Long orderId;
    @NotEmpty
    @Schema(title = "SKU ID")
    private List<ItemDto> items;

    /**
     * Gets sku id set.
     *
     * @return the sku id set
     */
    public Set<Long> getSkuIdSet() {
        return items.stream().map(ItemDto::getSkuId).collect(Collectors.toSet());
    }
}
