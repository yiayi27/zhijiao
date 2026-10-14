package com.medusa.gruul.common.redis.aspect;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 张治保
 * @since 2025/4/11
 */
@Slf4j
@RequiredArgsConstructor
public class ScheduledInterceptor implements MethodInterceptor {

    private static final String KEY_PREFIX = "scheduled:";
    /**
     * 用于解析字符串表达式
     */
    @Nullable
    private final StringValueResolver embeddedValueResolver;
    /**
     * 用于获取当前执行方法的锁定时间
     */
    private final Map<String, LockDuration> lockDurationMap = new WeakConcurrentMap<>();

    private static Duration toDuration(String value, TimeUnit timeUnit) {
        if (isDurationString(value)) {
            return Duration.parse(value);
        }
        return Duration.of(Long.parseLong(value), timeUnit.toChronoUnit());
    }

    private static boolean isDurationString(String value) {
        return (value.length() > 1 && (isP(value.charAt(0)) || isP(value.charAt(1))));
    }

    private static boolean isP(char ch) {
        return (ch == 'P' || ch == 'p');
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        LocalDateTime execTime = LocalDateTime.now();
        Method method = invocation.getMethod();
        //获取当前执行的方法路径 作为唯一key
        String methodPath = method.getDeclaringClass().getName() + StrPool.DOT + method.getName();
        //获取锁的key
        String key = RedisUtil.getLockKey(KEY_PREFIX + methodPath);
        //获取锁的时间
        LockDuration lockDuration = lockDuration(key, method);
        //如果没有锁定时间，直接执行
        if (lockDuration == NoTime.INSTANCE) {
            return invocation.proceed();
        }
        RedisTemplate<String, Object> redisTemplate = RedisUtil.getRedisTemplate();
        //如果已被锁定
        Boolean locked = redisTemplate.hasKey(key);
        if (locked != null && locked) {
            //如果获取锁失败，直接返回
            log.debug("[被锁定] 定时任务 {}，跳过本次执行", methodPath);
            return null;
        }
        //如果锁定时间小于等于0，直接执行
        boolean success = true;
        //如果锁定时间是无需锁定
        //获取锁定时间
        Duration duration = lockDuration.duration(execTime);
        //如果锁定时间大于0，才去获取锁
        if (duration.isPositive()) {
            Boolean lockSuccess = redisTemplate.opsForValue()
                    .setIfAbsent(key, 1, duration);
            success = lockSuccess != null && lockSuccess;
            if (success) {
                log.debug("[锁定] 定时任务 {}，锁定时间 {}", methodPath, duration);
            }
        }
        //如果获取锁成功，执行方法
        if (success) {
            return invocation.proceed();
        }
        //如果获取锁失败，直接返回
        log.debug("[被锁定] 定时任务 {}，跳过本次执行", methodPath);
        return null;
    }

    /**
     * 获取锁的时间
     *
     * @param method 当前执行的方法
     * @return LockDuration
     */
    private LockDuration lockDuration(String key, Method method) {
        return lockDurationMap.computeIfAbsent(
                key,
                _ -> {
                    Scheduled scheduled = AnnotationUtils.findAnnotation(method, Scheduled.class);
                    if (scheduled == null) {
                        return NoTime.INSTANCE;
                    }
                    return getLockDurationFromScheduled(scheduled);
                }
        );
    }

    /**
     * 解析字符串表达式
     *
     * @param expression 表达式
     * @return 解析后的字符串
     */
    private String resolveStringValue(String expression) {
        if (embeddedValueResolver == null) {
            return expression;
        }
        return embeddedValueResolver.resolveStringValue(expression);
    }

