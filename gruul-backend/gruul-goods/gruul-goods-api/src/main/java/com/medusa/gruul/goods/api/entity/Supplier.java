package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * <p>
 * 供应商信息
 * </p>
 *
 * @author lcysike
 * @since 2023-10-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_supplier")
@Schema(title = "Supplier对象", description = "供应商信息")
public class Supplier extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 本店店铺id
     */
    @Schema(title = "本店店铺id")
    @TableField("shop_id")
    private String shopId;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    /**
     * 供应商识别号
     */
    @Schema(title = "供应商识别号")
    @TableField("supplier_sn")
    private String supplierSn;

    /**
     * 供应商名称
     */
    @NotBlank
    @Size(max = 64)
    @Schema(title = "供应商名称")
    @TableField("name")
    private String name;

    /**
     * 手机号码
     */
    @Schema(title = "手机号码")
    @TableField("mobile")
    private String mobile;

    /**
     * 省
     */
    @Schema(title = "省")
    @TableField("province")
    private String province;

    /**
     * 市
     */
    @Schema(title = "市")
    @TableField("city")
    private String city;

    /**
     * 县
     */
    @Schema(title = "县")
    @TableField("country")
    private String country;

    /**
     * 地区
     */
    @Schema(title = "地区")
    @TableField("address")
    private String address;

    /**
     * 完整地址
     */
    @Schema(title = "完整地址")
    @TableField("area")
    private String area;

    /**
     * 产品信息
     */
    @Schema(title = "产品信息")
    @TableField("product_info")
    private String productInfo;

    /**
     * 状态(默认待审核，0--已关闭，1--已审核，2--待审核，3--禁用中)
     */
    @Schema(title = "状态(默认待审核，0--已关闭，1--已审核，2--待审核，3--禁用中)")
    @TableField("status")
    private Integer status;

    /**
     * 评分
     */
    @Schema(title = "评分")
    @TableField("score")
    private BigDecimal score;

    /**
     * 注册来源（0--后台注册，1--小程序）
     */
    @Schema(title = "注册来源，0--后台注册，1--小程序")
    @TableField("come_from")
    private Integer comeFrom;

    /**
     * 订阅消息模版id
     */
    @Schema(title = "订阅消息模版id")
    @TableField("template_id")
    private String templateId;

}