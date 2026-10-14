package com.medusa.gruul.payment.handler.merchant;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.annotation.MerchantHandler;
import com.medusa.gruul.payment.common.model.MerchantConfig;
import com.medusa.gruul.payment.common.model.MerchantDetailsDTO;
import com.medusa.gruul.payment.conf.PayProperty;
import com.medusa.gruul.payment.conf.RedisMerchantPayConfigManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 支付宝商户处理器
 *
 * @author 张治保
 * date 2022/1/21
 */
@Component
@RequiredArgsConstructor
@MerchantHandler(PayType.ALIPAY)
public class AlipayMerchantHandler extends AbstractMerchantHandler {

    private final PayProperty payProperty;

    @Override
    protected void save(String shopId, MerchantDetailsDTO config) {
        String detailsId = config.getDetailsId();
        // id不存在进行商户新增
        RedisMerchantPayConfigManager.instance.save(
                new MerchantConfig()
                        .setShopId(shopId)
                        .setDetailsId(detailsId)
                        //固定值
                        .setPayType(PayType.ALIPAY)
                        .setCertStoreType(CertStoreType.PATH)
                        .setSignType(SignUtils.RSA2)
                        .setInputCharset(CharsetUtil.UTF_8)
                        .setIsTest(false)
                        //动态值
                        .setAppid(config.getAppid())
                        .setKeyPrivate(config.getKeyPrivate())
                        .setKeyPublic(config.getKeyPublic())
                        .setKeyCert(config.getKeyCert())
                        //回调 通知地址
                        .setNotifyUrl(StrUtil.format(payProperty.getNotifyTemplate(), detailsId))
                        .setReturnUrl(StrUtil.format(payProperty.getReturnTemplate(), PayType.ALIPAY.name()))
        );
    }

    @Override
    protected PayType type() {
        return PayType.ALIPAY;
    }
}
