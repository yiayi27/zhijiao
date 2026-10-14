package com.medusa.gruul.integral.web.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.integral.api.entity.IntegralOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 积分商品订单 vo 基础信息
 * @Author: xiaoq
 * @Date: 2023/11/26 10:25
 */
@Data
@Schema(title = "积分商品订单")
public class IntegralGoodsOrderVo {


    @Schema(title = "积分订单id")
    private Long prizeOrderId;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    private String createTime;


    @Schema(title = "积分商品id")
    private Long prizeId;


    @Schema(title = "积分物品订单类型 (0.待发货 1.已发货 2.已完成)")
    private Integer prizeOrderStatus;


    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "用户头像")
    private String memberAvatarUrl;


    @Schema(title = "支付时间")
    private LocalDateTime payTime;


    @Schema(title = "支付类型")
    private Integer payType;


    @Schema(title = "用户微信名")
    private String wxuserName;


    @Schema(title = "积分商品实际积分")
    private BigDecimal price;


    @Schema(title = "实付积分")
    private BigDecimal expendIntegral;


    @Schema(title = "配送方式(0.物流配送)目前只有物流配送 ")
    private Integer distriMode;


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

    @Schema(title = "收货人手机号")
    private String telPhone;


    @Schema(title = "物流单号")
    private String deliverySn;


    @Schema(title = "物流公司")
    private String deliveryCompany;


    @Schema(title = "邮费")
    private BigDecimal shippingFee;


    @Schema(title = "发货消息订阅")
    private String deliveryTemplateId;


    @Schema(title = "商家备注")
    private String backRemark;

    @Schema(title = "买家留言")
    private String buyersNote;

    @Schema(title = "积分商品名")
    private String integralGoodName;

    @Schema(title = "发货时间")
    private String sendTime;


    @Schema(title = "收货时间")
    private String receiveTime;

    @Schema(title = "积分商品图片(下单时)")
    private String integralGoodsPic;

    /**
     * 订单的积分商品信息
     */
    @Schema(title = "订单中商品总信息")
    private List<ShoppingInfVo> shoppingInfVos;

    public IntegralOrder coverIntegralOrder() {
        IntegralOrder integralOrder = new IntegralOrder();
        BeanUtil.copyProperties(this, integralOrder);
        return integralOrder;
    }
}
