package com.medusa.gruul.integral.web.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 积分操作类型枚举
 * @Author: xiaoq
 * @Date: 2023/12/13 13:14
 */
@Getter
@Schema(title = "积分操作类型枚举")
public enum IntegralOperationTypeEnum {
    /**
     * 积分操作类型 通知用户积分变化是使用
     */
    INCREASE(1, "增加积分"),
    REDUCE(2, "减少积分");


    /**
     * 值
     */
    @EnumValue
    private final int status;


    /**
     * 描述
     */
    private final String desc;

    IntegralOperationTypeEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

}
