package com.medusa.gruul.order.api.model;

import com.medusa.gruul.order.api.enums.DeliverTypeEnum;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import com.medusa.gruul.order.api.enums.PayTypeEnum;
import com.medusa.gruul.order.api.enums.SourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 创建订单参数
 *
 * @author alan
 * @date 2023/10/4 14:36
 */
@Data
@Schema(title = "创建订单参数", description = "创建订单参数")
public class CreateOrderDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "商品不能为空")
    @Schema(title = "商品规格数量Map")
    private List<ItemDto> itemDtoList;

    @NotNull(message = "配送方式不能为空")
    @Schema(title = "配送方式")
    private DeliverTypeEnum deliverType;

    @Schema(title = "提货点ID：用户自提时选择")
    private Long pointId;

    @NotNull(message = "收货地址不能为空")
    @Schema(title = "收货人信息id")
    private Long miniAccountAddressId;

    @Schema(title = "优惠券ID")
    private Long couponId;

    @Schema(title = "使用积分抵扣")
    private BigDecimal integral;

    @Schema(title = "参与的满减活动")
    private Long fullScaleId;

    @Schema(title = "用户留言")
    private String userNote;


    @Schema(title = "自定义表单")
    private String customForm;

    @NotNull(message = "是否使用会员价不能为空")
    @Schema(title = "是否使用会员价")
    private Boolean useMemberPrice;

    @NotNull(message = "支付方式不能为空")
    @Schema(title = "支付方式")
    private PayTypeEnum payType;


    @Schema(title = "微信formId")
    private String formId;

    @NotNull(message = "订单来源不能为空")
    @Schema(title = "订单来源")
    private SourceTypeEnum sourceType;

    @NotNull(message = "订单类型不能为空")
    @Schema(title = "订单类型")
    private OrderTypeEnum orderType;


    @Schema(title = "所在小区id")
    private Long communityId;

    @Schema(title = "发货消息订阅TemplateId")
    private String deliveryTemplateId;

    @Schema(title = "开通会员的订阅消息ID")
    private String memberTemplateId;

    @Schema(title = "接龙活动ID")
    private Long solitaireActivityId;

    @Schema(hidden = true)
    public List<Long> getItemSkuIds() {
        return new ArrayList<>(itemDtoList.stream().map(ItemDto::getSkuId).collect(Collectors.toSet()));
    }

}
