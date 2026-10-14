package com.medusa.gruul.common.core.constant.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 成功失败枚举
 * <p>Descrition: 返回状态 枚举类</P>
 * <p>Version: </p>
 * <p>ModifyVersion: </p>
 * <p>Author Mr.zhaozheng a</P>
 * <p>Date: 2023-11-21 13:11</p>
 * <p>Param a</P>
 * <p>return: a</P>
 * @author lcysike
 */
@Getter
@RequiredArgsConstructor
public enum CommonEnum {

    /**
     * 成功
     */
    SUCCESS_RESPONSE(200, "成功"),
    /**
     * 失败
     */
    FAILED_RESPONSE(500, "失败");

    private final int code;
    private final String msg;

}
