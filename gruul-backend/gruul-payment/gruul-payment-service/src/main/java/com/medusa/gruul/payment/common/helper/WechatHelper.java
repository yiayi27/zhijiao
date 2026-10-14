package com.medusa.gruul.payment.common.helper;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.crypto.symmetric.AES;
import com.egzosn.pay.common.util.XML;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.platform.api.model.PlatformConstant;
import jodd.util.Base64;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


/**
 * 微信证书文件辅助类
 *
 * @author 张治保
 * date 2024/12/20
 */
@Slf4j
public class WechatHelper {

    /**
     * 解密退款reqInfo加密信息
     *
     * @param reqInfo 加密信息
     * @param mchKey  商户秘钥
     * @return 解密结果
     */
    public static Map<String, Object> decodeReqInfo(String reqInfo, String mchKey) {
        String xmlReqInfo;
        try {
            xmlReqInfo = new AES(
                    Mode.ECB,
                    Padding.PKCS5Padding,
                    MD5.create()
                            .digestHex(mchKey)
                            .toLowerCase()
                            .getBytes(CharsetUtil.CHARSET_UTF_8)
            ).decryptStr(Base64.decode(reqInfo));
        } catch (Exception exception) {
            log.error("解密退款数据时发生异常", exception);
            throw exception;
        }
        return XML.toJSONObject(xmlReqInfo, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 获取小程序appid
     *
     * @param shopId 店铺id
     * @return 小程序appid
     */
    public static String getAppid(String shopId) {
        return RedisUtil.getCacheMapValue(
                RedisUtil.key(PlatformConstant.SHOP_MINIAPP_SECRET_CACHE_PREFIX, shopId),
                "appid"
        );
    }
}
