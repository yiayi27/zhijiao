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

import java.util.Date;

/**
 * <p>
 * 接龙活动信息表
 * </p>
 *
 * @author lcysike
 * @since 2024-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_solitaire_activity")
@Schema(title = "SolitaireActivity对象", description = "接龙活动信息表")
public class SolitaireActivity extends BaseEntity {

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
     * 活动名称
     */
    @Schema(title = "活动名称")
    @TableField("name")
    private String name;

    /**
     * 开始时间
     */
    @Schema(title = "开始时间")
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(title = "结束时间")
    @TableField("end_time")
    private Date endTime;

    /**
     * 预热时间
     */
    @Schema(title = "预热时间")
    @TableField("preheat_hour")
    private Integer preheatHour;

    /**
     * 适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与
     */
    @Schema(title = "适用商品：  0-全部商品参与  1-指定商品参与  2-指定商品不参与")
    @TableField("product_type")
    private Integer productType;

    /**
     * 封面海报
     */
    @Schema(title = "封面海报")
    @TableField("cover_poster")
    private String coverPoster;

    /**
     * 描述
     */
    @Schema(title = "描述")
    @TableField("content")
    private String content;

    /**
     * 活动状态：  0-未开始  1-进行中  2-已结束
     */
    @Schema(title = "活动状态：  0-未开始  1-进行中  2-已结束")
    @TableField("status")
    private Integer status;

    /**
     * 浏览次数
     */
    @Schema(title = "活动浏览次数")
    @TableField("views")
    private Integer views;
}
