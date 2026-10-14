package com.medusa.gruul.common.core.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 云服务商
 * @Author: alan
 * @Date: 2023/7/13 19:21
 */
@Getter
@AllArgsConstructor
public enum CloudServiceEnum {
    /**
     * 七牛云
     */
    QINIU(1, "七牛云"),
    /**
     * 阿里云
     */
    ALIYUN(2, "阿里云"),
    /**
     * 腾讯云
     */
    QCLOUD(3, "腾讯云");

    /**
     * 类型
     */
    private final int type;
    /**
     * 描述
     */
    private final String description;
}
