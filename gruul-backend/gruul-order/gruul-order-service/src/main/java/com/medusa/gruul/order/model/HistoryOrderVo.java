package com.medusa.gruul.order.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.medusa.gruul.order.api.enums.OrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The type Simple order vo.
 * <p>
 * 简单的订单结果
 *
 * @author alan
 * @date 2023/11/13 20:20
 */
@Data
@Schema(title = "提货点历史订单结果")
public class HistoryOrderVo implements Serializable {

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
     * 用户帐号
     */
    @Schema(title = "用户帐号")
    @TableField("user_id")
    private String userId;

    /**
     * 用户帐号
     */
    @Schema(title = "用户帐号")
    @TableField("user_name")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(title = "用户头像")
    @TableField("user_avatar_url")
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
     * 发货单名称
     */
    @Schema(title = "发货单名称")
    private String sendBillName;

    /**
     * 用户备注
     */
    @Schema(title = "用户备注")
    private String userNote;

    /**
     * 自定义表单
     */
    @Schema(title = "自定义表单")
    private String customForm;

    /**
     * 订单单个商品详情
     */
    @Schema(title = "订单单个商品详情")
    private List<SimpleOrderItemVo> itemVoList;

}
