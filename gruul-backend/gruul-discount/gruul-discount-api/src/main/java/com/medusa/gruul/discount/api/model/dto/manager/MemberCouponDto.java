package com.medusa.gruul.discount.api.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.discount.api.entity.MemberCoupon;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户优惠劵
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "新增或修改客户优惠劵信息DTO")
public class MemberCouponDto {

    private Long id;

    @Schema(title = "优惠劵id")
    @NotBlank(message = "分类名称不能为空")
    private Long couponId;

    @Schema(title = "有效类型： 0-固定时间  1-领劵立即生效")
    @NotBlank(message = "有效类型不能为空")
    private Integer effectiveType;

    @Schema(title = "有效天数")
    private Integer effectiveDays;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @Schema(title = "适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与")
    @NotBlank(message = "适用商品类型不能为空")
    private Integer productType;

    @Schema(title = "优惠劵类型：  0-无门槛折扣劵  1-无门槛现金劵  2-满折劵  3-满减劵")
    @NotBlank(message = "优惠劵类型不能为空")
    private Integer couponType;

    @Schema(title = "优惠折扣（不能大于10）")
    private BigDecimal discount;

    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(title = "满足条件金额")
    private BigDecimal conditionAmount;

    @Schema(title = "发行数量")
    @NotBlank(message = "发行数量不能为空")
    private Integer issueQuantity;

    @Schema(title = "限领(张/人)")
    @NotBlank(message = "限领张数不能为空")
    private Integer limitQuantity;

    @Schema(title = "优惠劵状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "赠送类型：  0-全民活动  1-赠予客户  2-积分优惠劵")
    private Integer giveType;

    @Schema(title = "所需积分")
    private Integer integral;

    public MemberCoupon coverMemberCoupon() {
        MemberCoupon memberCoupon = new MemberCoupon();
        BeanUtil.copyProperties(this, memberCoupon);
        return memberCoupon;
    }


}
