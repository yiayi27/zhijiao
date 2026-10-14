package com.medusa.gruul.solitaire.api.entity;

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
 * 资讯设置表
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_information_set")
@Schema(title = "InformationSet对象", description = "资讯设置表")
public class InformationSet extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 本店店铺id
     */
    @Schema(title = "本店店铺id")
    @TableField("shop_id")
    private String shopId;

    /**
     * 是否开启资讯功能,0=禁止,1=开启
     */
    @Schema(title = "是否开启资讯功能,0=禁止,1=开启")
    @TableField("information_type")
    private Integer informationType;

    /**
     * 留言管理员名称
     */
    @Schema(title = "留言管理员名称")
    @TableField("admin_name")
    private String adminName;

}
