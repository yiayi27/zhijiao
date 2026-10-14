package com.medusa.gruul.live.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description: 直播信息 vo
 * @Author: zhaokw
 * @Date: 2024/04/01 11:11
 */
@Data
@Schema(title = "LiveVo对象", description = "直播信息")
public class LiveVo {
    private Long id;

    @Schema(title = "商铺id")
    private String shopId;

    @Schema(title = "直播房间号")
    private String roomId;

    @Schema(title = "直播名称")
    private String liveName;

    @Schema(title = "开播时间")
    private Date startTime;

    @Schema(title = "停播时间")
    private Date endTime;

    @Schema(title = "主播信息")
    private String anchor;

    @Schema(title = "观看量")
    private Long viewCount;

    @Schema(title = "点赞量")
    private Long likeCount;

    @Schema(title = "状态(默认未开始，101--直播中，102--未开始，103--已结束，104--禁播，105--暂停中，106--异常，107--已过期)")
    private Integer status;

    @Schema(title = "创建时间")
    private LocalDateTime createTime;


    @Schema(title = "背景图")
    private String coverImg;

    @Schema(title = "分享卡片图")
    private String shareImg;

    @Schema(title = "主播头像图")
    private String anchorImg;


    @Schema(title = "首页排序")
    private Long sort;

    @Schema(title = "是否首页 0否 1是")
    private Integer firstFlag;


    public Long getSort() {
        if (null == this.sort) {
            return -1L;
        }

        return this.sort;
    }

}
