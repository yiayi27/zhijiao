package com.medusa.gruul.payment.common.model;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.payment.api.enums.PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 支付宝应用配置
 *
 * @author 张治保
 * date 2024/12/18
 */
@Data
public class MerchantDetailsDTO {

    /**
     * 配置id
     */
    @Schema(title = "支付配置id")
    private String detailsId;

    /**
     * 支付渠道枚举
     */
    @NotNull
    @Schema(title = "支付类型")
    private PayType payType;

    /**
     * 应用appid
     */
    @NotBlank
    @Schema(title = "appid")
    private String appid;

    /**
     * 商户号 微信支付需要
     */
    @Schema(title = "商户号 微信支付需要")
    private String mchId;

    /**
     * 微信支付密钥，支付宝私钥证书，
     */
    @NotBlank
    @Schema(title = "微信支付密钥，支付宝私钥证")
    private String keyPrivate;

    /**
     * 公钥
     * 1. 微信：公钥id和公钥文本
     * 2. 支付宝：公钥证书
     */
    @Schema(title = "公钥相关")
    private String keyPublic;

    /**
     * 支付证书
     */
    @NotBlank
    @Schema(title = "支付证书")
    private String keyCert;


    public void validParam() {
        if (PayType.WECHAT == payType && StrUtil.isBlank(mchId)) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
    }
}
