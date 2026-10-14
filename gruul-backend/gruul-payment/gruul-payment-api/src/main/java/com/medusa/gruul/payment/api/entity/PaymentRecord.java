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
@TableName("t_payment_record")
@Schema(title = "PaymentRecord对象", description = "")
public class PaymentRecord implements Serializable {

    /**
     * 主键id
     */
    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求对象数据
     */
    @Schema(title = "请求对象数据")
    @TableField("request_params")
    private String requestParams;

    /**
     * 最终发送数据
     */
    @Schema(title = "最终发送数据")
    @TableField("send_param")
    private String sendParam;

    /**
     * 第三方回调的数据
     */
    @Schema(title = "第三方回调的数据")
    @TableField("notify_param")
    private String notifyParam;

    /**
     * 支付 记录表id
     */
    @Schema(title = "支付 记录表id")
    @TableField("payment_id")
    private Long paymentId;

}
