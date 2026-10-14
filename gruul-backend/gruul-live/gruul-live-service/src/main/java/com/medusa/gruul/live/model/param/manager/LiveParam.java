package com.medusa.gruul.live.model.param.manager;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.common.core.param.AbstractQueryParam;
import com.medusa.gruul.live.api.entity.Live;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @Author: zhaokw
 * @Date: 2024/04/01
 */
@Data
@Schema(title = "LiveParam对象", description = "直播列表查询参数")
@Accessors(chain = true)
public class LiveParam extends AbstractQueryParam {
    private Long id;

    @Schema(title = "商铺id")
    private String shopId;

    @Schema(title = "直播房间号")
    private String roomId;

    @Schema(title = "直播名称")
    private String liveName;

    @Schema(title = "开播时间")
    private String startTime;

    @Schema(title = "停播时间")
    private String endTime;

    @Schema(title = "主播信息")
    private String anchor;


    @Schema(title = "背景图")
    private String coverImg;

    @Schema(title = "主播头像图")
    private String anchorImg;

    @Schema(title = "分享卡片图")
    private String shareImg;

    @Schema(title = "观看量")
    private Long viewCount;

    @Schema(title = "点赞量")
    private Long likeCount;

    @Schema(title = "首页排序")
    private Long sort;

    @Schema(title = "是否首页 0否 1是")
    private Integer firstFlag;


    public Live coverLive() {
        Live live = new Live();
        BeanUtil.copyProperties(this, live);
        return live;
    }
}
