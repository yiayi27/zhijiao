package com.medusa.gruul.afs.model;

import com.medusa.gruul.afs.api.enums.AfsOrderStatusEnum;
import com.medusa.gruul.afs.api.enums.AfsOrderTypeEnum;
import com.medusa.gruul.order.api.enums.DeliverTypeEnum;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The type Manage afs order vo.
 *
 * @author alan
 * @description: 商家售后订单列表
 * @date 2024/8/5 21:54
 */
@Data
@Schema(title = "商家售后订单列表")
public class ManageAfsOrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    @Schema(title = "工单ID")
    private Long id;

    /**
     * 工单编号
     */
    @Schema(title = "工单编号")
    private String no;

    /**
     * 工单类型：3.退款 4.换货 5.退货退款
     */
    @Schema(title = "工单类型：3.退款 4.换货 5.退货退款")
    private AfsOrderTypeEnum type;

    /**
     * 商品图片
     */
    @Schema(title = "商品图片")
    private String productPic;

    /**
     * 商品名
     */
    @Schema(title = "商品名")
    private String productName;

    /**
     * 商品规格
     */
    @Schema(title = "商品规格")
    private String specs;

    /**
     * 销售价格
     */
    @Schema(title = "销售价格")
    private BigDecimal productPrice;

    /**
     * 商品数量
     */
    @Schema(title = "商品数量")
    private Integer productQuantity;

    /**
     * 用户id
     */
    @Schema(title = "用户id", example = "1")
    private String userId;

    /**
     * 用户帐号
     */
    @Schema(title = "用户帐号")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    private String userAvatarUrl;

    /**
     * 配送方式
     */
    @Schema(title = "配送方式")
    private DeliverTypeEnum deliveryType;


    /**
     * 收货人姓名
     */
    @Schema(title = "收货人姓名")
    private String receiverName;

    /**
     * 收货人电话
     */
    @Schema(title = "收货人电话")
    private String receiverPhone;


    /**
     * 省份/直辖市
     */
    @Schema(title = "省份/直辖市")
    private String receiverProvince;

    /**
     * 城市
     */
    @Schema(title = "城市")
    private String receiverCity;

    /**
     * 区
     */
    @Schema(title = "区")
    private String receiverRegion;

    /**
     * 详细地址
     */
    @Schema(title = "详细地址")
    private String receiverDetailAddress;

    /**
     * 订单退款金额
     */
    @Schema(title = "订单退款金额")
    private BigDecimal refundAmount;

    /**
     * 审核状态：1.待商家审核 2.待退货 4.待发货 5.待提货  6.成功
     */
    @Schema(title = "审核状态：1.待商家审核 2.待退货 4.待发货 5.待提货  6.成功")
    private AfsOrderStatusEnum status;

    /**
     * 发货单ID
     */
    @Schema(title = "发货单ID")
    private String sendBillId;

    /**
     * 发货单名称
     */
    @Schema(title = "发货单名称")
    private String sendBillName;

    /**
     * 申请本次售后的订单ID
     */
    @Schema(title = "申请本次售后的订单ID，存在多个的时候为第一个的值")
    private Long orderId;

    /**
     * 创建时间
     */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @Schema(title = "备注")
    private String note;

    /**
     * 订单状态
     */
    @Schema(title = "订单状态")
    private OrderStatusEnum orderStatus;
}
