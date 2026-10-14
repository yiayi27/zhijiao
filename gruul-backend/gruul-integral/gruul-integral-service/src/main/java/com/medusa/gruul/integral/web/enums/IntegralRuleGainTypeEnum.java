package com.medusa.gruul.integral.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 积分规则获取类型
 * @Author: xiaoq
 * @Date: 2023/12/9 12:56
 */
@Getter
@Schema(title = "积分增加类型枚举")
public enum IntegralRuleGainTypeEnum {

    //	0.分享 1.登入 2.签到 3.步数

    SHARE(0, "分享"), LONG_IN(1, "登入"), SING_IN(2, "签到"), STEP_NUMBER(3, "步数");

    @EnumValue
    /**
     * 值
     */ private final int status;

    /**
     * 描述
     */
    private final String desc;

    IntegralRuleGainTypeEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }


}
