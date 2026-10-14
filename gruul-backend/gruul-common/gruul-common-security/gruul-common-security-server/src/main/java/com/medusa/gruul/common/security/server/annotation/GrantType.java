package com.medusa.gruul.common.security.server.annotation;

import java.lang.annotation.*;

/**
 * 登录认证类型
 *
 * @author 张治保
 * @since 2023/11/2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GrantType {

    String PARAMETER_NAME = "grant_type";


    /**
     * 登录认证类型
     *
     * @return 登录认证类型
     */
    String value();
}
