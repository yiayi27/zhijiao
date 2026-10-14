package com.medusa.gruul.common.security.resource.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 自行封装的安全异常
 *
 * @author 张治保
 * @since 2023/11/3
 */
public class SecurityException extends AuthenticationException {

    private SecurityException(Throwable cause) {
        super(cause.getMessage(), cause);
    }


    public static AuthenticationException of(Exception exception) {
        return new SecurityException(exception);
    }
}
