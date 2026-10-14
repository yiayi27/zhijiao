package com.medusa.gruul.recycling.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 回收设置表
 * </p>
 *
 * @author whh
 * @since 2024-04-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_recycling_setting")
@Schema(title = "RecyclingSetting对象", description = "回收设置表")
public class RecyclingSetting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(title = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;

    /**
     * 商品基金是否开启 true开启 false 关闭
     */
    @Schema(title = "商品基金是否开启 true开启 false 关闭")
    @TableField("goods_fund_state")
    private Boolean goodsFundState;

    /**
     * 基金额度
     */
    @Schema(title = "基金额度")
    @TableField("fund_quota")
    private BigDecimal fundQuota;

    /**
     * 页面背景图片地址
     */
    @Schema(title = "页面背景图片地址")
    @TableField("page_bg_img")
    private String pageBgImg;

    /**
     * 购买成功展示背景图地址
     */
    @Schema(title = "购买成功展示背景图地址")
    @TableField("buy_bg_img")
    private String buyBgImg;

    /**
     * 首页未寄出提示背景图地址
     */
    @Schema(title = "首页未寄出提示背景图地址")
    @TableField("home_prompt_bg_img")
    private String homePromptBgImg;

    /**
     * 回收券功能是否开启  true开启 false 关闭
     */
    @Schema(title = "回收券功能是否开启  true开启 false 关闭")
    @TableField("recycling_state")
    private Boolean recyclingState;

    /**
     * 商品详情回收入口是否开启  true开启 false 关闭
     */
    @Schema(title = "商品详情回收入口是否开启  true开启 false 关闭")
    @TableField("entrance_state")
    private Boolean entranceState;

    /**
     * 样式配置json
     */
    @Schema(title = "样式配置json")
    @TableField("btn_style_json")
    private String btnStyleJson;

    /**
     * 关联卡券id
     */
    @Schema(title = "关联卡券id")
    @TableField("ass_coupon_id")
    private Long assCouponId;

    /**
     * 页面轮播图
     */
    @Schema(title = "页面轮播图")
    @TableField("page_carousels")
    private String pageCarousels;

    /**
     * 活动说明
     */
    @Schema(title = "活动说明")
    @TableField("activity_desc")
    private String activityDesc;

    /**
     * 提交按钮
     */
    @Schema(title = "提交按钮")
    @TableField("submit_img")
    private String submitImg;

}
