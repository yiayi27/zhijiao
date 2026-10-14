package com.medusa.gruul.live.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 直播表
 * </p>
 *
 * @author xiaoq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_live")
@Schema(title = "live对象", description = "直播间信息表")
public class Live<T extends Model<?>> extends Model<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "直播表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "商铺id")
    @TableField("shop_id")
    private String shopId;

    @Schema(title = "直播房间号")
    @TableField("room_id")
    private String roomId;

    @Schema(title = "直播名称")
    @TableField("live_name")
    private String liveName;

    @Schema(title = "开播时间")
    @TableField("start_time")
    private Date startTime;

    @Schema(title = "停播时间")
    @TableField("end_time")
    private Date endTime;

    @Schema(title = "主播信息")
    @TableField("anchor")
    private String anchor;

    @Schema(title = "观看量")
    @TableField("view_count")
    private Long viewCount;

    @Schema(title = "点赞量")
    @TableField("like_count")
    private Long likeCount;

    @Schema(title = "状态(默认未开始，101--直播中，102--未开始，103--已结束，104--禁播，105--暂停中，106--异常，107--已过期)")
    @TableField("status")
    private Integer status;

    @Schema(title = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(title = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)


    private LocalDateTime updateTime;
    @TableLogic
    @Schema(title = "删除状态：0->未删除；1->已删除")
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Boolean deleted;

    @Schema(title = "背景封面图")
    @TableField("cover_img")
    private String coverImg;

    @Schema(title = "主播头像图")
    @TableField("anchor_img")
    private String anchorImg;

    @Schema(title = "分享卡片封面图")
    @TableField("share_img")
    private String shareImg;


    @Schema(title = "首页排序")
    @TableField("sort")
    private Long sort;

    @Schema(title = "是否首页 0否 1是")
    @TableField("first_flag")
    private Integer firstFlag;

}
