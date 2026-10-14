package com.medusa.gruul.common.data.annotation;


import com.medusa.gruul.common.system.ISystem;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @description: EscapeShopAspect.aj
 * @author: alan
 * @date: 2024/2/16 21:04
 */
@Aspect
@Component
public class EscapeShopAspect {


    /**
     * 注解EscapeShop的为切入点
     */
    @Pointcut("@annotation(com.medusa.gruul.common.data.annotation.EscapeShop)")
    public void escapeShopPointCut() {
    }


    /**
     * 在切点之前织入
     *
     * @return void
     * @author alan
     * @date 2024/2/16 21:22
     */
    @Around("escapeShopPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        return ISystem.disableTenant(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }


}

