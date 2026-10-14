package com.medusa.gruul.goods.api.model.param.manager;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lcysike
 * @Description: 销售专区
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "SaleModeParam", description = "销售专区查询参数")
public class SaleModeParam extends AbstractQueryParam {

    @Schema(title = "专区名称")
    private String modeName;

}