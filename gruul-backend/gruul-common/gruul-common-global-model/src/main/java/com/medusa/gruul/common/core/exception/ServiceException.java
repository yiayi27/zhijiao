package com.medusa.gruul.common.core.exception;

import com.medusa.gruul.common.core.util.IResultCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * @Description: ServiceException
 * @Author: alan
 * @Date: 2023/7/13 19:22
 */
@Setter
@Getter
@NoArgsConstructor
@Accessors(chain = true)
public class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public ServiceException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ServiceException(IResultCode resultCode) {
        super(resultCode.getMsg());
        this.msg = resultCode.getMsg();
        this.code = resultCode.getCode();
    }

    public ServiceException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public ServiceException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public ServiceException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }


}
