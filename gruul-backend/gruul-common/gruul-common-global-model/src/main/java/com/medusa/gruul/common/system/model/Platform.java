package com.medusa.gruul.common.system.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * APP平台类型
 *
 * @author 张治保
 * date 2024/11/30
 */
@Getter
@RequiredArgsConstructor
public enum Platform {
    /**
     * PC浏览器
     */
    PC(1),

    /**
     * 微信小程序
     */
    MINI_APP(2),

    /**
     * 移动端h5
     */
    H5(3),

    /**
     * APP
     */
    APP(4);

    @EnumValue
    private final int value;


}
