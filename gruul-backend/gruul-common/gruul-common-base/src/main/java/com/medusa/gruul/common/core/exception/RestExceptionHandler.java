package com.medusa.gruul.common.core.exception;

import com.medusa.gruul.common.core.util.Result;
import com.medusa.gruul.common.core.util.SystemCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;

/**
 * 全局异常处理，处理可预见的异常，Order 排序优先级高
 *
 * <p>
 * Bean-Violation 异常处理
 * </p>
 *
 * @author L.cm
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE +1)
@Configuration
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RestExceptionHandler {

    /**
     * 未能捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("未知异常", e);
        return Result.failed(SystemCode.FAILURE_CODE, "服务器内部错误");
    }

    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException e) {
        log.error("自定义异常", e);
        return Result.failed(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleError(MissingServletRequestParameterException e) {
        log.error("缺少请求参数", e);
        String message = String.format("缺少必要的请求参数: %s", e.getParameterName());
        return Result.failed(SystemCode.PARAM_MISS, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleError(MethodArgumentTypeMismatchException e) {
        log.error("请求参数格式错误", e);
        String message = String.format("请求参数格式错误: %s", e.getName());
        return Result.failed(SystemCode.PARAM_TYPE_ERROR, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleError(MethodArgumentNotValidException e) {
        log.error("参数验证失败", e);
        return handleError(e.getBindingResult());
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleError(BindException e) {
        log.error("参数绑定失败", e);
        return handleError(e.getBindingResult());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleError(ConstraintViolationException e) {
        log.error("参数验证失败", e);
        return handleError(e.getConstraintViolations());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleError(HttpMessageNotReadableException e) {
        log.error("消息不能读取", e);
        return Result.failed(SystemCode.MSG_NOT_READABLE, "请求参数错误");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleError(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法", e);
        return Result.failed(SystemCode.METHOD_NOT_SUPPORTED, e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result<Void> handleError(HttpMediaTypeNotSupportedException e) {
        log.error("不支持当前媒体类型", e);
        return Result.failed(SystemCode.MEDIA_TYPE_NOT_SUPPORTED, e.getMessage());
    }

//####################################抽取共性 通用部分代码################################################

    /**
     * 处理 BindingResult
     *
     * @param result BindingResult
     * @return Result
     */
    protected Result<Void> handleError(BindingResult result) {
        FieldError error = result.getFieldError();
        String message = error == null ? "" : String.format("%s:%s", error.getField(), error.getDefaultMessage());
        return Result.failed(SystemCode.PARAM_BIND_ERROR, message);
    }

    /**
     * 处理 ConstraintViolation
     *
     * @param violations 校验结果
     * @return Result
     */
    protected Result<Void> handleError(Set<ConstraintViolation<?>> violations) {
        ConstraintViolation<?> violation = violations.iterator().next();
        String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
        String message = String.format("%s:%s", path, violation.getMessage());
        return Result.failed(SystemCode.PARAM_VALID_ERROR, message);
    }

}
