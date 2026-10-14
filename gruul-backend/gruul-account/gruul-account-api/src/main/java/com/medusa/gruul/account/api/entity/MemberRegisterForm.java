package com.medusa.gruul.account.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.data.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员注册表单表
 * </p>
 *
 * @author zhaokw
 * @since 2024-05-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_register_form")
@Schema(title = "MemberRegisterForm对象", description = "会员注册表单表")
public class MemberRegisterForm extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 表单id
     */
    @Schema(title = "表单id")
    @TableField("form_id")
    private String formId;

    /**
     * 表单名称
     */
    @Schema(title = "表单名称")
    @TableField("form_name")
    private String formName;

    /**
     * 表单格式，1表示身份证，2表示文本
     */
    @Schema(title = "表单格式，1表示身份证，2表示文本")
    @TableField("form_pattern")
    private Integer formPattern;

    /**
     * 是否必填，1表示必填，2表示非必填
     */
    @Schema(title = "是否必填，1表示必填，2表示非必填")
    @TableField("is_required")
    private Integer isRequired;

    /**
     * 提示语
     */
    @Schema(title = "提示语")
    @TableField("tips")
    private String tips;

    /**
     * 店铺id
     */
    @Schema(title = "店铺id")
    @TableField("shop_id")
    private String shopId;
}
