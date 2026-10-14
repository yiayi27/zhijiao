package com.medusa.gruul.shops.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Schema(title = "ShopsRenovationTemplateVo 实体", description = "店铺装修模板实体 vo")
public class ShopsRenovationTemplateVo implements Serializable {

    private static final long serialVersionUID = 1L;


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
     * 创建时间
     */
    @Schema(title = "创建时间")
    private Date createTime;


    /**
     * 修改时间
     */
    @Schema(title = "修改时间")
    private Date updateTime;


    /**
     * 模板name
     */
    @Schema(title = " 模板name")
    private String name;


    /**
     * 归属聚合页面
     */
    @Schema(title = "归属聚合页面")
    private List<ShopsRenovationPageVo> pages;


    /**
     * 归属聚合控件
     */
    @Schema(title = " 归属聚合控件")
    private List<ShopsRenovationPluginVo> plugins;
}