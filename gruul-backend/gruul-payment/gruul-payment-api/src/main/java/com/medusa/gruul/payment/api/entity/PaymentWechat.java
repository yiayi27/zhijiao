package com.medusa.gruul.payment.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author whh
 * @since 2023-11-04
 */
@Data
@Accessors(chain = true)
@TableName("t_payment_wechat")
@Schema(title = "PaymentWechat对象", description = "")
public class PaymentWechat implements Serializable {

    /**
     * 主键id
     */
    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 交易类型 1-JSAPI支付（或小程序支付）、2-Native支付、3-app支付，4-H5支付，
     */
    @Schema(title = "交易类型 1-JSAPI支付（或小程序支付）、2-Native支付、3-app支付，4-H5支付，6")
    @TableField("trade_type")
    private Integer tradeType;

    /**
     * 主记录表id
     */
    @Schema(title = "主记录表id")
    @TableField("payment_id")
    private Long paymentId;

    /**
     * 业务订单号
     */
    @Schema(title = "业务订单号")
    @TableField("out_trade_no")
    private String outTradeNo;

    /**
     * 商品的标题/交易标题/订单标题/订单关键字等。
     */
    @Schema(title = "商品的标题/交易标题/订单标题/订单关键字等。")
    @TableField("subject")
    private String subject;

    /**
     * 用户openId
     */
    @Schema(title = "用户openId")
    @TableField("open_id")
    private String openId;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;


}
