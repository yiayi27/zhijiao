package com.medusa.gruul.common.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "gruul.tenant")
public class TenantConfigProperties {

    /**
     * 维护商铺列名称
     */
    private String shop_column = "shop_id";

    /**
     * 是否使用商铺
     */
    private Boolean use_shop = true;

    /**
     * 多店铺的数据表集合
     */
    private Set<String> tables = new HashSet<>();
}
