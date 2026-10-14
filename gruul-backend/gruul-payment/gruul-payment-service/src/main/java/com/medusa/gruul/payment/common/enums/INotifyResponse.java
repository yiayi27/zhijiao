package com.medusa.gruul.payment.common.enums;

/**
 * @author 张治保
 * date 2024/12/23
 */
public interface INotifyResponse {
    /**
     * 获取响应码
     *
     * @return 响应码
     */
    String getCode();
    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    String getMsg();
}
