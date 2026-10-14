package com.medusa.gruul.live.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * The type Room info vo.
 * @author alan
 */
@Data
@Accessors(chain = true)
public class RoomInfoVo {
    /**
     * 房间名.
     */
    private String name;
    /**
     * 房间id.
     */
    private Integer roomid;
    /**
     * 直播间背景墙.
     */
    private String cover_img;
    /**
     * live_status 直播状态 101: 直播中, 102: 未开始, 103: 已结束, 104: 禁播, 105: 暂停中, 106: 异常, 107: 已过期.
     */
    private Integer live_status;
    /**
     * 开始时间.
     */
    private Long start_time;
    /**
     * 结束时间.
     */
    private Long end_time;
    /**
     * 主播名称.
     */
    private String anchor_name;
    /**
     * 主播头像.
     */
    private String anchor_img;
    /**
     * 分享卡片封面.
     */
    private String share_img;
    /**
     * 商品集合.
     */
    private List<GoodsVo> goods;
}
