package com.medusa.gruul.platform.service;

import com.medusa.gruul.platform.model.dto.WechatOpenCodeDTO;

/**
 * 微信开放平台服务
 *
 * @author 张治保
 * @since 2025/4/2
 */
public interface WechatOpenService {

    /**
     * 获取微信授权二维码
     *
     * @param param 微信开放平台授权参数
     * @return 微信授权二维码
     */
    String qrCode(WechatOpenCodeDTO param);


    /**
     * 获取微信openid
     *
     * @param code 微信开放平台授权码
     * @return 微信openid
     */
    String openid(String code);

    /**
     * 开放平台登陆是否启用
     *
     * @return 是否启用
     */
    Boolean enabled();

    /**
     * 绑定开放平台微信账号
     *
     * @param code 微信开放平台授权码
     */
    void bind(String code);
}
