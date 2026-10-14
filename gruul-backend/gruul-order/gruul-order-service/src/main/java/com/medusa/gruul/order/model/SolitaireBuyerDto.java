package com.medusa.gruul.order.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The type Solitaire buyer dto.
 * <p>
 * 查询接龙的买家
 *
 * @author alan
 * @date 2023/11/13 20:54
 */
@Data
@Schema(title = "查询接龙的买家", description = "查询接龙的买家")
public class SolitaireBuyerDto extends AbstractQueryParam {

    @NotNull
    @Schema(title = "接龙活动ID", example = "1")
    private Integer solitaireActivityId;
}
