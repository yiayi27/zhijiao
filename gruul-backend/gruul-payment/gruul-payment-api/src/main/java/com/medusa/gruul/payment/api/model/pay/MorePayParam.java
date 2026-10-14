package com.medusa.gruul.payment.api.model.pay;

import com.medusa.gruul.common.system.model.Platform;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 可传递更多支付参数
 *
 * @author 张治保
 * date 2022/1/11
 */
@Getter
@Setter
@Accessors(chain = true)
public class MorePayParam extends PayParam {
    /**
     * 当前调起支付的应用平台
     */
    @NotNull
    private Platform platform;
    /**
     * 客户端ip
     */
    private String ip;
    /**
     * openId
     */
    private String openId;
    /**
     * 付款条码串,人脸凭证，有关支付代码相关的，
     */
    private String authCode;
    /**
     * 微信专用，，，，
     * WAP支付链接
     */
    private String wapUrl;
    /**
     * 微信专用，，，，
     * WAP支付网页名称
     */
    private String wapName;
}
