package com.medusa.gruul.common.security.resource.exception;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.security.model.bean.SecureException;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author 张治保
 * date 2022/3/1
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SecureExceptionAdvice {

    /**
     * 认证异常映射
     *
     * @param ex 异常
     * @return Result
     */
    private static Result<Void> authExMapping(AuthenticationException ex) {
        if (ex instanceof SecurityException securityException) {
            SecureException cause = (SecureException) securityException.getCause();
            return Result.failed(cause.getCode(), cause.getMsg());
        }
        if (ex.getCause() instanceof SecurityException securityException) {
            SecureException cause = (SecureException) securityException.getCause();
            return Result.failed(cause.getCode(), cause.getMsg());
        }
        if (isMachInstance(ex, InsufficientAuthenticationException.class)) {
            return Result.failed(SecureCodes.NEED_LOGIN.getCode(), SecureCodes.NEED_LOGIN.getMsg());
        }
        if (isMachInstance(
                ex,
                UsernameNotFoundException.class,
                AuthenticationCredentialsNotFoundException.class,
                BadCredentialsException.class,
                CredentialsExpiredException.class,
                PreAuthenticatedCredentialsNotFoundException.class
        )
        ) {
            return Result.failed(SecureCodes.USERNAME_PASSWORD_INVALID.getCode(), SecureCodes.USERNAME_PASSWORD_INVALID.getMsg());
        }
        if (isMachInstance(
                ex,
                RememberMeAuthenticationException.class,
                CookieTheftException.class,
                InvalidCookieException.class,
                SessionAuthenticationException.class

        )) {
            return Result.failed(SecureCodes.PERMISSION_DENIED.getCode(), SecureCodes.PERMISSION_DENIED.getMsg());
        }
        return Result.failed(ex.getMessage());
    }

    /**
     * 认证异常处理
     *
     * @param ex 异常
     * @return Result
     */

    public static Result<Void> authExceptionMapping(Throwable ex) {
        log.debug("authExceptionMapping", ex);
        if (ex instanceof AuthenticationException authenticationException) {
            return authExMapping(authenticationException);
        }
        if (ex.getCause() instanceof AuthenticationException authenticationException) {
            return authExMapping(authenticationException);
        }
        if (ex instanceof AccessDeniedException && ISecurity.isAnonymous()) {
            return Result.failed(SecureCodes.NEED_LOGIN.getCode(), SecureCodes.NEED_LOGIN.getMsg());
        }
        return Result.failed(SecureCodes.PERMISSION_DENIED.getCode(), SecureCodes.PERMISSION_DENIED.getMsg());
    }

    /**
     * 判断ex 是否在classes实现类型中
     */
    public static boolean isMachInstance(Object ex, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAssignableFrom(ex.getClass())) {
                return true;
            }
        }
        return false;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> accessDeniedException(AccessDeniedException ex) {
        return SecureExceptionAdvice.authExceptionMapping(ex);
    }

    /**
     * AuthenticationException 认证服务异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> authException(AuthenticationException ex) {
        return SecureExceptionAdvice.authExceptionMapping(ex);
    }

    /**
     * SecureException
     *
     * @param ex 異常
     * @return void
     */
    @ExceptionHandler({SecureException.class})
    public Result<Void> secureException(SecureException ex) {
        log.debug("secureException", ex);
        return Result.failed(ex.getCode(), ex.getMsg());
    }
}
