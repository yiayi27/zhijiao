package com.medusa.gruul.goods.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lcysike
 * @Description: 展示分类
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ShowCategoryParam", description = "展示分类查询参数")
public class ShowCategoryParam extends AbstractQueryParam {

    @Schema(title = "销售专区")
    @NotBlank(message = "请选择专区")
    private Long saleMode;

}