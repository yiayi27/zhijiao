package com.medusa.gruul.integral.web.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @Description: 获得积分类型状态枚举
 * @Author: xiaoq
 * @Date: 2023/12/4 18:31
 */
@Getter
@Schema(title = "获得积分类型状态枚举")
public enum IntegralGainTypeEnum {

    /**
     * 获取积分类型有增有减 具体在单个业务逻辑中判断
     */
    EXCHANGE_INTEGRAL_GOODS(1, "兑换积分商品"),

    BUY_GOODS(2, "购买商品"),

    USE_BUY_GOODS(3, "积分抵扣"),

    DAY_LOGIN(4, "每日首次登入"),

    DAY_SHARE(5, "每日首次分享"),

    SIGN_IN(6, "签到"),

    DAY_STEP_NUMBER_REACH(7, "每日步数达标"),

    MERCHANT_RECHARGE(8, "商家充值"),

    MERCHANT_DEDUCT(9, "商家扣除"),

    EMPTY(10, "积分清零"),

    GIVE_BACK(11, "归还积分"),

    CONVERSION_CLUB_CARD(12, "兑换会员卡");

    /**
     * 值
     */

    @EnumValue
    private final int status;

    /**
     * 描述
     */
    private final String desc;

    IntegralGainTypeEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
