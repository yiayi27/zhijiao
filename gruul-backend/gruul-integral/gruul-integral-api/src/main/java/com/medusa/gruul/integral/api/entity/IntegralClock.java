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
 * 积分签到表
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_clock")
@Schema(title = "IntegralClock对象", description = "积分签到表")
public class IntegralClock implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(title = "签到表id")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    @Schema(title = "连续签到天数")
    @TableField("running_days")
    private Long runningDays;

    @Schema(title = "签到状态 0未签到 1已签到")
    @TableField("sign_in_state")
    private Boolean signInState;

    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;

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
