package com.medusa.gruul.common.redis.aspect;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * @author 张治保
 * date 2022/2/10
 */
public class RedissonAdvisor extends AbstractPointcutAdvisor {

    /**
     * 方法增强
     */
    private final Advice advice;
    /**
     * 切点
     */
    private final Pointcut pointcut;

    public RedissonAdvisor(RedissonInterceptor advice) {
        this.advice = advice;
        this.pointcut = new RedissonPointcut();
        super.setOrder(Ordered.LOWEST_PRECEDENCE - 100);
    }

    @Override
    @NonNull
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    @NonNull
    public Advice getAdvice() {
        return advice;
    }


}
