package com.medusa.gruul.common.redis;

import com.medusa.gruul.common.redis.aspect.RedissonAdvisor;
import com.medusa.gruul.common.redis.aspect.RedissonInterceptor;
import com.medusa.gruul.common.redis.aspect.ScheduledAdvisor;
import com.medusa.gruul.common.redis.aspect.ScheduledInterceptor;
import com.medusa.gruul.common.redis.properties.GruulRedisProperties;
import com.medusa.gruul.common.redis.spel.SpringElEngine;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.StringValueResolver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author 张治保
 * date 2022/2/16
 */
@RequiredArgsConstructor
@Import({RedissonAdvisor.class, RedissonInterceptor.class})
@EnableConfigurationProperties(GruulRedisProperties.class)
@ConditionalOnClass(name = {"org.springframework.data.redis.core.RedisTemplate", "org.springframework.data.redis.connection.RedisConnectionFactory"})
public class RedisAutoconfigure implements ApplicationContextAware {

    @Value("${spring.application.name}")
    private String appName;

    /**
     * task scheduler 线程池 使用虚拟线程
     *
     * @return ThreadPoolTaskScheduler
     */
    @Bean
    public ThreadPoolTaskScheduler gruulTaskScheduler(GruulRedisProperties redisProperties) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 使用虚拟线程工厂
        scheduler.setThreadFactory(
                Thread.ofVirtual()
                        .name("SCD", 1)
                        .factory()
        );
        scheduler.setPoolSize(redisProperties.getDoubleDeletionScheduleCorPoolSize());
        return scheduler;
    }

    /**
     * RedisTemplate 配置
     *
     * @param factory        redis 链接工厂
     * @param redissonClient redisson 客户端
     * @return RedisTemplate
     */
    @Primary
    @Bean("gruulRedisTemplate")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory,
            RedissonClient redissonClient,
            ThreadPoolTaskScheduler gruulTaskScheduler
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(RedisUtil.LazyRedisSerializer.KEY_SERIALIZER);
        template.setHashKeySerializer(RedisUtil.LazyRedisSerializer.HASH_KEY_SERIALIZER);

        template.setValueSerializer(RedisUtil.valueSerializer());
        template.setHashValueSerializer(RedisUtil.valueSerializer());
        //初始化RedisUtil
        RedisUtil.init(
                template,
                redissonClient,
                appName,
                gruulTaskScheduler
        );
        return template;
    }

    /**
     * redisson 线程池设置 使用虚拟线程
     *
     * @return RedissonAutoConfigurationCustomizer
     */
    @Bean
    public RedissonAutoConfigurationCustomizer gruulRedissonAutoConfigurationCustomizer() {
        return (config) -> {
            ExecutorService virtualThreadPerTaskExecutor = Executors.newVirtualThreadPerTaskExecutor();
            config.setExecutor(virtualThreadPerTaskExecutor)
                    .setNettyExecutor(virtualThreadPerTaskExecutor);
        };
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        SpringElEngine.setApplicationContext(applicationContext);
    }


    /**
     * 分布式@Scheduled增强
     */

    @Bean
    @ConditionalOnProperty(prefix = "gruul.redis", name = "enableScheduledLock", havingValue = "true", matchIfMissing = true)
    public ScheduledInterceptor scheduledInterceptor(@Nullable StringValueResolver embeddedValueResolver) {
        return new ScheduledInterceptor(embeddedValueResolver);
    }

    /**
     * 分布式@Scheduled增强
     */
    @Bean
    @ConditionalOnProperty(prefix = "gruul.redis", name = "enableScheduledLock", havingValue = "true", matchIfMissing = true)
    public ScheduledAdvisor scheduledAdvisor(ScheduledInterceptor scheduledInterceptor) {
        return new ScheduledAdvisor(scheduledInterceptor);
    }


}
