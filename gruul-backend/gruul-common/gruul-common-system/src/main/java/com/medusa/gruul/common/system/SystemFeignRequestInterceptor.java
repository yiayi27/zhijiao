package com.medusa.gruul.common.system;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.system.model.ClientType;
import com.medusa.gruul.common.system.model.Platform;
import com.medusa.gruul.common.system.model.Systems;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @author 张治保
 * date 2022/2/17
 */
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class SystemFeignRequestInterceptor {

    @Bean("systemRequestInterceptor")
    public feign.RequestInterceptor requestInterceptor() {
        return template -> {
            Systems systems = ISystem.get();

            String ip = systems.ip();
            if (StrUtil.isNotEmpty(ip)) {
                template.header(SystemHeaders.IP, ip);
            }

            String shopId = systems.shopId();
            if (StrUtil.isNotEmpty(shopId)) {
                template.header(SystemHeaders.SHOP_ID, shopId);
            }

            Platform platform = systems.platform();
            if (platform != null) {
                template.header(SystemHeaders.PLATFORM, platform.name());
            }

            ClientType clientType = systems.clientType();
            if (clientType != null) {
                template.header(SystemHeaders.CLIENT_TYPE, clientType.name());
            }
        };
    }
}
