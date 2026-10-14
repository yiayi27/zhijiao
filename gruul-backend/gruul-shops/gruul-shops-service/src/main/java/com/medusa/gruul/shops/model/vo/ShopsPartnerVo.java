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
@Schema(title = "shops partner vo 实体", description = "商铺数据 vo")
public class ShopsPartnerVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;


    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    private String shopId;


    /**
     * 总店Id
     */
    @Schema(title = "总店Id")
    private Long partnerId;


    /**
     * 电话
     */
    @Schema(title = "电话")
    private String phone;


    /**
     * 逻辑删除标识  0正常 1已删除
     */
    @Schema(title = "逻辑删除标识  0正常 1已删除")
    private String isDeleted;


    /**
     * 名字
     */
    @Schema(title = "名字")
    private String name;


    /**
     * 密码
     */
    @Schema(title = "密码")
    private String pass;


    /**
     * 地域
     */
    @Schema(title = "地域")
    private String region;


    /**
     * 区域编码
     */
    @Schema(title = "区域编码")
    private String areaCode;


    /**
     * 省编码
     */
    @Schema(title = "省编码")
    private String provinceCode;


    /**
     * 市编码
     */
    @Schema(title = "市编码")
    private String cityCode;


    /**
     * 身份证正面
     */
    @Schema(title = "身份证正面")
    private String cardIdUp;


    /**
     * 身份证反面
     */
    @Schema(title = "身份证反面")
    private String cardIdDown;


    /**
     * 地图X
     */
    @Schema(title = "地图X")
    private Double mapX;


    /**
     * 地图Y
     */
    @Schema(title = "地图Y")
    private Double mapY;

    /**
     * 审批状态 0审核中 1通过 2拒绝
     */
    @Schema(title = "审批状态 0审核中 1通过 2拒绝")
    private String approvalStatus;


    /**
     * 禁用状态  0正常  1 禁用
     */
    @Schema(title = "禁用状态  0正常  1 禁用")
    private String prohibitStatus;


    /**
     * 省name
     */
    @Schema(title = "省name")
    private String provinceName;


    /**
     * 市name
     */
    @Schema(title = "市name")
    private String cityName;


    /**
     * 区域name
     */
    @Schema(title = "区域name")
    private String areaName;


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
     * 平台用户id
     */
    @Schema(title = "平台用户id")
    private Long platformId;
}
