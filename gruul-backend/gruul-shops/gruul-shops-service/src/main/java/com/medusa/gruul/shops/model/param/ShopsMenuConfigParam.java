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
@Schema(title = "ShopsMenuConfigParam 实体", description = "店铺菜单属性配置 param")
public class ShopsMenuConfigParam extends AbstractQueryParam {


    @Schema(title = "id")
    private Long id;


    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    private String shopId;


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

}
