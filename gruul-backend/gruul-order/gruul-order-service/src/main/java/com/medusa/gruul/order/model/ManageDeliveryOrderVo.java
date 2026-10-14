package com.medusa.gruul.order.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * The type Manage delivery order vo.
 * <p>
 * 管理端订单返回结果
 *
 * @author alan
 * @date 2023/11/5 21:29
 */
@Data
@Schema(title = "管理端订单返回结果")
public class ManageDeliveryOrderVo implements Serializable {
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
     * 订单支付金额
     */
    @Schema(title = "订单支付金额")
    private BigDecimal payAmount;

    /**
     * 订单单个商品详情
     */
    @Schema(title = "订单单个商品详情")
    private List<SimpleOrderItemVo> itemVoList;
}
