package com.medusa.gruul.shops.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Schema(title = "ShopsMenuConfigVo 实体", description = "店铺菜单属性配置 vo")
public class ShopsMenuConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;


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

}