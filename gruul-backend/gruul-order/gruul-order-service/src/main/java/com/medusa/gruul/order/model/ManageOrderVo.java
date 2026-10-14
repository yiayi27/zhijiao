package com.medusa.gruul.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.afs.api.entity.AfsOrder;
import com.medusa.gruul.order.api.enums.DeliverTypeEnum;
import com.medusa.gruul.order.api.enums.OrderStatusEnum;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * The type Manage order vo.
 * <p>
 * 管理端订单返回结果
 *
 * @author alan
 * @date 2023/11/5 21:29
 */
@Data
@Schema(title = "管理端订单返回结果")
public class ManageOrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单id
     */
    @Schema(title = "订单id", example = "1")
    private Long orderId;

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
    private String createTime;

    /**
     * 分拣线路ID
     */
    @Schema(title = "分拣线路ID", example = "1")
    private Long lineId;

    /**
     * 分拣线路名称
     */
    @Schema(title = "分拣线路名称")
    private String lineName;

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
     * 收货人邮编
     */
    @Schema(title = "收货人邮编")
    private String receiverPostCode;

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
     * 物流公司
     */
    @Schema(title = "物流公司")
    private String deliveryCompany;


    /**
     * 物流单号
     */
    @Schema(title = "物流单号")
    private String deliverySn;


    /**
     * 订单支付金额
     */
    @Schema(title = "订单支付金额")
    private BigDecimal payAmount;


    /**
     * 订单状态
     */
    @Schema(title = "订单状态")
    private OrderStatusEnum status;


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
     * 商家备注
     */
    @Schema(title = "商家备注")
    private String note;

    /**
     * 享受的权益
     */
    @Schema(title = "享受的权益，多个以逗号分割： 0-自定义权益 1-商品权益 2-包邮权益 3-积分倍数权益 4-专属客服权益 5-优先发货 6-急速售后 ")
    private String privileges;


    /**
     * 返利金额
     */
    @Schema(title = "返利金额")
    private BigDecimal rebateAmount;


    /**
     * 订单单个商品详情
     */
    @Schema(title = "订单单个商品详情")
    private List<SimpleOrderItemVo> itemVoList;


    /**
     * 接龙活动ID
     */
    @Schema(title = "接龙活动ID")
    private Long solitaireActivityId;


    /**
     * 订单的售后详情
     */
    @Schema(title = "订单的售后详情")
    private List<AfsOrder> afsOrderList;
}
