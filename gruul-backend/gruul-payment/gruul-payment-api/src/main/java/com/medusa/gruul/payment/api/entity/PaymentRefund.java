package com.medusa.gruul.payment.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * @author create by zq
 * @date created in 2024/03/13
 */
@Data
@TableName("t_payment_refund")
@Schema(title = "t_payment_refund对象", description = "t_payment_refund实体")
public class PaymentRefund extends BaseEntity {

    /**
     * 主键id
     */
    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    private String shopId;


    /**
     * 订单id
     */
    @Schema(title = "订单id")
    private String orderId;


    /**
     * 同步请求参数
     */
    @Schema(title = "同步请求参数")
    @TableField(value = "asyn_request")
    private String asynRequest;


    /**
     * 同步返回结果
     */
    @Schema(title = "同步返回结果")
    @TableField("asyn_result")
    private String asynResult;


    /**
     * 异步回调数据
     */
    @Schema(title = "异步回调数据")
    @TableField("syn_callback")
    private String synCallback;


    /**
     * 路由键
     */
    @Schema(title = "路由键")
    @TableField("route_key")
    private String routeKey;

}
