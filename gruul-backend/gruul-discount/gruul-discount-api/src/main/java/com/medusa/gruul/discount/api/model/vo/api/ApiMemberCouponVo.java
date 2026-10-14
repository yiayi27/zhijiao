package com.medusa.gruul.discount.api.model.vo.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 客户优惠劵
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "MemberCouponVo", description = "客户优惠劵查询返回信息")
public class ApiMemberCouponVo {

    private Long id;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    @Schema(title = "优惠劵id")
    private Long couponId;

    @Schema(title = "优惠劵名称")
    private String name;

    @Schema(title = "有效类型： 0-固定时间  1-领劵立即生效")
    private Integer effectiveType;

    @Schema(title = "有效天数")
    private Integer effectiveDays;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @Schema(title = "优惠劵类型：  0-无门槛折扣劵  1-无门槛现金劵  2-满折劵  3-满减劵")
    private Integer couponType;

    @Schema(title = "适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与")
    private Integer productType;

    @Schema(title = "优惠折扣（不能大于10）")
    private BigDecimal discount;

    @Schema(title = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(title = "满足条件金额")
    private BigDecimal conditionAmount;

    @Schema(title = "发行数量")
    private Integer issueQuantity;

    @Schema(title = "限领(张/人)")
    private Integer limitQuantity;

    @Schema(title = "优惠劵使用状态：  0-未使用  1-已使用  2-已过期")
    private Integer useStatus;

    @Schema(title = "优惠劵状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "赠送类型：  0-全民活动  1-赠予客户  2-积分优惠劵")
    private Integer giveType;

    @Schema(title = "所需积分")
    private Integer integral;

}
