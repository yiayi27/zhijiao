package com.medusa.gruul.shops.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author create by zq
 * @date created in 2023/11/15
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("t_shops_partner")
@Schema(title = "shops partner 实体", description = "总店商铺表")
@Getter
@Setter
public class ShopsPartner extends BaseEntity {


    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 总店Id
     */
    @NotNull
    @Schema(title = "总店Id")
    @TableField("partner_id")
    private Long partnerId;


    /**
     * 店铺id
     */
    @NotNull
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;


    /**
     * 电话
     */
    @NotNull
    @Schema(title = "电话")
    @TableField("phone")
    private String phone;


    /**
     * 逻辑删除标识  0正常 1已删除
     */
    @NotNull
    @Schema(title = "逻辑删除标识  0正常 1已删除")
    @TableField("is_deleted")
    private String isDeleted;


    /**
     * 名字
     */
    @NotNull
    @Schema(title = "名字")
    @TableField("name")
    private String name;


    /**
     * 密码
     */
    @NotNull
    @Schema(title = "密码")
    @TableField("pass")
    private String pass;

    /**
     * 邀请码
     */
    @Schema(title = "邀请码")
    @TableField("invitation_code")
    private String invitationCode;


    /**
     * 地域
     */
    @NotNull
    @Schema(title = "地域")
    @TableField("region")
    private String region;


    /**
     * 区域编码
     */
    @NotNull
    @Schema(title = "区域编码")
    @TableField("area_code")
    private String areaCode;


    /**
     * 省编码
     */
    @NotNull
    @Schema(title = "省编码")
    @TableField("province_code")
    private String provinceCode;


    /**
     * 市编码
     */
    @NotNull
    @Schema(title = "市编码")
    @TableField("city_code")
    private String cityCode;


    /**
     * 身份证正面
     */
    @NotNull
    @Schema(title = "身份证正面")
    @TableField("card_id_up")
    private String cardIdUp;


    /**
     * 身份证反面
     */
    @NotNull
    @Schema(title = "身份证反面")
    @TableField("card_id_down")
    private String cardIdDown;


    /**
     * 地图X
     */
    @NotNull
    @Schema(title = "地图X")
    @TableField("map_x")
    private Double mapX;


    /**
     * 地图Y
     */
    @NotNull
    @Schema(title = "地图Y")
    @TableField("map_y")
    private Double mapY;


    /**
     * 审批状态 0审核中 1通过 2拒绝
     */
    @NotNull
    @Schema(title = "审批状态 0审核中 1通过 2拒绝")
    @TableField("approval_status")
    private String approvalStatus;


    /**
     * 禁用状态  0正常  1 禁用
     */
    @NotNull
    @Schema(title = "禁用状态  0正常  1 禁用")
    @TableField("prohibit_status")
    private String prohibitStatus;


    /**
     * 省name
     */
    @NotNull
    @Schema(title = "省name")
    @TableField("province_name")
    private String provinceName;


    /**
     * 市name
     */
    @NotNull
    @Schema(title = "市name")
    @TableField("city_name")
    private String cityName;


    /**
     * 区域name
     */
    @NotNull
    @Schema(title = "区域name")
    @TableField("area_name")
    private String areaName;
    /**
     * 平台用户id
     */
    @Schema(title = "平台用户id")
    @TableField("platform_id")
    private Long platformId;
}
