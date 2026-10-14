package com.medusa.gruul.shops.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户中心配置
 * </p>
 *
 * @author whh
 * @since 2023-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_account_center")
@Schema(title = "AccountCenter对象", description = "用户中心配置")
public class AccountCenter extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(title = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 头部风格 1-系统风格 2-自定义风格
     */
    @Schema(title = "头部风格 1-系统风格 2-自定义风格")
    @TableField("head_style")
    private Integer headStyle;

    /**
     * 自定义风格样式,json存储
     */
    @Schema(title = "自定义风格样式,json存储")
    @TableField("custom_style")
    private String customStyle;

    /**
     * 领卡文案
     */
    @Schema(title = "领卡文案")
    @TableField("get_cart_text")
    private String getCartText;

    /**
     * 非会员隐藏领卡入口 0-隐藏 1-显示
     */
    @Schema(title = "非会员隐藏领卡入口 0-隐藏 1-显示")
    @TableField("hide_cart_inlet")
    private Boolean hideCartInlet;

    /**
     * 订单icon信息 json存储
     */
    @Schema(title = "订单icon信息 json存储")
    @TableField("order_info")
    private String orderInfo;

    /**
     * 菜单选择样式 1-展开式 2-折叠式
     */
    @Schema(title = "菜单选择样式 1-展开式 2-折叠式")
    @TableField("menu_style")
    private Integer menuStyle;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;
}
