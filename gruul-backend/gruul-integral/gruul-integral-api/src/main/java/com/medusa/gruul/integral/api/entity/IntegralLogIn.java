package com.medusa.gruul.integral.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 积分登入表
 * </p>
 *
 * @author xiaoq
 * @since 2023-12-31
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_logIn")
@Schema(title = "IntegralLogIn", description = "积分登入表")
public class IntegralLogIn implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(title = "登入表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    @Schema(title = "连续签到天数")
    @TableField("running_days")
    private Long runningDays;

    @Schema(title = "登入状态 0未登入 1已登入")
    @TableField("logIn_state")
    private Boolean logInState;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)

    private LocalDateTime updateTime;
    @TableLogic
    @Schema(title = "删除状态：0->未删除；1->已删除")
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Boolean deleted;


}
