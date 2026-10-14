package com.medusa.gruul.recycling.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
/**
 * @author whh
 * @description
 * @data: 2024/5/5
 */
public class RecyclingOrderListVo {


    /**
     * 主键
     */
    @Schema(title = "主键")
    private Long id;

    /**
     * 物流单号
     */
    @Schema(title = "物流单号")
    private String shipmentNumber;

    /**
     * 物流公司
     */
    @Schema(title = "物流公司")
    private String shipmentName;

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


    @Schema(title = "寄出时间-年日月")
    private String sendOutDate;


    @Schema(title = "寄出时间-时秒")
    private String sendOutTime;


    @Schema(title = "创建时间--年日月")
    private String createDate;

    @Schema(title = "创建时间-时秒")
    private String createTime;


    /**
     * 回收状态 0-待寄出  1-已寄出  2-取消
     */
    @Schema(title = "回收状态 0-待寄出  1-已寄出  2-取消 ")
    private Integer edStatus;

}
