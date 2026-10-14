package com.medusa.gruul.shops.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Accessors(chain = true)
@Schema(title = "ShopsRenovationAssemblyVo 实体", description = "店铺装修模板页面组件实体 vo")
public class ShopsRenovationAssemblyVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    /**
     * 所属页面ID
     */
    @Schema(title = "所属页面ID")
    private Long pageId;


    /**
     * 组件属性 json串
     */
    @Schema(title = "组件属性 json串")
    private String properties;

}