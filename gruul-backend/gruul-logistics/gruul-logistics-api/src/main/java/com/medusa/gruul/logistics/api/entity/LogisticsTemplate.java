package com.medusa.gruul.logistics.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * t_logistics_template
 *
 * @author
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_logistics_template")
@Schema(title = "LogisticsTemplate对象", description = "运费模版信息")
public class LogisticsTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    @Schema(title = "模板名称")
    @TableField("name")
    private String name;

    /**
     * 计价方式: 1=按件数,2=按重量.
     */
    @NotNull
    @Schema(title = "计价方式: 1=按件数,2=按重量.")
    @TableField("valuation_model")
    private Integer valuationModel;

    /**
     * 是否指定条件包邮: 0=不包邮,1=指定条件包邮
     */
    @NotNull
    @Schema(title = "是否指定条件包邮: 0=不包邮,1=指定条件包邮")
    @TableField("choice_condition_postage")
    private Integer choiceConditionPostage;

}