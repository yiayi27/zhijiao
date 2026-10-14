package com.medusa.gruul.common.core.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medusa.gruul.common.core.constant.CommonConstants;
import com.medusa.gruul.common.core.constant.enums.CommonEnum;
import com.medusa.gruul.common.core.exception.ServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: 响应信息主体
 * @Author: alan
 * @Date: 2023/7/13 19:23
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(title = "响应信息主体")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(title = "返回标记：成功标记=200，失败标记=400")
    private int code;

    @Schema(title = "返回信息")
    private String msg;

    @Schema(title = "数据")
    private T data;

    public Result(CommonEnum commonEnum, T data) {
        this.code = commonEnum.getCode();
        this.msg = commonEnum.getMsg();
        this.data = data;
    }

    public Result(CommonEnum commonEnum) {
        this.code = commonEnum.getCode();
        this.msg = commonEnum.getMsg();
    }

    public static <T> Result<T> ok() {
        return restResult(null, CommonConstants.SUCCESS, null);
    }

    public static <T> Result<T> ok(T data) {
        return restResult(data, CommonConstants.SUCCESS, null);
    }

    public static <T> Result<T> ok(T data, String msg) {
        return restResult(data, CommonConstants.SUCCESS, msg);
    }

    public static <T> Result<T> failed() {
        return restResult(null, CommonConstants.FAIL, null);
    }

    public static <T> Result<T> failed(String msg) {
        return restResult(null, CommonConstants.FAIL, msg);
    }

    public static <T> Result<T> failed(T data) {
        return restResult(data, CommonConstants.FAIL, null);
    }

    public static <T> Result<T> failed(int code, String msg) {
        return restResult(null, code, msg);
    }

    public static <T> Result<T> failed(IResultCode code, String msg) {
        return restResult(null, code.getCode(), msg);
    }

    private static <T> Result<T> restResult(T data, int code, String msg) {
        Result<T> apiResult = new Result<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    /**
     * 判断是否成功
     *
     * @author 张治保
     * 2021年12月8日
     */
    @JsonIgnore
    public boolean isSuccess() {
        return CommonConstants.SUCCESS == this.code;
    }

    /**
     * 获取成功响应的数据,失败则抛出 ServiceException 异常
     *
     * @author 张治保
     * 2021年12月8日
     */
    public T successData() {
        if (this.isSuccess()) {
            return this.data;
        }
        throw new ServiceException(this.msg, this.code);
    }
}