    /**
     * 获取锁的时间
     * {@link ScheduledAnnotationBeanPostProcessor#processScheduledTask(Scheduled, Runnable, Method, Object)}
     *
     * @param scheduled scheduled注解
     * @return LockDuration
     */
    private LockDuration getLockDurationFromScheduled(Scheduled scheduled) {
        //cron 任务
        String cron = scheduled.cron();
        if (StrUtil.isNotEmpty(cron) && !Scheduled.CRON_DISABLED.equals(cron)) {
            cron = resolveStringValue(cron);
            return new CronTime(cron);
        }

        // 固定延迟  每次执行后延迟多久执行
        Duration fixedDelay = Duration.of(scheduled.fixedDelay(), scheduled.timeUnit().toChronoUnit());
        if (!fixedDelay.isNegative()) {
            return new FixedDelayTime(fixedDelay);
        }
        String fixedDelayString = scheduled.fixedDelayString();
        if (StrUtil.isNotEmpty(fixedDelayString)) {
            fixedDelayString = resolveStringValue(fixedDelayString);
            if (StrUtil.isNotEmpty(fixedDelayString)) {
                fixedDelay = toDuration(fixedDelayString, scheduled.timeUnit());
                if (!fixedDelay.isNegative()) {
                    return new FixedDelayTime(fixedDelay);
                }
            }
        }
        // 固定频率
        Duration fixedRate = Duration.of(scheduled.fixedRate(), scheduled.timeUnit().toChronoUnit());
        if (!fixedRate.isNegative()) {
            return new FixedRateTime(fixedRate);
        }
        String fixedRateString = scheduled.fixedRateString();
        if (StrUtil.isNotEmpty(fixedRateString)) {
            fixedRateString = resolveStringValue(fixedRateString);
            if (StrUtil.isNotEmpty(fixedRateString)) {
                fixedRate = toDuration(fixedRateString, scheduled.timeUnit());
                if (!fixedRate.isNegative()) {
                    return new FixedRateTime(fixedRate);
                }
            }
        }
        // 以上都没有，直接返回
        return NoTime.INSTANCE;
    }


    /**
     * 任务锁的时间
     * 1. cron表达式： 通过表达式计算下次执行时间和当前时间的差值 然后x80%作为锁定时间
     * 2. 固定延迟： 延迟时间作为锁定时间
     * 3. 固定频率： 延迟时间的80%作为锁定时间
     * 4. 无锁定时间： 直接执行 无需锁定
     */
    private interface LockDuration {

        /**
         * 获取锁定时间
         *
         * @param execTime 当前执行时间
         * @return 锁定时间
         */
        Duration duration(LocalDateTime execTime);

    }

    /**
     * cron表达式
     */
    private static class CronTime implements LockDuration {
        /**
         * cron表达式
         */
        private final CronExpression cronExpression;

        private CronTime(String cron) {
            this.cronExpression = CronExpression.parse(cron);
        }

        @Override
        public Duration duration(LocalDateTime execTime) {
            //获取下一个执行时间
            LocalDateTime next = cronExpression.next(execTime);
            //如果下一个执行时间为空，直接返回0
            if (next == null) {
                return Duration.ZERO;
            }
            //获取当前时间和下一个执行时间的差值 并取80% 作为锁定时间
            return Duration.between(execTime, next)
                    .multipliedBy(4).dividedBy(5);
        }
    }

    /**
     * 固定延迟
     *
     * @param fixedDelay 固定延迟时间
     */
    private record FixedDelayTime(Duration fixedDelay) implements LockDuration {

        @Override
        public Duration duration(LocalDateTime execTime) {
            return fixedDelay;
        }
    }

    /**
     * 固定频率时间
     */
    private record FixedRateTime(Duration fixedRate) implements LockDuration {

        /**
         * 去固定频率的80%时间作为锁定时间
         *
         * @param fixedRate 固定频率时间
         */
        private FixedRateTime(Duration fixedRate) {
            this.fixedRate = fixedRate.multipliedBy(4).dividedBy(5);
        }

        @Override
        public Duration duration(LocalDateTime execTime) {
            return fixedRate;
        }
    }

    /**
     * 没有锁定时间 即不需要锁定
     */
    private static class NoTime implements LockDuration {

        private static final LockDuration INSTANCE = new NoTime();


        @Override
        public Duration duration(LocalDateTime execTime) {
            return Duration.ZERO;
        }
    }


}
