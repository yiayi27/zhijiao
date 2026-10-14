package com.medusa.gruul.common.redis.aspect;

import org.aopalliance.aop.Advice;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author 张治保
 * @since 2025/4/11
 */
public class ScheduledAdvisor extends AbstractPointcutAdvisor {

    /**
     * 方法增强
     */
    private final Advice advice;
    /**
     * 切点
     */
    private final Pointcut pointcut;


    public ScheduledAdvisor(ScheduledInterceptor advice) {
        this.advice = advice;
        this.pointcut = AnnotationMatchingPointcut.forMethodAnnotation(Scheduled.class);
        super.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @NotNull
    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @NotNull
    @Override
    public Advice getAdvice() {
        return advice;
    }
}
