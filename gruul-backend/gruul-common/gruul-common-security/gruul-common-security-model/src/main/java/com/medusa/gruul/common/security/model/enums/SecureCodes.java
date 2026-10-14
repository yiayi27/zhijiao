package com.medusa.gruul.common.security.model.enums;

import com.medusa.gruul.common.security.model.bean.SecureException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/4/20
 */
@Getter
@RequiredArgsConstructor
public enum SecureCodes {
    /**
     * 需要登录
     */
    NEED_LOGIN(2, "请进行登录"),
    /**
     * token不可用
     */
    TOKEN_INVALID(3, "凭据不可用"),
    /**
     * token已过期
     */
    TOKEN_EXPIRED(4, "凭据已过期"),
    /**
     * refresh token不可用
     */
    REFRESH_TOKEN_INVALID(5, "刷新凭据不可用"),
    /**
     * refresh token 已过期
     */
    REFRESH_TOKEN_EXPIRED(6, "刷新凭据已过期"),
    /**
     * 权限不足
     */
    PERMISSION_DENIED(7, "无权访问"),

    /**
     * 账号已过期（账号数据变更） 尝试重新登录
     */
    ACCOUNT_EXPIRED(8, "账号资料发生变更，请重新登录"),
    /**
     * 密码有误
     */
    USERNAME_PASSWORD_INVALID(9, "用户名密码不正确"),
    /**
     * TOKEN 发生变化 已在其它地方登录
     */
    TOKEN_CHANGED(10, "已在其它地方登录"),
    /**
     * REQUEST_INVALID
     */
    REQUEST_INVALID(12, "无效的请求"),
    /**
     * 数据校验失败
     */
    DATA_VALID_ERROR(22, "数据校验失败"),
    /**
     * 未分配权限
     */
    UNALLOCATED_PERMISSION(23, "权限未分配"),
    ;

    private final int code;
    private final String msg;


    public SecureException exception() {
        return new SecureException(this.code, this.msg);
    }

}
