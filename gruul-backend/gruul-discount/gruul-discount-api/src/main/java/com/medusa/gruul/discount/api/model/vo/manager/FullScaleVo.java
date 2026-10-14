package com.medusa.gruul.discount.api.model.vo.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 满减活动
 *
 * @author lcysike
 * @since 2023-12-09
 */
@Data
@Schema(title = "FullScaleVo", description = "满减活动查询返回信息")
public class FullScaleVo {

    private Long id;

    @Schema(title = "活动名称")
    private String name;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @Schema(title = "可用抵扣： 0-积分")
    private String deductionType;

    @Schema(title = "适用优惠：0-会员价  1-优惠劵 (多个用逗号隔开)")
    private String applyType;

    @Schema(title = "适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与")
    private Integer productType;

    @Schema(title = "适用商品数量")
    private Integer productNumber;

    @Schema(title = "活动状态：  0-未开始  1-进行中  2-已结束")
    private Integer status;

    @Schema(title = "参与人数")
    private Integer peopleNum;

    @Schema(title = "支付订单数")
    private Integer payOrder;

    @Schema(title = "金额总数")
    private BigDecimal gaapTotal;

    @Schema(title = "满减活动关联商品信息")
    private List<FullScaleProductVo> fullScaleProducts;

    @Schema(title = "满减活动规则信息")
    private List<FullScaleRuleVo> fullScaleRules;

}
