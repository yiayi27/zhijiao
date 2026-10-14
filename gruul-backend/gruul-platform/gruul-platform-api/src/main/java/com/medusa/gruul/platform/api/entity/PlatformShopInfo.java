package com.medusa.gruul.platform.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * <p>
 * 店铺信息表
 * </p>
 *
 * @author whh
 * @since 2024-03-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_platform_shop_info")
@Schema(title = "PlatformShopInfo对象", description = "店铺信息表")
public class PlatformShopInfo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 店铺首页图片
     */
    @Schema(title = "店铺首页图片")
    @TableField("logo_url")
    private String logoUrl;

    /**
     * 店铺名称
     */
    @Schema(title = "店铺名称")
    @TableField("shop_name")
    private String shopName;

    /**
     * 营业时间
     */
    @Schema(title = "营业时间,自行分割")
    @TableField("business_hours")
    private String businessHours;

    /**
     * 店铺电话
     */
    @Schema(title = "店铺电话")
    @TableField("shop_phone")
    private String shopPhone;

    /**
     * 小程序底部打标
     */
    @Schema(title = "小程序底部打标")
    @TableField("mini_bottom_log")
    private String miniBottomLog;

}
