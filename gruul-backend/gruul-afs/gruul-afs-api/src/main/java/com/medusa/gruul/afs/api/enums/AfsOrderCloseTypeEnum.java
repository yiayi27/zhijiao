package com.medusa.gruul.afs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;


/**
 * The enum Afs order close type enum.
 *
 * @author alan
 * @Description: 售后订单类型枚举
 * @date 2023/10/1 22:02
 */
@Getter
@Schema(title = "售后订单类型枚举")
public enum AfsOrderCloseTypeEnum {
    /**
     * User cancel afs order close type enum.
     */
    USER_CANCEL(1, "用户撤销"),
    /**
     * Re exchange afs order close type enum.
     */
    RE_EXCHANGE(4, "重新申请换货"),
    /**
     * Seller refuse afs order close type enum.
     */
    SELLER_REFUSE(5, "卖家拒绝"),
    /**
     * Re apply afs order close type enum.
     */
    RE_APPLY(6, "重新申请售后");


    /**
     * 值
     */
    @EnumValue
    private final int code;
    /**
     * 描述
     */
    private final String desc;

    AfsOrderCloseTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
