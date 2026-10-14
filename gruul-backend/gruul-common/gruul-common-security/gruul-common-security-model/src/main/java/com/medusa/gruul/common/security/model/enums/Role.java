package com.medusa.gruul.common.security.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/2/23
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    /**
     * 超管
     */
    ADMIN(1, "管理员", false),

    /**
     * 子管理员
     */
    SUB_ADMIN(2, "自定义管理员", true),

    /**
     * 消费者
     */
    CONSUMER(3, "消费者", false),
    ;

    /**
     * 数据库 值
     */
    @EnumValue
    private final Integer value;


    /**
     * 名称描述
     */
    private final String desc;

    /**
     * 是否需要权限
     */
    private final boolean permNeed;

}
