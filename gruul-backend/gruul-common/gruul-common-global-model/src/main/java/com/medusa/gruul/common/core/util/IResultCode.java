package com.medusa.gruul.common.core.util;

import com.medusa.gruul.common.core.exception.ServiceException;

import java.io.Serializable;

/**
 * 状态码接口
 *
 * @author L.cm
 */
public interface IResultCode extends Serializable {
    /**
     * 返回的code码
     *
     * @return code
     */
    int getCode();

    /**
     * 返回的消息
     *
     * @return 消息
     */
    String getMsg();


    /**
     * 生成错误异常
     *
     * @return ServiceException
     */
    default ServiceException exception() {
        return new ServiceException(this.getMsg(), this.getCode());
    }
}
