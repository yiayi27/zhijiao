package com.medusa.gruul.common.security.model.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2025/3/29
 */
@Accessors(chain = true)
@Getter
public class SecureException extends RuntimeException {

    private final int code;

    @Setter
    private String msg;

    public SecureException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

}
