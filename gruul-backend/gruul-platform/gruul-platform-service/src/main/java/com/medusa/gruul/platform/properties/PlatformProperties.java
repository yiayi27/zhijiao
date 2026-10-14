package com.medusa.gruul.platform.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author 张治保
 * @since 2025/4/2
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "gruul.platform")
public class PlatformProperties {

    /**
     * 是否启用短信模拟发送
     */
    private boolean smsSimulation = true;

    /**
     * 微信开放平台配置
     */
    @NestedConfigurationProperty
    private WechatOpenProperties wechat = new WechatOpenProperties();

    /**
     * 消费者默认头像
     */
    private String defaultConsumerAvatar = "https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/head_portrait%20/images%20(3).png";

    /**
     * 微信开放平台配置详情
     */
    @Getter
    @Setter
    public static class WechatOpenProperties {
        /**
         * 是否启用开放平台认证功能
         */
        private boolean enabled = true;

        /**
         * 管理端基础访问地址
         */
        private String clientBaseUrl;

        /**
         * 开放平台 网站应用 appid
         */
        private String appId;

        /**
         * 开放平台 网站应用 appid 对应的 appSecret
         */
        private String appSecret;

        /**
         * 是否开启发货管理功能 暂时存储这里 后面优化到到配置中心
         */
        private boolean openShipping = true;
    }
}



