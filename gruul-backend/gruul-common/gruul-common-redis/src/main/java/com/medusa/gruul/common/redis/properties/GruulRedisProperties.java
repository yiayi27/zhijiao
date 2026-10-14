package com.medusa.gruul.common.redis.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张治保
 * date 2022/3/19
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.redis")
public class GruulRedisProperties {

    /**
     * 缓存延迟双删的核心线程数 默认为1024
     * 最终使用的是虚拟线程
     */
    private int doubleDeletionScheduleCorPoolSize = 1024;


    /**
     * 是否启用Scheduled注解的分布式增强
     */
    private boolean enableScheduledLock = true;

}
