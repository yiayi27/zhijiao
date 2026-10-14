package com.medusa.gruul.payment.handler.merchant;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.medusa.gruul.common.core.util.SystemCode;
import com.medusa.gruul.payment.api.enums.PayType;
import com.medusa.gruul.payment.common.annotation.MerchantHandler;
import com.medusa.gruul.payment.common.helper.WechatHelper;
import com.medusa.gruul.payment.common.model.MerchantConfig;
import com.medusa.gruul.payment.common.model.MerchantDetailsDTO;
import com.medusa.gruul.payment.conf.PayProperty;
import com.medusa.gruul.payment.conf.RedisMerchantPayConfigManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 微信商户处理器
 *
 * @author 张治保
 * date 2022/1/21
 */
@Component
@RequiredArgsConstructor
@MerchantHandler(PayType.WECHAT)
public class WechatMerchantHandler extends AbstractMerchantHandler {

    private final PayProperty payProperty;

    @Override
    protected void save(String shopId, MerchantDetailsDTO config) {
        // 获取appid
        String detailsId = config.getDetailsId();
        String appid = WechatHelper.getAppid(shopId);
        if (appid == null) {
            throw SystemCode.DATA_NOT_EXIST.exception().setMsg("请先在店铺设置设置APPID");
        }
        if (!appid.equals(config.getAppid())) {
            throw SystemCode.DATA_NOT_EXIST.exception().setMsg("请和店铺设置里的APPID和商户配置里的APPID保持一致");
        }

        // id不存在进行商户新增
        RedisMerchantPayConfigManager.instance.save(
                new MerchantConfig()
                        .setShopId(shopId)
                        .setDetailsId(detailsId)
                        //固定值
                        .setPayType(PayType.WECHAT)
                        .setCertStoreType(CertStoreType.PATH)
                        .setSignType(SignUtils.MD5)
                        .setInputCharset(CharsetUtil.UTF_8)
                        .setIsTest(Boolean.FALSE)
                        //动态值
                        .setAppid(config.getAppid())
                        .setMchId(config.getMchId())
                        .setKeyPrivate(config.getKeyPrivate())
                        .setKeyPublic(config.getKeyPublic())
                        .setKeyCert(config.getKeyCert())
                        .setKeyCertPwd(config.getMchId())
                        //回调 通知地址
                        .setNotifyUrl(StrUtil.format(payProperty.getNotifyTemplate(), detailsId))
                        .setReturnUrl(StrUtil.format(payProperty.getReturnTemplate(), PayType.ALIPAY.name()))
        );
    }

    @Override
    protected PayType type() {
        return PayType.WECHAT;
    }
}
