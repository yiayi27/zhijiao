package com.medusa.gruul.platform.api.entity;

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
 * 系统配置
 * </p>
 *
 * @author whh
 * @since 2023-09-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_system_conf")
@Schema(title = "SystemConf对象", description = "系统配置")
public class SystemConf extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * key
     */
    @Schema(title = "key")
    @TableField("param_key")
    private String paramKey;

    /**
     * value
     */
    @Schema(title = "value 格式json形式")
    @TableField("param_value")
    private String paramValue;

    /**
     * 状态 0：隐藏 1：显示
     */
    @Schema(title = "状态   0：隐藏   1：显示")
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @Schema(title = "备注")
    @TableField("remark")
    private String remark;

}
