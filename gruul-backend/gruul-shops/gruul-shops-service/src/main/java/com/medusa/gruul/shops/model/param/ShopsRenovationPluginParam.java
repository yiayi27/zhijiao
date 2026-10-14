package com.medusa.gruul.shops.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ShopsRenovationPluginParam 实体", description = "店铺装修模板全局控件请求参数实体")
public class ShopsRenovationPluginParam extends AbstractQueryParam {

    /**
     * 控件JSON
     */
    @Schema(title = "控件JSON")
    private String pluginProperties;


    /**
     * 备用字段
     */
    @Schema(title = "备用字段")
    private String spare;

    /**
     * 控件名称中文
     */
    @Schema(title = "控件名称中文")
    private String pluginNameCn;

    /**
     * 控件名称英文
     */
    @Schema(title = "控件名称英文")
    private String pluginNameEn;

    /**
     * 是否选中 0否 1是
     */
    @Schema(title = "是否选中 0否 1是")
    private String isSelection;


    /**
     * 是否允许取消 0否 1是
     */
    @Schema(title = "是否允许取消 0否 1是")
    private String isMandatory;
}
