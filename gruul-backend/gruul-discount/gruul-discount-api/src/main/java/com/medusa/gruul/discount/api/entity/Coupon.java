package com.medusa.gruul.discount.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 优惠劵
 * </p>
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_coupon")
@Schema(title = "Coupon对象", description = "优惠劵")
public class Coupon extends BaseEntity {

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
     * 优惠劵名称
     */
    @Schema(title = "优惠劵名称")
    @TableField("name")
    private String name;

    /**
     * 有效类型： 0-固定时间  1-领劵立即生效
     */
    @Schema(title = "有效类型： 0-固定时间  1-领劵立即生效")
    @TableField("effective_type")
    private Integer effectiveType;

    /**
     * 有效天数
     */
    @Schema(title = "有效天数")
    @TableField("effective_days")
    private Integer effectiveDays;

    /**
     * 开始时间
     */
    @Schema(title = "开始时间")
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(title = "结束时间")
    @TableField("end_time")
    private Date endTime;

    /**
     * 适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与
     */
    @Schema(title = "适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与")
    @TableField("product_type")
    private Integer productType;

    /**
     * 优惠劵类型：  0-无门槛折扣劵  1-无门槛现金劵  2-满折劵  3-满减劵
     */
    @Schema(title = "优惠劵类型：  0-无门槛折扣劵  1-无门槛现金劵  2-满折劵  3-满减劵")
    @TableField("coupon_type")
    private Integer couponType;

    /**
     * 优惠折扣（不能大于10）
     */
    @Schema(title = "优惠折扣（不能大于10）")
    @TableField("discount")
    private BigDecimal discount;

    /**
     * 优惠金额
     */
    @Schema(title = "优惠金额")
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 满足条件金额
     */
    @Schema(title = "满足条件金额")
    @TableField("condition_amount")
    private BigDecimal conditionAmount;

    /**
     * 发行数量
     */
    @Schema(title = "发行数量")
    @TableField("issue_quantity")
    private Integer issueQuantity;

    /**
     * 限领(张/人)
     */
    @Schema(title = "限领(张/人)")
    @TableField("limit_quantity")
    private Integer limitQuantity;

    /**
     * 优惠劵状态：  0-未开始  1-进行中  2-已结束
     */
    @Schema(title = "优惠劵状态：  0-未开始  1-进行中  2-已结束")
    @TableField("status")
    private Integer status;

    /**
     * 赠送类型：  0-全民活动  1-赠予客户
     */
    @Schema(title = "赠送类型：  0-全民活动  1-赠予客户  2-积分优惠劵")
    @TableField("give_type")
    private Integer giveType;

    /**
     * 所需积分
     */
    @Schema(title = "所需积分")
    @TableField("integral")
    private Integer integral;

    @Schema(title = "配置规则  0未参与回收返券  1参与")
    @TableField("recycle_activity")
    private Integer recycleActivity;

    @Schema(title = "回收反券图片")
    @TableField("recycle_img")
    private String recycleImg;

}
