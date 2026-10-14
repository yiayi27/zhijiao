package com.medusa.gruul.finance.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * The enum Audit status enum.
 *
 * @description: AuditStatusEnum
 * @author: alan
 * @date: 2024/2/22 16:54
 */
@Getter
@Schema(title = "审核状态")
public enum AuditStatusEnum {
    //审核状态:1->总店待审核；2->已通过；3->已拒绝
    /**
     * 待审核
     */
    PENDING_PARTNER(0, "待审核"),
    /**
     * 总店待审核
     */
    PENDING_MAIN(1, "总店待审核"),
    /**
     * 已通过
     */
    PASSED(2, "已通过"),
    /**
     * 已拒绝
     */
    REJECTED(3, "已拒绝"),
    ;


    /**
     * 值
     */
    @EnumValue
    private final int code;
    /**
     * 描述
     */
    private final String desc;


    AuditStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
