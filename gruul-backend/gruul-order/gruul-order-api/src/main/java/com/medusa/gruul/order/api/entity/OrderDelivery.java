package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.order.api.enums.DeliverTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单收货信息表
 * </p>
 *
 * @author alan
 * @since 2023-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order_delivery")
@Schema(title = "OrderDelivery对象", description = "订单收货信息表")
public class OrderDelivery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单物流id
     */
    @Schema(title = "订单物流id")
    @TableId(value = "order_id", type = IdType.INPUT)
    private Long orderId;


    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;


    /**
     * 配送方式
     */
    @Schema(title = "配送方式")
    @TableField("delivery_type")
    private DeliverTypeEnum deliveryType;

    /**
     * 发货时间
     */
    @Schema(title = "发货时间")
    @TableField("delivery_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;

    /**
     * 物流公司
     */
    @Schema(title = "物流公司")
    @TableField("delivery_company")
    private String deliveryCompany;

    /**
     * 物流单号
     */
    @Schema(title = "物流单号")
    @TableField("delivery_sn")
    private String deliverySn;

    /**
     * 收货人姓名
     */
    @Schema(title = "收货人姓名")
    @TableField("receiver_name")
    private String receiverName;

    /**
     * 收货人电话
     */
    @Schema(title = "收货人电话")
    @TableField("receiver_phone")
    private String receiverPhone;

    /**
     * 收货人邮编
     */
    @Schema(title = "收货人邮编")
    @TableField("receiver_post_code")
    private String receiverPostCode;

    /**
     * 省份/直辖市
     */
    @Schema(title = "省份/直辖市")
    @TableField("receiver_province")
    private String receiverProvince;

    /**
     * 城市
     */
    @Schema(title = "城市")
    @TableField("receiver_city")
    private String receiverCity;

    /**
     * 区
     */
    @Schema(title = "区")
    @TableField("receiver_region")
    private String receiverRegion;

    /**
     * 详细地址
     */
    @Schema(title = "详细地址")
    @TableField("receiver_detail_address")
    private String receiverDetailAddress;

    /**
     * 确认收货状态：0->未确认；1->已确认
     */
    @Schema(title = "确认收货状态：0->未确认；1->已确认")
    @TableField("is_received")
    private Boolean received;

    /**
     * 确认收货时间
     */
    @Schema(title = "确认收货时间")
    @TableField("receive_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;


    /**
     * 物流公司编码
     */
    @Schema(title = "物流公司编码")
    @TableField("delivery_code")
    private String deliveryCode;


    @Schema(title = "集包地信息")
    @TableField("package_name")
    private String packageName;

    @Schema(title = "集包地信息")
    @TableField("package_code")
    private String packageCode;

    @Schema(title = "分拣码")
    @TableField("sorting_code")
    private String sortingCode;


    /**
     * 发货消息订阅
     */
    @Schema(title = "发货消息订阅")
    @TableField("delivery_template_id")
    private String deliveryTemplateId;

}
