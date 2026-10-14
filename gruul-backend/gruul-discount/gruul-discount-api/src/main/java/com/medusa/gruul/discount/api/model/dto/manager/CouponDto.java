package com.medusa.gruul.discount.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.discount.api.entity.Coupon;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 优惠劵
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "新增或修改优惠劵信息DTO")
public class CouponDto {

    private Long id;

    @Schema(title = "优惠劵名称")
    private String name;

    @Schema(title = "有效类型： 0-固定时间  1-领劵立即生效")
    @NotNull(message = "有效类型不能为空")
    private Integer effectiveType;

    @Schema(title = "有效天数")
    private Integer effectiveDays;

    @Schema(title = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Schema(title = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Schema(title = "适用商品类型：  0-全部商品参与  1-指定商品参与  2-指定商品不参与")
    @NotNull(message = "适用商品类型不能为空")
    private Integer productType;

    @Schema(title = "优惠劵类型：  0-无门槛折扣劵  1-无门槛现金劵  2-满折劵  3-满减劵")
    @NotNull(message = "优惠劵类型不能为空")
    private Integer couponType;

    @Schema(title = "优惠折扣（不能大于10）")
    private BigDecimal discount;

    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(title = "满足条件金额")
    private BigDecimal conditionAmount;

    @Schema(title = "发行数量")
    @NotNull(message = "发行数量不能为空")
    private Integer issueQuantity;

    @Schema(title = "限领(张/人)")
    @NotNull(message = "限领张数不能为空")
    private Integer limitQuantity;

    @Schema(title = "优惠劵状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "赠送类型：  0-全民活动  1-赠予客户  2-积分优惠劵")
    @NotNull(message = "赠送类型不能为空")
    private Integer giveType;

    @Schema(title = "配置规则：0-不参与回收返券 1-参与回收返券")
    private Integer recycleActivity;

    @Schema(title = "回收反券图片")
    private String recycleImg;


    @Schema(title = "所需积分")
    private Integer integral;

    @Schema(title = "优惠劵关联商品信息")
    private List<CouponProductDto> couponProducts;

    public Coupon coverCoupon() {
        Coupon coupon = new Coupon();
        BeanUtil.copyProperties(this, coupon);
        return coupon;
    }
}
