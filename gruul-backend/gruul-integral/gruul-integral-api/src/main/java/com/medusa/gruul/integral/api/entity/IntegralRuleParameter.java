package com.medusa.gruul.integral.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 积分规则参数表
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_rule_parameter")
@Schema(title = "IntegralRuleParameter对象", description = "积分规则参数表")
public class IntegralRuleParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "参数表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "积分规则表id")
    @TableField("integral_rule_id")
    private Long integralRuleId;

    @Schema(title = "name")
    @TableField("key")
    private String key;

    @Schema(title = "存储各个规则的值")
    @TableField("value")
    private BigDecimal value;

    @Schema(title = "所属类型 (0.分享  1.登入  2.步数  3.签到)")
    @TableField("belong_type")
    private Integer belongType;

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
