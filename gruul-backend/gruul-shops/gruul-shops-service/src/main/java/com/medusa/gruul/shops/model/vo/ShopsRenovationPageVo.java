package com.medusa.gruul.shops.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Accessors(chain = true)
@Schema(title = "ShopsRenovationPageVo 实体", description = "店铺装修模板页面实体 vo")
public class ShopsRenovationPageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    /**
     * 自定义页面name
     */
    @Schema(title = "自定义页面name ")
    private String pageName;

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
     * 分区id
     */
    private String modelId;

    /**
     * 聚合页面插件
     */
    @Schema(title = "聚合页面插件")
    private List<ShopsRenovationAssemblyVo> assemblyVos;


}