package com.medusa.gruul.integral.web.model.dto.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.integral.api.entity.IntegralOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @Description: 积分订单dto (积分商品)
 * @Author: xiaoq
 * @Date: 2023/12/2 10:26
 */
@Data
@Schema(title = "创建订单dto")
public class  IntegralOrderDto {
    private Long prizeOrderId;

    @Schema(title = "积分商品id")
    private Long prizeId;

    @Schema(title = "优惠券id")
    private Long couponId;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "支付类型 0为微信支付 1为余额支付 2支付宝支付")
    private Integer payType;

    @Schema(title = "订单类型（0为积分商品 1为积分优惠卷）")
    private Boolean orderType;

    @Schema(title = "积分商品实际积分")
    @Min(0)
    @NotNull
    private BigDecimal price;

    @Schema(title = "积分商品图片(下单时)")
    private String integralGoodsPic;

    @Schema(title = "发货消息订阅")
    private String deliveryTemplateId;

    @Schema(title = "收货人姓名")
    private String receiverName;

    @Schema(title = "省级code")
    private String provinceCode;

    @Schema(title = "市级code")
    private String cityCode;

    @Schema(title = "区级code")
    private String areaCode;

    @Schema(title = "收货地址")
    private String address;

    @Schema(title = "收货人电话")
    private String telPhone;

    @Schema(title = "邮费")
    @Min(0)
    private BigDecimal shippingFee;

    @Schema(title = "买家留言")
    private String buyersNote;

    public IntegralOrder coverIntegralOrder() {
        IntegralOrder integralOrder = new IntegralOrder();
        BeanUtil.copyProperties(this, integralOrder);
        return integralOrder;
    }
}
