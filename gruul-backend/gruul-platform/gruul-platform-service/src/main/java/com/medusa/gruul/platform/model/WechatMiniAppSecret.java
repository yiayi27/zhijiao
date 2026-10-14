package com.medusa.gruul.platform.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 店铺小程序 appid 与密钥缓存对象
 *
 * @author 张治保
 */
@Getter
@Setter
@Accessors(chain = true)
public class WechatMiniAppSecret {
    /**
     * 小程序appid
     */
    private String appid;
    /**
     * 小程序密钥
     */
    private String secret;
}