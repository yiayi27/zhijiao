package com.medusa.gruul.order.model;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * The type Api search product evaluate dto.
 * <p>
 * 小程序端商品评论查询
 *
 * @author alan
 * @date 2023/11/10 9:21
 */
@Data
@Schema(title = "小程序端商品评论查询")
public class ApiSearchProductEvaluateDto extends AbstractQueryParam {

    @NotNull
    @Schema(title = "商品ID")
    private String productId;

    @NotNull
    @Schema(title = "查询范围：0->全部;1->有图片;2->有内容;")
    private int type;
}
