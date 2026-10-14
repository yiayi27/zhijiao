package com.medusa.gruul.common.core.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 张治保
 * date 2024/11/29
 */
@Slf4j
@Import(SpringUtil.class)
public class SpringUtils {
    private SpringUtils() {}

    public static <A extends Annotation> Map<String, Object> getBeansWithAnnotation(Class<A> annoClass){
        return SpringUtil.getApplicationContext().getBeansWithAnnotation(annoClass);
    }

    /**
     * 根据注解及注解的值获取 对应的Bean
     * 要求 注解必须包含方法value 且为空参
     * */
    @SuppressWarnings("unchecked")
    public static < B,A extends Annotation, E> B getBean(Class<A> annoClass, E value){
        Objects.requireNonNull(annoClass,"注解的值不能为空");
        Objects.requireNonNull(value,"注解value对应的值不能为空");
        Map<String, Object> beans = getBeansWithAnnotation(annoClass);
        Method valueMethod;
        try {
            valueMethod = annoClass.getDeclaredMethod("value");
        } catch (NoSuchMethodException e) {
            log.error(annoClass.getSimpleName()+"注解,未设置value方法");
            return null;
        }
        valueMethod.setAccessible(true);
        Optional<B> any = beans.values().stream().filter(
            bean -> {
                A anno = Optional.ofNullable(
                    bean.getClass().getAnnotation(annoClass)
                ).orElse(
                    bean.getClass().getSuperclass().getAnnotation(annoClass)
                );
                if (anno == null){
                    log.error("未正确获取注解,请给注解设置@Inherited");
                    return false;
                }
                Object invoke;
                try {
                    invoke =  valueMethod.invoke(anno);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    return false;
                }
                try{
                    if (invoke.getClass().isArray()){
                        E[] es = (E[]) invoke;
                        return ArrayUtil.contains(es, value);
                    }else{
                        E e = (E)invoke ;
                        return value == e || value.equals(e);
                    }
                }catch (Exception ignore){
                    log.error("value 类型错误无法转换");
                    return false;
                }
            }
        ).map(bean -> (B) bean).findAny();
        return any.orElseGet(
            ()->{
                log.error("未能从Spring容器中获取到{}对应的Bean",value.getClass());
                return null;
            }
        );
    }
}
