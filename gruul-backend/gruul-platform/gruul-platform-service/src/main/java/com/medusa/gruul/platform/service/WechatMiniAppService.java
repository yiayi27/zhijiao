package com.medusa.gruul.platform.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.medusa.gruul.platform.model.WechatMiniAppSecret;
import com.medusa.gruul.platform.model.dto.WxaGetwxacode;

/**
 * @author 张治保
 * @since 2025/4/7
 */
public interface WechatMiniAppService {


    /**
     * 获取指定店铺的 小程序接口服务
     *
     * @param shopId 店铺id
     * @return 小程序接口服务
     */
    WxMaService wxMaservice(String shopId);

    /**
     * 获取指定店铺的小程序码
     *
     * @param shopId 店铺id
     * @param param  小程序码配置参数
     * @return base64格式的小程序二维码图片
     */
    String wxaCode(String shopId, WxaGetwxacode param);


    /**
     * 保存小程序配置
     *
     * @param shopId 店铺id
     * @param appid  小程序appid
     * @param secret 小程序app secret
     */
    void saveConfig(String shopId, String appid, String secret);


    /**
     * 获取小程序配置
     *
     * @param shopId 店铺id
     * @return 小程序配置
     */
    WechatMiniAppSecret getConfig(String shopId);
}
