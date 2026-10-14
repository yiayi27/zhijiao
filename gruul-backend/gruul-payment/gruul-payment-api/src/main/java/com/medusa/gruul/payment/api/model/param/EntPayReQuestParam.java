package com.medusa.gruul.payment.api.model.param;


import com.medusa.gruul.payment.api.enums.CheckNameEnum;
import com.medusa.gruul.payment.api.enums.PayChannelEnum;
import com.medusa.gruul.payment.api.util.ParamMd5SignUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * @author create by zq
 * @date created in 2023/11/18
 */
@Data
@ToString(exclude = "md5")
@NoArgsConstructor
public class EntPayReQuestParam {


    /**
     * 支付渠道:1-微信
     */
    @NonNull
    @Schema(title = "支付渠道:1-微信")
    private PayChannelEnum payChannel;


    /**
     * 订单总金额，单位为分
     */
    @NonNull
    @Schema(title = "订单总金额，单位为分")
    private Integer amount;


    /**
     * 企业付款备注
     */
    @NonNull
    @Schema(title = "企业付款备注")
    private String description;


    /**
     * 用户标识
     */
    @NonNull
    @Schema(title = "用户标识")
    private String openid;


    /**
     * 校验用户姓名选项
     */
    @NonNull
    @Schema(title = "校验用户姓名选项")
    private CheckNameEnum checkName;


    /**
     * 收款用户姓名. 可选
     */
    @Schema(title = "收款用户姓名.")
    private String reUserName;


    /**
     * 调用接口的机器Ip地址
     */
    @NonNull
    @Schema(title = "调用接口的机器Ip地址")
    private String spbillCreateIp;


    /**
     * 参数校验
     */
    @NonNull
    @Schema(title = "参数校验")
    private String md5;


    public EntPayReQuestParam md5() {
        this.md5 = ParamMd5SignUtils.md5(this);
        return this;
    }

}
