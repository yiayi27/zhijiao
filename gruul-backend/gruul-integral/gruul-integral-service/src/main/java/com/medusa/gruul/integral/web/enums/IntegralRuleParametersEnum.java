package com.medusa.gruul.integral.web.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 积分规则参数枚举状态
 * @Author: xiaoq
 * @Date: 2023/11/28 13:52
 */
@Getter
@Schema(title = "积分规则参数类型枚举")
public enum IntegralRuleParametersEnum {

    /**
     * 积分规则参数信息
     */
    DAY_SHARE("day_share", "每日分享"),

    DAYS("days", "连续天数,第一个条件"),

    DAYSS("dayss", "连续天数,第二个条件"),

    DAY_LONGIN("day_login", "每日登入"),

    DAYS_LONGIN("days_login", "连续登入days天可获得多少积分"),

    DAYSS_LONGIN("dayss_login", "连续登入dayss天可获的多少积分"),

    DAY_SING_IN("day_sign_in", "每日签到"),

    DAYS_SING_IN("days_sign_in", "连续签到days天可获得多少积分"),

    DAYSS_SING_IN("dayss_sign_in", "连续签到dayss天可获得多少积分"),

    DAY_STEP_NUBER("day_step_number", "目标步数"),

    STEP_NUMBER_INTEGRAL("stop_number_integral", "达到目标步数可兑换积分");


    @EnumValue
    /**
     * 值
     */
    private final String status;

    /**
     * 描述
     */
    private final String desc;

    IntegralRuleParametersEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
