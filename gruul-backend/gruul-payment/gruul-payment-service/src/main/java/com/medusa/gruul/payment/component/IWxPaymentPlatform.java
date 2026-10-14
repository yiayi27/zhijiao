package com.medusa.gruul.payment.component;

import com.egzosn.pay.common.api.PayConfigStorage;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.spring.boot.core.merchant.bean.CommonPaymentPlatformMerchantDetails;
import com.egzosn.pay.spring.boot.core.provider.merchant.platform.WxPaymentPlatform;
import com.egzosn.pay.wx.v3.api.WxPayConfigStorage;
import com.egzosn.pay.wx.v3.api.WxPayService;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author 张治保
 * date 2024/12/21
 */
@Configuration(WxPaymentPlatform.PLATFORM_NAME)
public class IWxPaymentPlatform extends WxPaymentPlatform {


    @Override
    public PayService getPayService(PayConfigStorage payConfigStorage, HttpConfigStorage httpConfigStorage) {
        if (payConfigStorage instanceof WxPayConfigStorage) {
            WxPayService wxPayService = new IWxpayService((WxPayConfigStorage) payConfigStorage);
            wxPayService.setRequestTemplateConfigStorage(httpConfigStorage);
            return wxPayService;
        }
        WxPayConfigStorage configStorage = new WxPayConfigStorage();
        configStorage.setInputCharset(payConfigStorage.getInputCharset());
        configStorage.setAppId(payConfigStorage.getAppId());
        configStorage.setMchId(payConfigStorage.getPid());
        configStorage.setAttach(payConfigStorage.getAttach());
        configStorage.setKeyPrivate(payConfigStorage.getKeyPrivate());
        configStorage.setKeyPublic(payConfigStorage.getKeyPublic());
        configStorage.setNotifyUrl(payConfigStorage.getNotifyUrl());
        configStorage.setReturnUrl(payConfigStorage.getReturnUrl());
        configStorage.setPayType(payConfigStorage.getPayType());
        configStorage.setTest(payConfigStorage.isTest());
        configStorage.setSignType(payConfigStorage.getSignType());

        if (payConfigStorage instanceof CommonPaymentPlatformMerchantDetails) {
            CommonPaymentPlatformMerchantDetails merchantDetails = (CommonPaymentPlatformMerchantDetails) payConfigStorage;

            configStorage.setSubAppid(merchantDetails.getSubAppId());
            configStorage.setSubMchId(merchantDetails.getSubMchId());
            if (null != merchantDetails.getKeyCert()) {
                if (null == httpConfigStorage) {
                    httpConfigStorage = new HttpConfigStorage();
                }
                httpConfigStorage.setCertStoreType(merchantDetails.getCertStoreType());
                configStorage.setCertStoreType(merchantDetails.getCertStoreType());
                configStorage.setApiClientKeyP12(merchantDetails.getKeyCert());
                try {
                    httpConfigStorage.setKeystore(merchantDetails.getKeyCertInputStream());
                } catch (IOException e) {
                    LOG.error(e);
                }

                httpConfigStorage.setCertStoreType(CertStoreType.INPUT_STREAM);
                httpConfigStorage.setStorePassword(merchantDetails.getKeystorePwd());

            }
        }


        IWxpayService wxPayService = new IWxpayService(configStorage);
        wxPayService.setRequestTemplateConfigStorage(httpConfigStorage);
        return wxPayService;
    }
}
