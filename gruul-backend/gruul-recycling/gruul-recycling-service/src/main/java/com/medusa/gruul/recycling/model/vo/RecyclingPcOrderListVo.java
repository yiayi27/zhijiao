package com.medusa.gruul.recycling.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/5/7
 */
public class RecyclingPcOrderListVo {


    /**
     * 主键
     */
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "物流公司")
    private String shipmentName;

    /**
     * 物流单号
     */
    @Schema(title = "物流单号")
    private String shipmentNumber;

    /**
     * 物品图片
     */
    @Schema(title = "物品图片")
    private String articleImgs;

    /**
     * 凭证图片
     */
    @Schema(title = "凭证图片")
    private String proofImgs;

    /**
     * 寄出时间
     */
    @Schema(title = "寄出时间")
    private LocalDateTime sendOut;

    /**
     * 回收状态 0-待寄出  1-已寄出  2-取消
     */
    @Schema(title = "回收状态 0-待寄出  1-已寄出  2-取消  2-取消")
    private Integer edStatus;

    @Schema(title = "用户名称")
    private String nickName;

    @Schema(title = "头像url")
    private String avatarUrl;

}
