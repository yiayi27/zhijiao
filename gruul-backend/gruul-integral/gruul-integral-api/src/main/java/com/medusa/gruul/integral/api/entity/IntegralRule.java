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
 * 积分规则表
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_integral_rule")
@Schema(title = "IntegralRule对象", description = "积分规则表")
public class IntegralRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "积分规则表id")
    @TableId(value = "integral_rule_id", type = IdType.AUTO)
    private Long integralRuleId;

    @Schema(title = "积分商城id")
    @TableField("integral_shop_id")
    private Long integralShopId;

    @Schema(title = "积分有效期")
    @TableField("integral_validity")
    private Integer integralValidity;

    @Schema(title = "积分使用规则介绍")
    @TableField("use_rule_introduce")
    private String useRuleIntroduce;

    @Schema(title = "积分值规则")
    @TableField("integral_rule_info")
    private String integralRuleInfo;

    @Schema(title = "积分值获取类型(0.分享 1.登入 2.签到 3.步数)")
    @TableField("rule_type")
    private Integer ruleType;

    @Schema(title = "是否开启")
    @TableField("is_open")
    private Boolean open;

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
