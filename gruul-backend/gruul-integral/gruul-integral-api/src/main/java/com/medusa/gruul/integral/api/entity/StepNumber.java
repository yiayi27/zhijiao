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
 * 步数表
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Schema(title = "StepNumber对象", description = "")
public class StepNumber implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "步数表id")
    @TableId(value = "step_number_id", type = IdType.AUTO)
    private Long stepNumberId;

    @Schema(title = "积分规则表id")
    @TableField("integral_rule_id")
    private Long integralRuleId;

    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    @Schema(title = "用户头像")
    @TableField("user_url")
    private String userUrl;

    @Schema(title = "用户微信名")
    @TableField("user_wxname")
    private String userWxname;

    @Schema(title = "今日步数")
    @TableField("today_step")
    private Integer todayStep;

    @Schema(title = "目标步数")
    @TableField("target_step")
    private Integer targetStep;

    @Schema(title = "用户今日已兑换步数")
    @TableField("has_change")
    private Integer hasChange;

    @Schema(title = "商铺ID")
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
