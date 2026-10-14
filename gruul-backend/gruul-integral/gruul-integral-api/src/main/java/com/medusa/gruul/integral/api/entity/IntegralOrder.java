package com.medusa.gruul.integral.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 积分订单表
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_order")
@Schema(title = "IntegralOrder对象", description = "积分订单表")
public class IntegralOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "积分订单id")
    @TableId(value = "prize_order_id", type = IdType.INPUT)
    private Long prizeOrderId;

    @Schema(title = "商铺id")
    @TableField("shop_id")
    private String shopId;

    @Schema(title = "积分商品id")
    @TableField("prize_id")
    private Long prizeId;


    @Schema(title = "优惠券id")
    @TableField("coupon_id")
    private Long couponId;

    @Schema(title = "优惠券类型")
    @TableField("coupon_type")
    private Integer couponType;

    @Schema(title = "积分商品订单类型 (0.待发货 1.已发货 2.已完成)")
    @TableField("prize_order_status")
    private Integer prizeOrderStatus;

    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    @Schema(title = "用户openId")
    @TableField("open_id")
    private String openId;

    @Schema(title = "用户头像")
    @TableField("member_avatar_url")
    private String memberAvatarUrl;

    @Schema(title = "支付类型 100->余额支付;101->微信支付;102->好友代付;")
    @TableField("pay_type")
    private Integer payType;

    @Schema(title = "支付时间")
    @TableField("pay_time")
    private LocalDateTime payTime;

    @Schema(title = "用户微信名")
    @TableField("wxuser_name")
    private String wxuserName;

    @Schema(title = "支付费用")
    @TableField("pay_fee")
    private BigDecimal payFee;

    @Schema(title = "订单类型（0为积分商品 1为积分优惠卷）")
    @TableField("order_type")
    private Boolean orderType;

    @Schema(title = "积分商品实际积分")
    @TableField("price")
    private BigDecimal price;

    @Schema(title = "积分商品图片(下单时)")
    @TableField("integral_goods_pic")
    private String integralGoodsPic;

    @Schema(title = "实付积分")
    @TableField("expend_integral")
    private BigDecimal expendIntegral;

    @Schema(title = "收货人姓名")
    @TableField("receiver_name")
    private String receiverName;

    @Schema(title = "收货地址")
    @TableField("address")
    private String address;

    @Schema(title = "省级code")
    @TableField("province_code")
    private String provinceCode;

    @Schema(title = "市级code")
    @TableField("city_code")
    private String cityCode;

    @Schema(title = "区级code")
    @TableField("area_code")
    private String areaCode;


    @Schema(title = "收货人电话")
    @TableField("tel_phone")
    private String telPhone;

    @Schema(title = "物流单号")
    @TableField("delivery_sn")
    private String deliverySn;

    @Schema(title = "物流公司")
    @TableField("delivery_company")
    private String deliveryCompany;

    @Schema(title = "集包地信息")
    @TableField("package_name")
    private String packageName;

    @Schema(title = "集包地信息")
    @TableField("package_code")
    private String packageCode;

    @Schema(title = "分拣码")
    @TableField("sorting_code")
    private String sortingCode;

    @Schema(title = "邮费")
    @TableField("shipping_fee")
    private BigDecimal shippingFee;

    @Schema(title = "配送方式(0.物流配送)目前只有物流配送 ")
    @TableField("distri_mode")
    private Integer distriMode;

    @Schema(title = "配送信息")
    @TableField("delivery_staff")
    private String deliveryStaff;

    @Schema(title = "积分商品名称")
    @TableField("integral_good_name")
    private String integralGoodName;


    @Schema(title = "发货消息订阅")
    @TableField("delivery_template_id")
    private String deliveryTemplateId;

    @Schema(title = "发货时间")
    @TableField("send_time")
    private LocalDateTime sendTime;


    @Schema(title = "收货时间")
    @TableField("receive_time")
    private LocalDateTime receiveTime;


    @Schema(title = "商家备注")
    @TableField("back_remark")
    private String backRemark;

    @Schema(title = "买家留言")
    @TableField("buyers_note")
    private String buyersNote;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(title = "删除状态：0->未删除；1->已删除")
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Boolean deleted;

}
