package com.medusa.gruul.platform.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceOkHttpImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedissonConfigImpl;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.platform.api.model.PlatformConstant;
import com.medusa.gruul.platform.constant.MeConstant;
import com.medusa.gruul.platform.constant.PlatformError;
import com.medusa.gruul.platform.model.WechatMiniAppSecret;
import com.medusa.gruul.platform.model.dto.WxaGetwxacode;
import com.medusa.gruul.platform.service.WechatMiniAppService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.error.WxErrorException;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Base64;

/**
 * @author 张治保
 * @since 2025/4/7
 */
@Service
@RequiredArgsConstructor
public class WechatMiniAppServiceImpl implements WechatMiniAppService {

    private final WxMaService wxMaService = new WxMaServiceOkHttpImpl();
    private final RedissonClient redissonClient;

    /**
     * 获取指定店铺的 小程序接口服务
     *
     * @param shopId 店铺id
     * @return 小程序接口服务
     */
    @Override
    public WxMaService wxMaservice(String shopId) {
        WechatMiniAppSecret secret = getConfig(shopId);
        if (secret == null || StrUtil.isEmpty(secret.getAppid()) || StrUtil.isEmpty(secret.getSecret())) {
            throw PlatformError.WECHAT_MINI_SECRET_NOT_CONFIG.exception();
        }
        return wxMaService.switchoverTo(
                secret.getAppid(),
                _ -> {
                    WxMaRedissonConfigImpl config = new WxMaRedissonConfigImpl(redissonClient);
                    config.setAppid(secret.getAppid());
                    config.setSecret(secret.getSecret());
                    return config;
                }
        );
    }

    /**
     * 获取指定店铺的小程序码
     *
     * @param shopId 店铺id
     * @param param  小程序码配置参数
     * @return base64格式的小程序二维码图片
     */
    @Override
    @SneakyThrows
    public String wxaCode(String shopId, WxaGetwxacode param) {
        WxMaService wxMaService = wxMaservice(shopId);
        String appid = wxMaService.getWxMaConfig().getAppid();
        String key = RedisUtil.key(MeConstant.SHOP_MINIAPP_CODE_CACHE_PREFIX, appid, param.getPath(), param.getWidth(), param.getEnvVersion());
        return RedisUtil.getCache(
                () -> RedisUtil.getCacheObject(key),
                () -> {
                    WxMaQrcodeService qrcodeService = wxMaService.getQrcodeService();
                    byte[] qrcodeBytes;
                    try {
                        qrcodeBytes = qrcodeService.createWxaCodeBytes(param.getPath(), param.getEnvVersion(), param.getWidth(), true, null, false);
                    } catch (WxErrorException e) {
                        throw new RuntimeException(e);
                    }
                    return STR."data:image/png;base64,\{Base64.getEncoder().encodeToString(qrcodeBytes)}";
                },
                //缓存1天时间
                Duration.ofDays(1),
                MeConstant.SHOP_MINIAPP_CODE_CACHE_PREFIX
        );

    }

    /**
     * 保存小程序配置
     *
     * @param shopId 店铺id
     * @param appid  小程序appid
     * @param secret 小程序app secret
     */
    @Override
    public void saveConfig(String shopId, String appid, String secret) {
        RedisUtil.setCacheMap(
                RedisUtil.key(PlatformConstant.SHOP_MINIAPP_SECRET_CACHE_PREFIX, shopId),
                new WechatMiniAppSecret()
                        .setAppid(appid)
                        .setSecret(secret)
        );
    }

    /**
     * 获取小程序配置
     *
     * @param shopId 店铺id
     * @return 小程序配置
     */
    @Override
    public WechatMiniAppSecret getConfig(String shopId) {
        return RedisUtil.getCacheMap(
                RedisUtil.key(PlatformConstant.SHOP_MINIAPP_SECRET_CACHE_PREFIX, shopId),
                WechatMiniAppSecret.class
        );
    }


}
