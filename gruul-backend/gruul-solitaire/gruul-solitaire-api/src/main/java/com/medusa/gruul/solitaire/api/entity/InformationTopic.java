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
 * 资讯专题表
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_information_topic")
@Schema(title = "InformationTopic对象", description = "资讯专题表")
public class InformationTopic extends BaseEntity {

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
     * 专题名称
     */
    @Schema(title = "专题名称")
    @TableField("topic_name")
    private String topicName;

    /**
     * 专题图标
     */
    @Schema(title = "专题图标")
    @TableField("topic_logo")
    private String topicLogo;

    /**
     * 排序
     */
    @Schema(title = "排序")
    @TableField("sort")
    private String sort;

}
