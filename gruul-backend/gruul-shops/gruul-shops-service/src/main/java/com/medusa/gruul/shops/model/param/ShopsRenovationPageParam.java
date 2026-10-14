package com.medusa.gruul.shops.model.param;

import com.medusa.gruul.common.core.param.AbstractQueryParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 店铺装修模板页面信息
 * @author create by zq
 * @since created in 2023/11/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "ShopsRenovationPageParam 实体", description = "店铺装修模板页面请求参数实体")
public class ShopsRenovationPageParam extends AbstractQueryParam {


    @Schema(title = "id")
    private Long id;


    /**
     * 所属模板ID
     */
    @Schema(title = "所属模板ID")
    private Long templateId;


    /**
     * 自定义页面name
     */
    @Schema(title = "自定义页面name ")
    private String pageName;


    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    private String shopId;


    /**
     * 是否默认页面
     */
    @Schema(title = "是否默认页面")
    private String isDef;


    /**
     * 分类页
     */
    @Schema(title = "分类页")
    private String type;


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
     * modelId
     */
    @Schema(title = "modelId")
    private String modelId;

}
