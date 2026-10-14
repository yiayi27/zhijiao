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
 * 资讯专题关联表
 * </p>
 *
 * @author lcysike
 * @since 2024-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_information_info_topic")
@Schema(title = "InformationInfoTopic对象", description = "资讯专题关联表")
public class InformationInfoTopic extends BaseEntity {

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
     * 资讯id
     */
    @Schema(title = "资讯id")
    @TableField("info_id")
    private Long infoId;

    /**
     * 专题id
     */
    @Schema(title = "专题id")
    @TableField("topic_id")
    private Long topicId;

}
