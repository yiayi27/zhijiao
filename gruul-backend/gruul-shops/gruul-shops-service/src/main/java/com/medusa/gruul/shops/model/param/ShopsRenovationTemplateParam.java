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
@Schema(title = "ShopsRenovationTemplateParam 实体", description = "店铺装修模板请求参数实体")
public class ShopsRenovationTemplateParam extends AbstractQueryParam {


    @Schema(title = "id")
    private Long id;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    private String shopId;


    /**
     * 模板类型 0自定义 1拼团 2商超
     */
    @Schema(title = "模板类型 0自定义 1拼团 2商超")
    private String type;


    /**
     * 模板全局颜色 0红 1绿 2蓝
     */
    @Schema(title = "模板全局颜色 0红 1绿 2蓝")
    private String colour;


    /**
     * 模板是否使用中 0 否, 1 是
     */
    @Schema(title = "模板是否使用中 0 否, 1 是")
    private String onlineStatus;


    /**
     * 默认模板 1是 0否
     */
    @Schema(title = " 默认模板 1是 0否")
    private String isDevTemplate;


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
     * 模板name
     */
    @Schema(title = "模板name")
    private String name;


    /**
     * 是否所有(若自定义模板返回空则 返回默认模板数据)
     */
    @Schema(title = "是否所有(若自定义模板返回空则 返回默认模板数据)")
    private String isAll;

}
