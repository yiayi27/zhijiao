package com.medusa.gruul.common.security.resource.properties;

import com.medusa.gruul.common.security.resource.helper.ISecurity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/2/25
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.client")
public class SecurityClientProperties {


    /**
     * 是否允许iframe访问  会添加响应头 x-frame-options deny
     */
    private boolean enableFrameOptions = true;

    /**
     * jwt payload 中的key 用于加密用户信息
     */
    private String payloadKey = "MbXubWPr4DQLFFU3T1RdjA==";

    /**
     * 公钥 用于 jwt验签
     */
    private String publicKey;

    /**
     * 把uri作为权限校验
     */
    private UriPermission uriPermission = new UriPermission();


    /**
     * 设置额外数据类型
     *
     * @param extraType 额外数据类型
     */
    public void setExtraType(Class<?> extraType) {
        ISecurity.ExtraRef.set(extraType);
    }


    @Getter
    @Setter
    public static class UriPermission implements Serializable {

        /**
         * 是否启用
         */
        private boolean enable = false;

        /**
         * uri白名单 配置了就会使用 uri的权限校验方式
         */
        private String[] permits = new String[0];

    }
}
