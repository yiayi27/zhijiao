package com.medusa.gruul.common.system.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2025/3/29
 */
@Getter
@RequiredArgsConstructor
public enum ClientType {
    /**
     * 店铺管理端
     */
    SHOP(1),

    /**
     * 消费者端
     */
    CONSUMER(2);


    @EnumValue
    private final int value;
}
