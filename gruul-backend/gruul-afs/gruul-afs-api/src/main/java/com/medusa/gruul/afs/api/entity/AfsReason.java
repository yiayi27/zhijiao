package com.medusa.gruul.afs.api.entity;

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
 * 申请售后的原因
 * </p>
 *
 * @author alan
 * @since 2024-08-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_afs_reason")
@Schema(title = "申请售后的原因", description = "售后工单")
public class AfsReason extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 工单id
     */
    @Schema(title = "工单id")
    @TableId("id")
    private Long id;

    /**
     * 商铺ID
     */
    @Schema(title = "商铺ID")
    @TableField("shop_id")
    private String shopId;


    /**
     * 名称
     */
    @Schema(title = "名称")
    @TableField("name")
    private String name;


}
