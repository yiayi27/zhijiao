package com.medusa.gruul.payment.common.model;

import com.medusa.gruul.payment.api.enums.PayType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 支付商户渠道配置VO
 *
 * @author xiaoq
 * @ Description 支付商户渠道配置VO
 * @date 2022-07-20 17:04
 */
@Data
@Accessors(chain = true)
public class MerchantDetailsVO {

    /**
     * 配置详情id
     */
    private String detailsId;

    /**
     * 支付渠道
     */
    private PayType payType;

    /**
     * 应用appid
     */
    private String appid;

    /**
     * 应用私钥
     */
    private String keyPrivate;

    /**
     * 支付宝公钥
     */
    private String keyPublic;
    /**
     * 商户号
     */
    private String mchId;
    /**
     * 支付证书
     */
    private String keyCert;
}