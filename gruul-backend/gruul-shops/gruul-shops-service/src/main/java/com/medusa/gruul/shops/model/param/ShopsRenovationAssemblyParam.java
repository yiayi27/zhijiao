package com.medusa.gruul.shops.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 店铺装修模板页面组件
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ShopsRenovationAssemblyParam 实体", description = "店铺装修模板页面组件请求参数实体")
public class ShopsRenovationAssemblyParam extends AbstractQueryParam {


    @Schema(title = "id")
    private Long id;


    /**
     * 所属页面ID
     */
    @Schema(title = "所属页面ID")
    private Long pageId;


    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    private String shopId;


    /**
     * 组件序列
     */
    @Schema(title = "组件序列")
    private Long order;


    /**
     * 组件属性 json串
     */
    @Schema(title = "组件属性 json串")
    private String properties;


    /**
     * 逻辑删除标识  0正常 1已删除
     */
    @Schema(title = "逻辑删除标识  0正常 1已删除")
    private String isDeleted;

    /**
     * 操作人id
     */
    @Schema(title = "操作人id")
    private String operatorId;


    /**
     * 操作人name
     */
    @Schema(title = "操作人name")
    private String operatorName;


    /**
     * model_id
     */
    @Schema(title = "model_id")
    private String modelId;

}
