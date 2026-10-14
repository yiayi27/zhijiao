package com.medusa.gruul.payment.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import com.medusa.gruul.payment.api.enums.PayChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @author create by zq
 * @date created in 2024/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_ent_pay_back_log")
@Schema(title = "EntPayCallBackLog对象", description = "EntPayCallBackLog实体")
public class EntPayCallBackLog extends BaseEntity {


    /**
     * 主键id
     */
    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 关联支付单主键id
     */
    @Schema(title = "关联支付单主键id")
    @TableField(value = "ent_pay_id")
    private Long entPayId;


    /**
     * 支付渠道:1-微信
     */
    @Schema(title = "支付渠道:1-微信")
    @TableField("pay_channel")
    private PayChannelEnum payChannel;


    /**
     * 支付渠道类型
     */
    @Schema(title = "支付渠道类型")
    @TableField("pay_channel_type")
    private PayChannelEnum payChannelType;


    /**
     * 回调信息json内容
     */
    @Schema(title = "回调信息json内容")
    @TableField("callback_context")
    private String callbackContext;

}
