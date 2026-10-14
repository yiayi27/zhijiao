package com.medusa.gruul.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import com.medusa.gruul.order.api.enums.PayTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Api order vo.
 * <p>
 * 管理端订单返回结果
 *
 * @author alan
 * @date 2023/11/5 21:29
 */
@Data
@Schema(title = "管理端订单返回结果")
public class ApiOrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @Schema(title = "订单id", example = "1")
    private Long orderId;

    /**
     * 原订单id，只要换货单有此属性
     */
    @Schema(title = "原订单id，只要换货单有此属性", example = "1")
    private Long originalOrderId;

    /**
     * 订单类型
     */
    @Schema(title = "订单类型")
    private OrderTypeEnum type;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "过期时间")
    private LocalDateTime expireTime;


    /**
     * 订单支付金额
     */
    @Schema(title = "订单支付金额")
    private BigDecimal payAmount;


    /**
     * 支付类型
     */
    @Schema(title = "支付类型")
    private PayTypeEnum payType;


    /**
     * 订单状态
     */
    @Schema(title = "订单状态")
    private OrderStatusEnum status;

    /**
     * 订单单个商品详情
     */
    @Schema(title = "订单单个商品详情")
    private List<SimpleOrderItemVo> itemVoList;
}
