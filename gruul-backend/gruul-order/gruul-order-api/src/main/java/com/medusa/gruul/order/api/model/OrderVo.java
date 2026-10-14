package com.medusa.gruul.order.api.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.account.api.model.UserOrderDistributionVo;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderDelivery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情
 *
 * @author alan
 * @date 2023/11/5 21:29
 */
@Data
@Schema(title = "订单详情")
public class OrderVo extends Order {
    private static final long serialVersionUID = 1L;


    /**
     * 订单单个商品详情
     */
    @Schema(title = "订单单个商品详情")
    private List<OrderItemVo> orderItemList;

    /**
     * 订单物流详情
     */
    @Schema(title = "订单物流详情")
    private OrderDelivery orderDelivery;

    /**
     * 过期时间 前端倒计时用
     */
    @Schema(title = "过期时间")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireDate;


    /**
     * 商品总数量
     */
    @Schema(title = "商品总数量")
    @TableField(exist = false)
    private Integer productTotalQuantity;

    /**
     * 退款数量
     */
    @Schema(title = "退款数量")
    @TableField(exist = false)
    private Integer refundQuantity;

    /**
     * 应付金额（实际支付金额）=订单总金额+运费-促销优化金额
     */
    @Schema(title = "应付金额（实际支付金额）")
    private BigDecimal payAmount;

    /**
     * 订单用户id
     */
    @Schema(title = "订单用户id")
    private String userId;

    /**
     * 订单分销信息详情
     */
    @Schema(title = "订单分销信息详情")
    private UserOrderDistributionVo userOrderDistributionVo;

    /**
     * 一级分销金额
     */
    @Schema(title = "一级分销金额")
    private BigDecimal firstDistributionAmount;

    /**
     * 二级分销金额
     */
    @Schema(title = "二级分销金额")
    private BigDecimal secondDistributionAmount;

    @Schema(title = "是否开启评论")
    private Boolean openEvaluate;


}
